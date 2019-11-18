package com.sap.bulletinboard.ads.models;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
@Table(name = "advertisements")
public class Advertisement
{
    @NotBlank
    @Column(name = "mytitle")
    private String title;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public Advertisement() {
    }

    public Advertisement(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}