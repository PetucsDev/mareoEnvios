package sube.interviews.mareoenvios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sube.interviews.mareoenvios.domain.ShippingState;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;
import sube.interviews.mareoenvios.service.ShippingService;
import sube.interviews.mareoenvios.service.ShippingWriteService;

import java.time.LocalDate;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping", description = "Operaciones sobre envíos")
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
    public ResponseEntity<Page<ShippingResponse>> getShippingsByDateRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sendDateFrom,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sendDateTo,
            @PageableDefault(size = 20, sort = "sendDate") Pageable pageable) {
        return ResponseEntity.ok(shippingService.getShippingsByDateRange(sendDateFrom, sendDateTo, pageable));
    }

    // Nota: la consigna define /shipping/info/{state}, pero colisiona con /shipping/info/{shippingId}
    // (ambos son path variables de un solo segmento y Spring no puede distinguirlos en runtime
    // cuando el valor es numérico). Se usa /shipping/info/state/{state} para evitar la ambigüedad.
    @GetMapping("/info/state/{state}")
    @Operation(summary = "Listado de envíos por estado")
    public ResponseEntity<Page<ShippingResponse>> getShippingsByState(
            @PathVariable ShippingState state,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(shippingService.getShippingsByState(state, pageable));
    }

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva solicitud de envío")
    public ResponseEntity<ShippingResponse> createShipping(@RequestBody @Valid ShippingCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shippingWriteService.createShipping(request));
    }

    // --- Transiciones de estado ---

    @PatchMapping("/transition/sendToMail/{shippingId}")
    @Operation(summary = "Transición: Inicial → Entregado al correo")
    public ResponseEntity<ShippingResponse> sendToMail(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.SENT_TO_MAIL));
    }

    @PatchMapping("/transition/inTravel/{shippingId}")
    @Operation(summary = "Transición: Entregado al correo → En camino")
    public ResponseEntity<ShippingResponse> inTravel(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.IN_TRAVEL));
    }

    @PatchMapping("/transition/delivered/{shippingId}")
    @Operation(summary = "Transición: En camino → Entregado")
    public ResponseEntity<ShippingResponse> delivered(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.DELIVERED));
    }

    @PatchMapping("/transition/cancelled/{shippingId}")
    @Operation(summary = "Transición: Inicial/Entregado al correo → Cancelado")
    public ResponseEntity<ShippingResponse> cancelled(@PathVariable Long shippingId) {
        return ResponseEntity.ok(shippingWriteService.transitionState(shippingId, ShippingState.CANCELLED));
    }
}
