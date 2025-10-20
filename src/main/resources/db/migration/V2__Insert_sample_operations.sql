-- src/main/resources/db/migration/V2__Insert_sample_operations.sql

INSERT INTO operations (amount, category, type, date, currency) VALUES
                                                                         (50000.00, 'Зарплата', 'INCOME', '2025-09-01 09:00:00+03', 'RUB'),
                                                                         (1500.00, 'Продукты', 'EXPENSE', '2025-09-05 18:30:00+03', 'RUB'),
                                                                         (2000.00, 'Аренда', 'EXPENSE', '2025-09-10 12:00:00+03', 'RUB'),
                                                                         (100.00, 'Кофе', 'EXPENSE', '2025-09-15 10:15:00+03', 'RUB'),
                                                                         (75.50, 'Книга', 'EXPENSE', '2025-09-20 14:45:00+03', 'USD'),
                                                                         (1000.00, 'Фриланс', 'INCOME', '2025-09-25 20:00:00+03', 'USD');