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
import shoppingmall.member.constant.SessionConst;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.dto.LoginFormDto;
import shoppingmall.member.entity.Member;
import shoppingmall.member.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Optional;

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

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());
        return "member/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginFormDto loginFormDto, BindingResult bindingResult, HttpServletRequest request) throws SQLException {

        /* 검증 실패 */
        if (bindingResult.hasErrors()) {
            return "member/loginForm";
        }

        /* 검증 성공 */
        Optional<Member> member = memberService.login(loginFormDto.getId(), loginFormDto.getPassword());
        if (member.isEmpty()) { //로그인 실패
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "member/loginForm";
        }

        //로그인 성공
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member.get());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); //false: HttpSession 없는 비정상적인 상황을 고려하였음
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }
}
