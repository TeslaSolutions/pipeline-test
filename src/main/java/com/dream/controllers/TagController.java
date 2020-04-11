package com.dream.controllers;

import com.dream.configs.ApiResponseCodes;
import com.dream.dtos.TagDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.mappers.TagMapper;
import com.dream.services.TagService;
import com.dream.utils.Endpoints;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tags controller
 */

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(Endpoints.TAG)
@Api(value = Endpoints.TAG)
public class TagController {

    @Lazy
    @Autowired
    private TagService tagService;

    @GetMapping(value = Endpoints.ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all tags",
            notes = "Send GET request to get all tags",
            httpMethod = "GET", response = List.class)
    @ApiResponseCodes
    public ResponseEntity<List<TagDTO>> getAllTags() throws NotFoundException {
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
