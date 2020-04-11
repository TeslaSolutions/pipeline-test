package com.dream.services;

import com.dream.dtos.CommentDTO;
import com.dream.dtos.DreamDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Comment service interface
 */

public interface CommentService {

    /**
     * Get comment by id
     *
     * @param id
     * @return {@link Comment}
     * @throws NotFoundException
     */
    Comment getById(Long id) throws NotFoundException;

    /**
     * Get comments by dream id
     *
     * @param dreamId
     * @param pageable
     * @return page {@link Comment}
     */
    Page<Comment> getByDreamId(Long dreamId, Pageable pageable);

    /**
     * Create comment for a dream
     *
     * @param commentDTO {@link CommentDTO}
     * @param dreamId
     * @return created comment {@link Comment}
     * @throws NotFoundException
     */
    Comment create(CommentDTO commentDTO, Long dreamId) throws NotFoundException;

    /**
     * Update comment
     *
     * @param commentDTO
     * @return updated comment {@link Comment}
     * @throws NotFoundException
     */
    Comment update(CommentDTO commentDTO) throws Exception;

    /**
     * Delete comment by id
     *
     * @param id
     */
    void delete(Long id);

    /**
     * Add like to the comment
     *
     * @param id
     * @param ipAddress
     * @throws OperationNotPermittedException
     */
    /**
     * Add like to the comment
     * @param id
     * @param ipAddress
     * @return CommentDTO
     * @throws OperationNotPermittedException
     */
    CommentDTO like(Long id, String ipAddress) throws OperationNotPermittedException;

    /**
     * Add dislike to the comment
     *
     * @param id
     * @param ipAddress
     * @return CommentDTO
     * @throws OperationNotPermittedException
     */
    CommentDTO dislike(Long id, String ipAddress) throws OperationNotPermittedException;

    /**
     * Approve a comment
     *
     * @param id
     */
    void approve(Long id);

    /**
     * Get unapproved comments
     *
     * @return list
     */
    List<CommentDTO> getUnapprovedComments() throws NotFoundException;

    /**
     * Get comment replies
     *
     * @param parentId
     * @param pageable
     * @return page {@link Comment}
     */
    Page<Comment> getReplies(Long parentId, Pageable pageable);
}
