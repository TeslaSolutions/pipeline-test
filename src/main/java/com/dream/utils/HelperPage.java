package com.dream.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * HelperPage class used for integration testing of controllers with pagination
 * 
 *
 * @param <T> generic parameter which should be replaced with controller model
 */
public class HelperPage<T> extends PageImpl<T> {

	private static final long serialVersionUID = -1198560835253880335L;

	/**
	 * JSON Creator constructor
	 * 
	 * @param content
	 * @param number
	 * @param size
	 * @param totalElements
	 * @param pageable
	 * @param sort
	 * @param totalPages
	 * @param first
	 * @param last
	 * @param empty
	 * @param numberOfElements
	 */
	/*
	 * https://stackoverflow.com/questions/34647303/spring-resttemplate-with-paginated-api
	 */
	@JsonCreator
	public HelperPage(//
                      @JsonProperty("content") List<T> content, // PageImpl
                      @JsonProperty("number") int number, // PageImpl
                      @JsonProperty("size") int size, // PageImpl
                      @JsonProperty("totalElements") long totalElements, // PageImpl
                      @JsonProperty("pageable") JsonNode pageable, //
                      @JsonProperty("sort") JsonNode sort, //
                      @JsonProperty("totalPages") int totalPages, // computed
                      @JsonProperty("first") boolean first, // computed
                      @JsonProperty("last") boolean last, // computed
                      @JsonProperty("empty") boolean empty, // computed
                      @JsonProperty("numberOfElements") int numberOfElements // computed
	) {
		super(content, PageRequest.of(number, size), totalElements);
	}

}