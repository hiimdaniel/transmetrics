package com.daniel.transmetrics.rest.model;

import com.daniel.transmetrics.repository.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateApiResponse {

    private Integer version;

    private Billing billing;

    private List<Holiday> holidays;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Billing {
        private Integer credits;
    }
}
