-- ============================================================
-- Expense Categories & Subcategories seed data
-- Parent categories have parent_id = NULL and a type (EXPENSE/INCOME)
-- Subcategories have parent_id set and type = NULL (inherited from parent)
-- ============================================================

-- Housing (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000001', 'Housing', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000101', 'Rent',       'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000102', 'Utilities',           'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000104', 'Water',                 'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000105', 'Internet & Phone',      'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000106', 'Maintenance & Repairs', 'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000107', 'Home Insurance',        'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000108', 'Furnishings',           'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000109', 'Mortgage',              'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000110', 'Household Supplies',    'EXPENSE', '00000000-0000-0000-0000-000000000001', now(), now());

-- Food & Dining (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000002', 'Food & Dining', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000201', 'Groceries',       'EXPENSE', '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000202', 'Restaurants',     'EXPENSE', '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000203', 'Coffee & Drinks', 'EXPENSE', '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000204', 'Bars & Alcohol',  'EXPENSE', '00000000-0000-0000-0000-000000000002', now(), now());

-- Transportation (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000003', 'Transportation', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000301', 'Fuel',                  'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000302', 'Public Transit',        'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000303', 'Taxi & Ride-sharing',   'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000304', 'Parking',               'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000305', 'Tolls',                 'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000306', 'Vehicle Insurance',     'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000307', 'Vehicle Maintenance',   'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000308', 'Car Payment',           'EXPENSE', '00000000-0000-0000-0000-000000000003', now(), now());

-- Health (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000004', 'Health', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000401', 'Doctor & Medical', 'EXPENSE', '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000402', 'Pharmacy',         'EXPENSE', '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000403', 'Health Insurance', 'EXPENSE', '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000404', 'Mental Health',    'EXPENSE', '00000000-0000-0000-0000-000000000004', now(), now());

-- Entertainment (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000005', 'Entertainment', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000501', 'Streaming Services', 'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000502', 'Movies & Events',    'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000503', 'Hobbies',            'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000504', 'Books',              'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000505', 'Gaming',             'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000506', 'Splitwise',          'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000507', 'Fitness',            'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000508', 'Life Events',        'EXPENSE', '00000000-0000-0000-0000-000000000005', now(), now());

-- Shopping (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000006', 'Shopping',               'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000601', 'Clothing',               'EXPENSE', '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000602', 'Electronics',            'EXPENSE', '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000603', 'Personal Care & Beauty', 'EXPENSE', '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000605', 'Pets',                   'EXPENSE', '00000000-0000-0000-0000-000000000006', now(), now());

-- Education (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000007', 'Education', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000701', 'Tuition & Fees',  'EXPENSE', '00000000-0000-0000-0000-000000000007', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000702', 'Online Courses',  'EXPENSE', '00000000-0000-0000-0000-000000000007', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000703', 'Student Loan',    'EXPENSE', '00000000-0000-0000-0000-000000000007', now(), now());

-- Travel (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000008', 'Travel', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000801', 'Flights',                'EXPENSE', '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000802', 'Hotels & Accommodation', 'EXPENSE', '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000803', 'Car Rental',             'EXPENSE', '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000804', 'Travel Insurance',       'EXPENSE', '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000805', 'Activities & Tours',     'EXPENSE', '00000000-0000-0000-0000-000000000008', now(), now());

-- Financial Services (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000009', 'Financial Services', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000902', 'Investments',         'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000903', 'Taxes',               'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000904', 'Fees',                'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000905', 'Loan Repayment',      'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000906', 'Credit Card Payment', 'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000907', 'Fines',               'EXPENSE', '00000000-0000-0000-0000-000000000009', now(), now());

-- Gifts & Donations (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000010', 'Gifts & Donations', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000a01', 'Gifts',             'EXPENSE', '00000000-0000-0000-0000-000000000010', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000a02', 'Charity',           'EXPENSE', '00000000-0000-0000-0000-000000000010', now(), now());

-- Income (INCOME)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000011', 'Income', 'INCOME', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b01', 'Salary',        'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b02', 'Freelance',     'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b03', 'Investments',   'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b04', 'Rental Income', 'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b05', 'Gifts',         'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b06', 'Refunds',       'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b07', 'Sale',          'INCOME', '00000000-0000-0000-0000-000000000011', now(), now());

-- Transfer (TRANSFER)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000098', 'Transfer', 'INCOME', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000099', 'Transfer', 'EXPENSE', NULL, now(), now());

-- Communication, PC & Software (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000012', 'Communication, PC & Software', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000c01', 'Postal Services',              'EXPENSE', '00000000-0000-0000-0000-000000000012', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000c02', 'Software',                     'EXPENSE', '00000000-0000-0000-0000-000000000012', now(), now());

-- Work (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000013', 'Work', 'EXPENSE', NULL, now(), now());

-- Others (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000014', 'Others',   'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000e01', 'Missing',  'EXPENSE', '00000000-0000-0000-0000-000000000014', now(), now());