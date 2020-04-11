package com.dream.dtos;

import com.dream.models.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DreamDTO {

    private Long id;

    @NotNull(message = "Dream description cannot be null")
    @NotBlank(message = "Dream description cannot be empty")
    private String dreamDescription;
    private LocalDateTime createDate;
    private int likesNo;
    private int dislikesNo;
    private int sameDreamNo;
    @NotNull
    private Set<Tag> tags;
    private Long commentCount;
    
	@Override
	public String toString() {
		return "DreamDTO [id=" + id + ", dreamDescription=" + dreamDescription + "]";
	}
}

