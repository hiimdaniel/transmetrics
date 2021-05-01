package com.daniel.transmetrics.service.model;

import com.daniel.transmetrics.repository.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateApiResponse {
    private Integer version;
    private Billing billing;
    private List<Holiday> holidays;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Billing{
        private Integer credits;
    }
}
