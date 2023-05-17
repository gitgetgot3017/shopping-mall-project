package shoppingmall.cart.repository;

import shoppingmall.cart.dto.ItemDetailForm;

public interface CartRepository {

    int findItemStock(long itemNum);
    long findCartNum(long memberNum);
    long addCart(long memberNum);
    void addCartItem(long cartNum, ItemDetailForm itemDetailForm);
    boolean checkPresentCartItem(long cartNum, long itemNum);
    void increaseCartItemCount(long cartNum, ItemDetailForm itemDetailForm);
}