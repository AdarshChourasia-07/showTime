package com.showtime.catalog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "screen")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rowLabel ASC, seatNumber ASC")
    private List<Seat> seats = new ArrayList<>();

    protected Screen() {
    }

    public Screen(Venue venue, String name) {
        this.venue = venue;
        this.name = name;
    }

    /** Adds a full row of seats, numbered 1..count. */
    public void addRow(String rowLabel, int count, SeatCategory category) {
        for (int n = 1; n <= count; n++) {
            seats.add(new Seat(this, rowLabel, n, category));
        }
        totalSeats = seats.size();
    }

    public Set<SeatCategory> seatCategories() {
        Set<SeatCategory> categories = EnumSet.noneOf(SeatCategory.class);
        seats.forEach(s -> categories.add(s.getCategory()));
        return categories;
    }

    public Long getId() { return id; }
    public Venue getVenue() { return venue; }
    public String getName() { return name; }
    public int getTotalSeats() { return totalSeats; }
    public List<Seat> getSeats() { return seats; }
}
