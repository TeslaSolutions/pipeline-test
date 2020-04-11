package com.dream.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.dream.configs.ApiResponseCodes;
import com.dream.configs.MySSEEmitter;
import com.dream.configs.ObjectSSEEmitter;
import com.dream.configs.Secured;
import com.dream.dtos.CommentDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.mappers.CommentMapper;
import com.dream.services.CommentService;
import com.dream.utils.Endpoints;
import com.dream.utils.JsonUtil;
import com.dream.utils.ObjectType;

import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(Endpoints.COMMENT)
@Api(value = Endpoints.COMMENT)
public class CommentController {

    @Lazy
    @Autowired
    private CommentService commentService;
    
    @Lazy
    @Autowired
    private ObjectSSEEmitter commentSSEEmitter;

    @GetMapping(value = "{dream_id}")
    @ApiOperation(value = "Get comments for dream",
            notes = "Send GET request to get comments for dream",
            httpMethod = "GET", response = List.class)
    @ApiResponseCodes
    public ResponseEntity<Page<CommentDTO>> getCommentsForDream(@PathVariable(value = "dream_id") Long dreamId, Pageable pageable) {
        return ResponseEntity.ok(
                new PageImpl<>(commentService.getByDreamId(dreamId, pageable).getContent()
                        .stream().map(CommentMapper::toDto).collect(Collectors.toList())));
    }

    @PostMapping(value = Endpoints.CREATE + Endpoints.DREAM + "/{dream_id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create comment",
            notes = "Send POST request to create comment",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "POST", code = 200, response = CommentDTO.class)
    @ApiResponseCodes
    public ResponseEntity<CommentDTO> createComment(
            @ApiParam(value = "Param CommentDTO", required = true) @NotNull @RequestBody CommentDTO commentDTO,
            @NotNull @PathVariable(value = "dream_id") Long dreamId) throws NotFoundException {
        return ResponseEntity.ok(CommentMapper.toDto(commentService.create(commentDTO, dreamId)));
    }

    @PutMapping(value = Endpoints.UPDATE)
    @ApiOperation(value = "Update your comment",
            notes = "Send PUT request to update the comment",
            httpMethod = "PUT", response = CommentDTO.class)
    @ApiResponseCodes
    public ResponseEntity<CommentDTO> updateComment(@NotNull @RequestBody CommentDTO commentDTO) throws Exception {
        Assert.notNull(commentDTO.getId(), "Comment id cannot be null for update");
        return ResponseEntity.ok(CommentMapper.toDto(commentService.update(commentDTO)));
    }

    @DeleteMapping(value = Endpoints.DELETE + "/{id}")
    @ApiOperation(value = "Delete your comment",
            notes = "Send DELETE request to delete your comment",
            httpMethod = "DELETE", response = String.class)
    @ApiResponseCodes
    public ResponseEntity<String> deleteComment(@NotNull @PathVariable(name = "id") Long id) {
        commentService.delete(id);
        return ResponseEntity.ok(JsonUtil.toJson("Comment has been successfully deleted"));
    }

    @PutMapping(value = Endpoints.LIKE + "/{comment_id}")
    @ApiOperation(value = "Like a comment",
            notes = "Send PUT request to like a comment",
            httpMethod = "PUT", response = void.class)
    @ApiResponseCodes
    public ResponseEntity<?> likeAComment(@NotNull @PathVariable("comment_id") Long commentId, HttpServletRequest request) throws OperationNotPermittedException {
        CommentDTO numbers = commentService.like(commentId, request.getRemoteAddr());
        return ResponseEntity.ok(numbers);
    }

    @PutMapping(value = Endpoints.DISLIKE + "/{comment_id}")
    @ApiOperation(value = "Dislike a comment",
            notes = "Send PUT request to dislike a comment",
            httpMethod = "PUT", response = void.class)
    @ApiResponseCodes
    public ResponseEntity<?> dislikeAComment(@NotNull @PathVariable("comment_id") Long commentId, HttpServletRequest request) throws OperationNotPermittedException {
    	CommentDTO numbers = commentService.dislike(commentId, request.getRemoteAddr());
        return ResponseEntity.ok(numbers);
    }

    @Secured
    @PutMapping(value = Endpoints.APPROVE + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Approve comment by id",
            notes = "Send PUT secured request to approve comment by id",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "PUT", response = Void.class,
            authorizations = @Authorization(value = "Authorization"))
    @ApiResponseCodes
    public ResponseEntity<?> approveCommentById(@PathVariable(name = "id") @NotNull(message = "Dream id cannot be null") Long commentId) {
        commentService.approve(commentId);
        
        //Send the comment as last via SSE
        commentSSEEmitter.prepareAndSend(commentId, ObjectType.COMMENT);
        
        return ResponseEntity.ok(JsonUtil.toJson("Comment has been approved"));
    }

    @Secured
    @GetMapping(value = Endpoints.UNAPPROVED + Endpoints.ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all unapproved comments",
            notes = "Send GET request to get unapproved comments",
            httpMethod = "GET", response = Map.class,
            authorizations = @Authorization(value = "Authorization"))
    @ApiResponseCodes
    public ResponseEntity<List<CommentDTO>> getAllUnapprovedCommentByDream() throws NotFoundException {
        return ResponseEntity.ok(commentService.getUnapprovedComments());
    }

    @GetMapping(value = "{parent_id}/" + Endpoints.REPLY)
    @ApiOperation(value = "Get comment replies",
            notes = "Send GET request to get comment replies",
            httpMethod = "GET", response = List.class)
    @ApiResponseCodes
    public ResponseEntity<Page<CommentDTO>> getCommentReplies(@PathVariable(value = "parent_id") Long parentId, Pageable pageable) {
        return ResponseEntity.ok(
                new PageImpl<>(commentService.getReplies(parentId, pageable).getContent()
                        .stream().map(CommentMapper::toDto).collect(Collectors.toList())));
    }
    
    @GetMapping(value = Endpoints.LAST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "Get last approved comment via SSE",
        notes = "Comment will be sent to the client via SSE",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE,
        httpMethod = "GET", response = Void.class)
    @ApiResponseCodes
    public ResponseEntity<SseEmitter> getLatestComment() {
    	final MySSEEmitter emitter = new MySSEEmitter(-1l);
    	commentSSEEmitter.addEmitter(emitter);
		emitter.onCompletion(() -> 
			commentSSEEmitter.removeEmitter(emitter)
		);
		emitter.onTimeout(() -> 
			commentSSEEmitter.removeEmitter(emitter)
		);
		return new ResponseEntity<>(emitter, HttpStatus.OK);

    }
    
}
