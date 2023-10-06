INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted)
VALUES (1, 'q5', 'audi', 'SUV', 11, 10.00, DEFAULT);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES (2, 'user@example.com', '$2a$10$cwyYIIEiKJItEG1tD9/jGeH6x8o36o5Gu9DGVmHWR69Zvm.WYTuSy', 'John', 'Doe', DEFAULT);

INSERT INTO user_role (user_id, role_id)
VALUES (2, 1);

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES (1, '2024-09-03 13:42:15', '2024-10-03 13:42:23', '2024-10-03 13:42:29', 1, 2, DEFAULT);

INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount, is_deleted)
VALUES (1, 'PENDING', 'PAYMENT', 1,
        'https://checkout.stripe.com/c/pay/cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7#fidkdWxOYHwnPyd1blpxYHZxWjA0S3JvcVFBZEdOa1ZoQHBhNE1BYlxUQkF%2FPXVyRmJ3QWNpcGxiUnU8czdrQ3VDZE5SXDZjcjB%2FMEJScHwwVENPdjVMRGxHVWpzXHE1V21QV2lsVWlzYXM8NTU0V3c9MkxUNCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl',
        'cs_test_a10tdW8TopyzVkoNBU52NJl8SQb2PDKGP7pNqcc8FdwNDt1Gw8nSXnJ7V7', 30000.00, false);

