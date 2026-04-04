package com.zorvyn.dashboard.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import com.zorvyn.dashboard.dto.record.CreateRecordRequest;
import com.zorvyn.dashboard.dto.record.RecordResponse;
import com.zorvyn.dashboard.dto.record.UpdateRecordRequest;
import com.zorvyn.dashboard.entity.FinanceRecord;
import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.exception.ApiException;
import com.zorvyn.dashboard.repository.FinancialRecordRepository;
import com.zorvyn.dashboard.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final UserRepository userRepo;
    private final FinancialRecordRepository recordRepo;

    /*
     * Method to get Records but also apply advanced filtering on it, to avoid write
     * complex queries inside financialRecordRespository
     * return type is Page<RecordResponse> because it will return multiple records
     * not single records
     */
    public Page<RecordResponse> getRecords(String email, Pageable pageable, String category, String type,
            String fromDate, String toDate) {

        // First I need to fetch user from the db
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        /*
         * I will use specification and predicates for filtering , these comes from
         * spring data jpa
         * these are data driven design patterns, root is the access point here it is
         * FinancialRecordEntity
         * , No use of query but passed to follow the interface method signature and cb
         * is the criteria Builder, provide isNull, equal methods
         */
        Specification<FinanceRecord> spec = (root, query, cb) -> {
            /*
             * Predicates are the conditions so we will have condition as array list just
             * like where clause condition in mysql
             */
            List<Predicate> pred = new ArrayList<>();

            /*
             * soft deleted user should also not be displayed, means exclude soft deleted
             * user
             */
            pred.add(cb.isNull(root.get("deleteAt")));

            // Now check the user role , if role is viewer then it can only view its own
            // financial data
            if (Role.VIEWER.equals(user.getRole())) {
                pred.add(cb.equal(root.get("createdBy").get("id"), user.getId()));
            }

            /* Optional filters */

            if (category != null && !category.isEmpty()) {
                pred.add(cb.equal(root.get("category"), category));
            }

            /*
             * Type is Enum type but passed here as string so i have to convert string to
             * enum type for comparision
             */
            if (type != null && !type.isEmpty()) {
                pred.add(cb.equal(root.get("type"),
                        com.zorvyn.dashboard.enums.TransactionType.valueOf(type.toUpperCase())));
            }

            if (fromDate != null && !fromDate.isEmpty()) {
                pred.add(cb.greaterThanOrEqualTo(root.get("createdAt"), java.time.LocalDate.parse(fromDate)));
            }

            if (toDate != null && !toDate.isEmpty()) {
                pred.add(cb.lessThanOrEqualTo(root.get("createdAt"), java.time.LocalDate.parse(toDate)));
            }

            return cb.and(pred.toArray(new Predicate[0]));

        };

        /* Now we have to map the response according to the RecordResponse */
        return recordRepo.findAll(spec, pageable).map(RecordResponse::fromEntity);

    }

    /* Method to get the record by id */
    public RecordResponse getRecordById(String id, String email) {

        // First fetch the user from db using email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found!"));

        /* Fetch the record using record id */
        FinanceRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "record not found!!"));

        /* We also need to check the record is soft deleted or not */
        if (record.getDeleteAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "user not found");
        }

        /*
         * We also need to make sure viewer profile can view only its own record not
         * some-else ,so
         * first check profile is viewer or not and if profile is viewer then check
         * record user id by createdBy not equal to user id then throw exception of
         * Forbidden
         */
        if (Role.VIEWER.equals(user.getRole()) && !record.getCreatedBy().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access is denied");
        }

        return RecordResponse.fromEntity(record);

    }

    /* Method to create records Only for admin */
    public RecordResponse createRecord(CreateRecordRequest request, String email) {
        // First fetch the user from the db
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        // Create new Financial Record to store the information
        FinanceRecord record = new FinanceRecord();

        record.setAmount(request.getAmount());
        record.setCategory(request.getCategory());
        record.setType(request.getType());
        record.setCreatedBy(user);
        record.setNotes(request.getNotes());
        record.setRecordDate(request.getRecordDate());

        return RecordResponse.fromEntity(recordRepo.save(record));

    }

    /*
     * Method to update the record only for admin, there is no user is creating so
     * simply pass the record id which you want to update and updaterecordRequest
     */
    public RecordResponse updateRecord(String id, UpdateRecordRequest request) {

        // First fetch the record using id from the db
        FinanceRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        // Also check record is softdeleted or not
        if (record.getDeleteAt() != null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "user not found");
        }

        if (record.getAmount() != null) {
            record.setAmount(request.getAmount());
        }

        if (record.getCategory() != null) {
            record.setCategory(request.getCategory());
        }

        if (record.getType() != null) {
            record.setType(request.getType());
        }

        if (record.getRecordDate() != null) {
            record.setRecordDate(request.getRecordDate());
        }

        if (record.getNotes() != null) {
            record.setNotes(request.getNotes());
        }

        return RecordResponse.fromEntity(recordRepo.save(record));

    }

    /*
     * I have On delete restrict inside financial record means i cannot delete the
     * user
     * if its transaction record is there
     */

    /* Method to perform Soft delete operation on record only for Admin Role */
    public void deleteRecord(String id) {

        // First get the record using id from db
        FinanceRecord record = recordRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "record not found"));

        record.setDeleteAt(LocalDateTime.now());

        recordRepo.save(record);

    }

}
