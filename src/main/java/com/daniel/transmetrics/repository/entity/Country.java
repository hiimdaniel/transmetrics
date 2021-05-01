package com.daniel.transmetrics.repository.entity;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
@EqualsAndHashCode
@ToString
public class Country {

    @Id
    private String id;

    @NonNull
    private String name;
}
