package com.dream.mappers;

import com.dream.dtos.TagDTO;
import com.dream.models.Tag;

public class TagMapper {

    public static Tag fromDto(TagDTO tagDTO) {
        return Tag.builder()
                .id(tagDTO.getId())
                .name(tagDTO.getName())
                .build();
    }

    public static TagDTO toDto(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

}
