package shoppingmall.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shoppingmall.cart.service.CartService;
import shoppingmall.item.dto.ItemDetailForm;
import shoppingmall.item.dto.ItemDetailForms;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;

    @PostMapping("/orders-form")
    public String order(@ModelAttribute ItemDetailForms itemDetailForms, Model model,
                        @RequestParam String retView) {

        //ItemDetailForms -> List<ItemDetailForm>
        List<ItemDetailForm> itemDetailFormList = new ArrayList<>();
        for (int i=0; i<itemDetailForms.getItemNum().size(); i++) {
            itemDetailFormList.add(new ItemDetailForm(
                    itemDetailForms.getItemNum().get(i),
                    itemDetailForms.getSaveImgName().get(i),
                    itemDetailForms.getItemName().get(i),
                    itemDetailForms.getPrice().get(i),
                    itemDetailForms.getCount().get(i)
            ));
        }

        //상품 재고 검사
        String outOfStckAlertMsg = "";
        for (ItemDetailForm itemDetailForm : itemDetailFormList) {
            int stock = cartService.findItemStock(itemDetailForm.getItemNum());
            if (itemDetailForm.getCount() > stock) {
                outOfStckAlertMsg += ("재고 부족: [" + itemDetailForm.getItemName() + "] 상품의 재고가 " + stock + "개 남았습니다.\n");
            }
        }

        //orderForm으로 이동할 수 없는 경우: 상품 재고 부족
        if (!outOfStckAlertMsg.equals("")) {
            model.addAttribute("outOfStckAlertMsg", outOfStckAlertMsg);
            return retView;
        }

        model.addAttribute("itemDetailFormList", itemDetailFormList);
        return "order/orderForm";
    }
}
