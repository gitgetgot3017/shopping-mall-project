package shoppingmall.cart.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ItemDetailForm {

    @NotNull
    private Long itemNum;

    @NotNull
    @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
    private Integer count;
}