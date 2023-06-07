package shoppingmall.order.entity;

import lombok.Getter;
import lombok.Setter;
import shoppingmall.item.entity.Item;

@Getter
@Setter
public class OrderItem {

    private long orderItemNum;
    private Order order;
    private Item item;
    private int count;
    private int orderPrice;

    public OrderItem(Order order, Item item, int count, int orderPrice) {
        this.order = order;
        this.item = item;
        this.count = count;
        this.orderPrice = orderPrice;
    }

    public static OrderItem createOrderItem(Order order, Item item, int count, int orderPrice) {
        return new OrderItem(order, item, count, orderPrice);
    }
}