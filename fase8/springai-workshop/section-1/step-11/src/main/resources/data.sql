INSERT INTO customer (id, first_name, last_name) VALUES (1, 'Speedy', 'McWheels');
INSERT INTO customer (id, first_name, last_name) VALUES (2, 'Zoom', 'Thunderfoot');
INSERT INTO customer (id, first_name, last_name) VALUES (3, 'Vroom', 'Lightyear');
INSERT INTO customer (id, first_name, last_name) VALUES (4, 'Turbo', 'Gearshift');
INSERT INTO customer (id, first_name, last_name) VALUES (5, 'Drifty', 'Skiddy');

INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (1, 1, DATEADD('DAY', 1, CURRENT_DATE), DATEADD('DAY', 3, CURRENT_DATE), 'Verbier, Switzerland');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (2, 1, DATEADD('DAY', 14, CURRENT_DATE), DATEADD('DAY', 16, CURRENT_DATE), 'Sao Paulo, Brazil');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (3, 1, DATEADD('DAY', 30, CURRENT_DATE), DATEADD('DAY', 34, CURRENT_DATE), 'Antwerp, Belgium');

INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (4, 2, DATEADD('DAY', 2, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), 'Tokyo, Japan');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (5, 2, DATEADD('DAY', 60, CURRENT_DATE), DATEADD('DAY', 65, CURRENT_DATE), 'Brisbane, Australia');

INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (7, 3, DATEADD('DAY', 3, CURRENT_DATE), DATEADD('DAY', 8, CURRENT_DATE), 'Missoula, Montana');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (8, 3, DATEADD('DAY', 35, CURRENT_DATE), DATEADD('DAY', 41, CURRENT_DATE), 'Singapore');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (9, 3, DATEADD('DAY', 90, CURRENT_DATE), DATEADD('DAY', 96, CURRENT_DATE), 'Capetown, South Africa');

INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (10, 4, DATEADD('DAY', 1, CURRENT_DATE), DATEADD('DAY', 6, CURRENT_DATE), 'Nuuk, Greenland');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (11, 4, DATEADD('DAY', 75, CURRENT_DATE), DATEADD('DAY', 80, CURRENT_DATE), 'Santiago de Chile');
INSERT INTO booking (id, customer_id, date_from, date_to, location)
VALUES (12, 4, DATEADD('DAY', 120, CURRENT_DATE), DATEADD('DAY', 127, CURRENT_DATE), 'Dubai');
