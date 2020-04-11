package com.dream.models;

import org.springframework.beans.factory.annotation.Value;

public interface DreamNumbers {

	@Value("#{target.likes_no}")
	Integer getLikesNo();
	
	@Value("#{target.dislikes_no}")
	Integer getDislikesNo();
	
	@Value("#{target.same_dream_no}")
	Integer getSameDreamNo();
}
