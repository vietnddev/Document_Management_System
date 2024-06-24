package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.AccountRole;
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

    List<AccountRole> findByGroupId(Integer accountId);

    @Query("from AccountRole " +
            "where 1=1 " +
            "and (:groupId is null or groupId=:groupId) " +
            "and (:accountId is null or accountId=:accountId) " +
            "and module=:module " +
            "and action=:action")
    AccountRole isAuthorized(@Param("groupId") Integer groupId,
                             @Param("accountId") Integer accountId,
                             @Param("module") String module,
                             @Param("action") String action);

    @Modifying
    void deleteByAccountId(Integer accountId);

    @Query("from AccountRole r " +
            "where 1=1 " +
            "and (:module is null or r.module=:module) " +
            "and (:action is null or r.action=:action)")
    List<AccountRole> findByModuleAndAction(@Param("module") String module, @Param("action") String action);
}