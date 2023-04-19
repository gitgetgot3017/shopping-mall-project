package shoppingmall.item.respository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import shoppingmall.item.constant.ItemSellStatus;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static shoppingmall.jdbc.constant.ConnectionConst.*;

@Slf4j
@Repository
public class ItemRepository {

    public Optional<Item> saveItem(Item item) throws SQLException {
        String sql = "insert into item(item_name, price, item_detail, stock, item_sell_status, regdate) values(?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getItemName());
            pstmt.setInt(2, item.getPrice());
            pstmt.setString(3, item.getItemDetail());
            pstmt.setInt(4, item.getStock());
            pstmt.setString(5, String.valueOf(item.getItemSellStatus()));
            pstmt.setTimestamp(6, Timestamp.valueOf(item.getRegdate()));
            pstmt.executeUpdate();

            return findByItemName(item.getItemName()); //Optional.empty() 또는 Optional.of(item)
        } catch (SQLException e) {
            log.info("DB ERROR", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    public Optional<Item> findByItemName(String itemName) throws SQLException {
        String sql = "select * from item where item_name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, itemName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Item item = new Item();
                item.setItemNum(rs.getLong("item_num"));
                return Optional.of(item);
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.info("DB ERROR", e);
            throw e;
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
