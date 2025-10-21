package edu.mirea.financetracker.repository;

import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OperationRepositoryTest {

    @Autowired
    private OperationRepository repository;

    @Test
    void findByTypeAndTimestampBetween() {
        OffsetDateTime now = OffsetDateTime.now();
        repository.save(new Operation(null, new BigDecimal("100"), "Test", OperationType.EXPENSE, now.minusDays(2), "RUB"));
        repository.save(new Operation(null, new BigDecimal("200"), "Test", OperationType.EXPENSE, now.minusHours(1), "RUB"));
        repository.save(new Operation(null, new BigDecimal("300"), "Test", OperationType.INCOME, now.minusHours(1), "RUB"));

        List<Operation> expenses = repository.findByTypeAndDateBetween(OperationType.EXPENSE, now.minusDays(1), now);
        assertThat(expenses).hasSize(1);
        assertThat(expenses.getFirst().getAmount()).isEqualByComparingTo("200");
    }
}