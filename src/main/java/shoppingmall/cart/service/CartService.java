package shoppingmall.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.cart.dto.CartItemDto;
import shoppingmall.cart.dto.ItemDetailForm;
import shoppingmall.cart.repository.CartRepository;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.respository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

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

    public void updateItemInCart(long cartItemNum, int count) {
        cartRepository.updateCartItem(cartItemNum, count);
    }

    public List<CartItemDto> ShowItemInCart(long memberNum) {
        long cartNum = cartRepository.findCartNum(memberNum);
        if (cartNum != 0L) {
            return cartRepository.findCartItems(cartNum); //장바구니가 없는 회원이 호출해도 문제 없지만, 성능 상의 이유로 쿼리를 날리지 않을 것임
        }
        return new ArrayList<>();
    }

    public Optional<ItemEditDto> findItemStockAndName(long cartItemNum) {
        return itemRepository.findByItemNum(cartRepository.findItemNum(cartItemNum)); //cartItemNum 150, itemNum 95
    }
}