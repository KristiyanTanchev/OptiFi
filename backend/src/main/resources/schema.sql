-- Ensure schema exists
CREATE DATABASE IF NOT EXISTS optifi;

USE optifi;

-- TABLES (idempotent)
CREATE TABLE IF NOT EXISTS users
(
    id            bigint auto_increment primary key,
    username      varchar(32)                                                        not null,
    password_hash varchar(255)                                                       null,
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    provider_subject VARCHAR(128) NULL,
    email         varchar(100)                                                       not null,
    base_currency enum ('EUR', 'USD')                                                not null,
    locale        enum ('BG_BG', 'EN_GB', 'EN_US')                                   not null,
    time_zone_id  varchar(64)                                                        not null DEFAULT 'Europe/Sofia',
    role          enum ('ADMIN', 'BLOCKED', 'MODERATOR', 'USER', 'WAITING_APPROVAL') null,
    created_at    datetime(6)                                                        not null,
    updated_at    datetime(6)                                                        null,
    constraint uk_users_email unique (email),
    constraint uk_users_username unique (username),
    constraint uk_provider_subject unique (auth_provider, provider_subject)
);

CREATE TABLE IF NOT EXISTS accounts
(
    id          bigint auto_increment primary key,
    user_id     bigint                not null,
    name        varchar(32)           not null,
    currency    enum ('EUR', 'USD')   not null,
    type        enum ('BANK', 'CASH') not null,
    institution varchar(100)          null,
    created_at  datetime(6)           not null,
    updated_at  datetime(6)           null,
    archived    bit                   null,
    constraint uk_accounts_user_name unique (user_id, name),
    constraint accounts_users_id_fk
        foreign key (user_id) references users (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    icon        VARCHAR(255) NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NULL,
    user_id     BIGINT       NULL,
    -- Option A: enforce unique global names + unique per-user names
    -- (collapses NULL user_id into -1 for uniqueness checks)
    user_key    BIGINT GENERATED ALWAYS AS (COALESCE(user_id, -1)) STORED,

    CONSTRAINT uk_categories_userkey_name UNIQUE (user_key, name),
    CONSTRAINT categories_users_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS transactions
(
    id          bigint auto_increment primary key,
    account_id  bigint         not null,
    amount      decimal(19, 4) not null,
    description varchar(255)   null,
    category_id bigint         null,
    occurred_at datetime(6)    not null,
    created_at  datetime(6)    not null,
    updated_at  datetime(6)    null,
    constraint transactions_accounts_id_fk
        foreign key (account_id) references accounts (id),
    constraint transactions_categories_id_fk
        foreign key (category_id) references categories (id)
);

-- INDEXES (create only if missing)

-- accounts(archived)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'accounts'
    AND index_name = 'idx_accounts_archived');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_accounts_archived ON accounts (archived)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- accounts(user_id)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'accounts'
    AND index_name = 'idx_accounts_user_id');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_accounts_user_id ON accounts (user_id)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- transactions(account_id, occurred_at)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'transactions'
    AND index_name = 'idx_transactions_account_occurred');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_transactions_account_occurred ON transactions (account_id, occurred_at)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- users(email)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'users'
    AND index_name = 'idx_users_email');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_users_email ON users (email)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- users(username)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'users'
    AND index_name = 'idx_users_username');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_users_username ON users (username)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- transactions(category_id)
SET @idx := (SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
    AND table_name = 'transactions'
    AND index_name = 'idx_transactions_category_id');
SET @sql := IF(@idx = 0,
    'CREATE INDEX idx_transactions_category_id ON transactions (category_id)',
    'SELECT 1'
    );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
