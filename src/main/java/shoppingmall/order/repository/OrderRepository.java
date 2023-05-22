package shoppingmall.order.repository;

import shoppingmall.order.dto.OrderDto;
import shoppingmall.order.dto.OrderItemDto;
import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;

import java.util.List;

public interface OrderRepository {

    void addOrder(Order order);
    long findOrderNum();
    void addOrderItem(OrderItem orderItem);
    void reduceItemStock(RdStockInfo rdStockInfo, boolean stock0AfterOrder);
    List<OrderDto> getOrderHistory(long memberNum);
    List<OrderItemDto> getOrderItems(long orderNum);
    void updateOrderStatus(long orderNum);
    void updateOrderItemPrice(long orderNum);
    List<RdStockInfo> toRestoreItemStock(long orderNum);
    void restoreItemStock(RdStockInfo rdStockInfo);
}
