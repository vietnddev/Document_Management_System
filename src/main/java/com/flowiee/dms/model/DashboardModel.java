package com.flowiee.dms.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DashboardModel implements Serializable {
    private int totalDoc;
    private int totalFile;
    private int totalFolder;
    private String totalSize;
}