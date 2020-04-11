package com.dream.services;

import com.dream.models.Tracking;

import java.util.Optional;

/**
 * Tracking service interface
 */

public interface TrackingService {

    /**
     * Find IP address
     *
     * @return string
     */
    Tracking saveOrUpdate(Tracking tracking);

    /**
     * Get tracking by dream id and ip address
     *
     * @param ipAddress
     * @param dreamId
     * @return optional {@link Tracking}
     */
    Optional<Tracking> getByDreamAndIp(String ipAddress, Long dreamId);

    /**
     * Get tracking by comment id and ip address
     *
     * @param ipAddress
     * @param commentId
     * @return optional {@link Tracking}
     */
    Optional<Tracking> getByCommentAndIp(String ipAddress, Long commentId);
}
