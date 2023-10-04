INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES (1, 'customer@example.com', 'password', 'Customer', 'User', false);
INSERT INTO users (id, email, password, first_name, last_name, is_deleted) VALUES (2, 'manager@example.com', 'password', 'Manager', 'Admin', false);

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);