package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.system.GroupAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupAccountService extends BaseCurdService<GroupAccount> {
    Page<GroupAccount> findAll(int pageSize, int pageNum);

    List<GroupAccount> findAll();
}