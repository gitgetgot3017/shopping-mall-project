package shoppingmall.cart.repository;

import shoppingmall.cart.dto.CartItemDto;
import shoppingmall.cart.dto.ItemDetailForm;

import java.util.List;

public interface CartRepository {

    int findItemStock(long itemNum);
    long findCartNum(long memberNum);
    long addCart(long memberNum);
    void addCartItem(long cartNum, ItemDetailForm itemDetailForm);
    boolean checkPresentCartItem(long cartNum, long itemNum);
    void increaseCartItemCount(long cartNum, ItemDetailForm itemDetailForm);
    void deleteCartItem(long cartItemNum);
    void updateCartItem(long cartItemNum, int count);
    void deleteCartItemByCartNum(long cartNum);
    void deleteCart(long cartNum);
    List<CartItemDto> findCartItems(long cartNum);
    long findItemNum(long cartItemNum);
}