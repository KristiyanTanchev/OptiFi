/* ============================================================
   OptiFi SEED ONLY (PostgreSQL)
   Assumes schema already exists (users, accounts, categories, transactions, budgets, budget_* tables)
   Re-runnable:
     - uses ON CONFLICT DO NOTHING for idempotent inserts
     - deletes only [seed] transactions and [seed] budgets
   ============================================================ */

-- ------------------------------------------------------------
-- USERS
-- ------------------------------------------------------------
INSERT INTO public.users (
    created_at, updated_at,
    username, email,
    auth_provider, provider_subject,
    password_hash,
    base_currency, locale, role,
    time_zone_id
)
VALUES
    (NOW(), NOW(), 'admin',     'admin@optifi.test',     'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'EN_US', 'ADMIN',          'Europe/Sofia'),
    (NOW(), NOW(), 'moderator', 'moderator@optifi.test', 'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'MODERATOR',      'Europe/Sofia'),
    (NOW(), NOW(), 'kristiyan', 'kristiyan@optifi.test', 'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'USER',           'Europe/Sofia'),
    (NOW(), NOW(), 'maria',     'maria@optifi.test',     'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'EN_US', 'USER',           'Europe/Sofia'),
    (NOW(), NOW(), 'blocked',   'blocked@optifi.test',   'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'BLOCKED',        'Europe/Sofia'),
    (NOW(), NOW(), 'waiting',   'waiting@optifi.test',   'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'WAITING_APPROVAL','Europe/Sofia')
ON CONFLICT (username) DO NOTHING;

INSERT INTO public.users (
    created_at, updated_at,
    username, email,
    auth_provider, provider_subject,
    password_hash,
    base_currency, locale, role,
    time_zone_id
)
SELECT created_at, updated_at, username, email, auth_provider, provider_subject, password_hash, base_currency, locale, role, time_zone_id
FROM (VALUES
          (NOW(), NOW(), 'admin',     'admin@optifi.test',     'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'EN_US', 'ADMIN',          'Europe/Sofia'),
          (NOW(), NOW(), 'moderator', 'moderator@optifi.test', 'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'MODERATOR',      'Europe/Sofia'),
          (NOW(), NOW(), 'kristiyan', 'kristiyan@optifi.test', 'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'USER',           'Europe/Sofia'),
          (NOW(), NOW(), 'maria',     'maria@optifi.test',     'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'EN_US', 'USER',           'Europe/Sofia'),
          (NOW(), NOW(), 'blocked',   'blocked@optifi.test',   'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'USD', 'EN_US', 'BLOCKED',        'Europe/Sofia'),
          (NOW(), NOW(), 'waiting',   'waiting@optifi.test',   'LOCAL', NULL, '$2a$10$mVGkuNVpFOOxwrYX1hR7OefDyus.7h5kZw95DenpYtjG5/fI.uC9K', 'EUR', 'BG_BG', 'WAITING_APPROVAL','Europe/Sofia')
     ) AS v(created_at, updated_at, username, email, auth_provider, provider_subject, password_hash, base_currency, locale, role, time_zone_id)
ON CONFLICT (email) DO NOTHING;

-- ------------------------------------------------------------
-- ACCOUNTS
-- ------------------------------------------------------------
INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT FALSE, NOW(), NOW(), u.id, 'Main EUR', 'UniCredit', 'EUR', 'BANK'
FROM public.users u
WHERE u.username = 'admin'
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT FALSE, NOW(), NOW(), u.id, 'Cash', NULL, 'EUR', 'CASH'
FROM public.users u
WHERE u.username = 'admin'
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT FALSE, NOW(), NOW(), u.id, 'Revolut EUR', 'Revolut', 'EUR', 'BANK'
FROM public.users u
WHERE u.username = 'kristiyan'
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT FALSE, NOW(), NOW(), u.id, 'Wallet', NULL, 'EUR', 'CASH'
FROM public.users u
WHERE u.username = 'kristiyan'
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT FALSE, NOW(), NOW(), u.id, 'Salary USD', 'DSK', 'USD', 'BANK'
FROM public.users u
WHERE u.username = 'maria'
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO public.accounts (archived, created_at, updated_at, user_id, name, institution, currency, type)
SELECT TRUE, NOW(), NOW(), u.id, 'Old Cash', NULL, 'USD', 'CASH'
FROM public.users u
WHERE u.username = 'maria'
ON CONFLICT (user_id, name) DO NOTHING;

-- ------------------------------------------------------------
-- DEFAULT CATEGORIES (global: user_id IS NULL)
-- ------------------------------------------------------------
INSERT INTO public.categories (name, description, icon, created_at, updated_at, user_id)
VALUES
    ('Food & drinks',           'Groceries, restaurants, coffee, takeout', 'restaurant',     NOW(), NOW(), NULL),
    ('Shopping',                'Clothes, gadgets, household shopping',     'shopping_cart',  NOW(), NOW(), NULL),
    ('Housing',                 'Rent, utilities, home services',           'home',           NOW(), NOW(), NULL),
    ('Transportation',          'Public transport, taxi, commute',          'directions_bus', NOW(), NOW(), NULL),
    ('Vehicle',                 'Fuel, maintenance, repairs, car costs',    'directions_car', NOW(), NOW(), NULL),
    ('Life & Entertainment',    'Movies, fun, hobbies, events',             'movie',          NOW(), NOW(), NULL),
    ('Communication, PC',       'Internet, phone, software, devices',       'computer',       NOW(), NOW(), NULL),
    ('Financial expenses',      'Fees, charges, subscriptions, interest',   'receipt_long',   NOW(), NOW(), NULL),
    ('Investments',             'Stocks, ETFs, crypto, investing',          'trending_up',    NOW(), NOW(), NULL),
    ('Income',                  'Salary and other income',                  'attach_money',   NOW(), NOW(), NULL)
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- TRANSACTIONS (re-runnable: delete only [seed] ones)
-- ------------------------------------------------------------
DELETE FROM public.transactions
WHERE description LIKE '[seed] %';

-- kristiyan - Revolut EUR
WITH
    a AS (
        SELECT acc.id
        FROM public.accounts acc
                 JOIN public.users u ON u.id = acc.user_id
        WHERE u.username = 'kristiyan' AND acc.name = 'Revolut EUR'
    ),
    c_income AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Income'),
    c_food   AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Food & drinks'),
    c_bills  AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Housing'),
    c_ent    AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Life & Entertainment'),
    t(amount, occurred_at, description) AS (
        VALUES
            ( 2500.00::numeric(19,4), (NOW() - INTERVAL '25 days'), 'Salary'),
            (  -55.40::numeric(19,4), (NOW() - INTERVAL '20 days'), 'Groceries - Lidl'),
            (  -12.99::numeric(19,4), (NOW() - INTERVAL '18 days'), 'Netflix'),
            ( -120.00::numeric(19,4), (NOW() - INTERVAL '14 days'), 'Utilities'),
            (   -6.80::numeric(19,4), (NOW() - INTERVAL '3 days'),  'Coffee'),
            (  -34.50::numeric(19,4), (NOW() - INTERVAL '1 day'),   'Dinner')
    )
INSERT INTO public.transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount,
    (SELECT id FROM a),
    CASE
        WHEN t.amount > 0 OR POSITION('salary' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_income)
        WHEN POSITION('lidl' IN LOWER(t.description)) > 0 OR POSITION('grocer' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_food)
        WHEN POSITION('netflix' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_ent)
        WHEN POSITION('utilit' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_bills)
        WHEN POSITION('coffee' IN LOWER(t.description)) > 0 OR POSITION('dinner' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_food)
        ELSE (SELECT id FROM c_food)
        END,
    NOW(),
    t.occurred_at,
    NOW(),
    '[seed] ' || t.description
FROM t;

-- kristiyan - Wallet (cash)
WITH
    a AS (
        SELECT acc.id
        FROM public.accounts acc
                 JOIN public.users u ON u.id = acc.user_id
        WHERE u.username = 'kristiyan' AND acc.name = 'Wallet'
    ),
    c_income AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Income'),
    c_food   AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Food & drinks'),
    c_trans  AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Transportation'),
    t(amount, occurred_at, description) AS (
        VALUES
            ( -10.00::numeric(19,4), (NOW() - INTERVAL '7 days'), 'Taxi (cash)'),
            ( -22.30::numeric(19,4), (NOW() - INTERVAL '6 days'), 'Snacks'),
            (  50.00::numeric(19,4), (NOW() - INTERVAL '5 days'), 'ATM withdrawal')
    )
INSERT INTO public.transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount,
    (SELECT id FROM a),
    CASE
        WHEN t.amount > 0 THEN (SELECT id FROM c_income)
        WHEN POSITION('atm' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_trans)
        WHEN POSITION('taxi' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_trans)
        ELSE (SELECT id FROM c_food)
        END,
    NOW(),
    t.occurred_at,
    NOW(),
    '[seed] ' || t.description
FROM t;

-- maria - Salary USD
WITH
    a AS (
        SELECT acc.id
        FROM public.accounts acc
                 JOIN public.users u ON u.id = acc.user_id
        WHERE u.username = 'maria' AND acc.name = 'Salary USD'
    ),
    c_income AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Income'),
    c_food   AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Food & drinks'),
    c_house  AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Housing'),
    c_ent    AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Life & Entertainment'),
    t(amount, occurred_at, description) AS (
        VALUES
            ( 3200.00::numeric(19,4), (NOW() - INTERVAL '28 days'), 'Monthly salary'),
            ( -800.00::numeric(19,4), (NOW() - INTERVAL '16 days'), 'Rent'),
            (  -65.10::numeric(19,4), (NOW() - INTERVAL '9 days'),  'Groceries'),
            (  -19.99::numeric(19,4), (NOW() - INTERVAL '2 days'),  'Spotify')
    )
INSERT INTO public.transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount,
    (SELECT id FROM a),
    CASE
        WHEN t.amount > 0 OR POSITION('salary' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_income)
        WHEN POSITION('rent' IN LOWER(t.description)) > 0 OR POSITION('utilit' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_house)
        WHEN POSITION('spotify' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_ent)
        WHEN POSITION('grocer' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_food)
        ELSE (SELECT id FROM c_house)
        END,
    NOW(),
    t.occurred_at,
    NOW(),
    '[seed] ' || t.description
FROM t;

-- admin - Main EUR
WITH
    a AS (
        SELECT acc.id
        FROM public.accounts acc
                 JOIN public.users u ON u.id = acc.user_id
        WHERE u.username = 'admin' AND acc.name = 'Main EUR'
    ),
    c_income AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Income'),
    c_house  AS (SELECT id FROM public.categories WHERE user_id IS NULL AND name = 'Housing'),
    t(amount, occurred_at, description) AS (
        VALUES
            (10000.00::numeric(19,4), (NOW() - INTERVAL '60 days'), 'Initial funding'),
            (  -99.00::numeric(19,4), (NOW() - INTERVAL '10 days'), 'Domain renewal')
    )
INSERT INTO public.transactions (amount, account_id, category_id, created_at, occurred_at, updated_at, description)
SELECT
    t.amount,
    (SELECT id FROM a),
    CASE
        WHEN t.amount > 0 OR POSITION('funding' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_income)
        WHEN POSITION('domain' IN LOWER(t.description)) > 0 THEN (SELECT id FROM c_house)
        ELSE (SELECT id FROM c_house)
        END,
    NOW(),
    t.occurred_at,
    NOW(),
    '[seed] ' || t.description
FROM t;

-- ------------------------------------------------------------
-- BUDGETS (new v1 tables)
-- ------------------------------------------------------------
DELETE FROM public.budget_categories bc
    USING public.budgets b
WHERE bc.budget_id = b.id
  AND b.name LIKE '[seed] %';

DELETE FROM public.budget_accounts ba
    USING public.budgets b
WHERE ba.budget_id = b.id
  AND b.name LIKE '[seed] %';

DELETE FROM public.budgets
WHERE name LIKE '[seed] %';

-- Create a couple of example budgets for kristiyan
-- 1) Monthly "Food & drinks" budget on all accounts (no budget_accounts rows)
INSERT INTO public.budgets (user_id, name, period, amount, currency, start_date, end_date, archived, created_at, updated_at)
SELECT
    u.id,
    '[seed] Food monthly',
    'MONTH',
    300.00::numeric(19,4),
    'EUR',
    date_trunc('month', CURRENT_DATE)::date,
    (date_trunc('month', CURRENT_DATE) + INTERVAL '1 month - 1 day')::date,
    FALSE,
    NOW(),
    NOW()
FROM public.users u
WHERE u.username = 'kristiyan';

INSERT INTO public.budget_categories (budget_id, category_id)
SELECT
    b.id,
    c.id
FROM public.budgets b
         JOIN public.users u ON u.id = b.user_id
         JOIN public.categories c ON c.user_id IS NULL AND c.name = 'Food & drinks'
WHERE u.username = 'kristiyan'
  AND b.name = '[seed] Food monthly'
ON CONFLICT DO NOTHING;

-- 2) Monthly "Housing" budget scoped to Revolut EUR account only
INSERT INTO public.budgets (user_id, name, period, amount, currency, start_date, end_date, archived, created_at, updated_at)
SELECT
    u.id,
    '[seed] Housing (Revolut) monthly',
    'MONTH',
    400.00::numeric(19,4),
    'EUR',
    date_trunc('month', CURRENT_DATE)::date,
    (date_trunc('month', CURRENT_DATE) + INTERVAL '1 month - 1 day')::date,
    FALSE,
    NOW(),
    NOW()
FROM public.users u
WHERE u.username = 'kristiyan';

INSERT INTO public.budget_categories (budget_id, category_id)
SELECT
    b.id,
    c.id
FROM public.budgets b
         JOIN public.users u ON u.id = b.user_id
         JOIN public.categories c ON c.user_id IS NULL AND c.name = 'Housing'
WHERE u.username = 'kristiyan'
  AND b.name = '[seed] Housing (Revolut) monthly'
ON CONFLICT DO NOTHING;

INSERT INTO public.budget_accounts (budget_id, account_id)
SELECT
    b.id,
    a.id
FROM public.budgets b
         JOIN public.users u ON u.id = b.user_id
         JOIN public.accounts a ON a.user_id = u.id AND a.name = 'Revolut EUR'
WHERE u.username = 'kristiyan'
  AND b.name = '[seed] Housing (Revolut) monthly'
ON CONFLICT DO NOTHING;
