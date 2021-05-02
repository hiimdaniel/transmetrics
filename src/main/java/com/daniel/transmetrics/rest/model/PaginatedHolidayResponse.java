package com.daniel.transmetrics.rest.model;

import com.daniel.transmetrics.repository.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedHolidayResponse {

    private List<Holiday> holidays;
    private Integer pageSize;
    private Integer pageNumber;
    private Long totalElements;
    private Integer totalPages;
    private Boolean last;
    private Boolean first;
    private URI nextPage;

    @Builder
    public static class NextPage {
        private final String host;
        private final String path;
        private final Integer port;
        private final String protocol;
        private final Integer nextPageNo;
        private final String queryString;

        public URI getURI() throws URISyntaxException {
            return new URI(protocol, null, host, port, path, queryString, null);
        }
    }
}
