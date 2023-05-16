package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;
import shoppingmall.item.constant.ItemSortBy;

@Getter
@Setter
public class MainItemSearchDto {

    private String search;
    private ItemSortBy sortBy;
}
