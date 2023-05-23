package shoppingmall.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.dto.ItemEditForm;
import shoppingmall.item.dto.ItemRegForm;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;
import shoppingmall.item.service.ItemImgService;
import shoppingmall.item.service.ItemService;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemImgService itemImgService;
    private final int INDEXNUM_PER_PAGE = 5;

    @GetMapping("/items-regform")
    public String itemReg(Model model) {
        model.addAttribute("itemRegForm", new ItemRegForm());
        return "item/itemRegForm";
    }

    @PostMapping("/items")
    public String itemReg(@Validated @ModelAttribute ItemRegForm itemRegForm, BindingResult bindingResult) {

        //검증 실패
        validateAttachFile(itemRegForm.getItemImgFiles(), bindingResult); //첨부 파일 검증

        if (bindingResult.hasErrors()) {
            return "item/itemRegForm";
        }

        //검증 성공 -> DB 저장
        try {
            //item에 Item 저장
            Item item = ItemRegForm.createMember(itemRegForm); //ItemRegForm -> Item
            item = itemService.regItem(item).get();

            //storage에 상품 이미지, item_img에 ItemImg 저장
            boolean repImg = true;
            for (MultipartFile itemImgFile : itemRegForm.getItemImgFiles()) {
                if (!itemImgFile.isEmpty()) {
                    itemImgService.regItemImg(itemImgFile, item, repImg);
                    repImg = false;
                }
            }
        } catch (IllegalStateException e) {
            log.info("itemReg Fail");
            bindingResult.reject("itemRegFail", "해당 상품명은 이미 등록되어 있습니다.");
            return "item/itemRegForm";
        } catch (NoSuchElementException e) {
            log.info("regItemImg WithoutItem");
            bindingResult.reject("regItemImgWithoutItem", "삭제된 상품에 대한 이미지를 등록할 수 없습니다.");
            return "item/itemRegForm";
        } catch (IOException e) {
            log.info("ItemImgSave Error");
            bindingResult.reject("ItemImgSaveError", "상품 이미지를 저장하는 과정에서 오류가 발생했습니다. 잠시 후에 다시 시도해주세요.");
            return "item/itemRegForm";
        }

        //상품 등록 성공
        return "redirect:/items";
    }

    private void validateAttachFile(List<MultipartFile> itemImgFiles, BindingResult bindingResult) {
        //확장자 검사(첨부한 파일이 이미지 파일인지)
        for (MultipartFile itemImgFile : itemImgFiles) {
            if (!itemImgFile.isEmpty()) {
                if (imgExtensionCheck(itemImgFile.getOriginalFilename()) == false) {
                    bindingResult.reject("FileNotImage", "이미지 파일을 첨부해주세요.");
                    return;
                }
            }
        }

        //대표 파일 첨부 여부 검사
        if (FileAttachCheck(itemImgFiles.get(0)) == false) {
            bindingResult.reject( "RepFileNotAttach", "대표 상품 이미지 등록은 필수입니다.");
        }
    }

    private boolean imgExtensionCheck(String filename) {
        int pos = filename.lastIndexOf(".");
        String extension = filename.substring(pos+1);

        if ("jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) ||
                "gif".equals(extension) || "bmp".equals(extension)) {
            return true;
        }
        return false;
    }

    private boolean FileAttachCheck(MultipartFile itemImgFile) {
        if (itemImgFile.isEmpty()) {
            return false;
        }
        return true;
    }

    @GetMapping("/items")
    public String itemList(@RequestParam(defaultValue = "1") int page, @ModelAttribute ItemSearchDto itemSearchDto, Model model) {

        List<Item> items = itemService.showItems(itemSearchDto, page, model);
        model.addAttribute("items", items);
        pageInfoToView(page, model);

        return "item/itemList";
    }

    public void pageInfoToView(int page, Model model) {
        //Model에 currentPage 저장
        model.addAttribute("currentPage", page);

        //Model에 startPage 저장
        int startPage = page%INDEXNUM_PER_PAGE==0 ? INDEXNUM_PER_PAGE*((page/INDEXNUM_PER_PAGE)-1)+1 : INDEXNUM_PER_PAGE*(page/INDEXNUM_PER_PAGE)+1;
        model.addAttribute("startPage", startPage);

        //Model에 endPage 저장
        int pageNum = (int) model.getAttribute("pageNum");
        int endPage = startPage+(INDEXNUM_PER_PAGE-1);
        if (pageNum < endPage) {
            endPage = pageNum;
        }
        model.addAttribute("endPage", endPage);
    }

    @GetMapping("/items-editform/{itemNum}")
    public String itemEdit(@PathVariable long itemNum, Model model) {

        try {
            ItemEditDto itemEditDto = itemService.findItem(itemNum).get();
            model.addAttribute("itemEditDto", itemEditDto);
        } catch (NoSuchElementException e) {
            log.info("EditItem WithoutItem");
//            bindingResult.reject("regItemImgWithoutItem", "삭제된 상품에 대한 이미지를 등록할 수 없습니다.");
//            return "item/itemRegForm";
        }

        return "item/itemEditForm";
    }

    @PutMapping("/items/{itemNum}")
    public String itemEdit(@Validated @ModelAttribute("itemEditDto") ItemEditForm itemEditForm, BindingResult bindingResult, @PathVariable long itemNum) {

        //검증 실패
        if (bindingResult.hasErrors()) {
            return "item/itemEditForm";
        }

        //상품명(PK) 중복 오류
        try {
            Item item = ItemEditForm.createMember(itemEditForm);
            itemService.modifyItem(item);
        } catch (DuplicateKeyException e) {
            bindingResult.reject("duplidateItemName", "이미 존재하는 상품명입니다. 다른 상품명을 입력해주세요.");
            return "item/itemEditForm";
        }

        return "redirect:/items";
    }

    @GetMapping("/items/{itemNum}")
    public String itemDetail(@PathVariable long itemNum, Model model) {

        //악의적 요청에 대비한 코드 - POSTMAN 등을 통해 존재하지 않는 itemNum를 요청
        if (itemService.findItem(itemNum).isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청: 존재하지 않는 itemNum을 요청했습니다.");
        }

        model.addAttribute("itemDetailDto", itemService.findItemDetail(itemNum));
        return "item/itemDetail";
    }
}
