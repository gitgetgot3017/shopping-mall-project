package shoppingmall.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;
import shoppingmall.item.respository.ItemRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Optional<Item> regItem(Item item) throws SQLException {
        validateDuplicateItem(item); //중복 상품이 존재하는지 확인
        return itemRepository.saveItem(item);
    }

    public void validateDuplicateItem(Item item) throws SQLException {
        Optional<Item> findItem = itemRepository.findByItemName(item.getItemName());
        if (findItem.isPresent()) {
            throw new IllegalStateException("해당 상품명은 이미 등록되어 있습니다.");
        }
    }

    public List<Item> showItems(ItemSearchDto itemSearchDto, int page, Model model) throws SQLException {
        return itemRepository.findItems(itemSearchDto, page, model);
    }
}
