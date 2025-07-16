-- Script de datos de ejemplo para planes de membresía con características detalladas
-- Este archivo puede ser usado para insertar datos de prueba o reemplazar los datos existentes

-- Limpiar datos existentes (opcional)
-- DELETE FROM membership_plans WHERE id IN (1, 2);

-- Insertar planes con características detalladas
INSERT INTO membership_plans (id, name, description, price, duration_days, billing_cycle, features, max_contracts, max_resources, priority_support, display_order) VALUES 

-- Plan Básico Mensual
(1, 'Plan Básico', 'Acceso premium mensual con todas las funcionalidades esenciales de FiTech', 9.99, 30, 'MONTHLY', 
 '{
   "features": [
     {"name": "Contratos ilimitados con trainers", "icon": "people", "enabled": true},
     {"name": "Hasta 10 dietas personalizadas", "icon": "restaurant_menu", "enabled": true},
     {"name": "Hasta 10 rutinas de ejercicio", "icon": "fitness_center", "enabled": true},
     {"name": "Análisis básico de progreso", "icon": "analytics", "enabled": true},
     {"name": "Acceso a la app móvil", "icon": "smartphone", "enabled": true},
     {"name": "Soporte por email", "icon": "email", "enabled": true}
   ],
   "benefits": {
     "unlimited_contracts": true,
     "max_diets": 10,
     "max_routines": 10,
     "basic_analytics": true,
     "mobile_app": true,
     "email_support": true
   }
 }', 
 NULL, 10, FALSE, 1),

-- Plan Premium Anual
(2, 'Plan Premium', 'Acceso premium anual con todas las funcionalidades avanzadas y soporte prioritario', 99.99, 365, 'ANNUAL', 
 '{
   "features": [
     {"name": "Contratos ilimitados con trainers", "icon": "people", "enabled": true},
     {"name": "Dietas personalizadas ilimitadas", "icon": "restaurant_menu", "enabled": true},
     {"name": "Rutinas de ejercicio ilimitadas", "icon": "fitness_center", "enabled": true},
     {"name": "Análisis avanzado con IA", "icon": "psychology", "enabled": true},
     {"name": "Acceso completo a la app móvil", "icon": "smartphone", "enabled": true},
     {"name": "Soporte prioritario 24/7", "icon": "support_agent", "enabled": true},
     {"name": "Consultas gratuitas mensuales", "icon": "video_call", "enabled": true},
     {"name": "Contenido exclusivo premium", "icon": "star", "enabled": true},
     {"name": "Descarga de contenido offline", "icon": "download", "enabled": true},
     {"name": "Descuento del 17% anual", "icon": "local_offer", "enabled": true}
   ],
   "benefits": {
     "unlimited_contracts": true,
     "unlimited_resources": true,
     "advanced_analytics": true,
     "ai_insights": true,
     "priority_support": true,
     "free_consultations": 2,
     "exclusive_content": true,
     "offline_content": true,
     "annual_discount": true,
     "mobile_app": true
   }
 }', 
 NULL, NULL, TRUE, 2),

-- Plan Empresarial (ejemplo adicional)
(3, 'Plan Empresarial', 'Solución completa para empresas con múltiples usuarios y gestión centralizada', 299.99, 365, 'ANNUAL', 
 '{
   "features": [
     {"name": "Hasta 50 usuarios incluidos", "icon": "groups", "enabled": true},
     {"name": "Dashboard administrativo", "icon": "admin_panel_settings", "enabled": true},
     {"name": "Reportes y métricas empresariales", "icon": "assessment", "enabled": true},
     {"name": "Integración con APIs", "icon": "api", "enabled": true},
     {"name": "Soporte dedicado", "icon": "headset_mic", "enabled": true},
     {"name": "Personalización de marca", "icon": "palette", "enabled": true},
     {"name": "Backup y seguridad avanzada", "icon": "security", "enabled": true}
   ],
   "benefits": {
     "max_users": 50,
     "admin_dashboard": true,
     "enterprise_reports": true,
     "api_integration": true,
     "dedicated_support": true,
     "white_label": true,
     "advanced_security": true
   }
 }', 
 NULL, NULL, TRUE, 3)

ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  price = VALUES(price),
  features = VALUES(features),
  updated_at = CURRENT_TIMESTAMP; 