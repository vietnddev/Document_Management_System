package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailStatusRepository extends JpaRepository<MailStatus, Long> {
}