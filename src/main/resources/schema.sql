-- Ensure schema exists
CREATE DATABASE IF NOT EXISTS optifi;
-- (Optional) If your connection doesn't already use optifi:
-- USE optifi;

-- TABLES (idempotent)
CREATE TABLE IF NOT EXISTS optifi.users
(
    created_at    datetime(6)                                                        not null,
    id            bigint auto_increment primary key,
    updated_at    datetime(6)                                                        null,
    username      varchar(32)                                                        not null,
    email         varchar(100)                                                       not null,
    password_hash varchar(255)                                                       not null,
    base_currency enum ('EUR', 'USD')                                                not null,
    locale        enum ('BG_BG', 'EN_GB', 'EN_US')                                   not null,
    role          enum ('ADMIN', 'BLOCKED', 'MODERATOR', 'USER', 'WAITING_APPROVAL') null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email),
    constraint UKr43af9ap4edm43mmtq01oddj6 unique (username)
);

CREATE TABLE IF NOT EXISTS optifi.accounts
(
    archived    bit                   not null,
    created_at  datetime(6)           not null,
    id          bigint auto_increment primary key,
    updated_at  datetime(6)           null,
    user_id     bigint                not null,
    name        varchar(32)           not null,
    institution varchar(100)          null,
    currency    enum ('EUR', 'USD')   not null,
    type        enum ('BANK', 'CASH') not null,
    constraint uk_user_account_name unique (user_id, name),
    constraint FKnjuop33mo69pd79ctplkck40n
        foreign key (user_id) references optifi.users (id)
);

CREATE TABLE IF NOT EXISTS optifi.transactions
(
    amount      decimal(19, 4) not null,
    account_id  bigint         not null,
    created_at  datetime(6)    not null,
    id          bigint auto_increment primary key,
    occurred_at datetime(6)    not null,
    updated_at  datetime(6)    null,
    description varchar(255)   null,
    constraint FK20w7wsg13u9srbq3bd7chfxdh
        foreign key (account_id) references optifi.accounts (id)
);

-- INDEXES (create only if missing)

-- optifi.accounts(archived)
SET @idx := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
      AND table_name   = 'accounts'
      AND index_name   = 'idx_accounts_archived'
);
SET @sql := IF(@idx = 0,
               'CREATE INDEX idx_accounts_archived ON optifi.accounts (archived)',
               'SELECT 1'
            );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- optifi.accounts(user_id)
SET @idx := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
      AND table_name   = 'accounts'
      AND index_name   = 'idx_accounts_user_id'
);
SET @sql := IF(@idx = 0,
               'CREATE INDEX idx_accounts_user_id ON optifi.accounts (user_id)',
               'SELECT 1'
            );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- optifi.transactions(account_id, occurred_at)
SET @idx := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
      AND table_name   = 'transactions'
      AND index_name   = 'idx_transactions_account_occurred'
);
SET @sql := IF(@idx = 0,
               'CREATE INDEX idx_transactions_account_occurred ON optifi.transactions (account_id, occurred_at)',
               'SELECT 1'
            );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- optifi.users(email)
SET @idx := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
      AND table_name   = 'users'
      AND index_name   = 'idx_users_email'
);
SET @sql := IF(@idx = 0,
               'CREATE INDEX idx_users_email ON optifi.users (email)',
               'SELECT 1'
            );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- optifi.users(username)
SET @idx := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = 'optifi'
      AND table_name   = 'users'
      AND index_name   = 'idx_users_username'
);
SET @sql := IF(@idx = 0,
               'CREATE INDEX idx_users_username ON optifi.users (username)',
               'SELECT 1'
            );
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
