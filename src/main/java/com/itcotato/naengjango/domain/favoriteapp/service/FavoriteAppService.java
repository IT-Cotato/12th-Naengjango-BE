package com.itcotato.naengjango.domain.favoriteapp.service;

import com.itcotato.naengjango.domain.favoriteapp.dto.FavoriteAppRequestDto;
import com.itcotato.naengjango.domain.favoriteapp.dto.FavoriteAppResponseDto;
import com.itcotato.naengjango.domain.favoriteapp.entity.FavoriteApp;
import com.itcotato.naengjango.domain.favoriteapp.exception.FavoriteAppException;
import com.itcotato.naengjango.domain.favoriteapp.exception.code.FavoriteAppErrorCode;
import com.itcotato.naengjango.domain.favoriteapp.repository.FavoriteAppRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteAppService {

    private final FavoriteAppRepository favoriteAppRepository;

    /** 즐겨찾기 앱 추가 */
    public void add(Member member, FavoriteAppRequestDto.FavoriteAppCreate request) {
        String appName = normalize(request.appName());

        if (favoriteAppRepository.existsByMemberAndAppName(member, appName)) {
            throw new FavoriteAppException(FavoriteAppErrorCode.DUPLICATE_FAVORITE_APP);
        }

        favoriteAppRepository.save(
                new FavoriteApp(member, appName)
        );
    }

    /** 즐겨찾기 앱 조회 */
    @Transactional(readOnly = true)
    public List<FavoriteAppResponseDto.FavoriteAppResponse> findMyApps(Member member) {
        return favoriteAppRepository.findByMember(member).stream()
                .map(FavoriteAppResponseDto.FavoriteAppResponse::from)
                .toList();
    }

    /** 즐겨찾기 앱 삭제 */
    public void delete(Member member, Long appId) {
        favoriteAppRepository.deleteByIdAndMember(appId, member);
    }

    private String normalize(String appName) {
        if (appName == null || appName.isBlank()) {
            throw new FavoriteAppException(FavoriteAppErrorCode.INVALID_FAVORITE_APP_NAME);
        }
        return appName.trim();
    }
}
