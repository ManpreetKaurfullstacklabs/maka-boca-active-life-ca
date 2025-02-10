package io.reactivestax.activelife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ShoppingCartResponseDTO {
    private Long familyMemberId;
    private List<ShoppingCartDTO> courses;
    private double totalPrice;
}
