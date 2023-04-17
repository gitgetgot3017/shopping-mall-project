package shoppingmall.member.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import shoppingmall.member.entity.Member;

import java.sql.*;
import java.util.Optional;

import static shoppingmall.jdbc.constant.ConnectionConst.*;

@Slf4j
@Repository
public class MemberRepository {

    public void saveMember(Member member) throws SQLException {
        String sql = "insert into member(id, password, name, phone, email, regdate, role) values(?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getPhone());
            pstmt.setString(5, member.getEmail());
            pstmt.setTimestamp(6, Timestamp.valueOf(member.getRegdate()));
            pstmt.setString(7, String.valueOf(member.getRole()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.info("DB ERROR", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    public Optional<Member> findMemberById(String id) throws SQLException {
        String sql = "select * from member where id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setPassword(rs.getString("password"));
                return Optional.of(member);
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
