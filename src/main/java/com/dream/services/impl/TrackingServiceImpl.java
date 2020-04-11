package com.dream.services.impl;

import com.dream.models.Tracking;
import com.dream.repositories.TrackingRepository;
import com.dream.services.TrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Tracking service implementation
 */

@Slf4j
@Service
public class TrackingServiceImpl implements TrackingService {

    @Lazy
    @Autowired
    private TrackingRepository trackingRepository;

    @Override
    public Tracking saveOrUpdate(Tracking tracking) {
        return trackingRepository.save(tracking);
    }

    @Override
    public Optional<Tracking> getByDreamAndIp(String ipAddress, Long dreamId) {
        return trackingRepository.findByIpAddressAndDreamId(ipAddress, dreamId);
    }

    @Override
    public Optional<Tracking> getByCommentAndIp(String ipAddress, Long commentId) {
        return trackingRepository.findByIpAddressAndCommentId(ipAddress, commentId);
    }
}
