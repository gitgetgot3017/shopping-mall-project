package shoppingmall.item.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import shoppingmall.item.constant.ItemSellStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class Item {

    private long itemNum;
    private String itemName;
    private int price;
    private String itemDetail;
    private int stock;
    private ItemSellStatus itemSellStatus;
    private LocalDateTime regdate;

    public Item() {
    }

    public Item(long itemNum, String itemName, int price, int stock, ItemSellStatus itemSellStatus, LocalDateTime regdate) {
        this.itemNum = itemNum;
        this.itemName = itemName;
        this.price = price;
        this.stock = stock;
        this.itemSellStatus = itemSellStatus;
        this.regdate = regdate;
    }
}
