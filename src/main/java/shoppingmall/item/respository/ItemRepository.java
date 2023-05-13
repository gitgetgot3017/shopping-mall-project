package shoppingmall.item.respository;

import org.springframework.ui.Model;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> saveItem(Item item);
    Optional<Item> findByItemName(String itemName);
    List<Item> findItems(ItemSearchDto itemSearchDto, int page, Model model);
    int findRowNum(ItemSearchDto itemSearchDto);
}
