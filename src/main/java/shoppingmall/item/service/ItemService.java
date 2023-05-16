package shoppingmall.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.dto.MainItemDto;
import shoppingmall.item.dto.MainItemSearchDto;
import shoppingmall.item.entity.Item;
import shoppingmall.item.respository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final int ITEMNUM_PER_PAGE = 6;
    private final int PAGENUMCNT_PER_PAGE = 5;

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

    public List<MainItemDto> showMainItems(MainItemSearchDto mainSearchDto, int page) {
        return itemRepository.findItemsMain(mainSearchDto, page);
    }

    //pageination을 처리하기 위한 비지니스 로직
    public void paginationBiz(MainItemSearchDto mainSearchDto, int page, Model model) {
        int pageNum = findPageNum(mainSearchDto);
        int startPage = findStartPage(page);
        int endPage = findEndPage(startPage, pageNum);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("prevOx", startPage != 1);
        model.addAttribute("nextOx", endPage != pageNum);
    }

    private int findPageNum(MainItemSearchDto mainSearchDto) {
        int rowNum = itemRepository.findRowNum(mainSearchDto.getSearch());
        return rowNum % ITEMNUM_PER_PAGE == 0 ? rowNum / ITEMNUM_PER_PAGE : rowNum / ITEMNUM_PER_PAGE + 1;
    }

    private int findStartPage(int page) {
        return ((page-1)/PAGENUMCNT_PER_PAGE) * PAGENUMCNT_PER_PAGE + 1;
    }

    private int findEndPage(int startPage, int pageNum) {
        int endPage = startPage + (PAGENUMCNT_PER_PAGE-1);
        if (endPage > pageNum) {
            endPage = pageNum;
        }
        return endPage;
    }
}
