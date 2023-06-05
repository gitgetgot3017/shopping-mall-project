package shoppingmall.cart.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import shoppingmall.cart.dto.CartItemDto;
import shoppingmall.cart.dto.ItemDetailForm;
import shoppingmall.exception.RuntimeSQLException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CartRepositoryImpl implements CartRepository {

    private final DataSource dataSource;

    @Override
    public int findItemStock(long itemNum) {
        String sql = "select stock from item where item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, itemNum);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0; //해당 item_num을 가진 상품이 삭제된 경우로, return문 제거하고 예외를 던지도록 하자
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public long findCartNum(long memberNum) {
        String sql = "select cart_num from cart where member_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, memberNum);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("cart_num");
            }
            return 0L; //경우1. 해당 member의 cart가 없는 경우(일반적) 경우2. 해당 member_num을 가진 member가 없는 경우(특수, 문제)
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public long addCart(long memberNum) {
        String sql = "insert into cart(member_num) values(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, memberNum);
            pstmt.executeUpdate();

            return findCartNum(memberNum);
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void addCartItem(long cartNum, ItemDetailForm itemDetailForm) {
        String sql = "insert into cart_item(cart_num, item_num, count) values(?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartNum);
            pstmt.setLong(2, itemDetailForm.getItemNum());
            pstmt.setInt(3, itemDetailForm.getCount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public boolean checkPresentCartItem(long cartNum, long itemNum)  {
        String sql = "select * from cart_item where cart_num = ? and item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartNum);
            pstmt.setLong(2, itemNum);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void increaseCartItemCount(long cartNum, ItemDetailForm itemDetailForm) {
        String sql = "update cart_item set count = count+? where cart_num = ? and item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemDetailForm.getCount());
            pstmt.setLong(2, cartNum);
            pstmt.setLong(3, itemDetailForm.getItemNum());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void deleteCartItem(long cartItemNum) {
        String sql = "delete from cart_item where cart_item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartItemNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void updateCartItem(long cartItemNum, int count) {
        String sql = "update cart_item set count = ? where cart_item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, count);
            pstmt.setLong(2, cartItemNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void deleteCartItemByCartNum(long cartNum) {
        String sql = "delete from cart_item where cart_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void deleteCart(long cartNum) {
        String sql = "delete from cart where cart_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public List<CartItemDto> findCartItems(long cartNum) {
        //sql문 작성 순서
        //1. cart_item(fci), item_img(fii) 조인(fcifii)
        //2. fciii, item(i) 조인
        String sql = "select fciii.*, item_name, price " +
                "from (select cart_item_num, fci.item_num, count, save_img_name " +
                "from (select * from cart_item where cart_num = ?) fci join (select * from item_img where rep_img = true) fii on fci.item_num = fii.item_num) fciii join item i on fciii.item_num = i.item_num " +
                "order by cart_item_num desc";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartNum);
            rs = pstmt.executeQuery();

            List<CartItemDto> cartItemDtoList = new ArrayList<>();
            while (rs.next()) {
                cartItemDtoList.add(new CartItemDto(
                        rs.getLong("cart_item_num"),
                        rs.getLong("item_num"),
                        rs.getString("save_img_name"),
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("count")
                ));
            }
            return cartItemDtoList;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public long findItemNum(long cartItemNum) {
        String sql = "select item_num from cart_item where cart_item_num = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, cartItemNum);
            rs = pstmt.executeQuery();

            rs.next();
            return rs.getLong("item_num");
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}

