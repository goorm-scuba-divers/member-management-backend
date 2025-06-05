package io.goorm.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T> (
        @Schema(description = "Content 내용")
        List<T> content,

        @Schema(description = "페이지 번호")
        int page,

        @Schema(description = "페이지 사이즈")
        int size,

        @Schema(description = "총 개수")
        long total
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<T>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
