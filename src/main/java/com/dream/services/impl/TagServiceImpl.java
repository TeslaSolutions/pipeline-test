package com.dream.services.impl;

import com.dream.dtos.TagDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.mappers.TagMapper;
import com.dream.models.Tag;
import com.dream.repositories.TagRepository;
import com.dream.services.TagService;
import com.dream.utils.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link TagService}
 */

@Service
public class TagServiceImpl implements TagService{

    @Lazy
    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<TagDTO> getAllTags() throws NotFoundException {
        List<Tag> tags = tagRepository.findAll();

        if(tags.isEmpty()) {
            throw new NotFoundException(ErrorCode.TAG_NOT_FOUND, "Tags not found");
        }

        int totalNumberOfDreams = 0;

        for (Tag tag : tags) {
            totalNumberOfDreams = totalNumberOfDreams + tag.getDreams().size();
        }

        List<TagDTO> tagDTOs = new ArrayList<>();

        int finalTotalNumberOfDreams = totalNumberOfDreams;
        tags.forEach(tag -> {
            TagDTO tagDTO = TagMapper.toDto(tag);
            tagDTO.setPercent(tag.getDreams().size() * 100 / finalTotalNumberOfDreams);
            tagDTOs.add(tagDTO);
        });

        return tagDTOs;
    }
}
