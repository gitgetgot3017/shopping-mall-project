package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemDetailDto {

    private long itemNum;
    private String itemName;
    private int price;
    private String itemDetail;
    private String repSaveImgName;
    private List<String> notRepsaveImgNames;
    private int count = 1;

    public ItemDetailDto(long itemNum, String itemName, int price, String itemDetail, String repSaveImgName, List<String> notRepsaveImgNames) {
        this.itemNum = itemNum;
        this.itemName = itemName;
        this.price = price;
        this.itemDetail = itemDetail;
        this.repSaveImgName = repSaveImgName;
        this.notRepsaveImgNames = notRepsaveImgNames;
    }
}
