package shoppingmall.order.repository;

import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;

public interface OrderRepository {

    void addOrder(Order order);
    long findOrderNum();
    void addOrderItem(OrderItem orderItem);
    void reduceItemStock(RdStockInfo rdStockInfo, boolean stock0AfterOrder);
}
