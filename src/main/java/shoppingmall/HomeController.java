package shoppingmall;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.item.dto.MainItemDto;
import shoppingmall.item.dto.MainItemSearchDto;
import shoppingmall.item.service.ItemImgService;
import shoppingmall.item.service.ItemService;

import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;
    private final ItemImgService itemImgService;

    @GetMapping
    public String home(@ModelAttribute("mainSearchDto") MainItemSearchDto mainSearchDto, @RequestParam(defaultValue = "1") int page, Model model) {

        List<MainItemDto> mainItemDtos = itemService.showMainItems(mainSearchDto, page);
        if (mainItemDtos.size() > 0) {
            model.addAttribute("mainItems", mainItemDtos);
            itemService.paginationBiz(mainSearchDto, page, model);
            model.addAttribute("search", mainSearchDto.getSearch());
            model.addAttribute("presentItemYn", true);
        }
        return "home";
    }

    @ResponseBody
    @GetMapping("/image/{saveImgName}")
    public Resource image(@PathVariable String saveImgName) {
        try {
            return new UrlResource("file:" + itemImgService.getFullPath(saveImgName));
        } catch (MalformedURLException e) {

        }
        return null;
    }
}
