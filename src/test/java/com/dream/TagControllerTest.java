package com.dream;

import com.dream.base.ControllerTest;
import com.dream.dtos.TagDTO;
import com.dream.utils.Endpoints;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TagControllerTest extends ControllerTest {

    @Test
    public void getAllTagsTest() {
        final String url = StringUtils.join(Endpoints.TAG, Endpoints.ALL);
        ResponseEntity<TagDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(authorizedHeadersDefault()), TagDTO[].class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Content cannot be null", response.getBody());
        Assert.assertEquals("Wrong number of tags", 2, response.getBody().length);
    }

}
