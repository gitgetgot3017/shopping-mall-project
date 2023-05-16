package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private long itemNum;
    private String itemName;
    private int price;
    private String saveImgName;

    public MainItemDto(long itemNum, String itemName, int price, String saveImgName) {
        this.itemNum = itemNum;
        this.itemName = itemName;
        this.price = price;
        this.saveImgName = saveImgName;
    }
}