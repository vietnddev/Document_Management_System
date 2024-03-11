package com.flowiee.dms.repository;

import com.flowiee.dms.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    List<AccountRole> findByAccountId(Integer accountId);

    @Query("from AccountRole where accountId=:accountId and module=:module and action=:action")
    AccountRole isAuthorized(@Param("accountId") Integer accountId, @Param("module") String module, @Param("action") String action);

    @Modifying
    void deleteByAccountId(Integer accountId);
}