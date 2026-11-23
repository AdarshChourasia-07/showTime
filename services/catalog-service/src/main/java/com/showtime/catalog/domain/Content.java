package com.showtime.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A movie, concert or event that can be scheduled as shows. */
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(length = 100)
    private String genre;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(length = 10)
    private String certificate;

    @Column(length = 2000)
    private String description;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    protected Content() {
    }

    public Content(ContentType type, String title, String language, String genre, int durationMinutes,
                   String certificate, String description, String posterUrl) {
        this.type = type;
        this.title = title;
        this.language = language;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.certificate = certificate;
        this.description = description;
        this.posterUrl = posterUrl;
    }

    public Long getId() { return id; }
    public ContentType getType() { return type; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getCertificate() { return certificate; }
    public String getDescription() { return description; }
    public String getPosterUrl() { return posterUrl; }
}
