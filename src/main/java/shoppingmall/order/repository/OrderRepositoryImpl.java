package shoppingmall.order.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import shoppingmall.exception.RuntimeSQLException;
import shoppingmall.order.dto.OrderDto;
import shoppingmall.order.dto.OrderItemDto;
import shoppingmall.order.dto.RdStockInfo;
import shoppingmall.order.entity.Order;
import shoppingmall.order.entity.OrderItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<OrderDto> getOrderHistory(long memberNum) {
        String sql = "select o.order_num, order_date, sum(order_price) total_price " +
                "from orders o join order_item oi on o.order_num = oi.order_num " +
                "where member_num = ? " +
                "group by order_num " +
                "order by order_num desc";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, memberNum);
            rs = pstmt.executeQuery();

            List<OrderDto> orderDtoList = new ArrayList<>();
            while (rs.next()) {
                LocalDate orderDate = rs.getTimestamp("order_date").toLocalDateTime().toLocalDate();
                long orderNum = rs.getLong("order_num");
                int totalPrice = rs.getInt("total_price");

                orderDtoList.add(new OrderDto(
                        orderDate,
                        orderNum,
                        totalPrice,
                        getOrderItems(orderNum),
                        decideCancelOrd(orderDate, totalPrice))
                );
            }
            return orderDtoList;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<OrderItemDto> getOrderItems(long orderNum) {
        //sql문 작성 순서
        //1. order_item(foi) 준비
        //2. item(i), item_img(fii) 조인(ifii)
        //3. foi, ifii 조인
        String sql = "select count, order_price, ifii.* " +
                "from (select * from order_item where order_num = ?) foi " +
                "join (select i.item_num, item_name, save_img_name from item i join (select * from item_img where rep_img = true) fii on i.item_num = fii.item_num) ifii " +
                "on foi.item_num = ifii.item_num";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderNum);
            rs = pstmt.executeQuery();

            List<OrderItemDto> orderItemDtoList = new ArrayList<>();
            while (rs.next()) {
                orderItemDtoList.add(new OrderItemDto(
                        rs.getLong("item_num"),
                        rs.getString("save_img_name"),
                        rs.getString("item_name"),
                        rs.getInt("count"),
                        rs.getInt("order_price"))
                );
            }
            return orderItemDtoList;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private boolean decideCancelOrd(LocalDate orderDate, int totalPrice) { //totalPrice == 0: 주문 취소 상태 -> '주문 취소' 버튼 보이지 않도록
        return (orderDate.plusDays(7).isEqual(LocalDate.now()) || orderDate.plusDays(7).isAfter(LocalDate.now()))
                && (totalPrice != 0);
    }

    @Override
    public void updateOrderStatus(long orderNum) {
        String sql = "update orders set order_status = 'CANCEL' where order_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void updateOrderItemPrice(long orderNum) {
        String sql = "update order_item set order_price = 0 where order_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public List<RdStockInfo> toRestoreItemStock(long orderNum) {
        String sql = "select item_num, count from order_item where order_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderNum);
            rs = pstmt.executeQuery();

            List<RdStockInfo> rdStockInfoList = new ArrayList<>();
            while (rs.next()) {
                rdStockInfoList.add(new RdStockInfo(
                        rs.getLong("item_num"),
                        rs.getInt("count"))
                );
            }
            return rdStockInfoList;
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void restoreItemStock(RdStockInfo rdStockInfo) {
        String sql = "update item set stock = stock+? where item_num = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rdStockInfo.getCount());
            pstmt.setLong(2, rdStockInfo.getItemNum());
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