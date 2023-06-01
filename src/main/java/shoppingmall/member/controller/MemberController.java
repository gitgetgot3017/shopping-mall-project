package shoppingmall.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shoppingmall.member.constant.SessionConst;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.dto.LoginFormDto;
import shoppingmall.member.dto.MemberEditForm;
import shoppingmall.member.entity.Member;
import shoppingmall.member.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static shoppingmall.member.constant.SessionConst.LOGIN_MEMBER;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members-joinform")
    public String join(Model model) {
        model.addAttribute("joinFormDto", new JoinFormDto());
        return "member/joinForm";
    }

    @PostMapping("/members")
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
        }

        //회원가입 성공
        return "redirect:/";
    }

    @GetMapping("/members-loginform")
    public String login(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());
        return "member/loginForm";
    }

    @PostMapping("/members-login")
    public String login(@Validated @ModelAttribute LoginFormDto loginFormDto, BindingResult bindingResult, HttpServletRequest request) {

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

    @GetMapping("/members-logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); //false: HttpSession 없는 비정상적인 상황을 고려하였음
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    @GetMapping("/members")
    public String editMember(@SessionAttribute(name = LOGIN_MEMBER) Member member, Model model) {

        model.addAttribute("member", memberService.getMemberInfo(member.getId()).get());
        return "member/memberEdit";
    }

    @PutMapping("/members")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm, BindingResult bindingResult,
                             @SessionAttribute(name = LOGIN_MEMBER) Member member, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/memberEdit";
        }

        if (memberEditForm.getPassword().equals("")) { //사용자가 비밀번호를 입력하지 않은 경우. 즉, 비밀번호를 변경할 의사 없음
            memberEditForm.setPassword(memberEditForm.getStoredPassword());
        }

        memberService.modifyMemberInfo(member.getMember_num(), memberEditForm);
        model.addAttribute("modifyMemberInfoAlert", true);
        return "member/memberEdit";
    }

    @DeleteMapping("/members")
    public String withdrawMember(@SessionAttribute(name = LOGIN_MEMBER) Member member, HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {

        memberService.withdrawMember(member.getMember_num(), request);
        redirectAttributes.addAttribute("withdrawMemberAlert", true);
        return "redirect:/";
    }
}
