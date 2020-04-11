package com.dream.services;

import com.dream.dtos.TagDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.models.Tag;

import java.util.List;

/**
 * Tag service
 */

public interface TagService {

    /**
     * Get all tags
     * @return list {@link TagDTO}
     */
    List<TagDTO> getAllTags() throws NotFoundException;

}
