package com.showtime.catalog.repository;

import com.showtime.catalog.domain.Show;
import com.showtime.catalog.domain.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowRepository extends JpaRepository<Show, Long> {

    /** Shows in a city within a time window, optionally for one movie/event. Fetches everything the list page needs. */
    @Query("""
            select s from Show s
              join fetch s.content c
              join fetch s.screen sc
              join fetch sc.venue v
              join fetch v.city ci
            where ci.code = :cityCode
              and s.status = :status
              and s.startsAt >= :from and s.startsAt < :to
              and (:contentId is null or c.id = :contentId)
            order by s.startsAt, v.name
            """)
    List<Show> search(@Param("cityCode") String cityCode,
                      @Param("from") OffsetDateTime from,
                      @Param("to") OffsetDateTime to,
                      @Param("contentId") Long contentId,
                      @Param("status") ShowStatus status);

    /** Shows on one screen near a time; used to reject overlapping schedules. */
    @Query("""
            select s from Show s
            where s.screen.id = :screenId
              and s.status = com.showtime.catalog.domain.ShowStatus.SCHEDULED
              and s.startsAt < :to and s.endsAt > :from
            """)
    List<Show> findScheduledOnScreenBetween(@Param("screenId") Long screenId,
                                            @Param("from") OffsetDateTime from,
                                            @Param("to") OffsetDateTime to);

    @Query("""
            select s from Show s
              join fetch s.content
              join fetch s.screen sc
              join fetch sc.venue v
              join fetch v.city
            where s.id = :id
            """)
    Optional<Show> findDetailedById(@Param("id") Long id);
}
