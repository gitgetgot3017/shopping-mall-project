package shoppingmall.order.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import shoppingmall.exception.RuntimeSQLException;
import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;

import javax.sql.DataSource;
import java.sql.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final DataSource dataSource;

    @Override
    public void addOrder(Order order) {
        String sql = "insert into orders(order_date, order_status, member_num) values(?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, Timestamp.valueOf(order.getOrderDate()));
            pstmt.setString(2, String.valueOf(order.getOrderStatus()));
            pstmt.setLong(3, order.getMember().getMember_num());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public long findOrderNum() {
        String sql = "select last_insert_id()";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, stmt, rs);
        }
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        String sql = "insert into order_item(order_num, item_num, count, order_price) values(?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderItem.getOrder().getOrderNum());
            pstmt.setLong(2, orderItem.getItem().getItemNum());
            pstmt.setInt(3, orderItem.getCount());
            pstmt.setInt(4, orderItem.getOrderPrice() * orderItem.getCount());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void reduceItemStock(RdStockInfo rdStockInfo, boolean stock0AfterOrder) {
        String sql = "update item set stock = stock-?, item_sell_status = ? where item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rdStockInfo.getCount());
            if (stock0AfterOrder) {
                pstmt.setString(2, "SOLD_OUT");
            } else {
                pstmt.setString(2, "SELL");
            }
            pstmt.setLong(3, rdStockInfo.getItemNum());
            pstmt.executeUpdate();
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