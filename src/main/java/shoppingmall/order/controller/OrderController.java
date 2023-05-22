package shoppingmall.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.cart.service.CartService;
import shoppingmall.item.dto.ItemDetailForm;
import shoppingmall.item.dto.ItemDetailForms;
import shoppingmall.item.entity.Item;
import shoppingmall.member.entity.Member;
import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;
import shoppingmall.order.service.OrderService;

import java.util.ArrayList;
import java.util.List;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    @PostMapping("/orders-form")
    public String order(@ModelAttribute ItemDetailForms itemDetailForms, Model model,
                        @RequestParam String retView) {

        //ItemDetailForms -> List<ItemDetailForm>
        List<ItemDetailForm> itemDetailFormList = changeDataFormat(itemDetailForms);

        //상품 재고 검사
        String outOfStckAlertMsg = checkItemStock(itemDetailFormList, null);

        //orderForm으로 이동할 수 없는 경우: 상품 재고 부족
        if (!outOfStckAlertMsg.equals("")) {
            model.addAttribute("outOfStckAlertMsg", outOfStckAlertMsg);
            return retView;
        }

        model.addAttribute("itemDetailFormList", itemDetailFormList);
        return "order/orderForm";
    }

    @PostMapping("/orders")
    public String order(@ModelAttribute ItemDetailForms itemDetailForms, Model model,
                        @SessionAttribute(name = LOGIN_MEMBER, required = false) Member member) {

        //ItemDetailForms -> List<ItemDetailForm>
        List<ItemDetailForm> itemDetailFormList = changeDataFormat(itemDetailForms);

        //상품 재고 검사
        List<Boolean> stock0AfterOrderList = new ArrayList<>();
        String outOfStckAlertMsg = checkItemStock(itemDetailFormList, stock0AfterOrderList);

        //주문할 수 없는 경우: 상품 재고 부족
        if (!outOfStckAlertMsg.equals("")) {
            model.addAttribute("outOfStckAlertMsg", outOfStckAlertMsg);
            model.addAttribute("itemDetailFormList", itemDetailFormList);
            return "order/orderForm";
        }

        //Order, List<OrderItem>, List<RdStockInfo> 생성 -> orderService.orderRequestBiz()
        Order order = Order.createOrder(member);
        List<OrderItem> orderItems = new ArrayList<>();
        List<RdStockInfo> rdStockInfos = new ArrayList<>();
        int totalPrice = fillInfos(itemDetailFormList, orderItems, rdStockInfos, 0);

        orderService.orderRequestBiz(order, orderItems, rdStockInfos, stock0AfterOrderList);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("itemDetailFormList", itemDetailFormList);
        model.addAttribute("totalPrice", totalPrice);
        return "order/orderCompleted";
    }

    private int fillInfos(List<ItemDetailForm> itemDetailFormList, List<OrderItem> orderItems, List<RdStockInfo> rdStockInfos, int totalPrice) {

        for (ItemDetailForm itemDetailForm : itemDetailFormList) {
            orderItems.add(OrderItem.createOrderItem(
                    null, new Item(itemDetailForm.getItemNum()), itemDetailForm.getCount(), itemDetailForm.getPrice()
                    ));
            rdStockInfos.add(new RdStockInfo(
                    itemDetailForm.getItemNum(), itemDetailForm.getCount()
            ));
            totalPrice += (itemDetailForm.getPrice() * itemDetailForm.getCount());
        }
        return totalPrice;
    }

    private String checkItemStock(List<ItemDetailForm> itemDetailFormList, List<Boolean> stock0AfterOrderList) {

        String outOfStckAlertMsg = "";
        for (ItemDetailForm itemDetailForm : itemDetailFormList) {
            int stock = cartService.findItemStock(itemDetailForm.getItemNum());
            if (itemDetailForm.getCount() > stock) {
                outOfStckAlertMsg += ("재고 부족: [" + itemDetailForm.getItemName() + "] 상품은 " + stock + "개 이하로 구매해주세요.\n");
            }
            if (stock0AfterOrderList != null) {
                stock0AfterOrderList.add(itemDetailForm.getCount() == stock);
            }
        }
        return outOfStckAlertMsg;
    }

    private List<ItemDetailForm> changeDataFormat(ItemDetailForms itemDetailForms) {

        List<ItemDetailForm> itemDetailFormList = new ArrayList<>();
        for (int i = 0; i< itemDetailForms.getItemNum().size(); i++) {
            itemDetailFormList.add(new ItemDetailForm(
                    itemDetailForms.getItemNum().get(i),
                    itemDetailForms.getSaveImgName().get(i),
                    itemDetailForms.getItemName().get(i),
                    itemDetailForms.getPrice().get(i),
                    itemDetailForms.getCount().get(i)
            ));
        }
        return itemDetailFormList;
    }

    @GetMapping("/orders")
    public String order(@SessionAttribute(name = LOGIN_MEMBER, required = false) Member member, Model model) {

        model.addAttribute("orderDtoList",  orderService.viewOrderHistory(member.getMember_num()));
        return "order/orderHistory";
    }
}
