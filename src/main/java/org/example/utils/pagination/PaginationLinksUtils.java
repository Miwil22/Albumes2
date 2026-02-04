package org.example.utils.pagination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class PaginationLinksUtils {

    public String createLinkHeader(Page<?> page, UriComponentsBuilder uriBuilder) {
        final StringBuilder linkHeader = new StringBuilder();

        // First page
        if (page.getNumber() > 0) {
            final String uriFirst = constructUri(0, page.getSize(), uriBuilder);
            linkHeader.append(buildLinkHeader(uriFirst, "first")).append(", ");
        }

        // Previous page
        if (page.hasPrevious()) {
            final String uriPrev = constructUri(page.getNumber() - 1, page.getSize(), uriBuilder);
            linkHeader.append(buildLinkHeader(uriPrev, "prev")).append(", ");
        }

        // Next page
        if (page.hasNext()) {
            final String uriNext = constructUri(page.getNumber() + 1, page.getSize(), uriBuilder);
            linkHeader.append(buildLinkHeader(uriNext, "next")).append(", ");
        }

        // Last page
        if (page.getNumber() < page.getTotalPages() - 1) {
            final String uriLast = constructUri(page.getTotalPages() - 1, page.getSize(), uriBuilder);
            linkHeader.append(buildLinkHeader(uriLast, "last"));
        }

        return linkHeader.toString();
    }

    private String constructUri(int page, int size, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .encode()
                .toUriString();
    }

    private String buildLinkHeader(String uri, String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
