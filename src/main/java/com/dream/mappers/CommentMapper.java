package com.dream.mappers;

import com.dream.dtos.CommentDTO;
import com.dream.models.Comment;
import com.dream.models.CommentNumbers;

public class CommentMapper {

    public static Comment fromDto(CommentDTO commentDTO) {
        return Comment.builder()
                .commentDescription(commentDTO.getCommentDescription())
                .createdAt(commentDTO.getCreatedAt())
                .id(commentDTO.getId())
                .dream(commentDTO.getDream() != null ? DreamMapper.fromDto(commentDTO.getDream()) : null)
                .likesNo(commentDTO.getLikesNo())
                .dislikesNo(commentDTO.getDislikesNo())
                .parentId(commentDTO.getParentId())
                .build();
    }

    public static CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .commentDescription(comment.getCommentDescription())
                .createdAt(comment.getCreatedAt())
                .id(comment.getId())
                .dream(comment.getDream() != null ? DreamMapper.toDto(comment.getDream()) : null)
                .likesNo(comment.getLikesNo())
                .dislikesNo(comment.getDislikesNo())
                .parentId(comment.getParentId())
                .build();
    }

	public static CommentDTO toDto(CommentNumbers numbers) {
		return CommentDTO.builder()
                .likesNo(numbers.getLikesNo())
                .dislikesNo(numbers.getDislikesNo())
                .build();
	}

}
