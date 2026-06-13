package sube.interviews.mareoenvios.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    Long id;
    String firstName;
    String lastName;
    String address;
    String city;
}
