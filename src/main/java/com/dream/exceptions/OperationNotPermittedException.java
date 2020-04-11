package com.dream.exceptions;

import com.dream.utils.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(HttpStatus.ALREADY_REPORTED)
public class OperationNotPermittedException extends Exception {

    private static final long serialVersionUID = 2967461196741036187L;
	private ErrorCode errorCode;

    public OperationNotPermittedException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
