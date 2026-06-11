package sube.interviews.mareoenvios.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sube.interviews.mareoenvios.domain.*;
import sube.interviews.mareoenvios.dto.response.ShippingResponse;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ShippingMapper — tests unitarios")
class ShippingMapperTest {

    private ShippingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShippingMapper(new CustomerMapper());
    }

    private Customer buildCustomer() {
        return Customer.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .address("Av. Corrientes 1234")
                .city("Buenos Aires")
                .build();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(10L)
                .description("Notebook")
                .weight(1.5)
                .build();
    }

    @Test
    @DisplayName("toResponse debe mapear todos los campos de Shipping a ShippingResponse")
    void toResponse_shouldMapAllFields() {
        Product product = buildProduct();
        ShippingItem item = ShippingItem.builder()
                .id(100L)
                .product(product)
                .productCount(2)
                .build();

        Shipping shipping = Shipping.builder()
                .id(1L)
                .customer(buildCustomer())
                .state(ShippingState.INITIAL)
                .sendDate(LocalDate.of(2024, 1, 15))
                .arriveDate(LocalDate.of(2024, 1, 20))
                .priority(1)
                .items(List.of(item))
                .build();

        ShippingResponse response = mapper.toResponse(shipping);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getState()).isEqualTo(ShippingState.INITIAL);
        assertThat(response.getSendDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(response.getArriveDate()).isEqualTo(LocalDate.of(2024, 1, 20));
        assertThat(response.getPriority()).isEqualTo(1);
        assertThat(response.getCustomer().getId()).isEqualTo(1L);
        assertThat(response.getCustomer().getFirstName()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("toResponse debe mapear los items con productId, description, weight y quantity")
    void toResponse_shouldMapItemsCorrectly() {
        Product product = buildProduct();
        ShippingItem item = ShippingItem.builder()
                .id(100L)
                .product(product)
                .productCount(3)
                .build();

        Shipping shipping = Shipping.builder()
                .id(1L)
                .customer(buildCustomer())
                .state(ShippingState.INITIAL)
                .priority(1)
                .items(List.of(item))
                .build();

        ShippingResponse response = mapper.toResponse(shipping);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductId()).isEqualTo(10L);
        assertThat(response.getItems().get(0).getDescription()).isEqualTo("Notebook");
        assertThat(response.getItems().get(0).getWeight()).isEqualTo(1.5);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("toResponse debe retornar lista de items vacía cuando el envío no tiene items")
    void toResponse_shouldReturnEmptyItems_whenNoItems() {
        Shipping shipping = Shipping.builder()
                .id(1L)
                .customer(buildCustomer())
                .state(ShippingState.INITIAL)
                .priority(1)
                .items(List.of())
                .build();

        ShippingResponse response = mapper.toResponse(shipping);

        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("toResponseList debe mapear una lista de Shippings correctamente")
    void toResponseList_shouldMapList() {
        Shipping s1 = Shipping.builder().id(1L).customer(buildCustomer()).state(ShippingState.INITIAL).priority(1).items(List.of()).build();
        Shipping s2 = Shipping.builder().id(2L).customer(buildCustomer()).state(ShippingState.SENT_TO_MAIL).priority(2).items(List.of()).build();

        List<ShippingResponse> responses = mapper.toResponseList(List.of(s1, s2));

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getState()).isEqualTo(ShippingState.SENT_TO_MAIL);
    }

    @Test
    @DisplayName("toResponseList debe retornar lista vacía para input vacío")
    void toResponseList_shouldReturnEmptyList_whenInputIsEmpty() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
