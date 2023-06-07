package shoppingmall.order.entity;

import lombok.Getter;
import lombok.Setter;
import shoppingmall.member.entity.Member;
import shoppingmall.order.constant.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class Order {

    private long orderNum;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Member member;

    public Order(LocalDateTime orderDate, OrderStatus orderStatus, Member member) {
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.member = member;
    }

    public static Order createOrder(Member member) {
        return new Order(LocalDateTime.now(), OrderStatus.ORDER, member);
    }
}