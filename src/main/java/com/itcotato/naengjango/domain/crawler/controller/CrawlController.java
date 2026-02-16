package com.itcotato.naengjango.domain.crawler.controller;

import com.itcotato.naengjango.domain.crawler.dto.CrawlRequest;
import com.itcotato.naengjango.domain.crawler.dto.CrawlResponse;
import com.itcotato.naengjango.domain.crawler.service.CrawlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crawler", description = "외부 쇼핑몰 링크에서 상품명/가격을 크롤링합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlController {

    private final CrawlService crawlService;

    @Operation(
            summary = "상품 링크 크롤링 by 이정환(개발 완료)",
            description = """
			단축/딥링크(zigzag, ably, musinsa onelink, oliveyoung, 오늘의집 등)를 입력받아
			최종 상품 URL로 해석 후 상품명/가격을 추출합니다.
			
			- 추출 실패 시 title/price는 null로 내려갈 수 있습니다.
			- 차단 페이지(Access Denied 등)는 title/price가 null로 반환될 수 있습니다.
			"""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "크롤링 결과 반환 (일부 필드 null 가능)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrawlResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "지그재그 성공 예시",
                                            value = """
							{
							  "title": "오뉴이 로테 체크 프릴 롱 스커트",
							  "price": 25170,
							  "productUrl": "https://zigzag.kr/p/161129424",
							  "site": "ZIGZAG"
							}
						"""
                                    ),
                                    @ExampleObject(
                                            name = "가격 추출 실패 예시",
                                            value = """
							{
							  "title": "(magsafe) Angel epoxy case",
							  "price": null,
							  "productUrl": "https://www.a-bly.com/goods/58099594",
							  "site": "ABLY"
							}
						"""
                                    ),
                                    @ExampleObject(
                                            name = "차단/접근 제한 예시",
                                            value = """
							{
							  "title": null,
							  "price": null,
							  "productUrl": "https://ohou.se/productions/3089581/selling",
							  "site": "OHOUSE"
							}
						"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식 오류(예: url 누락)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public CrawlResponse crawl(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "크롤링할 상품 URL",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CrawlRequest.class),
                            examples = {
                                    @ExampleObject(name = "지그재그 링크", value = "{\"url\":\"https://s.zigzag.kr/EMOffXPf65\"}"),
                                    @ExampleObject(name = "에이블리 링크", value = "{\"url\":\"https://applink.a-bly.com/vjln66y\"}"),
                                    @ExampleObject(name = "무신사 링크", value = "{\"url\":\"https://musinsa.onelink.me/PvkC/ugpm58t1\"}"),
                                    @ExampleObject(name = "올리브영 링크", value = "{\"url\":\"https://m.oliveyoung.co.kr/m/G.do?goodsNo=A000000224526\"}"),
                                    @ExampleObject(name = "오늘의집 링크", value = "{\"url\":\"https://ozip.me/c1aRW9G\"}")
                            }
                    )
            )
            @Valid @RequestBody CrawlRequest request
    ) {
        return crawlService.crawl(request.url());
    }
}
