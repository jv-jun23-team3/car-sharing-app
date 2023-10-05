DELETE FROM rentals;
DELETE FROM cars;
DELETE FROM users where id != 1;
DELETE FROM user_role where user_id != 1;
