CREATE TABLE financial_records (
    id          VARCHAR(36)  PRIMARY KEY,
    amount      DECIMAL(15,2)     NOT NULL,  -- stored as rupees
    type        ENUM('INCOME','EXPENSE') NOT NULL,
    category    VARCHAR(100) NOT NULL,
    record_date DATE         NOT NULL,
    notes       TEXT,
    created_by  VARCHAR(36)  NOT NULL,
    deleted_at  DATETIME     NULL,      -- for soft delete
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);
