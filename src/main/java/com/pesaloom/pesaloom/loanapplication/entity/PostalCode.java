package com.pesaloom.pesaloom.loanapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "postal_codes")
public class PostalCode {

    @Id
    private String code;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private String county;

    protected PostalCode() {
        // JPA
    }

    public String getCode() {
        return code;
    }

    public String getTown() {
        return town;
    }

    public String getCounty() {
        return county;
    }
}
