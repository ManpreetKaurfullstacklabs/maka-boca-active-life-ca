package io.reactivestax.activelife.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Member Login ID cannot be blank")
    @Size(min = 5, max = 50, message = "Member Login ID must be between 5 and 50 characters")
    private String memberLoginId;

    @NotBlank(message = "PIN cannot be blank")
    private String pin;

}
