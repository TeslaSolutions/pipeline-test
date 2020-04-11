package com.dream.controllers;

import com.dream.configs.*;
import com.dream.dtos.DreamDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.mappers.DreamMapper;
import com.dream.models.Dream;
import com.dream.models.DreamNumbers;
import com.dream.repositories.TagRepository;
import com.dream.services.DreamService;
import com.dream.utils.Endpoints;
import com.dream.utils.JsonUtil;
import com.dream.utils.ObjectType;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dream controller
 */

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(Endpoints.DREAM)
@Api(value = Endpoints.DREAM)
@ApiResponseCodes
@Validated
public class DreamController {

    @Lazy
    @Autowired
    private DreamService dreamService;

    @Lazy
    @Autowired
    private TagRepository tagRepository;

    @Lazy
    @Autowired
    private ObjectSSEEmitter dreamSSEEmitter;

    @PostMapping(value = Endpoints.CREATE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create dream",
            notes = "Send POST request to create dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "POST", code = 200, response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> createDream(
            @ApiParam(value = "Param DreamDTO", required = true) @Valid @NotNull @RequestBody DreamDTO dream) {
        return ResponseEntity.ok(DreamMapper.toDto(dreamService.create(dream)));
    }

    @GetMapping(value = "/{dream_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get one dream",
            notes = "Send GET request to get one specific dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> getDream(@NotNull @PathVariable(value = "dream_id") Long dreamId) throws NotFoundException {
        return ResponseEntity.ok(dreamService.getDTOById(dreamId));
    }

