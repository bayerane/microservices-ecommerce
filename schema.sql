-- ============================================
-- SCRIPT DE CRÉATION DES BASES DE DONNÉES
-- ============================================

-- Connexion en tant que superuser postgres
-- psql -U postgres -W

-- ============================================
-- 1. CRÉATION DES BASES DE DONNÉES
-- ============================================

CREATE DATABASE auth_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE user_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE order_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- ============================================
-- 2. SCHÉMA AUTH_DB
-- ============================================

\c auth_db

-- Table users pour l'authentification
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour recherche rapide par email
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Trigger pour mise à jour automatique de updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Données initiales (mot de passe : "admin123" et "user123")
-- Hash BCrypt avec force 10
INSERT INTO users (email, password, role, enabled) VALUES
('admin@microservices.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true),
('user@microservices.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye2XqJd8v5eL5qJH0pQJWJL1aXVL5vGHy', 'USER', true);

-- ============================================
-- 3. SCHÉMA USER_DB
-- ============================================

\c user_db

-- Table users (informations complètes)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_last_name ON users(last_name);

-- Trigger updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Données initiales correspondant aux utilisateurs auth
INSERT INTO users (id, email, first_name, last_name, phone, city, country) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin@microservices.com', 'Admin', 'System', '+33612345678', 'Paris', 'France'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'user@microservices.com', 'John', 'Doe', '+33687654321', 'Lyon', 'France');

-- ============================================
-- 4. SCHÉMA ORDER_DB
-- ============================================

\c order_db

-- Type ENUM pour le statut des commandes
CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

-- Table orders
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    status order_status DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    currency VARCHAR(3) DEFAULT 'EUR',
    description TEXT,
    shipping_address TEXT,
    shipping_city VARCHAR(100),
    shipping_country VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- Trigger updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Fonction pour générer un numéro de commande unique
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS VARCHAR AS $$
DECLARE
    new_order_number VARCHAR;
BEGIN
    new_order_number := 'ORD-' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || '-' || LPAD(NEXTVAL('order_number_seq')::TEXT, 6, '0');
    RETURN new_order_number;
END;
$$ LANGUAGE plpgsql;

-- Séquence pour les numéros de commande
CREATE SEQUENCE order_number_seq START 1;

-- Données initiales
INSERT INTO orders (user_id, order_number, status, total_amount, description, shipping_city, shipping_country) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'ORD-20250123-000001', 'DELIVERED', 150.50, 'Premium package', 'Lyon', 'France'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'ORD-20250123-000002', 'PENDING', 89.99, 'Standard package', 'Lyon', 'France'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ORD-20250123-000003', 'CONFIRMED', 299.00, 'Enterprise package', 'Paris', 'France');

-- ============================================
-- 5. VUES POUR STATISTIQUES (OPTIONNEL)
-- ============================================

\c order_db

-- Vue pour statistiques par utilisateur
CREATE VIEW user_order_stats AS
SELECT 
    user_id,
    COUNT(*) as total_orders,
    SUM(total_amount) as total_spent,
    AVG(total_amount) as average_order_value,
    MAX(created_at) as last_order_date
FROM orders
GROUP BY user_id;

-- Vue pour statistiques par statut
CREATE VIEW order_status_stats AS
SELECT 
    status,
    COUNT(*) as count,
    SUM(total_amount) as total_amount
FROM orders
GROUP BY status;

-- ============================================
-- 6. PERMISSIONS (OPTIONNEL - SÉCURITÉ)
-- ============================================

-- Créer des utilisateurs spécifiques pour chaque service
-- CREATE USER auth_service_user WITH PASSWORD 'secure_password_1';
-- CREATE USER user_service_user WITH PASSWORD 'secure_password_2';
-- CREATE USER order_service_user WITH PASSWORD 'secure_password_3';

-- Accorder les permissions
-- GRANT CONNECT ON DATABASE auth_db TO auth_service_user;
-- GRANT CONNECT ON DATABASE user_db TO user_service_user;
-- GRANT CONNECT ON DATABASE order_db TO order_service_user;

-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO auth_service_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO user_service_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO order_service_user;

-- ============================================
-- 7. COMMANDES UTILES
-- ============================================

-- Lister toutes les bases de données
-- \l

-- Se connecter à une base spécifique
-- \c auth_db

-- Lister les tables d'une base
-- \dt

-- Afficher la structure d'une table
-- \d users

-- Supprimer une base (attention !)
-- DROP DATABASE IF EXISTS auth_db;

-- ============================================
-- FIN DU SCRIPT
-- ============================================