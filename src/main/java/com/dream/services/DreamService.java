package com.dream.services;

import com.dream.dtos.DreamDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.models.Dream;
import com.dream.models.DreamNumbers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Dream service interface
 */

public interface DreamService {

    /**
     * Create dream from dto
     *
     * @param dream {@link DreamDTO}
     * @return created dream {@link Dream}
     */
    Dream create(DreamDTO dream);

    /**
     * Get dream by id
     *
     * @param id
     * @return {@link Dream}
     * @throws NotFoundException
     */
    DreamDTO getDTOById(Long id) throws NotFoundException;
    
    /**
     * Get dream by id
     *
     * @param id
     * @return {@link Dream}
     * @throws NotFoundException
     */
    Dream getById(Long id) throws NotFoundException;

    /**
     * Delete dream by id
     *
     * @param id
     * @throws NotFoundException
     */
    void delete(Long id) throws NotFoundException;

    /**
     * Update dream from dto
     *
     * @param dream {@link DreamDTO}
     * @return updated dream {@link Dream}
     */
    Dream update(DreamDTO dream);

    /**
     * Like a dream
     *
     * @param id
     * @param ipAddress
     * @return {@link DreamDTO}
     * @throws OperationNotPermittedException
     */
    DreamDTO like(Long id, String ipAddress) throws OperationNotPermittedException;

    /**
     * Dislike a dream
     *
     * @param id
     * @param ipAddress
     * @return {@link DreamDTO}
     * @throws OperationNotPermittedException
     */
    DreamDTO dislike(Long id, String ipAddress) throws OperationNotPermittedException;

    /**
     * Approve a dream
     *
     * @param id
     */
    void approve(Long id);

    /**
     * Add same dream to the dream
     * @param id
     * @param ipAddress
     * @return {@link DreamDTO}
     * @throws OperationNotPermittedException
     */
    DreamDTO addSameDream(Long id, String ipAddress) throws OperationNotPermittedException;

    /**
     * Get a random dream
     *
     * @return {@link Dream}
     * @throws NotFoundException
     */
    DreamDTO getRandom() throws NotFoundException;

    /**
     * Get most liked dream
     *
     * @return {@link Dream}
     * @throws NotFoundException 
     */
    DreamDTO getMostLiked() throws NotFoundException;

    /**
     * Get most disliked dream
     *
     * @return {@link Dream}
     * @throws NotFoundException 
     */
    DreamDTO getMostDisliked() throws NotFoundException;

    /**
     * Get all unapproved dreams
     *
     * @param pageable
     * @return page {@link Dream}
     */
    Page<Dream> getAllUnapproved(Pageable pageable);

    /**
     * Get all dreams
     *
     * @param tagId    optional param for search by tag id
     * @param pageable
     * @return page {@link Dream}
     */
    Page<DreamDTO> getAll(String keyword, Long tagId, Pageable pageable);

    /**
     * Get most the same dream
     *
     * @return {@link Dream}
     * @throws NotFoundException 
     */
    DreamDTO getMostSame() throws NotFoundException;
}
