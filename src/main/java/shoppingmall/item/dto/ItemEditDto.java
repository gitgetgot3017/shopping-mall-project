package shoppingmall.item.dto;

import lombok.Getter;
import shoppingmall.item.constant.ItemSellStatus;

@Getter
public class ItemEditDto {

    private long itemNum;
    private String itemName;
    private int price;
    private String itemDetail;
    private int stock;
    private ItemSellStatus itemSellStatus;

    public ItemEditDto(long itemNum, String itemName, int price, String itemDetail, int stock, ItemSellStatus itemSellStatus) {
        this.itemNum = itemNum;
        this.itemName = itemName;
        this.price = price;
        this.itemDetail = itemDetail;
        this.stock = stock;
        this.itemSellStatus = itemSellStatus;
    }
}
