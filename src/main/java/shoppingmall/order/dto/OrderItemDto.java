package shoppingmall.order.dto;

import lombok.Getter;

@Getter
public class OrderItemDto {

    private long itemNum;
    private String saveImgName;
    private String itemName;
    private int count;
    private int orderPrice;

    public OrderItemDto(long itemNum, String saveImgName, String itemName, int count, int orderPrice) {
        this.itemNum = itemNum;
        this.saveImgName = saveImgName;
        this.itemName = itemName;
        this.count = count;
        this.orderPrice = orderPrice;
    }
}
