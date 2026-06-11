package sube.interviews.mareoenvios.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import sube.interviews.mareoenvios.dto.request.ShippingCreateRequest;
import sube.interviews.mareoenvios.dto.request.ShippingItemRequest;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ShippingController — tests de integración")
class ShippingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Helper para crear un shipping y devolver su ID ---
    private long createShippingAndGetId() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .arriveDate(LocalDate.now().plusDays(5))
                .priority(1)
                .items(List.of(
                        ShippingItemRequest.builder().productId(1L).quantity(2).build()
                ))
                .build();

        MvcResult result = mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @DisplayName("GET /shipping/info/{id} debe retornar 200 con el envío creado")
    void getShippingById_shouldReturn200() throws Exception {
        long id = createShippingAndGetId();

        mockMvc.perform(get("/shipping/info/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.state").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @DisplayName("GET /shipping/info/999999 debe retornar 404")
    void getShippingById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/shipping/info/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /shipping/info/state/INITIAL debe retornar página")
    void getShippingsByState_shouldReturn200() throws Exception {
        mockMvc.perform(get("/shipping/info/state/INITIAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /shipping/info/{from}/{to} debe retornar página por rango de fechas")
    void getShippingsByDateRange_shouldReturn200() throws Exception {
        mockMvc.perform(get("/shipping/info/2024-01-01/2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("POST /shipping/create debe crear envío y retornar 201")
    void createShipping_shouldReturn201() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .arriveDate(LocalDate.now().plusDays(5))
                .priority(1)
                .items(List.of(
                        ShippingItemRequest.builder().productId(1L).quantity(2).build()
                ))
                .build();

        mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("INITIAL"));
    }

    @Test
    @DisplayName("PATCH /shipping/transition/sendToMail/{id} debe transicionar a SENT_TO_MAIL")
    void sendToMail_shouldTransitionState() throws Exception {
        long id = createShippingAndGetId();

        mockMvc.perform(patch("/shipping/transition/sendToMail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("SENT_TO_MAIL"));
    }

    @Test
    @DisplayName("POST /shipping/create sin items debe retornar 400")
    void createShipping_withoutItems_shouldReturn400() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .priority(1)
                .items(List.of())
                .build();

        mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /shipping/create con arriveDate anterior a sendDate debe retornar 400")
    void createShipping_withInvalidDateRange_shouldReturn400() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now().plusDays(5))
                .arriveDate(LocalDate.now()) // arriveDate antes que sendDate
                .priority(1)
                .items(List.of(
                        ShippingItemRequest.builder().productId(1L).quantity(1).build()
                ))
                .build();

        mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /shipping/create con customerId inexistente debe retornar 400")
    void createShipping_withNonExistentCustomerId_shouldReturn400() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(999999L)
                .sendDate(LocalDate.now())
                .arriveDate(LocalDate.now().plusDays(5))
                .priority(1)
                .items(List.of(
                        ShippingItemRequest.builder().productId(1L).quantity(1).build()
                ))
                .build();

        mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /shipping/create con productId inexistente debe retornar 400")
    void createShipping_withNonExistentProductId_shouldReturn400() throws Exception {
        ShippingCreateRequest request = ShippingCreateRequest.builder()
                .customerId(1L)
                .sendDate(LocalDate.now())
                .arriveDate(LocalDate.now().plusDays(5))
                .priority(1)
                .items(List.of(
                        ShippingItemRequest.builder().productId(999999L).quantity(1).build()
                ))
                .build();

        mockMvc.perform(post("/shipping/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /shipping/transition/inTravel desde INITIAL debe retornar 422")
    void transitionToInTravel_fromInitial_shouldReturn422() throws Exception {
        long id = createShippingAndGetId(); // estado INITIAL

        // Intentar saltar SENT_TO_MAIL e ir directo a IN_TRAVEL
        mockMvc.perform(patch("/shipping/transition/inTravel/{id}", id))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /shipping/transition/sendToMail desde DELIVERED debe retornar 422")
    void transitionFromFinalState_shouldReturn422() throws Exception {
        long id = createShippingAndGetId();

        // Llevar al estado final DELIVERED: INITIAL → SENT_TO_MAIL → IN_TRAVEL → DELIVERED
        mockMvc.perform(patch("/shipping/transition/sendToMail/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/shipping/transition/inTravel/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/shipping/transition/delivered/{id}", id))
                .andExpect(status().isOk());

        // DELIVERED es estado final — cualquier nueva transición debe dar 422
        mockMvc.perform(patch("/shipping/transition/cancelled/{id}", id))
                .andExpect(status().isUnprocessableEntity());
    }
}
