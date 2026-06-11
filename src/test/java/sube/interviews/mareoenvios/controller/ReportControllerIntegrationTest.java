package sube.interviews.mareoenvios.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ReportController — tests de integración")
class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /reports/topSended debe retornar 200 con un array de hasta 3 productos")
    void getTop3Products_shouldReturn200WithList() throws Exception {
        MvcResult result = mockMvc.perform(get("/reports/topSended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());

        // El endpoint retorna como máximo 3 productos (puede ser menos si hay pocos datos)
        assertThat(body.size()).isGreaterThan(0).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("GET /reports/topSended debe retornar productos con description y totalQuantity")
    void getTop3Products_shouldReturnCorrectFields() throws Exception {
        MvcResult result = mockMvc.perform(get("/reports/topSended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());

        // Verificar que cada elemento tenga los campos esperados
        body.forEach(product -> {
            assertThat(product.has("description")).isTrue();
            assertThat(product.has("totalQuantity")).isTrue();
            assertThat(product.get("totalQuantity").asLong()).isGreaterThan(0);
        });
    }

    @Test
    @DisplayName("GET /reports/topSended debe retornar productos ordenados por cantidad descendente")
    void getTop3Products_shouldBeOrderedDescending() throws Exception {
        MvcResult result = mockMvc.perform(get("/reports/topSended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());

        // Verificar ORDER BY DESC: cada elemento debe tener totalQuantity >= al siguiente
        for (int i = 0; i < body.size() - 1; i++) {
            long current = body.get(i).get("totalQuantity").asLong();
            long next = body.get(i + 1).get("totalQuantity").asLong();
            assertThat(current)
                    .as("Elemento %d (%d) debe ser >= elemento %d (%d)", i, current, i + 1, next)
                    .isGreaterThanOrEqualTo(next);
        }
    }
}
