package edu.mirea.financetracker.repository;

import edu.mirea.financetracker.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    // Фильтрация по периоду
    List<Operation> findByDateBetween(OffsetDateTime from, OffsetDateTime to);

    // Сумма доходов за период
    @Query("SELECT SUM(o.amount) FROM Operation o WHERE o.type = 'INCOME' AND o.date BETWEEN :from AND :to")
    BigDecimal getTotalIncome(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    // Сумма расходов за период
    @Query("SELECT SUM(o.amount) FROM Operation o WHERE o.type = 'EXPENSE' AND o.date BETWEEN :from AND :to")
    BigDecimal getTotalExpense(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    // Баланс по валюте
    @Query("SELECT o.currency, SUM(CASE WHEN o.type = 'INCOME' THEN o.amount ELSE -o.amount END) " +
            "FROM Operation o GROUP BY o.currency")
    List<Object[]> getBalanceByCurrency();
}