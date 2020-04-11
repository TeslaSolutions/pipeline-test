package com.dream.exceptions;

import com.dream.utils.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {

    private ErrorCode errorCode;

    public AuthorizationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
