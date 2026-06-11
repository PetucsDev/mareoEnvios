package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerResponse {
    Long id;
    String firstName;
    String lastName;
    String address;
    String city;
}
