/* ============================================================
   OptiFI SEED ONLY (assumes schema already exists)
   Seeds: users, accounts, categories, transactions + category assignment
   Re-runnable: uses INSERT IGNORE and deletes only [seed] transactions
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
SELECT 0,
       NOW(6),
       NOW(6),
       u.id,
       'Main EUR',
       'UniCredit',
       'EUR',
       'BANK'
FROM users u
WHERE u.username = 'admin';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0,
       NOW(6),
       NOW(6),
       u.id,
       'Cash',
       NULL,
       'EUR',
       'CASH'
FROM users u
WHERE u.username = 'admin';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0,
       NOW(6),
       NOW(6),
       u.id,
       'Revolut EUR',
       'Revolut',
       'EUR',
       'BANK'
FROM users u
WHERE u.username = 'kristiyan';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0,
       NOW(6),
       NOW(6),
       u.id,
       'Wallet',
       NULL,
       'EUR',
       'CASH'
FROM users u
WHERE u.username = 'kristiyan';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 0,
       NOW(6),
       NOW(6),
       u.id,
       'Salary USD',
       'DSK',
       'USD',
       'BANK'
FROM users u
WHERE u.username = 'maria';

INSERT IGNORE INTO accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT 1,
       NOW(6),
       NOW(6),
       u.id,
       'Old Cash',
       NULL,
       'USD',
       'CASH'
FROM users u
WHERE u.username = 'maria';

-- ------------------------------------------------------------
-- CATEGORIES (5 per user) - idempotent via unique(user_id, name)
-- ------------------------------------------------------------
INSERT IGNORE INTO categories (name, description, icon, created_at, updated_at, user_id)
SELECT x.name, x.description, x.icon, NOW(6), NOW(6), u.id
FROM users u
         JOIN (SELECT 'Income' AS name, 'Salary and other income' AS description, 'income' AS icon
               UNION ALL
               SELECT 'Groceries', 'Food and household items', 'groceries'
               UNION ALL
               SELECT 'Subscriptions', 'Recurring subscriptions', 'subscriptions'
               UNION ALL
               SELECT 'Bills', 'Rent, utilities, services', 'bills'
               UNION ALL
               SELECT 'Eating Out', 'Restaurants, coffee, takeout', 'eating_out') x;

-- ------------------------------------------------------------
-- TRANSACTIONS (re-runnable: delete only [seed] ones)
-- ------------------------------------------------------------
DELETE
FROM transactions
WHERE description LIKE '[seed] %';

-- kristiyan - Revolut EUR
INSERT INTO transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN (SELECT 2500.00 AS amount, (NOW(6) - INTERVAL 25 DAY) AS occurred_at, 'Salary' AS description
               UNION ALL
               SELECT -55.40, (NOW(6) - INTERVAL 20 DAY), 'Groceries - Lidl'
               UNION ALL
               SELECT -12.99, (NOW(6) - INTERVAL 18 DAY), 'Netflix'
               UNION ALL
               SELECT -120.00, (NOW(6) - INTERVAL 14 DAY), 'Utilities'
               UNION ALL
               SELECT -6.80, (NOW(6) - INTERVAL 3 DAY), 'Coffee'
               UNION ALL
               SELECT -34.50, (NOW(6) - INTERVAL 1 DAY), 'Dinner') t
WHERE u.username = 'kristiyan'
  AND a.name = 'Revolut EUR';

-- kristiyan - Wallet (cash)
INSERT INTO transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN (SELECT -10.00 AS amount, (NOW(6) - INTERVAL 7 DAY) AS occurred_at, 'Taxi (cash)' AS description
               UNION ALL
               SELECT -22.30, (NOW(6) - INTERVAL 6 DAY), 'Snacks'
               UNION ALL
               SELECT 50.00, (NOW(6) - INTERVAL 5 DAY), 'ATM withdrawal') t
WHERE u.username = 'kristiyan'
  AND a.name = 'Wallet';

-- maria - Salary USD
INSERT INTO transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN (SELECT 3200.00 AS amount, (NOW(6) - INTERVAL 28 DAY) AS occurred_at, 'Monthly salary' AS description
               UNION ALL
               SELECT -800.00, (NOW(6) - INTERVAL 16 DAY), 'Rent'
               UNION ALL
               SELECT -65.10, (NOW(6) - INTERVAL 9 DAY), 'Groceries'
               UNION ALL
               SELECT -19.99, (NOW(6) - INTERVAL 2 DAY), 'Spotify') t
WHERE u.username = 'maria'
  AND a.name = 'Salary USD';

-- admin - Main EUR
INSERT INTO transactions (amount, account_id, created_at, occurred_at, updated_at, description)
SELECT t.amount,
       a.id,
       NOW(6),
       t.occurred_at,
       NOW(6),
       CONCAT('[seed] ', t.description)
FROM accounts a
         JOIN users u ON u.id = a.user_id
         JOIN (SELECT 10000.00 AS amount, (NOW(6) - INTERVAL 60 DAY) AS occurred_at, 'Initial funding' AS description
               UNION ALL
               SELECT -99.00, (NOW(6) - INTERVAL 10 DAY), 'Domain renewal') t
WHERE u.username = 'admin'
  AND a.name = 'Main EUR';

-- ------------------------------------------------------------
-- CATEGORY ASSIGNMENT
-- ------------------------------------------------------------

-- Null out invalid category references (defensive)
UPDATE transactions t
    LEFT JOIN categories c ON c.id = t.category_id
SET t.category_id = NULL
WHERE t.category_id IS NOT NULL
  AND c.id IS NULL;

-- Assign category_id (only where NULL)
UPDATE transactions t
    JOIN accounts a ON a.id = t.account_id
    JOIN users u ON u.id = a.user_id
    JOIN categories c_income ON c_income.user_id = u.id AND c_income.name = 'Income'
    JOIN categories c_groceries ON c_groceries.user_id = u.id AND c_groceries.name = 'Groceries'
    JOIN categories c_subs ON c_subs.user_id = u.id AND c_subs.name = 'Subscriptions'
    JOIN categories c_bills ON c_bills.user_id = u.id AND c_bills.name = 'Bills'
    JOIN categories c_eat ON c_eat.user_id = u.id AND c_eat.name = 'Eating Out'
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
WHERE t.category_id IS NULL;
