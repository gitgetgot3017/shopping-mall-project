package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDetailForm {

    private long itemNum;
    private String saveImgName;
    private String itemName;
    private int price;
    private int count;

    public ItemDetailForm(long itemNum, String saveImgName, String itemName, int price, int count) {
        this.itemNum = itemNum;
        this.saveImgName = saveImgName;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
    }
}