package shoppingmall.item.respository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import shoppingmall.exception.RuntimeSQLException;
import shoppingmall.item.entity.ItemImg;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ItemImgRepositoryImpl implements ItemImgRepository {

    private final DataSource dataSource;

    @Override
    public void saveItemImg(ItemImg itemImg) {
        String sql = "insert into item_img(item_num, save_img_name, upload_img_name, rep_img) values(?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, itemImg.getItem().getItemNum());
            pstmt.setString(2, itemImg.getSaveImgName());
            pstmt.setString(3, itemImg.getUploadImgName());
            pstmt.setBoolean(4, itemImg.isRepImg());
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
