package com.dream.repositories;

import com.dream.models.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Tracking}
 */

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    Optional<Tracking> findByIpAddressAndDreamId(String ipAddress, Long dreamId);

    Optional<Tracking> findByIpAddressAndCommentId(String ipAddress, Long commentId);

}
