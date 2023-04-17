package shoppingmall.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.entity.Member;
import shoppingmall.member.repository.MemberRepository;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(Member member) throws SQLException {
        validateDuplicateMember(member); //중복 회원이 존재하는지 확인
        memberRepository.saveMember(member);
    }

    private void validateDuplicateMember(Member member) throws SQLException {
        Optional<Member> findMember = memberRepository.findMemberById(member.getId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("아미 가입된 회원입니다.");
        }
    }
}
