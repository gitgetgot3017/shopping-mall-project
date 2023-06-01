package shoppingmall.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.cart.repository.CartRepository;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.dto.MemberEditForm;
import shoppingmall.member.entity.Member;
import shoppingmall.member.repository.MemberRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    public void join(Member member) {
        validateDuplicateMember(member); //중복 회원이 존재하는지 확인
        memberRepository.saveMember(member);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findMemberById(member.getId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("아미 가입된 회원입니다.");
        }
    }

    public Optional<Member> login(String id, String password) {

        Optional<Member> member = memberRepository.findMemberById(id);
        if (member.isPresent()) {
            if (member.get().getPassword().equals(password)) {
                return member;
            }
        }
        return Optional.empty();
    }

    public Optional<Member> getMemberInfo(String id) {
        return memberRepository.findMemberById(id);
    }

    public void modifyMemberInfo(long memberNum, MemberEditForm memberEditForm) {
        memberRepository.updateMember(memberNum, memberEditForm);
    }

    //회원 탈퇴 관련 비지니스 로직
    public void withdrawMember(long memberNum, HttpServletRequest request) {

        //회원 탈퇴 이력 남기기
        memberRepository.addMemberHistory(memberNum);

        //회원 테이블에서 일부 정보 masking하기
        memberRepository.updateMemberPwd(memberNum);

        //(장바구니가 없는 경우에도 delete 쿼리를 날려도 문제 없지만, 성능 상의 이유로 쿼리를 날리지 않을 것임)
        long cartNum = cartRepository.findCartNum(memberNum);
        if (cartNum != 0L) {
            cartRepository.deleteCartItemByCartNum(cartNum); //장바구니에 담긴 물건 제거
            cartRepository.deleteCart(cartNum); //장바구니 제거
        }

        //로그아웃시키기
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
