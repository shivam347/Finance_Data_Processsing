package com.zorvyn.dashboard.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.dashboard.dto.record.CreateRecordRequest;
import com.zorvyn.dashboard.dto.record.RecordResponse;
import com.zorvyn.dashboard.dto.record.UpdateRecordRequest;
import com.zorvyn.dashboard.service.FinancialRecordService;
import com.zorvyn.dashboard.utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Endpoints for managing financial records")
public class FinanceRecordController {

    private final FinancialRecordService recordService;

    /*
     * First controller is to get all records based on role like viewer can access
     * it own transaction records
     * and admin and analyst can view all records
     */
    @GetMapping
    @Operation(summary = "List Records (VIEWER see only their own data , ANALYST/ADMIN can view all records")
    public ResponseEntity<ApiResponse<Page<RecordResponse>>> getRecords(Authentication auth,
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        return ResponseEntity.ok(ApiResponse
                .success(recordService.getRecords(auth.getName(), pageable, category, type, fromDate, toDate)));

    }

    /* Controller to get Records based on the id means to get single record by id */
    @GetMapping("/{id}")
    @Operation(summary = "Get Single Record")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(recordService.getRecordById(id, auth.getName())));

    }

    /* Controller to create Record Only for ADMIN */
    @PostMapping
    @Operation(summary = "Create Record (Only By ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(@Valid @RequestBody CreateRecordRequest request,
            Authentication auth) {

        return ResponseEntity.ok(ApiResponse.success(recordService.createRecord(request, auth.getName()),
                "Created Record Successfully"));

    }


    /* Controller for Updating record Only By Admin */
    @PutMapping("/{id}")
    @Operation(summary = "Update a record (ADMIN Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(@PathVariable String id, @Valid @RequestBody UpdateRecordRequest request) {

        return ResponseEntity.ok(ApiResponse.success(recordService.updateRecord(id, request),
                "Record updated Successfully"));

    }

    /* Controller for soft delete record by admin */
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft deletion Operation performed by Admin only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable String id){

        recordService.deleteRecord(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Record Deleted"));

    }
}
