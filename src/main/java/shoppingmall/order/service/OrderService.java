package shoppingmall.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.order.dto.OrderDto;
import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;
import shoppingmall.order.repository.OrderRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void orderRequestBiz(Order order, List<OrderItem> orderItems, List<RdStockInfo> rdStockInfos, List<Boolean> stock0AfterOrderList) {

        orderRepository.addOrder(order);

        long orderNum = orderRepository.findOrderNum();

        for (int i=0; i<orderItems.size(); i++) {
            order.setOrderNum(orderNum); //orderNum 필드가 채워진 Order
            orderItems.get(i).setOrder(order);
            orderRepository.addOrderItem(orderItems.get(i));
            orderRepository.reduceItemStock(rdStockInfos.get(i), stock0AfterOrderList.get(i));
        }
    }

    public List<OrderDto> viewOrderHistory(long memberNum) {
        return orderRepository.getOrderHistory(memberNum);
    }

    public void cancelOrder(long orderNum) {

        orderRepository.updateOrderStatus(orderNum);
        orderRepository.updateOrderItemPrice(orderNum);
        for(RdStockInfo rdStockInfo : orderRepository.toRestoreItemStock(orderNum)) {
            orderRepository.restoreItemStock(rdStockInfo);
        }
    }
}
