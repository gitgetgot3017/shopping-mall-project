package shoppingmall.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

    private long cartItemNum;
    private long itemNum;
    private String saveImgName;
    private String itemName;
    private int price;
    private int count;

    public CartItemDto(long cartItemNum, long itemNum, String saveImgName, String itemName, int price, int count) {
        this.cartItemNum = cartItemNum;
        this.itemNum = itemNum;
        this.saveImgName = saveImgName;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
    }
}
