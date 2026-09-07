package com.portfolio.basketball_stats_api.shot.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedShotsResponse(
        List<ShotResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static PagedShotsResponse from(Page<ShotResponse> result) {
        return new PagedShotsResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast());
    }
}
