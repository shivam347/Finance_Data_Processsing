package com.zorvyn.dashboard.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.zorvyn.dashboard.dto.dashboard.CategoryTotalResponse;
import com.zorvyn.dashboard.dto.dashboard.MonthlyTrendResponse;
import com.zorvyn.dashboard.dto.dashboard.SummaryResponse;
import com.zorvyn.dashboard.dto.record.RecordResponse;
import com.zorvyn.dashboard.entity.FinanceRecord;
import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.TransactionType;
import com.zorvyn.dashboard.exception.ApiException;
import com.zorvyn.dashboard.repository.FinancialRecordRepository;
import com.zorvyn.dashboard.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor /* create constructor of final fields so no need for autowired annotation */
public class DashboardService {

    private final UserRepository userRepo;
    private final FinancialRecordRepository recordRepo;

    /*
     * This method will be used as private helper method
     * return the list of records of currently logged in user + role based also
     * we need this logic for summary, category , trends as for every dashboard
     * we need to check role , deleted filter etc
     */
    private List<FinanceRecord> getAccessibleRecords(String userEmail) {

        // fetch the current logged in user
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        /* User predicates and specification for queries */
        Specification<FinanceRecord> spec = (root, query, cb) -> {
            // Create arrayList of PREDICATE
            List<Predicate> pred = new ArrayList<>();

            // Now check soft delete should be null then only allow
            pred.add(cb.isNull(root.get("deleteAt")));

            // Fetch the records based on the role type to display on dashboard
            if (Role.VIEWER.equals(user.getRole())) {
                pred.add(cb.equal(root.get("createdBy").get("id"), user.getId()));
            }

            return cb.and(pred.toArray(new Predicate[0]));
        };

        return recordRepo.findAll(spec);
    }

    /* Method to get the summary Response */
    public SummaryResponse getSummary(String email) {

        // First fetch all the records belongs to this email
        List<FinanceRecord> records = getAccessibleRecords(email);

        // Now create two variable for income and expense
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        // Now apply for each loop on Finance Records to calculate the income and
        // expense
        for (FinanceRecord record : records) {

            if (record.getType() == TransactionType.INCOME) {
                income = income.add(record.getAmount());
            } else if (record.getType() == TransactionType.EXPENSE) {
                expense = expense.add(record.getAmount());
            }
        }

        return SummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .netBalance(income.subtract(expense))
                .build();

    }

    /* Method for getTotalsByCategory */
    public List<CategoryTotalResponse> getTotalsByCategory(String email) {

        // First fetch all the allowed records for the current logged in user
        List<FinanceRecord> records = getAccessibleRecords(email);

        /*
         * Now we have to group records based on category , transaction type and add
         * amount of it
         * records are -> Food, EXPENSE, 5000 -> AFTER GROUPING IT BECOMES
         * Food -> EXPENSE -> 5000 , Create category wise totals
         */

        Map<String, Map<TransactionType, BigDecimal>> grouped = records.stream()
                .collect(Collectors.groupingBy(FinanceRecord::getCategory,
                        Collectors.groupingBy(FinanceRecord::getType,
                                Collectors.reducing(BigDecimal.ZERO, FinanceRecord::getAmount, BigDecimal::add))));

        /*
         * I don't want to return map , our return type is List of Category
         * TotalResponse like
         * {category: "Food", type: EXPENSE, total: 5000}
         */
        List<CategoryTotalResponse> responses = new ArrayList<>();
        grouped.forEach((category, typeMap) -> {
            typeMap.forEach((type, total) -> {
                responses.add(new CategoryTotalResponse(category, type, total));
            });
        });

        return responses;
    }

    /* Method will show recent transactions list on the dashboard */
    public Page<RecordResponse> getRecentActivity(String email) {

        // First fetch the user from the db
        User user = userRepo.findById(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        /*
         * Creates page and sort settings , page number, page size means no of records
         * and sorting, sort is done desc
         * to get the latest records
         */
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "recordDate", "createdAt"));

        // if user is VIEWER then fetch only its records , otherwise give records of all, exclude soft deleted one
        if (Role.VIEWER.equals(user.getRole())) {

            return recordRepo.findByCreatedByIdAndDeleteAtIsNull(user.getId(), pageRequest)
                    .map(RecordResponse::fromEntity);
        } else {

            return recordRepo.findByDeleteAtIsNull(pageRequest).map(RecordResponse::fromEntity);
        }

    }


    /* Method for Monthly trends like in jan month how much was the earning 
    and how much was the expense,  provide month(01) - income - Expense */
    public List<MonthlyTrendResponse> getMonthlyTrends(String userEmail) {
        List<FinanceRecord> records = getAccessibleRecords(userEmail);

        // Simplified grouping by month "YYYY-MM"
        Map<String, Map<TransactionType, BigDecimal>> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRecordDate().getYear() + "-"
                                + String.format("%02d", r.getRecordDate().getMonthValue()),
                        Collectors.groupingBy(FinanceRecord::getType,
                                Collectors.reducing(BigDecimal.ZERO, FinanceRecord::getAmount, BigDecimal::add))));

        /* send the output into our desired format not in the map */
        List<MonthlyTrendResponse> responses = new ArrayList<>();
        grouped.forEach((month, typeMap) -> {
            BigDecimal income = typeMap.getOrDefault(TransactionType.INCOME, BigDecimal.ZERO); // IF no income on particular month then set it to zero
            BigDecimal expense = typeMap.getOrDefault(TransactionType.EXPENSE, BigDecimal.ZERO); // If no expense on particular month then set it to zero
            responses.add(new MonthlyTrendResponse(month, income, expense));
        });

        // Sort by month ascending
        responses.sort((a, b) -> a.getMonth().compareTo(b.getMonth())); // Sort the  Month in ascending order
        return responses;
    }
}
