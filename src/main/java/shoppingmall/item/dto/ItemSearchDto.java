package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;
import shoppingmall.item.constant.ItemSellStatus;

@Getter
@Setter
public class ItemSearchDto {

    private String regDuration;
    private ItemSellStatus itemSellStatus;
    private String search;
}
