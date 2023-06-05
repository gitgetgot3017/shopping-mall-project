package shoppingmall.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shoppingmall.cart.dto.CartItemDto;
import shoppingmall.cart.dto.ItemDetailForm;
import shoppingmall.cart.service.CartService;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.service.ItemService;
import shoppingmall.member.entity.Member;

import java.util.List;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;

    @PostMapping
    public String cart(@SessionAttribute(name = LOGIN_MEMBER) Member member,
                       @Validated @ModelAttribute ItemDetailForm itemDetailForm, BindingResult bindingResult, Model model) {

        //장바구니 담기 불가1. 상품 상세에서 itemNum 또는 count에 유효하지 않은 값을 보낸 경우
        if (bindingResult.hasErrors() || itemService.findItem(itemDetailForm.getItemNum()).isEmpty()) {
            //(alert로 이유 설명. 상품 상세 커밋 이후에 가능)
            return "item/itemDetail";
        }

        //장바구니 담기 불가2. 재고보다 큰 수량을 장바구니에 담으려는 경우
        int stock = cartService.findItemStock(itemDetailForm.getItemNum());
        if (itemDetailForm.getCount() > stock) {
            //(alert로 이유 설명. 상품 상세 커밋 이후에 가능)
            return "item/itemDetail";
        }

        cartService.putItemInCart(member.getMember_num(), itemDetailForm);
        //(장바구니에 상품이 담겼음으로 알리기. 상품 상세 커밋 이후에 가능)
        return "item/itemDetail";
    }

    @DeleteMapping("/{cartItemNum}")
    public String cart(@PathVariable long cartItemNum, Model model,
                       @SessionAttribute(name = LOGIN_MEMBER) Member member) {

        cartService.deleteItemFromCart(cartItemNum);
        decideModelAttributeToCartView(cartService.ShowItemInCart(member.getMember_num()), model);
        return "cart/cart";
    }

    @PutMapping("/{cartItemNum}")
    public String cart(@PathVariable long cartItemNum, @RequestParam int count, Model model,
                       @SessionAttribute(name = LOGIN_MEMBER) Member member) {

        ItemEditDto itemEditDto = cartService.findItemStockAndName(cartItemNum).get();
        int stock = itemEditDto.getStock();

        if (count > stock) {
            model.addAttribute("outOfStckAlertMsg", "재고 부족: [" + itemEditDto.getItemName() + "] 상품은 " + stock + "개 이하로 담아주세요.\n");
        } else {
            cartService.updateItemInCart(cartItemNum, count);
        }

        decideModelAttributeToCartView(cartService.ShowItemInCart(member.getMember_num()), model);
        return "cart/cart";
    }

    @GetMapping
    public String cart(@SessionAttribute(name = LOGIN_MEMBER) Member member, Model model) {

        decideModelAttributeToCartView(cartService.ShowItemInCart(member.getMember_num()), model);
        return "cart/cart";
    }

    //메소드명: Cart라는 View에 전달되는 Model에 attribute 넣을지를 결정한다
    private void decideModelAttributeToCartView(List<CartItemDto> cartItemDtoList, Model model) {
        if (cartItemDtoList.size() > 0) {
            model.addAttribute("presentCartItem", true);
            model.addAttribute("cartItemDtoList", cartItemDtoList);
        }
    }
}