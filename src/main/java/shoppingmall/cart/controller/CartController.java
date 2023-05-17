package shoppingmall.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import shoppingmall.cart.dto.ItemDetailForm;
import shoppingmall.cart.service.CartService;
import shoppingmall.item.service.ItemService;
import shoppingmall.member.dto.LoginFormDto;
import shoppingmall.member.entity.Member;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;

    @PostMapping
    public String cart(@SessionAttribute(name = LOGIN_MEMBER, required = false) Member member,
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

        //장바구니 담기 불가3. 로그인되지 않은 회원
        if (member == null) {
            model.addAttribute("loginFormDto", new LoginFormDto());
            model.addAttribute("logout", true);
            return "member/loginForm";
        }

        cartService.putItemInCart(member.getMember_num(), itemDetailForm);
        //(장바구니에 상품이 담겼음으로 알리기. 상품 상세 커밋 이후에 가능)
        return "item/itemDetail";
    }
}