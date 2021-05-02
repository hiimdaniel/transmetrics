package com.daniel.transmetrics.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "holidays")
@EqualsAndHashCode
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Holiday {

    public static final String JSON_COLUMN_TYPE = "jsonb";

    @Id
    private String uid;

    private Long id;

    @Column(name = "url_id")
    @JsonProperty("urlid")
    private String urlId;

    private String url;

    private String countryId;

    private String name;

    private String oneLiner;

    @Type(type = JSON_COLUMN_TYPE)
    private JsonNode types;

    @Type(type = JSON_COLUMN_TYPE)
    private JsonNode subtype;

    private LocalDate date;

    @JsonProperty("oneliner")
    private void unpackNestedOneliner(List<Map<String, String>> onelinerObject) {
        oneLiner = onelinerObject.get(0).get("text");
    }

    @JsonProperty("name")
    private void unpackNestedName(List<Map<String, String>> nameObject) {
        name = nameObject.get(0).get("text");
    }

    @JsonProperty("country")
    private void unpackNestedCountry(Map<String, String> countryObject) {
        countryId = countryObject.get("id");
    }

    @JsonProperty("date")
    private void unpackNestedDate(Map<String, Object> dateObject) {
        Map<String, Integer> dateTimeMap = (Map<String, Integer>) dateObject.get("datetime");
        date = LocalDate.of(dateTimeMap.get("year"), dateTimeMap.get("month"), dateTimeMap.get("day"));
    }

}
