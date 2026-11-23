package com.showtime.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "venue")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 300)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id")
    private City city;

    /** Keycloak username of the partner who owns this venue. */
    @Column(name = "partner_id", nullable = false, length = 100)
    private String partnerId;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Venue() {
    }

    public Venue(String name, String address, City city, String partnerId) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.partnerId = partnerId;
    }

    public boolean isOwnedBy(String username) {
        return partnerId.equals(username);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public City getCity() { return city; }
    public String getPartnerId() { return partnerId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
