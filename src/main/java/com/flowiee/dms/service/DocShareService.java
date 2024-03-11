package com.flowiee.dms.service;

import com.flowiee.dms.entity.DocShare;

import java.util.List;

public interface DocShareService {

    List<DocShare> findAll();

    DocShare findById(int id);

    boolean isShared(int documentId);
}