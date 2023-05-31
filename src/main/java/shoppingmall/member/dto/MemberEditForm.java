package shoppingmall.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberEditForm {

    @NotBlank
    @Length(max = 10)
    private String id;

    @Length(max = 10)
    private String password;
    private String storedPassword; //DB에서 가져온 값

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
}