    @GetMapping(value = Endpoints.ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiPageable
    @ApiOperation(value = "Get all dreams",
            notes = "Send GET request to get all dream",
            httpMethod = "GET", response = List.class)
    @ApiResponseCodes
    public ResponseEntity<Page<DreamDTO>> getAllDreams(@ApiParam(value = "Parameter keyword is used for searching dreams") @RequestParam(name = "keyword", required = false) String keyword,
    												   @ApiParam(value = "Parameter tagId is used to filter dreams by tag") @RequestParam(name = "tagId", required = false) Long tagId, 
    												   @ApiIgnore Pageable pageable) {
        Page<DreamDTO> dreamDTOS = dreamService.getAll(keyword, tagId, pageable);

        return ResponseEntity.ok(dreamDTOS);
    }

    @PutMapping(value = Endpoints.UPDATE)
    @ApiOperation(value = "Update your dream",
            notes = "Send PUT request to update the dream",
            httpMethod = "PUT", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> updateDream(@Valid @NotNull @RequestBody DreamDTO dreamDTO) {
        Assert.notNull(dreamDTO.getId(), "Dream id cannot be null for update");
        return ResponseEntity.ok(DreamMapper.toDto(dreamService.update(dreamDTO)));
    }

    @DeleteMapping(value = Endpoints.DELETE + "/{id}")
    @ApiOperation(value = "Delete your dream",
            notes = "Send DELETE request to delete your dream",
            httpMethod = "DELETE", response = String.class)
    @ApiResponseCodes
    public ResponseEntity<String> deleteDream(@NotNull @PathVariable(name = "id") Long id) throws NotFoundException {
        dreamService.delete(id);
        return ResponseEntity.ok(JsonUtil.toJson("Dream has been successfully deleted"));
    }

    @PutMapping(value = Endpoints.LIKE + "/{dream_id}")
    @ApiOperation(value = "Like a dream",
            notes = "Send PUT request to like a dream",
            httpMethod = "PUT")
    @ApiResponseCodes
    public ResponseEntity<?> likeADream(@NotNull @PathVariable("dream_id") Long dreamId, HttpServletRequest request) throws OperationNotPermittedException {
    	DreamDTO numbers = dreamService.like(dreamId, request.getRemoteAddr());
    	return ResponseEntity.ok(numbers);
    }

    @PutMapping(value = Endpoints.SAME_DREAM + "/{dream_id}")
    @ApiOperation(value = "I dreamed the same dream",
            notes = "Send PUT request to dream the same dream",
            httpMethod = "PUT", response = void.class)
    @ApiResponseCodes
    public ResponseEntity<?> iDreamedTheSameDream(@NotNull @PathVariable("dream_id") Long dreamId, HttpServletRequest request) throws OperationNotPermittedException {
    	DreamDTO numbers = dreamService.addSameDream(dreamId, request.getRemoteAddr());
    	return ResponseEntity.ok(numbers);
    }

    @GetMapping(value = Endpoints.RANDOM, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get one random dream",
            notes = "Send GET request to get one random dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> getRandomDream() throws NotFoundException {
        return ResponseEntity.ok(dreamService.getRandom());
    }

    @PutMapping(value = Endpoints.DISLIKE + "/{dream_id}")
    @ApiOperation(value = "Dislike a dream",
            notes = "Send PUT request to dislike a dream",
            httpMethod = "PUT")
    @ApiResponseCodes
    public ResponseEntity<?> dislikeADream(@NotNull @PathVariable("dream_id") Long dreamId, HttpServletRequest request) throws OperationNotPermittedException {
    	DreamDTO numbers = dreamService.dislike(dreamId, request.getRemoteAddr());
        return ResponseEntity.ok(numbers);
    }

    @GetMapping(value = Endpoints.MAX_LIKES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get most liked dream",
            notes = "Send GET request to get most liked dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> getMostLikedDream() throws NotFoundException {
        return ResponseEntity.ok(dreamService.getMostLiked());
    }

    @GetMapping(value = Endpoints.MAX_DISLIKES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get most disliked dream",
            notes = "Send GET request to get most disliked dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> getMostDislikedDream() throws NotFoundException {
        return ResponseEntity.ok(dreamService.getMostDisliked());
    }

    @Secured
    @PutMapping(value = Endpoints.APPROVE + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Approve dream by id",
            notes = "Send PUT secured request to approve dream by id",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "PUT", response = Void.class,
            authorizations = @Authorization(value = "Authorization"))
    @ApiResponseCodes
    public ResponseEntity<?> approveDreamById(@PathVariable(name = "id") @NotNull(message = "Dream id cannot be null") Long dreamId) {
        dreamService.approve(dreamId);

        //Send the dream as last via SSE
        dreamSSEEmitter.prepareAndSend(dreamId, ObjectType.DREAM);

        return ResponseEntity.ok(JsonUtil.toJson("Dream has been approved"));
    }

    @Secured
    @GetMapping(value = Endpoints.UNAPPROVED + Endpoints.ALL, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiPageable
    @ApiOperation(value = "Get all unapproved dreams",
            notes = "Send GET request to get all dream",
            httpMethod = "GET", response = List.class,
            authorizations = @Authorization(value = "Authorization"))
    @ApiResponseCodes
    public ResponseEntity<PageImpl<DreamDTO>> getAllUnapprovedDreams(@ApiIgnore Pageable pageable) {
        Page<Dream> dreams = dreamService.getAllUnapproved(pageable);
        return ResponseEntity.ok(new PageImpl<>(dreams.getContent().stream().map(DreamMapper::toDto).collect(Collectors.toList()), dreams.getPageable(), dreams.getTotalElements()));
    }


    @GetMapping(value = Endpoints.LAST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "Get last approved dream via SSE",
            notes = "Dream will be sent to the client via SSE",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            httpMethod = "GET", response = Void.class)
    @ApiResponseCodes
    public ResponseEntity<SseEmitter> getLatestDream() {
        final MySSEEmitter emitter = new MySSEEmitter(-1l);
        dreamSSEEmitter.addEmitter(emitter);

        emitter.onTimeout(() -> {
            dreamSSEEmitter.removeEmitter(emitter);
        });

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    @GetMapping(value = Endpoints.MAX_SAME_DREAM, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get most the same dream",
            notes = "Send GET request to get most the same dream",
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET", response = DreamDTO.class)
    @ApiResponseCodes
    public ResponseEntity<DreamDTO> getMostSameDream() throws NotFoundException {
        return ResponseEntity.ok(dreamService.getMostSame());
    }
}
