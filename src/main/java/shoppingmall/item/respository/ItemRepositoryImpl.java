package shoppingmall.item.respository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import shoppingmall.exception.RuntimeSQLException;
import shoppingmall.item.constant.ItemSellStatus;
import shoppingmall.item.dto.ItemEditDto;
import shoppingmall.item.dto.ItemSearchDto;
import shoppingmall.item.entity.Item;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final DataSource dataSource;

    private final int ROWNUM_PER_PAGE = 5;

    @Override
    public Optional<Item> saveItem(Item item) {
        String sql = "insert into item(item_name, price, item_detail, stock, item_sell_status, regdate) values(?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
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
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public Optional<Item> findByItemName(String itemName) {
        String sql = "select * from item where item_name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
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
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Item> findItems(ItemSearchDto itemSearchDto, int page, Model model) {
        String sql = "select * from (select * from item where item_name like ? and item_sell_status like ? and regdate >= ? limit ? offset ?) standard order by item_num desc";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            //1st parameter
            if (itemSearchDto.getSearch() == null) {
                pstmt.setString(1, "%%");
            } else {
                pstmt.setString(1, "%" + itemSearchDto.getSearch() + "%");
            }

            //2nd parameter
            if (itemSearchDto.getItemSellStatus() == null) {
                pstmt.setString(2, "%%");
            } else {
                pstmt.setString(2, "%" + itemSearchDto.getItemSellStatus() + "%");
            }

            //3rd parameter
            pstmt.setTimestamp(3, Timestamp.valueOf(findRegDate(itemSearchDto.getRegDuration())));

            //4th parameter
            pstmt.setInt(4, ROWNUM_PER_PAGE);

            //5th parameter
            int pageNum = findPageNum(itemSearchDto);
            model.addAttribute("pageNum", pageNum);
            if (pageNum == 0) {
                pstmt.setInt(5, 0);
            } else {
                pstmt.setInt(5, ROWNUM_PER_PAGE * (pageNum - page));
            }

            rs = pstmt.executeQuery();

            List<Item> items = new ArrayList<>();
            while (rs.next()) {
                Item item = new Item(
                        rs.getLong("item_num"),
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getInt("stock"),
                        ItemSellStatus.valueOf(rs.getString("item_sell_status")),
                        rs.getTimestamp("regdate").toLocalDateTime()
                );
                items.add(item);
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private int findPageNum(ItemSearchDto itemSearchDto) {
        int rowNum = findRowNum(itemSearchDto);
        return rowNum % ROWNUM_PER_PAGE == 0 ? rowNum / ROWNUM_PER_PAGE : rowNum / ROWNUM_PER_PAGE + 1;
    }

    @Override
    public int findRowNum(ItemSearchDto itemSearchDto) {
        String sql = "select count(*) from item where item_name like ? and item_sell_status like ? and regdate >= ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);

            //1st parameter
            if (itemSearchDto.getSearch() == null) {
                pstmt.setString(1, "%%");
            } else {
                pstmt.setString(1, "%" + itemSearchDto.getSearch() + "%");
            }

            //2nd parameter
            if (itemSearchDto.getItemSellStatus() == null) {
                pstmt.setString(2, "%%");
            } else {
                pstmt.setString(2, "%" + itemSearchDto.getItemSellStatus() + "%");
            }

            //3rd parameter
            pstmt.setTimestamp(3, Timestamp.valueOf(findRegDate(itemSearchDto.getRegDuration())));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private LocalDateTime findRegDate(String regDuration) {

        LocalDateTime dateTime = LocalDateTime.now();
        if (regDuration == null || "".equals(regDuration) || "all".equals(regDuration)) {
            dateTime = dateTime.minusYears(10); //임시 코드임. 이 코드 수정할 것임.
        } else if ("1d".equals(regDuration)) {
            dateTime = dateTime.minusDays(1);
        } else if ("1w".equals(regDuration)) {
            dateTime = dateTime.minusWeeks(1);
        } else if ("1m".equals(regDuration)) {
            dateTime = dateTime.minusMonths(1);
        } else if ("6m".equals(regDuration)) {
            dateTime = dateTime.minusMonths(6);
        }
        return dateTime;
    }

    @Override
    public Optional<ItemEditDto> findByItemNum(long itemNum) {
        String sql = "select item_num, item_name, price, item_detail, stock, item_sell_status from item where item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, itemNum);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ItemEditDto itemEditDto = new ItemEditDto(
                        rs.getLong("item_num"),
                        rs.getString("item_name"),
                        rs.getInt("price"),
                        rs.getString("item_detail"),
                        rs.getInt("stock"),
                        ItemSellStatus.valueOf(rs.getString("item_sell_status")));
                return Optional.of(itemEditDto);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void updateItem(Item item) {
        String sql = "update item set item_name = ?, price = ?, item_detail = ?, stock = ?, item_sell_status = ? where item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getItemName());
            pstmt.setInt(2, item.getPrice());
            pstmt.setString(3, item.getItemDetail());
            pstmt.setInt(4, item.getStock());
            pstmt.setString(5, String.valueOf(item.getItemSellStatus()));
            pstmt.setLong(6, item.getItemNum());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLErrorCodeSQLExceptionTranslator(dataSource).translate("update", sql, e); //EX. throw new DuplicateKeyException(e);
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
