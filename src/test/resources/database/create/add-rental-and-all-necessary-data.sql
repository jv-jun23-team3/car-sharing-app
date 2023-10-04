INSERT INTO rentals (id, rental_date, return_date, car_id, user_id, is_deleted) VALUES (1, '2023-10-10 10:10:00', DATE_ADD('2023-10-10 10:10:00', INTERVAL 7 DAY), 1, 1, false);
INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted) VALUES (2, '2023-10-10 10:10:00', DATE_ADD('2023-10-10 10:10:00', INTERVAL 7 DAY), '2023-10-17 10:10:00', 2, 1, false);
