package shoppingmall.item.respository;

import org.springframework.ui.Model;
import shoppingmall.item.dto.*;
import shoppingmall.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> saveItem(Item item);
    Optional<Item> findByItemName(String itemName);
    List<Item> findItems(ItemSearchDto itemSearchDto, int page, Model model);
    int findRowNum(ItemSearchDto itemSearchDto);
    Optional<ItemEditDto> findByItemNum(long itemNum);
    void updateItem(Item item);
    List<MainItemDto> findItemsMain(MainItemSearchDto mainSearchDto, int page);
    int findRowNum(String search);
    ItemDetailDto findItemDetail(long itemNum);
    String getSaveImgNames(long itemNum, List<String> notRepsaveImgName);
}
