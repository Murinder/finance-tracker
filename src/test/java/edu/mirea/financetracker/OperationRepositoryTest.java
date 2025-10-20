package edu.mirea.financetracker;

import edu.mirea.financetracker.entity.Operation;
import edu.mirea.financetracker.repository.OperationRepository;
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
    void findByTimestampBetween() {
        Operation op1 = new Operation(null, new BigDecimal("100"), "Test", "EXPENSE", OffsetDateTime.now().minusDays(2), "RUB");
        Operation op2 = new Operation(null, new BigDecimal("200"), "Test", "INCOME", OffsetDateTime.now().minusHours(1), "RUB");

        repository.saveAll(List.of(op1, op2));

        var from = OffsetDateTime.now().minusDays(1);
        var to = OffsetDateTime.now();

        List<Operation> result = repository.findByDateBetween(from, to);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("200");
    }

    @Test
    void getBalanceByCurrency() {
        repository.save(new Operation(null, new BigDecimal("300"), "Salary", "INCOME", OffsetDateTime.now(), "RUB"));
        repository.save(new Operation(null, new BigDecimal("100"), "Food", "EXPENSE", OffsetDateTime.now(), "RUB"));

        List<Object[]> balances = repository.getBalanceByCurrency();
        assertThat(balances).hasSize(1);
        assertThat((String) balances.get(0)[0]).isEqualTo("RUB");
        assertThat((BigDecimal) balances.get(0)[1]).isEqualByComparingTo("200");
    }
}