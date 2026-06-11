package sube.interviews.mareoenvios.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sube.interviews.mareoenvios.domain.Customer;
import sube.interviews.mareoenvios.dto.request.CustomerRequest;
import sube.interviews.mareoenvios.dto.response.CustomerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomerMapper — tests unitarios")
class CustomerMapperTest {

    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMapper();
    }

    @Test
    @DisplayName("toResponse debe mapear todos los campos de Customer a CustomerResponse")
    void toResponse_shouldMapAllFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .address("Av. Corrientes 1234")
                .city("Buenos Aires")
                .build();

        CustomerResponse response = mapper.toResponse(customer);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Juan");
        assertThat(response.getLastName()).isEqualTo("Pérez");
        assertThat(response.getAddress()).isEqualTo("Av. Corrientes 1234");
        assertThat(response.getCity()).isEqualTo("Buenos Aires");
    }

    @Test
    @DisplayName("toResponse debe manejar campos opcionales nulos")
    void toResponse_shouldHandleNullOptionalFields() {
        Customer customer = Customer.builder()
                .id(2L)
                .firstName("Ana")
                .lastName("García")
                .build();

        CustomerResponse response = mapper.toResponse(customer);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getFirstName()).isEqualTo("Ana");
        assertThat(response.getAddress()).isNull();
        assertThat(response.getCity()).isNull();
    }

    @Test
    @DisplayName("toEntity debe mapear todos los campos de CustomerRequest a Customer")
    void toEntity_shouldMapAllFields() {
        CustomerRequest request = CustomerRequest.builder()
                .firstName("María")
                .lastName("López")
                .address("Calle Falsa 123")
                .city("Rosario")
                .build();

        Customer entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("María");
        assertThat(entity.getLastName()).isEqualTo("López");
        assertThat(entity.getAddress()).isEqualTo("Calle Falsa 123");
        assertThat(entity.getCity()).isEqualTo("Rosario");
    }

    @Test
    @DisplayName("toEntity no debe asignar ID (lo genera la base de datos)")
    void toEntity_shouldNotSetId() {
        CustomerRequest request = CustomerRequest.builder()
                .firstName("Pedro")
                .lastName("Ramirez")
                .build();

        Customer entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNull();
    }
}
