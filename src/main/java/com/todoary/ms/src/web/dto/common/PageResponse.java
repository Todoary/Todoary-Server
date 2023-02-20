package com.todoary.ms.src.web.dto.common;

import lombok.*;
import org.springframework.data.domain.Slice;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PageResponse<T> {
    private List<T> contents;
    private PageInfo pageInfo;

    private PageResponse(List<T> contents, PageInfo pageInfo) {
        this.contents = contents;
        this.pageInfo = pageInfo;
    }

    public static <T> PageResponse<T> of(Slice<T> pagedResult) {
        return new PageResponse<>(
                pagedResult.getContent(),
                PageInfo.builder()
                        .pageNumber(pagedResult.getNumber())
                        .empty(pagedResult.isEmpty())
                        .last(pagedResult.isLast())
                        .build()
        );
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor @Builder
    @Getter
    @ToString
    public static class PageInfo {
        private int pageNumber; // 현재 페이지 번호 (0번부터 시작)
        private boolean empty; // 빈 값인지
        private boolean last; // 마지막 페이지인지
    }
}
