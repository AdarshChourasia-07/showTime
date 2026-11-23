package com.showtime.catalog.repository;

import com.showtime.catalog.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    @Query("select v from Venue v join fetch v.city c where c.code = :cityCode order by v.name")
    List<Venue> findByCityCode(@Param("cityCode") String cityCode);

    @Query("select v from Venue v join fetch v.city where v.partnerId = :partnerId order by v.name")
    List<Venue> findByPartnerId(@Param("partnerId") String partnerId);
}
