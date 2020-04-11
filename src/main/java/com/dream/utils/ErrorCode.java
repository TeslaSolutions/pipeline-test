package com.dream.utils;

public enum ErrorCode {

    UNAUTHORIZED,
    FORBIDDEN,
    SECURED,

    TAG_NOT_FOUND,
    DREAM_NOT_FOUND,
    DREAM_NOT_APPROVED,
    COMMENT_NOT_FOUND,
    DREAM_ALREADY_LIKED,
    DREAM_ALREADY_DISLIKED,
    DREAM_ALREADY_MARKED_AS_SAME,
    COMMENT_ALREADY_LIKED,
    COMMENT_ALREADY_DISLIKED,

    //Common error codes
    PLATFORM_CONFLICT,
    BAD_REQUEST,
    SERVER_ERROR;
}