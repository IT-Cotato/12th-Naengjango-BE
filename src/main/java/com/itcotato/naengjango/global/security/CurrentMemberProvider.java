package com.itcotato.naengjango.global.security;

import com.itcotato.naengjango.domain.member.entity.Member;

public interface CurrentMemberProvider {
    Member getCurrentMember();
}
