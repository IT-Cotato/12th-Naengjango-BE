package com.itcotato.naengjango.member.service;

import com.itcotato.naengjango.member.dto.MyPageDto;
import com.itcotato.naengjango.member.entity.Member;
import com.itcotato.naengjango.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MyPageDto.MeResponse getMe(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.MeResponse(
                member.getMemberId(),
                member.getName(),
                member.getLoginId(),
                member.getPhoneNumber(),
                member.getBudget(),
                member.getSocialType().name(),
                member.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public MyPageDto.BudgetResponse getBudget(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.BudgetResponse(member.getBudget());
    }

    @Transactional
    public MyPageDto.BudgetResponse updateBudget(Long memberId, MyPageDto.UpdateBudgetRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateBudget(req.budget());
        return new MyPageDto.BudgetResponse(member.getBudget());
    }
}
