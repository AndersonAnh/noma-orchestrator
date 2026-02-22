-- liquibase formatted sql

-- changeset orchestrator:insert-client-data
INSERT INTO client (id, full_name, email, phone, age) VALUES
('3fa85f64-5717-4562-b3fc-2c963f66afa6', 'Иван Петров', 'ivan.petrov@example.com', '+7-999-123-45-67', 40),
('3fa85f64-5717-4562-b3fc-2c963f66afa7', 'Мария Сидорова', 'maria.sidorova@example.com', '+7-999-234-56-78', 35),
('3fa85f64-5717-4562-b3fc-2c963f66afa8', 'Алексей Иванов', 'alexey.ivanov@example.com', '+7-999-345-67-89', 46),
('3fa85f64-5717-4562-b3fc-2c963f66afa9', 'Елена Смирнова', 'elena.smirnova@example.com', '+7-999-456-78-90', 30),
('3fa85f64-5717-4562-b3fc-2c963f66afaa', 'Дмитрий Козлов', 'dmitry.kozlov@example.com', '+7-999-567-89-01', 37);
