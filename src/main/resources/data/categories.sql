-- ============================================================
-- Expense Categories & Subcategories seed data
-- Parent categories have parent_id = NULL and a type (EXPENSE/INCOME)
-- Subcategories have parent_id set and type = NULL (inherited from parent)
-- ============================================================

-- Housing (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000001', 'Housing', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000101', 'Rent & Mortgage',       NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000102', 'Electricity',           NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000103', 'Gas & Heating',         NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000104', 'Water',                 NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000105', 'Internet & Phone',      NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000106', 'Maintenance & Repairs', NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000107', 'Home Insurance',        NULL, '00000000-0000-0000-0000-000000000001', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000108', 'Furnishings',           NULL, '00000000-0000-0000-0000-000000000001', now(), now());

-- Food & Dining (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000002', 'Food & Dining', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000201', 'Groceries',       NULL, '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000202', 'Restaurants',     NULL, '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000203', 'Coffee & Drinks', NULL, '00000000-0000-0000-0000-000000000002', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000204', 'Bars & Alcohol',  NULL, '00000000-0000-0000-0000-000000000002', now(), now());

-- Transportation (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000003', 'Transportation', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000301', 'Fuel',                  NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000302', 'Public Transit',        NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000303', 'Taxi & Ride-sharing',   NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000304', 'Parking',               NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000305', 'Tolls',                 NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000306', 'Vehicle Insurance',     NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000307', 'Vehicle Maintenance',   NULL, '00000000-0000-0000-0000-000000000003', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000308', 'Car Payment',           NULL, '00000000-0000-0000-0000-000000000003', now(), now());

-- Health (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000004', 'Health', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000401', 'Doctor & Medical', NULL, '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000402', 'Pharmacy',         NULL, '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000403', 'Health Insurance', NULL, '00000000-0000-0000-0000-000000000004', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000404', 'Mental Health',    NULL, '00000000-0000-0000-0000-000000000004', now(), now());

-- Entertainment (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000005', 'Entertainment', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000501', 'Streaming Services', NULL, '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000502', 'Movies & Events',    NULL, '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000503', 'Hobbies',            NULL, '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000504', 'Books & Magazines',  NULL, '00000000-0000-0000-0000-000000000005', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000505', 'Gaming',             NULL, '00000000-0000-0000-0000-000000000005', now(), now());

-- Shopping (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000006', 'Shopping', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000601', 'Clothing',               NULL, '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000602', 'Electronics',            NULL, '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000603', 'Personal Care & Beauty', NULL, '00000000-0000-0000-0000-000000000006', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000604', 'Household Supplies',     NULL, '00000000-0000-0000-0000-000000000006', now(), now());

-- Education (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000007', 'Education', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000701', 'Tuition & Fees',  NULL, '00000000-0000-0000-0000-000000000007', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000702', 'Books & Supplies',NULL, '00000000-0000-0000-0000-000000000007', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000703', 'Online Courses',  NULL, '00000000-0000-0000-0000-000000000007', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000704', 'Student Loan',    NULL, '00000000-0000-0000-0000-000000000007', now(), now());

-- Travel (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000008', 'Travel', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000801', 'Flights',                NULL, '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000802', 'Hotels & Accommodation', NULL, '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000803', 'Car Rental',             NULL, '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000804', 'Travel Insurance',       NULL, '00000000-0000-0000-0000-000000000008', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000805', 'Activities & Tours',     NULL, '00000000-0000-0000-0000-000000000008', now(), now());

-- Financial Services (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000009', 'Financial Services', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000902', 'Investments',         NULL, '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000903', 'Taxes',               NULL, '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000904', 'Loan Repayment',      NULL, '00000000-0000-0000-0000-000000000009', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000905', 'Credit Card Payment', NULL, '00000000-0000-0000-0000-000000000009', now(), now());

-- Gifts & Donations (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000010', 'Gifts & Donations', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000a01', 'Gifts',   NULL, '00000000-0000-0000-0000-000000000010', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000a02', 'Charity', NULL, '00000000-0000-0000-0000-000000000010', now(), now());

-- Income (INCOME)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000011', 'Income', 'INCOME', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b01', 'Salary',        NULL, '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b02', 'Freelance',     NULL, '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b03', 'Investments',   NULL, '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b04', 'Rental Income', NULL, '00000000-0000-0000-0000-000000000011', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000b05', 'Gifts',         NULL, '00000000-0000-0000-0000-000000000011', now(), now());

-- Communication, PC & Software (EXPENSE)
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000012', 'Communication, PC & Software', 'EXPENSE', NULL, now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000c01', 'Postal Service',NULL, '00000000-0000-0000-0000-000000000012', now(), now());
INSERT INTO categories (id, name, type, parent_id, created_at, updated_at) VALUES ('00000000-0000-0000-0000-000000000c02', 'Software',      NULL, '00000000-0000-0000-0000-000000000012', now(), now());
