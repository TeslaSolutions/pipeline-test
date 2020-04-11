package com.dream.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CommentDTO {
    private Long id;
    @NotBlank
    private String commentDescription;
    private LocalDateTime createdAt;
    private int likesNo;
    private int dislikesNo;
    private DreamDTO dream;
    private Long parentId;
    private String parentCommentDescription;
}
