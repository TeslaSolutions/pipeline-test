package com.dream;

import com.dream.base.ControllerTest;
import com.dream.dtos.DreamDTO;
import com.dream.exceptions.AuthorizationException;
import com.dream.exceptions.OperationNotPermittedException;
import com.dream.models.Tag;
import com.dream.utils.Endpoints;
import com.dream.utils.ErrorCode;
import com.dream.utils.ErrorResponse;
import com.dream.utils.HelperPage;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.internal.util.collections.Sets;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class DreamControllerTest extends ControllerTest {

    @Test
    public void createDreamValidTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.CREATE);
        final DreamDTO request = DreamDTO.builder()
                .createDate(LocalDateTime.now())
                .dreamDescription("Novi san iz testa")
                .tags(Sets.newSet(Tag.builder().id(1l).name("kosmar").build()))
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
    }

    @Test
    public void createDreamNoTagsTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.CREATE);
        final DreamDTO request = DreamDTO.builder()
                .createDate(LocalDateTime.now())
                .dreamDescription("Novi san iz testa bez flaga")
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void createDreamNoDescriptionTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.CREATE);
        final DreamDTO request = DreamDTO.builder()
                .createDate(LocalDateTime.now())
                .tags(Sets.newSet(Tag.builder().id(1l).name("kosmar").build()))
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void createDreamNoCreateDateValidTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.CREATE);
        final DreamDTO request = DreamDTO.builder()
                .dreamDescription("Novi san iz testa")
                .tags(Sets.newSet(Tag.builder().id(1l).name("kosmar").build()))
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
    }

    @Test
    public void getDreamByIdValidTest() {
        final long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, "/", dreamId);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
        Assert.assertNotNull("Dream description cannot be null", response.getBody().getDreamDescription());
    }

    @Test
    public void getDreamByIdNotFoundTest() {
        final long dreamId = 9999l;
        final String url = StringUtils.join(Endpoints.DREAM, "/", dreamId);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, null, DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void getDreamAllTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL);
        ResponseEntity<HelperPage<DreamDTO>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
        });

        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of pages", 1, response.getBody().getTotalPages());
        Assert.assertEquals("Wrong number of records", 9, response.getBody().getTotalElements());
    }

    @Test
    public void getAllDreamCommentsSortTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?sort=comments");
        ResponseEntity<HelperPage<DreamDTO>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
        });

        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong dream is on the first place", Long.valueOf(2L), response.getBody().getContent().iterator().next().getId());
    }

    @Test
    public void getAllDreamPagingTest() {
        final int page = 0;
        final int size = 3;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?page=", page, "&size=", size);
        ResponseEntity<HelperPage<DreamDTO>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
        });

        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of pages", 3, response.getBody().getTotalPages());
        Assert.assertEquals("Wrong number of records", 9, response.getBody().getTotalElements());
    }

    @Test
    public void updateDreamValidTest() {
        final Long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.UPDATE);
        final DreamDTO request = DreamDTO.builder()
                .id(dreamId)
                .createDate(LocalDateTime.now())
                .dreamDescription("Updated prvi san")
                .tags(Sets.newSet(Tag.builder().id(1l).name("kosmar").build()))
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Id of dream is not valid", Long.valueOf(1), response.getBody().getId());
        Assert.assertEquals("Dream description is not valid", "Updated prvi san", response.getBody().getDreamDescription());
    }

    @Test
    public void updateDreamNoTagsTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.UPDATE);
        final Long dreamId = 1l;
        final DreamDTO request = DreamDTO.builder()
                .createDate(LocalDateTime.now())
                .id(dreamId)
                .dreamDescription("Novi san iz testa")
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void updateDreamNoDescriptionTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.UPDATE);
        final Long dreamId = 1l;
        final DreamDTO request = DreamDTO.builder()
                .createDate(LocalDateTime.now())
                .id(dreamId)
                .tags(Sets.newSet(Tag.builder().id(1l).name("kosmar").build()))
                .build();
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void deleteDreamValidTest() {
        final int dreamId = 5;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.DELETE, "/", dreamId);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(authorizedHeadersDefault()), String.class);

        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong message", "Dream has been successfully deleted", substringResponse(response.getBody()));
    }

    @Test
    public void deleteDreamInValidTest() {
        final int dreamId = 41;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.DELETE, "/", dreamId);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(authorizedHeadersDefault()), String.class);

        Assert.assertEquals("Wrong response status", HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void likeDreamAValidTest() {
        final Long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.LIKE, "/", dreamId);
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of likes", 5, response.getBody().getLikesNo());
    }

    @Test
    public void likeDreamOperationNotPermitedTest() {
        final Long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.LIKE, "/", dreamId);
        ResponseEntity<OperationNotPermittedException> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), OperationNotPermittedException.class);
        Assert.assertEquals("Wrong response status", HttpStatus.ALREADY_REPORTED.value(), response.getStatusCodeValue());
    }

    @Test
    public void likeDreamThatAlreadyDislikedTest() {
        final Long dreamId = 3l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.LIKE, "/", dreamId);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of likes", 2, response.getBody().getLikesNo());
        Assert.assertEquals("Wrong number of dislikes", 0, response.getBody().getDislikesNo());
    }

    @Test
    public void dislikeDreamAValidTest() {
        final Long dreamId = 2l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.DISLIKE, "/", dreamId);
        ResponseEntity<DreamDTO> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of likes", 2, response.getBody().getDislikesNo());
    }

    @Test
    public void dislikeDreamOperationNotPermitedTest() {
        final Long dreamId = 2l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.DISLIKE, "/", dreamId);
        ResponseEntity<OperationNotPermittedException> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), OperationNotPermittedException.class);
        Assert.assertEquals("Wrong response status", HttpStatus.ALREADY_REPORTED.value(), response.getStatusCodeValue());
    }

    @Test
    public void dislikeDreamThatAlreadyLikedTest() {
        final Long dreamId = 4l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.DISLIKE, "/", dreamId);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of likes", 0, response.getBody().getLikesNo());
        Assert.assertEquals("Wrong number of dislikes", 2, response.getBody().getDislikesNo());
    }

    @Test
    public void getRandomDreamTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.RANDOM);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
        Assert.assertNotNull("Dream description cannot be null", response.getBody().getDreamDescription());
    }

    @Test
    public void sameDreamAValidTest() {
        final Long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.SAME_DREAM, "/", dreamId);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of same dreams", 1, response.getBody().getSameDreamNo());
    }

    @Test
    public void sameDreamInValidTest() {
        final Long dreamId = 1l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.SAME_DREAM, "/", dreamId);
        ResponseEntity<OperationNotPermittedException> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), OperationNotPermittedException.class);
        Assert.assertEquals("Wrong response status", HttpStatus.ALREADY_REPORTED.value(), response.getStatusCodeValue());
    }

    @Test
    public void getMaxLikesDreamTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.MAX_LIKES);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
        Assert.assertNotNull("Dream description cannot be null", response.getBody().getDreamDescription());
        Assert.assertEquals("Wrong number of likes", 9, response.getBody().getLikesNo());
    }

    @Test
    public void getMaxDislikesDreamTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.MAX_DISLIKES);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
        Assert.assertNotNull("Dream description cannot be null", response.getBody().getDreamDescription());
        Assert.assertEquals("Wrong number of dislikes", 13, response.getBody().getDislikesNo());
    }

    @Test
    public void getMaxSameDreamTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.MAX_SAME_DREAM);
        ResponseEntity<DreamDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", response.getBody().getId());
        Assert.assertNotNull("Tags cannot be null", response.getBody().getTags());
        Assert.assertNotNull("Create date cannot be null", response.getBody().getCreateDate());
        Assert.assertNotNull("Dream description cannot be null", response.getBody().getDreamDescription());
        Assert.assertEquals("Wrong number of same dreams", 17, response.getBody().getSameDreamNo());
    }

    @Test
    public void aGetDreamUnapprovedAllTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.UNAPPROVED, Endpoints.ALL);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersWithBearer()), new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                });

        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of records", 2, response.getBody().getTotalElements());
    }

    @Test
    public void getDreamUnapprovedAllUnauthorizedTest() {
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.UNAPPROVED, Endpoints.ALL);
        ResponseEntity<AuthorizationException> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), AuthorizationException.class);

        Assert.assertEquals("Wrong response status", HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
    }

    @Test
    public void approveDreamValidTest() {
        final long dreamId = 9l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.APPROVE + "/", dreamId);
        ResponseEntity<String> response = restTemplate
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersWithBearer()), String.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong message", "Dream has been approved", substringResponse(response.getBody()));

        final String urlGet = StringUtils.join(Endpoints.DREAM, "/", dreamId);
        ResponseEntity<DreamDTO> dream = restTemplate.exchange(urlGet, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), DreamDTO.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), dream.getStatusCodeValue());
        Assert.assertNotNull("Id of dream cannot be null", dream.getBody().getId());
        Assert.assertNotNull("Dream description cannot be null", dream.getBody().getDreamDescription());
    }

    @Test
    public void getUnapprovedDream() {
        final long dreamId = 10l;
        final String urlGet = StringUtils.join(Endpoints.DREAM, "/", dreamId);
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(urlGet, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), ErrorResponse.class);
        Assert.assertEquals("Wrong response status", HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong error code", ErrorCode.DREAM_NOT_FOUND, response.getBody().getErrorCode());
    }

    @Test
    public void approveDreamUnauthorizedTest() {
        final long dreamId = 9l;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.APPROVE + "/", dreamId);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authorizedHeadersDefault()), String.class);
        Assert.assertEquals("Wrong response status", HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
    }

    @Test
    public void searchDreamTest() {
        final String keyword = "dislikes";
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of responses", 2, response.getBody().getTotalElements());
    }

    @Test
    public void searchDreamNoResultsTest() {
        final String keyword = "tralalla";
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of responses", 0, response.getBody().getTotalElements());
    }

    @Test
    public void searchDreamByKeywordAndTagTest() {
        final String keyword = "approved";
        final int tagId = 2;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword, "&tagId=", tagId);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of responses", 1, response.getBody().getTotalElements());
    }

    @Test
    public void searchDreamWithPagingTest() {
        final String keyword = "dream";
        final int size = 3;
        final int page = 0;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword, "&page=", page, "&size=", size);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong number of responses", 9, response.getBody().getTotalElements());
        Assert.assertEquals("Wrong number of pages", 3, response.getBody().getTotalPages());
    }

    @Test
    public void searchDreamWithSortingTest() {
        final String keyword = "dream";
        final String sortLikeDesc = "likes_no,desc";
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword, "&sort=", sortLikeDesc);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong dream id on first place", Long.valueOf(6L), response.getBody().getContent().iterator().next().getId());

        final String sortCreateDateAsc = "create_date,asc";
        final String urlSort = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword, "&sort=", sortCreateDateAsc);
        ResponseEntity<HelperPage<DreamDTO>> responseSort =
                restTemplate.exchange(urlSort, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), responseSort.getStatusCodeValue());
        Assert.assertEquals("Wrong dream id on first place", Long.valueOf(1L), responseSort.getBody().getContent().iterator().next().getId());
    }

    @Test
    public void searchDreamWithSortingAndTagTest() {
        final String keyword = "dream";
        final String sortLikeAsc = "likes_no,asc";
        final int tagId = 1;
        final String url = StringUtils.join(Endpoints.DREAM, Endpoints.ALL, "?keyword=", keyword, "&sort=", sortLikeAsc, "&tagId=", tagId);
        ResponseEntity<HelperPage<DreamDTO>> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()),
                        new ParameterizedTypeReference<HelperPage<DreamDTO>>() {
                        });
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertEquals("Wrong dream id on first place", Long.valueOf(2L), response.getBody().getContent().iterator().next().getId());
    }
}