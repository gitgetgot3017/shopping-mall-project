package shoppingmall.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemDetailForms {

    private List<Long> itemNum;
    private List<String> saveImgName;
    private List<String> itemName;
    private List<Integer> price;
    private List<Integer> count;
}