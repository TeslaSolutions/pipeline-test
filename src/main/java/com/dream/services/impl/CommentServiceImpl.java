package com.dream.services.impl;

import com.dream.dtos.CommentDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.mappers.CommentMapper;
import com.dream.mappers.DreamMapper;
import com.dream.models.Comment;
import com.dream.models.CommentNumbers;
import com.dream.models.Dream;
import com.dream.models.Tracking;
import com.dream.repositories.CommentRepository;
import com.dream.services.CommentService;
import com.dream.services.DreamService;
import com.dream.services.TrackingService;
import com.dream.utils.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CommentService}
 */

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Lazy
    @Autowired
    private CommentRepository commentRepository;

    @Lazy
    @Autowired
    private DreamService dreamService;

    @Lazy
    @Autowired
    private TrackingService trackingService;

    @Override
    public Comment getById(Long id) throws NotFoundException {
        return commentRepository.findByIdAndApprovedTrue(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND,
                        StringUtils.join("No comment found for id: {}", id)));
    }

    @Override
    public Page<Comment> getByDreamId(Long dreamId, Pageable pageable) {
        return commentRepository.findByDreamIdAndApprovedTrue(dreamId, pageable);
    }

    @Override
    public Comment create(CommentDTO commentDTO, Long dreamId) throws NotFoundException {
        Dream dream = dreamService.getById(dreamId);
        Comment comment = CommentMapper.fromDto(commentDTO);
        comment.setDream(dream);
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(CommentDTO commentDTO) throws NotFoundException {
        log.info("Updating comment with id: {}", commentDTO.getId());
        Comment commentFromDB = getById(commentDTO.getId());
        commentDTO.setDream(DreamMapper.toDto(commentFromDB.getDream()));
        return commentRepository.save(CommentMapper.fromDto(commentDTO));
    }

    @Override
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentDTO like(Long id, String ipAddress) throws OperationNotPermittedException {
        Optional<Tracking> tracking = trackingService.getByCommentAndIp(ipAddress, id);
        Tracking trackLike = Tracking.builder()
                .like(true)
                .ipAddress(ipAddress)
                .commentId(id)
                .build();
        if (tracking.isPresent()) {
            if (tracking.get().isLike()) {
                throw new OperationNotPermittedException(ErrorCode.COMMENT_ALREADY_LIKED, "Comment has already been liked from this ip address");
            } else if (tracking.get().isDislike()) {
                trackLike.setDislike(false);
                commentRepository.removeDislikeFromComment(id);
            }
            trackLike.setSameDream(tracking.get().isSameDream());
            trackLike.setId(tracking.get().getId());
        }
        commentRepository.addLikeToComment(id);
        trackingService.saveOrUpdate(trackLike);
        
        CommentNumbers numbers = commentRepository.getCommentNumbers(id);
        return CommentMapper.toDto(numbers);
    }

    @Override
    @Transactional
    public CommentDTO dislike(Long id, String ipAddress) throws OperationNotPermittedException {
        Optional<Tracking> tracking = trackingService.getByCommentAndIp(ipAddress, id);
        Tracking trackDislike = Tracking.builder()
                .dislike(true)
                .ipAddress(ipAddress)
                .commentId(id)
                .build();
        if (tracking.isPresent()) {
            if (tracking.get().isDislike()) {
                throw new OperationNotPermittedException(ErrorCode.COMMENT_ALREADY_DISLIKED, "Comment has already been disliked from this ip address");
            } else if (tracking.get().isLike()) {
                trackDislike.setLike(false);
                commentRepository.removeLikeFromComment(id);
            }
            trackDislike.setSameDream(tracking.get().isSameDream());
            trackDislike.setId(tracking.get().getId());
        }
        commentRepository.addDislikeToComment(id);
        trackingService.saveOrUpdate(trackDislike);
        
        CommentNumbers numbers = commentRepository.getCommentNumbers(id);
        return CommentMapper.toDto(numbers);
    }

    @Override
    public void approve(Long id) {
        commentRepository.approveComment(id);
    }

    @Override
    public List<CommentDTO> getUnapprovedComments() throws NotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CommentDTO> commentDTOS = new ArrayList<>();
        List<Comment> comments = commentRepository.findByApprovedFalse();

        Map<Long, List<Comment>> unapprovedReplies = new HashMap<>();
        for (Comment comment : comments) {
            if (comment.getParentId() != null) {
                unapprovedReplies.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
            }
        }

        for (Long commendId : unapprovedReplies.keySet()) {
            Comment comment = commentRepository.findById(commendId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND, StringUtils.join("Comment not found for id: ", commendId)));
            List<CommentDTO> commentDTOList = unapprovedReplies.get(commendId).stream().map(item -> {
                CommentDTO commentDTO = CommentMapper.toDto(item);
                commentDTO.setParentCommentDescription(comment.getCommentDescription());
                return commentDTO;
            }).collect(Collectors.toList());
            commentDTOS.addAll(commentDTOList);
            comments.removeAll(unapprovedReplies.get(commendId));
        }

        commentDTOS.addAll(comments.stream().map(CommentMapper::toDto).collect(Collectors.toList()));
        stopWatch.stop();
        long time = stopWatch.getTotalTimeMillis();
        log.info("total time in millis for getUnapprovedComments is: " + time);
        return commentDTOS;
//        return commentRepository.findByApprovedFalse().stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<Comment> getReplies(Long parentId, Pageable pageable) {
        return commentRepository.findByParentIdAndApprovedTrue(parentId, pageable);
    }
}
