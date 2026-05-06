-- Stations
INSERT INTO STATION (id, name) VALUES (1, 'Budapest');
INSERT INTO STATION (id, name) VALUES (2, 'Vienna');
INSERT INTO STATION (id, name) VALUES (3, 'Bratislava');
INSERT INTO STATION (id, name) VALUES (4, 'Prague');
INSERT INTO STATION (id, name) VALUES (5, 'Warsaw');

-- Routes
INSERT INTO ROUTE (id, name) VALUES (1, 'Budapest-Vienna Express');
INSERT INTO ROUTE (id, name) VALUES (2, 'Vienna-Prague Express');

-- Route 1 stations: Budapest -> Bratislava -> Vienna
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (1, 1, 1, 0);
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (2, 1, 3, 1);
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (3, 1, 2, 2);

-- Route 2 stations: Vienna -> Bratislava -> Prague
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (4, 2, 2, 0);
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (5, 2, 3, 1);
INSERT INTO ROUTE_STATION (id, route_id, station_id, stop_order) VALUES (6, 2, 4, 2);

-- Trains
INSERT INTO TRAIN (id, name, capacity, route_id) VALUES (1, 'IC-101', 100, 1);
INSERT INTO TRAIN (id, name, capacity, route_id) VALUES (2, 'IC-202', 80, 2);

-- Schedules
INSERT INTO SCHEDULE (id, train_id, departure_time, delay_minutes) VALUES (1, 1, '2026-06-01 08:00:00', NULL);
INSERT INTO SCHEDULE (id, train_id, departure_time, delay_minutes) VALUES (2, 1, '2026-06-01 14:00:00', NULL);
INSERT INTO SCHEDULE (id, train_id, departure_time, delay_minutes) VALUES (3, 2, '2026-06-01 09:00:00', NULL);
INSERT INTO SCHEDULE (id, train_id, departure_time, delay_minutes) VALUES (4, 2, '2026-06-01 16:00:00', NULL);