-- Script para crear la base de datos del carrito de compras
-- Ejecutar en MariaDB antes de iniciar la aplicación

CREATE DATABASE IF NOT EXISTS cart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cart_db;

-- Las tablas se crean automáticamente con spring.jpa.hibernate.ddl-auto=update
-- Este script es solo para insertar datos de prueba

-- Insertar productos de ejemplo (ejecutar DESPUÉS de iniciar la aplicación por primera vez)
INSERT INTO productos (nombre, descripcion, precio, stock) VALUES
('Laptop HP 15"', 'Laptop HP con procesador Intel Core i5, 8GB RAM, 256GB SSD', 4500.00, 10),
('Mouse Inalámbrico', 'Mouse inalámbrico ergonómico con receptor USB', 150.00, 50),
('Teclado Mecánico', 'Teclado mecánico RGB con switches blue', 350.00, 25),
('Monitor 24"', 'Monitor Full HD IPS 24 pulgadas 75Hz', 1800.00, 15),
('Audífonos Bluetooth', 'Audífonos over-ear con cancelación de ruido', 600.00, 30),
('Webcam HD', 'Webcam 1080p con micrófono integrado', 400.00, 20),
('USB Hub 7 puertos', 'Hub USB 3.0 con 7 puertos y alimentación', 200.00, 40),
('SSD Externo 1TB', 'Unidad de estado sólido externa USB 3.1', 800.00, 18);
