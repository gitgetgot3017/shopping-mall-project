package shoppingmall.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import shoppingmall.member.constant.Role;
import shoppingmall.member.entity.Member;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class JoinFormDto {

    @NotBlank
    @Length(max = 10)
    private String id;

    @NotBlank
    @Length(max = 10)
    private String password;

    @NotBlank
    @Length(max = 10)
    private String name;

    @NotBlank
    @Length(max = 11)
    private String phone;

    @NotBlank
    @Email
    @Length(max = 20)
    private String email;

    private static ModelMapper modelMapper = new ModelMapper();

    public static Member createMember(JoinFormDto joinFormDto) {
        Member member = modelMapper.map(joinFormDto, Member.class);
        member.setRegdate(LocalDateTime.now());
        member.setRole(Role.USER);
        return member;
    }
}
