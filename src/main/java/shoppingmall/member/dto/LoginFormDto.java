package shoppingmall.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginFormDto {

    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
