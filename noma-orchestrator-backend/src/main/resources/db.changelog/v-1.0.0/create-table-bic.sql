--liquibase formatted sql logicalFilePath:db/changelog/v-1.0.0
--changeset a.solovev:1.0.0-create-table-bic failOnError:true
CREATE TABLE IF NOT EXISTS bic (
                                   id INTEGER PRIMARY KEY,
                                   bic_catalog XML NOT NULL,        -- Содержит файл, загружаемый в БД вручную
                                   uploaded_at TIMESTAMP NOT NULL   -- Дата загрузки
);