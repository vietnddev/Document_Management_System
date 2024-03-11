package com.flowiee.dms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationModel {
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private long totalElements;

    public PaginationModel(int pageNum, int pageSize, int totalPage, long totalElements) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalElements = totalElements;
    }
}