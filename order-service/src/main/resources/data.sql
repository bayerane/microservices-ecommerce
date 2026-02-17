-- ============================================================================
-- Script SQL pour Order Service - Données de Test
-- ============================================================================

-- Notes :
-- Les UUIDs des utilisateurs doivent correspondre à ceux du User Service
-- USER1: 550e8400-e29b-41d4-a716-446655440001 (john.doe@example.com)
-- USER2: 550e8400-e29b-41d4-a716-446655440002 (jane.smith@example.com)
-- ADMIN: 550e8400-e29b-41d4-a716-446655440003 (admin@example.com)

-- ============================================================================
-- Commandes de John Doe (USER1)
-- ============================================================================

-- Commande 1 - En attente
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    notes,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440001',
    'ORD-20250120-100001',
    'PENDING',
    299.99,
    'EUR',
    'Ordinateur portable Dell XPS 15',
    '123 Rue de la République',
    'Paris',
    'France',
    '75001',
    'Livraison urgente demandée',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Commande 2 - Confirmée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440002',
    '550e8400-e29b-41d4-a716-446655440001',
    'ORD-20250118-100002',
    'CONFIRMED',
    89.99,
    'EUR',
    'Souris sans fil Logitech MX Master 3',
    '123 Rue de la République',
    'Paris',
    'France',
    '75001',
    CURRENT_TIMESTAMP - INTERVAL '2' DAY,
    CURRENT_TIMESTAMP - INTERVAL '2' DAY
);

-- Commande 3 - Expédiée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    notes,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440003',
    '550e8400-e29b-41d4-a716-446655440001',
    'ORD-20250115-100003',
    'SHIPPED',
    1499.99,
    'EUR',
    'iPhone 15 Pro Max 256GB',
    '123 Rue de la République',
    'Paris',
    'France',
    '75001',
    'Numéro de suivi: FR123456789',
    CURRENT_TIMESTAMP - INTERVAL '5' DAY,
    CURRENT_TIMESTAMP - INTERVAL '1' DAY
);

-- Commande 4 - Livrée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440004',
    '550e8400-e29b-41d4-a716-446655440001',
    'ORD-20250110-100004',
    'DELIVERED',
    49.99,
    'EUR',
    'Câble USB-C vers Lightning',
    '123 Rue de la République',
    'Paris',
    'France',
    '75001',
    CURRENT_TIMESTAMP - INTERVAL '10' DAY,
    CURRENT_TIMESTAMP - INTERVAL '8' DAY
);

-- ============================================================================
-- Commandes de Jane Smith (USER2)
-- ============================================================================

-- Commande 5 - En attente
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440005',
    '550e8400-e29b-41d4-a716-446655440002',
    'ORD-20250121-100005',
    'PENDING',
    799.99,
    'EUR',
    'MacBook Air M2',
    '456 Avenue des Champs-Élysées',
    'Paris',
    'France',
    '75008',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Commande 6 - Confirmée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    notes,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440006',
    '550e8400-e29b-41d4-a716-446655440002',
    'ORD-20250119-100006',
    'CONFIRMED',
    199.99,
    'EUR',
    'AirPods Pro 2',
    '456 Avenue des Champs-Élysées',
    'Paris',
    'France',
    '75008',
    'Cadeau - emballage requis',
    CURRENT_TIMESTAMP - INTERVAL '1' DAY,
    CURRENT_TIMESTAMP - INTERVAL '1' DAY
);

-- Commande 7 - Annulée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    notes,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440007',
    '550e8400-e29b-41d4-a716-446655440002',
    'ORD-20250117-100007',
    'CANCELLED',
    59.99,
    'EUR',
    'Chargeur rapide USB-C 30W',
    '456 Avenue des Champs-Élysées',
    'Paris',
    'France',
    '75008',
    'Annulée à la demande du client',
    CURRENT_TIMESTAMP - INTERVAL '3' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
);

-- ============================================================================
-- Commandes de Admin (USER3) - Pour les tests
-- ============================================================================

-- Commande 8 - Livrée
INSERT INTO orders (
    id, 
    user_id, 
    order_number, 
    status, 
    total_amount, 
    currency, 
    description,
    shipping_address,
    shipping_city,
    shipping_country,
    shipping_postal_code,
    created_at, 
    updated_at
) VALUES (
    '650e8400-e29b-41d4-a716-446655440008',
    '550e8400-e29b-41d4-a716-446655440003',
    'ORD-20250112-100008',
    'DELIVERED',
    2499.99,
    'EUR',
    'MacBook Pro 16" M3 Max',
    '789 Rue de Rivoli',
    'Paris',
    'France',
    '75001',
    CURRENT_TIMESTAMP - INTERVAL '8' DAY,
    CURRENT_TIMESTAMP - INTERVAL '5' DAY
);

-- ============================================================================
-- Statistiques des données insérées
-- ============================================================================

-- Total des commandes par utilisateur :
-- John Doe   : 4 commandes (1 PENDING, 1 CONFIRMED, 1 SHIPPED, 1 DELIVERED)
-- Jane Smith : 3 commandes (1 PENDING, 1 CONFIRMED, 1 CANCELLED)
-- Admin      : 1 commande  (1 DELIVERED)

-- Total général : 8 commandes
-- Montant total : 5,489.91 EUR