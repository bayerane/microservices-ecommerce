-- ============================================
-- Données initiales pour User Service
-- ============================================

-- Utilisateur Admin
INSERT INTO users (id, email, first_name, last_name, phone, address, city, country, postal_code, created_at, updated_at)
SELECT 
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
    'admin@microservices.com',
    'Admin',
    'System',
    '+33612345678',
    '123 Rue de la République',
    'Paris',
    'France',
    '75001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@microservices.com'
);

-- Utilisateur Standard
INSERT INTO users (id, email, first_name, last_name, phone, address, city, country, postal_code, created_at, updated_at)
SELECT 
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid,
    'user@microservices.com',
    'John',
    'Doe',
    '+33687654321',
    '456 Avenue des Champs',
    'Lyon',
    'France',
    '69001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'user@microservices.com'
);

-- Utilisateur de test
INSERT INTO users (id, email, first_name, last_name, phone, city, country, created_at, updated_at)
SELECT 
    'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid,
    'test@microservices.com',
    'Marie',
    'Martin',
    '+33698765432',
    'Marseille',
    'France',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'test@microservices.com'
);

-- ============================================
-- Résumé des utilisateurs créés:
-- ============================================
-- 1. admin@microservices.com - Admin System (Paris)
-- 2. user@microservices.com - John Doe (Lyon)
-- 3. test@microservices.com - Marie Martin (Marseille)
-- ============================================