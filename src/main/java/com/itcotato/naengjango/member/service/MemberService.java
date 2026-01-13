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
    public MyPageDto.MeResponse getMe(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.MeResponse(
                member.getUserId(),
                member.getName(),
                member.getLoginId(),
                member.getPhoneNumber(),
                member.getBudget(),
                member.getSocialType().name(),
                member.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public MyPageDto.BudgetResponse getBudget(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.BudgetResponse(member.getBudget());
    }

    @Transactional
    public MyPageDto.BudgetResponse updateBudget(Long userId, MyPageDto.UpdateBudgetRequest req) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateBudget(req.budget());
        return new MyPageDto.BudgetResponse(member.getBudget());
    }
}
