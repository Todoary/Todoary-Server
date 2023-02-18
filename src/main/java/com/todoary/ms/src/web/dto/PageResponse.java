package com.todoary.ms.src.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {
    private List<T> content;
    private PageInfo pageable;

    public PageResponse(List<T> content, PageInfo pageable) {
        this.content = content;
        this.pageable = pageable;
    }

    public static <T> PageResponse<T> of(Page<T> pagedResult) {
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
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int pageNumber; // 현재 페이지 번호 (0번부터 시작)
        private boolean empty = false; // 빈 값인지
        private boolean last = false; // 마지막 페이지인지
    }
}
