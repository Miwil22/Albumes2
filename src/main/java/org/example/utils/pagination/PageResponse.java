package org.example.utils.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private int pageNumber;
    private boolean first;
    private boolean last;
    private String sortBy;
    private String direction;

    public static <T> PageResponse<T> of(Page<T> page, String sortBy, String direction) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .pageNumber(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .sortBy(sortBy)
                .direction(direction)
                .build();
    }
}
