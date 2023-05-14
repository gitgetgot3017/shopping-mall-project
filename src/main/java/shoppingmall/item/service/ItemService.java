package shoppingmall.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;
import shoppingmall.item.respository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Optional<Item> regItem(Item item) {
        validateDuplicateItem(item); //중복 상품이 존재하는지 확인
        return itemRepository.saveItem(item);
    }

    public void validateDuplicateItem(Item item) {
        Optional<Item> findItem = itemRepository.findByItemName(item.getItemName());
        if (findItem.isPresent()) {
            throw new IllegalStateException("해당 상품명은 이미 등록되어 있습니다.");
        }
    }

    public List<Item> showItems(ItemSearchDto itemSearchDto, int page, Model model) {
        return itemRepository.findItems(itemSearchDto, page, model);
    }

    public Optional<ItemEditDto> findItem(long itemNum) {
        return itemRepository.findByItemNum(itemNum);
    }

    public void modifyItem(Item item) {
        itemRepository.updateItem(item);
    }
}
