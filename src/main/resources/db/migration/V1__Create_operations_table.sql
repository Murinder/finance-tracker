-- src/main/resources/db/migration/V1__Create_operations_table.sql

CREATE TABLE operations (
                            id BIGSERIAL PRIMARY KEY,
                            amount NUMERIC(38, 2) NOT NULL,
                            category VARCHAR(255),
                            type VARCHAR(50) NOT NULL, -- 'INCOME' or 'EXPENSE'
                            date TIMESTAMP WITH TIME ZONE NOT NULL,
                            currency VARCHAR(10) NOT NULL
);