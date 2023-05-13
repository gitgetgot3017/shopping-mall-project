package shoppingmall.member.repository;

import shoppingmall.member.entity.Member;

import java.util.Optional;

public interface MemberRepository {

    void saveMember(Member member);
    Optional<Member> findMemberById(String id);
}
