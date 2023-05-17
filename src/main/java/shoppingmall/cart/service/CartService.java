package shoppingmall.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingmall.cart.dto.ItemDetailForm;
import shoppingmall.cart.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public int findItemStock(long itemNum) {
        return cartRepository.findItemStock(itemNum);
    }

    //장바구니 담기 관련 비지니스 로직
    public void putItemInCart(long memberNum, ItemDetailForm itemDetailForm) {

        //해당 회원이 장바구니가 없다면, 장바구니를 만들어준다.
        long cartNum = cartRepository.findCartNum(memberNum); //1-1 2-1 3-1
        if (cartNum == 0L) {
            cartNum = cartRepository.addCart(memberNum); //1-2
        }

        //장바구니에 상품을 추가한다.
        if (!cartRepository.checkPresentCartItem(cartNum, itemDetailForm.getItemNum())) {
            cartRepository.addCartItem(cartNum, itemDetailForm); //1-3 2-2
        }

        //이미 장바구니에 존재하는 상품이면, 수량을 증가시킨다.
        else {
            cartRepository.increaseCartItemCount(cartNum, itemDetailForm); //3-2
        }
    }

    public void deleteItemFromCart(long cartItemNum) {
        cartRepository.deleteCartItem(cartItemNum);
    }
}