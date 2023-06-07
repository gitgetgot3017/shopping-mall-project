package shoppingmall.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RdStockInfo { //상품의 재고를 감소시키기 위해 필요한 정보

    private long itemNum;
    private int count;

    public RdStockInfo(long itemNum, int count) {
        this.itemNum = itemNum;
        this.count = count;
    }
}
