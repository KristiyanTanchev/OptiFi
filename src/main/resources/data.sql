-- USERS
INSERT IGNORE INTO optifi.users (
    created_at, updated_at, username, email, password_hash, base_currency, locale, role
) VALUES
      (NOW(6), NOW(6), 'admin',     'admin@optifi.test',     '$2a$10$adminhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'EUR', 'EN_GB', 'ADMIN'),
      (NOW(6), NOW(6), 'moderator', 'moderator@optifi.test', '$2a$10$modhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'USD', 'EN_US', 'MODERATOR'),
      (NOW(6), NOW(6), 'kristiyan', 'kristiyan@optifi.test', '$2a$10$userhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'EUR', 'BG_BG', 'USER'),
      (NOW(6), NOW(6), 'maria',     'maria@optifi.test',     '$2a$10$userhash2xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','EUR', 'EN_GB', 'USER'),
      (NOW(6), NOW(6), 'blocked',   'blocked@optifi.test',   '$2a$10$blockedhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','USD','EN_US','BLOCKED'),
      (NOW(6), NOW(6), 'waiting',   'waiting@optifi.test',   '$2a$10$waitinghashxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','EUR','BG_BG','WAITING_APPROVAL');

-- ACCOUNTS (note unique(user_id, name))
INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Main EUR', 'UniCredit', 'EUR', 'BANK'
FROM optifi.users u WHERE u.username='admin';

INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Cash', NULL, 'EUR', 'CASH'
FROM optifi.users u WHERE u.username='admin';

INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Revolut EUR', 'Revolut', 'EUR', 'BANK'
FROM optifi.users u WHERE u.username='kristiyan';

INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Wallet', NULL, 'EUR', 'CASH'
FROM optifi.users u WHERE u.username='kristiyan';

INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Salary USD', 'DSK', 'USD', 'BANK'
FROM optifi.users u WHERE u.username='maria';

INSERT IGNORE INTO optifi.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 1, NOW(6), NOW(6), u.id, 'Old Cash', NULL, 'USD', 'CASH'
FROM optifi.users u WHERE u.username='maria';

-- TRANSACTIONS
DELETE FROM optifi.transactions
WHERE description LIKE '[seed] %';

-- helper: target specific accounts via join by (username, account name)
-- kristiyan - Revolut EUR
INSERT INTO optifi.transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount, a.id, NOW(6), t.occurred_at, NOW(6), t.description
FROM optifi.accounts a
         JOIN optifi.users u ON u.id = a.user_id
         JOIN (
    SELECT  2500.00 AS amount, (NOW(6) - INTERVAL 25 DAY) AS occurred_at, 'Salary' AS description
    UNION ALL SELECT  -55.40, (NOW(6) - INTERVAL 20 DAY), 'Groceries - Lidl'
    UNION ALL SELECT  -12.99, (NOW(6) - INTERVAL 18 DAY), 'Netflix'
    UNION ALL SELECT -120.00, (NOW(6) - INTERVAL 14 DAY), 'Utilities'
    UNION ALL SELECT   -6.80, (NOW(6) - INTERVAL  3 DAY), 'Coffee'
    UNION ALL SELECT  -34.50, (NOW(6) - INTERVAL  1 DAY), 'Dinner'
) t
WHERE u.username='kristiyan' AND a.name='Revolut EUR';

-- kristiyan - Wallet (cash)
INSERT INTO optifi.transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount, a.id, NOW(6), t.occurred_at, NOW(6), t.description
FROM optifi.accounts a
         JOIN optifi.users u ON u.id = a.user_id
         JOIN (
    SELECT -10.00 AS amount, (NOW(6) - INTERVAL 7 DAY) AS occurred_at, 'Taxi (cash)' AS description
    UNION ALL SELECT -22.30, (NOW(6) - INTERVAL 6 DAY), 'Snacks'
    UNION ALL SELECT  50.00, (NOW(6) - INTERVAL 5 DAY), 'ATM withdrawal'
) t
WHERE u.username='kristiyan' AND a.name='Wallet';

-- maria - Salary USD
INSERT INTO optifi.transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount, a.id, NOW(6), t.occurred_at, NOW(6), t.description
FROM optifi.accounts a
         JOIN optifi.users u ON u.id = a.user_id
         JOIN (
    SELECT  3200.00 AS amount, (NOW(6) - INTERVAL 28 DAY) AS occurred_at, 'Monthly salary' AS description
    UNION ALL SELECT -800.00, (NOW(6) - INTERVAL 16 DAY), 'Rent'
    UNION ALL SELECT  -65.10, (NOW(6) - INTERVAL  9 DAY), 'Groceries'
    UNION ALL SELECT  -19.99, (NOW(6) - INTERVAL  2 DAY), 'Spotify'
) t
WHERE u.username='maria' AND a.name='Salary USD';

-- admin - Main EUR (some audit-ish activity)
INSERT INTO optifi.transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount, a.id, NOW(6), t.occurred_at, NOW(6), t.description
FROM optifi.accounts a
         JOIN optifi.users u ON u.id = a.user_id
         JOIN (
    SELECT 10000.00 AS amount, (NOW(6) - INTERVAL 60 DAY) AS occurred_at, 'Initial funding' AS description
    UNION ALL SELECT  -99.00, (NOW(6) - INTERVAL 10 DAY), 'Domain renewal'
) t
WHERE u.username='admin' AND a.name='Main EUR';

-- Quick sanity check
SELECT u.username, a.name AS account, COUNT(t.id) AS tx_count
FROM optifi.users u
         JOIN optifi.accounts a ON a.user_id = u.id
         LEFT JOIN optifi.transactions t ON t.account_id = a.id
GROUP BY u.username, a.name
ORDER BY u.username, a.name;
