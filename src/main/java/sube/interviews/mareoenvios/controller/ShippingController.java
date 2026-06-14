package sube.interviews.mareoenvios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.dto.response.PagedResponse;
import sube.interviews.mareoenvios.service.ShippingService;
import sube.interviews.mareoenvios.service.ShippingWriteService;

import java.time.LocalDate;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping", description = "Operaciones sobre envíos")
@Slf4j
public class ShippingController {

    private final ShippingService shippingService;
    private final ShippingWriteService shippingWriteService;

    public ShippingController(
            ShippingService shippingService,
            @Qualifier("shippingRetryWrapper") ShippingWriteService shippingWriteService) {
        this.shippingService = shippingService;
        this.shippingWriteService = shippingWriteService;
    }

    @GetMapping("/info/{shippingId}")
    @Operation(summary = "Obtener información de un envío por ID")
    public ResponseEntity<ShippingResponse> getShippingById(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingService.getShippingById(shippingId));
    }

    @GetMapping("/info/{sendDateFrom}/{sendDateTo}")
    @Operation(summary = "Listado de envíos por rango de fecha de envío")
    public ResponseEntity<PagedResponse<ShippingResponse>> getShippingsByDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sendDateFrom,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sendDateTo,
            @PageableDefault(size = 20, sort = "sendDate") Pageable pageable) {
        return ResponseEntity.ok(shippingService.getShippingsByDateRange(sendDateFrom, sendDateTo, pageable));
    }

    // Nota: la consigna define /shipping/info/{state}, pero colisiona con /shipping/info/{shippingId}.
    // Spring resuelve el handler por patrón de URL antes de conocer el tipo del parámetro, por lo que
    // ambos mapeos son idénticos para el dispatcher ("/shipping/info/{variable}") y generan
    // IllegalStateException al iniciar. Se agrega el segmento fijo /state/ para evitar la colisión.
    @GetMapping("/info/state/{state}")
    @Operation(summary = "Listado de envíos por estado")
    public ResponseEntity<PagedResponse<ShippingResponse>> getShippingsByState(
            @PathVariable ShippingState state,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(shippingService.getShippingsByState(state, pageable));
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva solicitud de envío")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envío creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o customer/producto no encontrado"),
            @ApiResponse(responseCode = "422", description = "Error de validación de negocio")
    })
    public ResponseEntity<ShippingResponse> createShipping(@RequestBody @Valid ShippingCreateRequest request) {
        log.info("Creating new shipping for customer ID: {} with {} items", 
                request.getCustomerId() != null ? request.getCustomerId() : "new customer", 
                request.getItems().size());
        
        ShippingResponse response = shippingWriteService.createShipping(request);
        
        log.info("Successfully created shipping with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- Transiciones de estado ---

    @PatchMapping("/transition/sendToMail/{shippingId}")
    @Operation(summary = "Transición: Inicial → Entregado al correo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transición exitosa"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    public ResponseEntity<ShippingResponse> sendToMail(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.SENT_TO_MAIL));
    }

    @PatchMapping("/transition/inTravel/{shippingId}")
    @Operation(summary = "Transición: Entregado al correo → En camino")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transición exitosa"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    public ResponseEntity<ShippingResponse> inTravel(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.IN_TRAVEL));
    }

    @PatchMapping("/transition/delivered/{shippingId}")
    @Operation(summary = "Transición: En camino → Entregado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transición exitosa"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    public ResponseEntity<ShippingResponse> delivered(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.DELIVERED));
    }

    @PatchMapping("/transition/cancelled/{shippingId}")
    @Operation(summary = "Transición: Inicial/Entregado al correo → Cancelado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transición exitosa"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    public ResponseEntity<ShippingResponse> cancelled(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.CANCELLED));
    }
}
