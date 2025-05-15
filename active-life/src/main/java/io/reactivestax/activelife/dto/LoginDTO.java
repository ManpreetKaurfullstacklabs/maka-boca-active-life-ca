package io.reactivestax.activelife.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

  //  @NotBlank(message = "Member Login ID cannot be blank")
    private String memberLoginId;

 //   @NotBlank(message = "PIN cannot be blank")
    private String pin;

}
