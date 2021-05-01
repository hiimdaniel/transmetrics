package com.daniel.transmetrics.repository.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "holidays")
@EqualsAndHashCode
@ToString
@org.hibernate.annotations.TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Holiday {

    public static final String JSON_COLUMN_TYPE = "jsonb";

    @Id
    private Long id;

    @Column(name = "url_id")
    private String urlId;

    private String url;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private String name;

    private String oneLiner;

    @Type(type = JSON_COLUMN_TYPE)
    private JsonNode types;

    @Type(type = JSON_COLUMN_TYPE)
    private JsonNode subtype;

    private Date date;

    private String uid;
}
