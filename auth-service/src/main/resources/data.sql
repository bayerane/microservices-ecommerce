-- ============================================
-- Données initiales pour Auth Service
-- ============================================

-- NOTE: Ces insertions ne s'exécuteront que si les tables sont vides
-- Spring Boot ne les exécutera pas si spring.jpa.hibernate.ddl-auto=update
-- et que des données existent déjà

-- Utilisateur ADMIN
-- Email: admin@microservices.com
-- Mot de passe: admin123
-- Hash BCrypt avec force 10
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
SELECT 
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
    'admin@microservices.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@microservices.com'
);

-- Utilisateur USER standard
-- Email: user@microservices.com
-- Mot de passe: user123
-- Hash BCrypt avec force 10
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
SELECT 
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid,
    'user@microservices.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye2XqJd8v5eL5qJH0pQJWJL1aXVL5vGHy',
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'user@microservices.com'
);

-- Utilisateur de test désactivé
-- Email: disabled@microservices.com
-- Mot de passe: disabled123
INSERT INTO users (id, email, password, role, enabled, created_at, updated_at)
SELECT 
    'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid,
    'disabled@microservices.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye3YrLe9w6fM7nKpRqSyXzM2bYwM6vNHm',
    'USER',
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'disabled@microservices.com'
);

-- ============================================
-- Résumé des utilisateurs créés:
-- ============================================
-- 1. admin@microservices.com / admin123 (ADMIN, enabled)
-- 2. user@microservices.com / user123 (USER, enabled)
-- 3. disabled@microservices.com / disabled123 (USER, disabled)
-- ============================================