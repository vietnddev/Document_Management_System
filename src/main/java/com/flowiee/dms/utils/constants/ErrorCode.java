package com.flowiee.dms.utils.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SEARCH_ERROR_OCCURRED(2000, "An error occurred while search %s"),
    CREATE_ERROR_OCCURRED(2001, "An error occurred while create %s"),
    UPDATE_ERROR_OCCURRED(2002, "An error occurred while update %s"),
    DELETE_ERROR_OCCURRED(2003, "An error occurred while delete %s"),
    UNAUTHORIZED(2004, "Unauthorized"),
    ERROR_FORBIDDEN(2005, "You are not authorized to use this function!"),
    ERROR_NOTFOUND( 2006, "The resource you are accessing dose not found!"),
    ERROR_DATA_LOCKED( 2007, "The resource is currently in use and cannot be update or delete at this time!");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}