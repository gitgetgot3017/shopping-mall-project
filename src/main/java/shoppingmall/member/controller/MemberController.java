package shoppingmall.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.entity.Member;
import shoppingmall.member.service.MemberService;

import java.sql.SQLException;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("joinFormDto", new JoinFormDto());
        return "member/joinForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute JoinFormDto joinFormDto, BindingResult bindingResult) {

        /* 검증 실패 */
        if (bindingResult.hasErrors()) {
            return "member/joinForm";
        }

        /* 검증 성공 */
        Member member = JoinFormDto.createMember(joinFormDto); //JoinFormDto -> Member

        try {
            memberService.join(member);
        } catch (IllegalStateException e) { //회원가입 실패
            bindingResult.reject("joinFail", "이미 가입된 회원입니다.");
            return "member/joinForm";
        } catch (SQLException e) {
            bindingResult.reject("DBError", "DB에 오류가 발생했습니다. 잠시 후에 다시 시도해주세요.");
            return "member/joinForm";
        }

        //회원가입 성공
        return "redirect:/";
    }
}
