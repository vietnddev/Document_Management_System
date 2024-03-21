package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.model.DashboardModel;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.storage.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public DashboardModel loadDashboard() {
        Object[] data = documentRepository.summaryStorage().get(0);
        DashboardModel model = new DashboardModel();
        model.setTotalDoc(Integer.parseInt(String.valueOf(data[0])) + Integer.parseInt(String.valueOf(data[1])));
        model.setTotalFolder(Integer.parseInt(String.valueOf(data[0])));
        model.setTotalFile(Integer.parseInt(String.valueOf(data[1])));
        model.setTotalSize(String.valueOf(data[2]));
        return model;
    }
}