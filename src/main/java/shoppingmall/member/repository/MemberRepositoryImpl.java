package shoppingmall.member.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import shoppingmall.exception.RuntimeSQLException;
import shoppingmall.member.constant.Role;
import shoppingmall.member.entity.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final DataSource dataSource;

    @Override
    public void saveMember(Member member) {
        String sql = "insert into member(id, password, name, phone, email, regdate, role) values(?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSource.getConnection();
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
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public Optional<Member> findMemberById(String id) {
        String sql = "select * from member where id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMember_num(rs.getLong("member_num"));
                member.setPassword(rs.getString("password"));
                member.setName(rs.getString("name"));
                member.setRole(Role.valueOf(rs.getString("role")));
                return Optional.of(member);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}
