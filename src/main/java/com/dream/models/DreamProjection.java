package com.dream.models;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

public interface DreamProjection {

	@Value("#{target.id}")
	Long getId();
	@Value("#{target.dream_description}")
	String getDreamDescription();
	@Value("#{target.create_date}")
	LocalDateTime getCreateDate();
	@Value("#{target.likes_no}")
	Integer getLikesNo();
	@Value("#{target.dislikes_no}")
	Integer getDislikesNo();
	@Value("#{target.same_dream_no}")
	Integer getSameDreamNo();
	@Value("#{target.approved}")
	Boolean getApproved();
	@Value("#{target.commentCount}")
	Long getCommentsCount();
	@Value("#{@tagRepository.findByDreamId(target.id)}")
	Set<Tag> getTags();
}
