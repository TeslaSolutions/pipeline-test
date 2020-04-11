package com.dream.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dream.dtos.DreamDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.mappers.DreamMapper;
import com.dream.models.Dream;
import com.dream.models.DreamNumbers;
import com.dream.models.DreamProjection;
import com.dream.models.Tracking;
import com.dream.repositories.DreamRepository;
import com.dream.services.DreamService;
import com.dream.services.TrackingService;
import com.dream.utils.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link DreamService}
 */

@Slf4j
@Service
public class DreamServiceImpl implements DreamService {

    public static final String ORDER_BY_COMMENTS = "comments";
    public static final String ORDER_BY_LIKES_NO_FROM_YESTERDAY = "likes_no_from_yesterday";

    @Lazy
    @Autowired
    private DreamRepository dreamRepository;

    @Lazy
    @Autowired
    private TrackingService trackingService;

    @Override
    public Dream create(DreamDTO dream) {
        if (dream.getCreateDate() == null) {
            dream.setCreateDate(LocalDateTime.now());
        }
        return dreamRepository.save(DreamMapper.fromDto(dream));
    }

    @Override
    @Transactional(readOnly = true)
    public DreamDTO getDTOById(Long id) throws NotFoundException {
        Optional<DreamProjection> dreamP = dreamRepository.getDreamProjectionById(id);
    	if(dreamP.isPresent()) {
    		return DreamMapper.toDto(dreamP.get());
    	}else {
    		throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND,
                    StringUtils.join("Dream not found with id: ", id));
    	}
	}
    
    @Override
    @Transactional(readOnly = true)
    public Dream getById(Long id) throws NotFoundException {
    	Optional<Dream> dream = dreamRepository.findById(id);
    	
    	if(!dream.isPresent()) {
    		throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND,
    				StringUtils.join("Dream not found with id: ", id));
    	}
    	
        if(!dream.get().isApproved()) {
        	throw new NotFoundException(ErrorCode.DREAM_NOT_APPROVED,
        			StringUtils.join("Dream with id: ", id, " has not been approved"));
        }
        return dream.get();
	}

    @Override
    public void delete(Long id) throws NotFoundException {
        log.info("Deleting dream with id: {}", id);
        dreamRepository.delete(getById(id));
    }

    @Override
    public Dream update(DreamDTO dream) {
        return dreamRepository.save(DreamMapper.fromDto(dream));
    }

    @Override
    @Transactional
    public DreamDTO like(Long id, String ipAddress) throws OperationNotPermittedException {
        Optional<Tracking> tracking = trackingService.getByDreamAndIp(ipAddress, id);
        Tracking trackLike = Tracking.builder()
                .like(true)
                .ipAddress(ipAddress)
                .dreamId(id)
                .build();
        if (tracking.isPresent()) {
            if (tracking.get().isLike()) {
                throw new OperationNotPermittedException(ErrorCode.DREAM_ALREADY_LIKED, "Dream has already been liked from this ip address");
            } else if (tracking.get().isDislike()) {
                trackLike.setDislike(false);
                dreamRepository.removeDislikeFromDream(id);
            }
            trackLike.setSameDream(tracking.get().isSameDream());
            trackLike.setId(tracking.get().getId());
        }
        dreamRepository.addLikeToDream(id);
        trackingService.saveOrUpdate(trackLike);
        
        DreamNumbers numbers = dreamRepository.getDreamNumbers(id);
        return DreamMapper.toDto(numbers);
    }

    @Override
    @Transactional
    public DreamDTO dislike(Long id, String ipAddress) throws OperationNotPermittedException {
        Optional<Tracking> tracking = trackingService.getByDreamAndIp(ipAddress, id);
        Tracking trackDislike = Tracking.builder()
                .dislike(true)
                .ipAddress(ipAddress)
                .dreamId(id)
                .build();
        if (tracking.isPresent()) {
            if (tracking.get().isDislike()) {
                throw new OperationNotPermittedException(ErrorCode.DREAM_ALREADY_DISLIKED, "Dream has already been disliked from this ip address");
            } else if (tracking.get().isLike()) {
                trackDislike.setLike(false);
                dreamRepository.removeLikeFromDream(id);
            }
            trackDislike.setSameDream(tracking.get().isSameDream());
            trackDislike.setId(tracking.get().getId());
        }
        dreamRepository.addDislikeToDream(id);
        trackingService.saveOrUpdate(trackDislike);
        
        DreamNumbers numbers = dreamRepository.getDreamNumbers(id);
        return DreamMapper.toDto(numbers);
    }

    @Override
    public void approve(Long id) {
        dreamRepository.approveDream(id);
    }

    @Override
    public DreamDTO addSameDream(Long id, String ipAddress) throws OperationNotPermittedException {
        Optional<Tracking> tracking = trackingService.getByDreamAndIp(ipAddress, id);
        Tracking track = Tracking.builder()
                .ipAddress(ipAddress)
                .sameDream(true)
                .dreamId(id)
                .build();
        if (tracking.isPresent()) {
            if (tracking.get().isSameDream()) {
                throw new OperationNotPermittedException(ErrorCode.DREAM_ALREADY_MARKED_AS_SAME, "Dream has already been marked as the same from this ip address");
            } else {
                track.setId(tracking.get().getId());
            }
        }
        dreamRepository.addSameDreamToDream(id);
        trackingService.saveOrUpdate(track);
        
        DreamNumbers numbers = dreamRepository.getDreamNumbers(id);
        return DreamMapper.toDto(numbers);
    }

    @Override
    public DreamDTO getRandom() throws NotFoundException {
        Long qty = dreamRepository.count();
        int idx = (int) (Math.random() * qty);
        Optional<DreamProjection> dreamP = dreamRepository.getRandomDream(idx);
        if (dreamP.isPresent()) {
            return DreamMapper.toDto(dreamP.get());
        } else {
            throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND, "Random dream not found");
        }
    }

    @Override
    public DreamDTO getMostLiked() throws NotFoundException {
    	Optional<DreamProjection> dreamP = dreamRepository.getMostLikedDream();
        if (dreamP.isPresent()) {
            return DreamMapper.toDto(dreamP.get());
        } else {
            throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND, "Dream with maximum likes not found");
        }
    }

    @Override
    public DreamDTO getMostDisliked() throws NotFoundException {
    	Optional<DreamProjection> dreamP = dreamRepository.getMostDislikedDream();
        if (dreamP.isPresent()) {
            return DreamMapper.toDto(dreamP.get());
        } else {
        	throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND, "Dream with maximum dislikes not found");
        }
    }

    @Override
    public Page<Dream> getAllUnapproved(Pageable pageable) {
        Page<Dream> byApproved = dreamRepository.findByApproved(false, pageable);
        log.info("Retreiving unapproved {} dreams", byApproved.getContent().size());
        for (Dream dream : byApproved.getContent()) {
            log.info(" - dream id: {}, dream desc: {}",dream.getId(),dream.getDreamDescription());
        }
        return byApproved;
    }

    @Override
    public DreamDTO getMostSame() throws NotFoundException {
    	Optional<DreamProjection> dreamP = dreamRepository.getMostSameDream();
        if (dreamP.isPresent()) {
            return DreamMapper.toDto(dreamP.get());
        } else {
        	throw new NotFoundException(ErrorCode.DREAM_NOT_FOUND, "Dream with maximum dislikes not found");
        }
    }

    @Override
    public Page<DreamDTO> getAll(String keyword, Long tagId, Pageable pageable) {
        Page<DreamProjection> dreamPage = null;;
        if (pageable.getSort().getOrderFor(ORDER_BY_COMMENTS) != null) {
            Pageable pageableUnsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());
            dreamPage = dreamRepository.findAllOrderByCommentCountDesc(keyword, tagId, pageableUnsorted);
        } else if (pageable.getSort().getOrderFor(ORDER_BY_LIKES_NO_FROM_YESTERDAY) != null) {
            LocalDateTime yesterdayMidnight = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(1);
            Pageable pageableUnsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());
            dreamPage = dreamRepository.findByCreateDateGreaterThanEqualOrderByLikesNoDesc(keyword, tagId, yesterdayMidnight, pageableUnsorted);
        } else {
            dreamPage = dreamRepository.findAll(keyword, tagId, pageable);
        }
        
        List<DreamDTO> dreamDTOs = dreamPage.stream().map(DreamMapper::toDto).collect(Collectors.toList());
        return new PageImpl<>(dreamDTOs, dreamPage.getPageable(), dreamPage.getTotalElements());
    }


}
