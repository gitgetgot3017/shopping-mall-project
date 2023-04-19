package shoppingmall.item.respository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import shoppingmall.item.entity.Item;
import shoppingmall.item.entity.ItemImg;

import java.sql.*;

import static shoppingmall.jdbc.constant.ConnectionConst.*;

@Slf4j
@Repository
public class ItemImgRepository {
    public void saveItemImg(ItemImg itemImg) throws SQLException {
        String sql = "insert into item_img(item_num, save_img_name, upload_img_name, rep_img) values(?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, itemImg.getItem().getItemNum());
            pstmt.setString(2, itemImg.getSaveImgName());
            pstmt.setString(3, itemImg.getUploadImgName());
            pstmt.setBoolean(4, itemImg.isRepImg());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("DB ERROR", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
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
