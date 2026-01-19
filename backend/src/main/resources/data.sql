/* ============================================================
   OptiFI SEED ONLY (assumes schema already exists)
   Seeds: users, accounts, categories, transactions + category assignment
   Re-runnable: INSERT IGNORE + deletes only [seed] transactions
   ============================================================ */

USE optifi;

-- ------------------------------------------------------------
-- USERS
-- ------------------------------------------------------------
INSERT IGNORE INTO users (created_at, updated_at, username, email, password_hash, base_currency, locale, role)
VALUES (NOW(6), NOW(6), 'admin', 'admin@optifi.test', '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K',
        'EUR', 'EN_GB', 'ADMIN'),
       (NOW(6), NOW(6), 'moderator', 'moderator@optifi.test',
        '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'MODERATOR'),
       (NOW(6), NOW(6), 'kristiyan', 'kristiyan@optifi.test',
        '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'USER'),
       (NOW(6), NOW(6), 'maria', 'maria@optifi.test', '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K',
        'EUR', 'EN_GB', 'USER'),
       (NOW(6), NOW(6), 'blocked', 'blocked@optifi.test',
        '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'BLOCKED'),
       (NOW(6), NOW(6), 'waiting', 'waiting@optifi.test',
        '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'WAITING_APPROVAL');

-- ------------------------------------------------------------
-- ACCOUNTS (unique(user_id, name) so INSERT IGNORE works)
-- ------------------------------------------------------------
INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Main EUR', 'UniCredit', 'EUR', 'BANK'
FROM users u WHERE u.username = 'admin';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Cash', NULL, 'EUR', 'CASH'
FROM users u WHERE u.username = 'admin';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Revolut EUR', 'Revolut', 'EUR', 'BANK'
FROM users u WHERE u.username = 'kristiyan';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Wallet', NULL, 'EUR', 'CASH'
FROM users u WHERE u.username = 'kristiyan';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0, NOW(6), NOW(6), u.id, 'Salary USD', 'DSK', 'USD', 'BANK'
FROM users u WHERE u.username = 'maria';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 1, NOW(6), NOW(6), u.id, 'Old Cash', NULL, 'USD', 'CASH'
FROM users u WHERE u.username = 'maria';

-- ------------------------------------------------------------
-- DEFAULT CATEGORIES (global: user_id IS NULL)
-- ------------------------------------------------------------
INSERT IGNORE INTO categories (name, description, icon, created_at, updated_at, user_id)
VALUES
    ('Income',        'Salary and other income',        'income',        NOW(6), NOW(6), NULL),
    ('Groceries',     'Food and household items',       'groceries',     NOW(6), NOW(6), NULL),
    ('Subscriptions', 'Recurring subscriptions',        'subscriptions', NOW(6), NOW(6), NULL),
    ('Bills',         'Rent, utilities, services',      'bills',         NOW(6), NOW(6), NULL),
    ('Eating Out',    'Restaurants, coffee, takeout',   'eating_out',    NOW(6), NOW(6), NULL);

-- ------------------------------------------------------------
-- TRANSACTIONS (re-runnable: delete only [seed] ones)
-- ------------------------------------------------------------
DELETE FROM transactions
WHERE description LIKE '[seed] %';

-- Convenience: join global default categories once per insert
-- kristiyan - Revolut EUR
INSERT INTO transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       CASE
           WHEN t.amount > 0 OR LOWER(t.description) LIKE '%salary%' THEN c_income.id
           WHEN LOWER(t.description) LIKE '%grocer%' OR LOWER(t.description) LIKE '%lidl%' THEN c_groceries.id
           WHEN LOWER(t.description) LIKE '%netflix%' THEN c_subs.id
           WHEN LOWER(t.description) LIKE '%utilit%' THEN c_bills.id
           WHEN LOWER(t.description) LIKE '%coffee%' OR LOWER(t.description) LIKE '%dinner%' THEN c_eat.id
           ELSE c_eat.id
           END AS category_id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN categories c_income    ON c_income.user_id IS NULL AND c_income.name = 'Income'
         JOIN categories c_groceries ON c_groceries.user_id IS NULL AND c_groceries.name = 'Groceries'
         JOIN categories c_subs      ON c_subs.user_id IS NULL AND c_subs.name = 'Subscriptions'
         JOIN categories c_bills     ON c_bills.user_id IS NULL AND c_bills.name = 'Bills'
         JOIN categories c_eat       ON c_eat.user_id IS NULL AND c_eat.name = 'Eating Out'
         JOIN (
    SELECT 2500.00 AS amount, (NOW(6) - INTERVAL 25 DAY) AS occurred_at, 'Salary' AS description
    UNION ALL SELECT -55.40, (NOW(6) - INTERVAL 20 DAY), 'Groceries - Lidl'
    UNION ALL SELECT -12.99, (NOW(6) - INTERVAL 18 DAY), 'Netflix'
    UNION ALL SELECT -120.00, (NOW(6) - INTERVAL 14 DAY), 'Utilities'
    UNION ALL SELECT -6.80,  (NOW(6) - INTERVAL 3 DAY),  'Coffee'
    UNION ALL SELECT -34.50, (NOW(6) - INTERVAL 1 DAY),  'Dinner'
) t
WHERE u.username = 'kristiyan'
  AND a.name = 'Revolut EUR';

-- kristiyan - Wallet (cash)
INSERT INTO transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       CASE
           WHEN t.amount > 0 THEN c_income.id
           WHEN LOWER(t.description) LIKE '%atm%' THEN c_bills.id
           WHEN LOWER(t.description) LIKE '%snack%' THEN c_eat.id
           WHEN LOWER(t.description) LIKE '%taxi%' THEN c_eat.id
           ELSE c_eat.id
           END AS category_id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN categories c_income ON c_income.user_id IS NULL AND c_income.name = 'Income'
         JOIN categories c_bills  ON c_bills.user_id IS NULL AND c_bills.name = 'Bills'
         JOIN categories c_eat    ON c_eat.user_id IS NULL AND c_eat.name = 'Eating Out'
         JOIN (
    SELECT -10.00 AS amount, (NOW(6) - INTERVAL 7 DAY) AS occurred_at, 'Taxi (cash)' AS description
    UNION ALL SELECT -22.30, (NOW(6) - INTERVAL 6 DAY), 'Snacks'
    UNION ALL SELECT  50.00, (NOW(6) - INTERVAL 5 DAY), 'ATM withdrawal'
) t
WHERE u.username = 'kristiyan'
  AND a.name = 'Wallet';

-- maria - Salary USD
INSERT INTO transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       CASE
           WHEN t.amount > 0 OR LOWER(t.description) LIKE '%salary%' THEN c_income.id
           WHEN LOWER(t.description) LIKE '%grocer%' THEN c_groceries.id
           WHEN LOWER(t.description) LIKE '%spotify%' THEN c_subs.id
           WHEN LOWER(t.description) LIKE '%rent%' THEN c_bills.id
           ELSE c_bills.id
           END AS category_id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN categories c_income    ON c_income.user_id IS NULL AND c_income.name = 'Income'
         JOIN categories c_groceries ON c_groceries.user_id IS NULL AND c_groceries.name = 'Groceries'
         JOIN categories c_subs      ON c_subs.user_id IS NULL AND c_subs.name = 'Subscriptions'
         JOIN categories c_bills     ON c_bills.user_id IS NULL AND c_bills.name = 'Bills'
         JOIN (
    SELECT 3200.00 AS amount, (NOW(6) - INTERVAL 28 DAY) AS occurred_at, 'Monthly salary' AS description
    UNION ALL SELECT -800.00, (NOW(6) - INTERVAL 16 DAY), 'Rent'
    UNION ALL SELECT -65.10,  (NOW(6) - INTERVAL 9 DAY),  'Groceries'
    UNION ALL SELECT -19.99,  (NOW(6) - INTERVAL 2 DAY),  'Spotify'
) t
WHERE u.username = 'maria'
  AND a.name = 'Salary USD';

-- admin - Main EUR
INSERT INTO transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       CASE
           WHEN t.amount > 0 OR LOWER(t.description) LIKE '%funding%' THEN c_income.id
           WHEN LOWER(t.description) LIKE '%domain%' THEN c_bills.id
           ELSE c_bills.id
           END AS category_id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN categories c_income ON c_income.user_id IS NULL AND c_income.name = 'Income'
         JOIN categories c_bills  ON c_bills.user_id IS NULL AND c_bills.name = 'Bills'
         JOIN (
    SELECT 10000.00 AS amount, (NOW(6) - INTERVAL 60 DAY) AS occurred_at, 'Initial funding' AS description
    UNION ALL SELECT -99.00,  (NOW(6) - INTERVAL 10 DAY), 'Domain renewal'
) t
WHERE u.username = 'admin'
  AND a.name = 'Main EUR';

-- ------------------------------------------------------------
-- OPTIONAL: CATEGORY ASSIGNMENT (defensive, seed only)
-- ------------------------------------------------------------
UPDATE transactions t
    JOIN categories c_income    ON c_income.user_id IS NULL AND c_income.name = 'Income'
    JOIN categories c_groceries ON c_groceries.user_id IS NULL AND c_groceries.name = 'Groceries'
    JOIN categories c_subs      ON c_subs.user_id IS NULL AND c_subs.name = 'Subscriptions'
    JOIN categories c_bills     ON c_bills.user_id IS NULL AND c_bills.name = 'Bills'
    JOIN categories c_eat       ON c_eat.user_id IS NULL AND c_eat.name = 'Eating Out'
SET t.category_id =
        CASE
            WHEN t.amount > 0
                OR LOWER(t.description) LIKE '%salary%'
                OR LOWER(t.description) LIKE '%funding%'
                THEN c_income.id
            WHEN LOWER(t.description) LIKE '%grocer%'
                OR LOWER(t.description) LIKE '%lidl%'
                THEN c_groceries.id
            WHEN LOWER(t.description) LIKE '%netflix%'
                OR LOWER(t.description) LIKE '%spotify%'
                THEN c_subs.id
            WHEN LOWER(t.description) LIKE '%utilit%'
                OR LOWER(t.description) LIKE '%rent%'
                OR LOWER(t.description) LIKE '%domain%'
                THEN c_bills.id
            WHEN LOWER(t.description) LIKE '%coffee%'
                OR LOWER(t.description) LIKE '%dinner%'
                OR LOWER(t.description) LIKE '%snack%'
                OR LOWER(t.description) LIKE '%restaurant%'
                OR LOWER(t.description) LIKE '%taxi%'
                THEN c_eat.id
            ELSE c_eat.id
            END
WHERE t.description LIKE '[seed] %';

