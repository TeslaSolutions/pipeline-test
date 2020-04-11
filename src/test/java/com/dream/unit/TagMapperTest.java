package com.dream.unit;

import com.dream.dtos.TagDTO;
import com.dream.mappers.TagMapper;
import com.dream.models.Tag;
import org.junit.Assert;
import org.junit.Test;

public class TagMapperTest {

    private static final TagDTO tagDTO = TagDTO.builder()
            .id(11L)
            .name("sexy")
            .build();

    @Test
    public void tagFromDtoTest() {
        Tag tag = TagMapper.fromDto(TagMapperTest.tagDTO);
        Assert.assertEquals("Wrong id", Long.valueOf(11L), tag.getId());
        Assert.assertEquals("Wrong name", "sexy", tag.getName());
    }
}
