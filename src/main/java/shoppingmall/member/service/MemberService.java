package shoppingmall.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shoppingmall.member.dto.JoinFormDto;
import shoppingmall.member.entity.Member;
import shoppingmall.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
}
