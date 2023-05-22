package shoppingmall.order.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class OrderDto {

    private LocalDate orderDate;
    private long orderNum;
    private int totalPrice;
    private List<OrderItemDto> orderItemDtoList;
    private boolean presentCancelOrdBtn; //'주문 취소' 버튼 존재 여부

    public OrderDto(LocalDate orderDate, long orderNum, int totalPrice, List<OrderItemDto> orderItemDtoList, boolean presentCancelOrdBtn) {
        this.orderDate = orderDate;
        this.orderNum = orderNum;
        this.totalPrice = totalPrice;
        this.orderItemDtoList = orderItemDtoList;
        this.presentCancelOrdBtn = presentCancelOrdBtn;
    }
}
