package shoppingmall.member.entity;

import lombok.Getter;
import lombok.Setter;
import shoppingmall.member.constant.Role;

import java.time.LocalDateTime;

@Getter
@Setter
public class Member {

    private long member_num;
    private String id;
    private String password;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime regdate;
    private Role role;
}
