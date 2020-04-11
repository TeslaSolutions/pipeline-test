package com.dream.models;

import org.springframework.beans.factory.annotation.Value;

public interface CommentNumbers {

	@Value("#{target.likes_no}")
	Integer getLikesNo();
	
	@Value("#{target.dislikes_no}")
	Integer getDislikesNo();
}
