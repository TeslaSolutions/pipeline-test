package com.dream.mappers;

import com.dream.dtos.DreamDTO;
import com.dream.models.Dream;
import com.dream.models.DreamNumbers;
import com.dream.models.DreamProjection;

public class DreamMapper {
	
    public static Dream fromDto(DreamDTO dreamDTO) {
    	return Dream.builder()
                .id(dreamDTO.getId())
                .createDate(dreamDTO.getCreateDate())
                .dreamDescription(dreamDTO.getDreamDescription())
                .likesNo(dreamDTO.getLikesNo())
                .dislikesNo(dreamDTO.getDislikesNo())
                .sameDreamNo(dreamDTO.getSameDreamNo())
                .tags(dreamDTO.getTags())
                .build();
    }

    public static DreamDTO toDto(Dream dream) {
    	return DreamDTO.builder()
                .id(dream.getId())
                .createDate(dream.getCreateDate())
                .dreamDescription(dream.getDreamDescription())
                .tags(dream.getTags())
                .likesNo(dream.getLikesNo())
                .dislikesNo(dream.getDislikesNo())
                .sameDreamNo(dream.getSameDreamNo())
                .build();
    }
    
    public static DreamDTO toDto(DreamProjection dreamProj) {
    	return DreamDTO.builder()
                .id(dreamProj.getId())
                .createDate(dreamProj.getCreateDate())
                .dreamDescription(dreamProj.getDreamDescription())
                .tags(dreamProj.getTags())
                .likesNo(dreamProj.getLikesNo())
                .dislikesNo(dreamProj.getDislikesNo())
                .sameDreamNo(dreamProj.getSameDreamNo())
                .commentCount(dreamProj.getCommentsCount())
                .build();
    }
    
    public static DreamDTO toDto(DreamNumbers dreamNum) {
    	return DreamDTO.builder()
                .likesNo(dreamNum.getLikesNo())
                .dislikesNo(dreamNum.getDislikesNo())
                .sameDreamNo(dreamNum.getSameDreamNo())
                .build();
    }

}
