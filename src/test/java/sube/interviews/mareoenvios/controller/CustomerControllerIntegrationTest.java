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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CustomerController — tests de integración")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /customer/info debe retornar 200 con página de customers")
    void getAllCustomers_shouldReturn200() throws Exception {
        mockMvc.perform(get("/customer/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /customer/info/{id} debe retornar 200 usando un ID obtenido del listado")
    void getCustomerById_shouldReturn200_whenExists() throws Exception {
        // Obtener el primer ID disponible del listado en lugar de hardcodear id=1
        MvcResult listResult = mockMvc.perform(get("/customer/info"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode content = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .get("content");
        long existingId = content.get(0).get("id").asLong();

        mockMvc.perform(get("/customer/info/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    @DisplayName("GET /customer/info/999999 debe retornar 404")
    void getCustomerById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/customer/info/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
