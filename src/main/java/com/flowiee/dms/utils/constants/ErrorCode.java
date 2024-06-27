package com.flowiee.dms.utils.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SEARCH_ERROR(2000, "An error occurred while search %s"),
    CREATE_ERROR(2001, "An error occurred while create %s"),
    UPDATE_ERROR(2002, "An error occurred while update %s"),
    DELETE_ERROR(2003, "An error occurred while delete %s"),
    AUTHEN_ERROR(2004, "Unauthorized"),
    FORBIDDEN_ERROR(2005, "You are not authorized to use this function!"),
    NOTFOUND_ERROR( 2006, "The resource you are accessing dose not found!"),
    DATA_LOCKED_ERROR( 2007, "The resource is currently in use and cannot be update or delete at this time!");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}