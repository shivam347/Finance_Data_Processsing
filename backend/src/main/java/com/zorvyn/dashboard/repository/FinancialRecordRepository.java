package com.zorvyn.dashboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.zorvyn.dashboard.entity.FinanceRecord;

/* For advanced filtering using JpaSpecificationExecutor 
filter by date, filter by amount range, filter by user, mainly used for dynamic search api
 Without writing sql query , spring write query itself */

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinanceRecord, String>, JpaSpecificationExecutor<FinanceRecord>{

    /* fetch those records which are not deleted , talking about soft delete*/
    Page<FinanceRecord> findByDeleteAtIsNull(Pageable pageable);

    /* fetch only specific user, whose records are not deleted */
    Page<FinanceRecord> findByCreatedByIdAndDeleteAtIsNull(String userId, Pageable pageable);
    
}
