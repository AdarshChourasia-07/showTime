package com.showtime.catalog.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.Map;

/** One screening of a piece of content on a screen at a given time. */
@Entity
@Table(name = "shows")
public class Show {

    /** Gap kept free after every show for cleaning and the next audience to enter. */
    public static final Duration TURNAROUND = Duration.ofMinutes(30);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShowStatus status;

    @Version
    private long version;

    @ElementCollection
    @CollectionTable(name = "show_price", joinColumns = @JoinColumn(name = "show_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "category")
    @Column(name = "price", nullable = false)
    private Map<SeatCategory, BigDecimal> prices = new EnumMap<>(SeatCategory.class);

    protected Show() {
    }

    public Show(Content content, Screen screen, OffsetDateTime startsAt, Map<SeatCategory, BigDecimal> prices) {
        this.content = content;
        this.screen = screen;
        this.startsAt = startsAt;
        this.endsAt = startsAt.plusMinutes(content.getDurationMinutes());
        this.status = ShowStatus.SCHEDULED;
        this.prices.putAll(prices);
    }

    /** True if this show and the given time window would need the screen at the same time. */
    public boolean overlaps(OffsetDateTime otherStart, OffsetDateTime otherEnd) {
        if (status == ShowStatus.CANCELLED) {
            return false;
        }
        return otherStart.isBefore(endsAt.plus(TURNAROUND)) && startsAt.isBefore(otherEnd.plus(TURNAROUND));
    }

    public void cancel() {
        this.status = ShowStatus.CANCELLED;
    }

    public BigDecimal lowestPrice() {
        return prices.values().stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    public Long getId() { return id; }
    public Content getContent() { return content; }
    public Screen getScreen() { return screen; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public ShowStatus getStatus() { return status; }
    public Map<SeatCategory, BigDecimal> getPrices() { return prices; }
}
