INSERT INTO users (id,first_name, last_name, password, email, shipping_address, is_deleted)
VALUES
    (3,'UserName 1', 'LastName 1', 'Password 1', 'email@gmail.com', 'Address 1', false),
    (4,'UserName 2', 'LastName 2', 'Password 2', 'Email@gmail.com 2', 'Address 2', false);

INSERT INTO user_role (user_id, role_id)
VALUES
    (3, 1),
    (4, 2);
