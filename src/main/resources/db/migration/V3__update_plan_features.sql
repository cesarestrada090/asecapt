-- Actualizar planes con características completas
UPDATE membership_plans SET 
features = '{"features":[{"name":"Contratos ilimitados","icon":"people","enabled":true},{"name":"Dietas y rutinas","icon":"restaurant_menu","enabled":true},{"name":"Análisis de progreso","icon":"analytics","enabled":true},{"name":"App móvil","icon":"smartphone","enabled":true}]}'
WHERE name = 'Plan Mensual';

UPDATE membership_plans SET 
features = '{"features":[{"name":"Contratos ilimitados","icon":"people","enabled":true},{"name":"Dietas y rutinas","icon":"restaurant_menu","enabled":true},{"name":"Análisis de progreso","icon":"analytics","enabled":true},{"name":"App móvil","icon":"smartphone","enabled":true},{"name":"Soporte prioritario","icon":"priority_high","enabled":true},{"name":"Descuento anual","icon":"local_offer","enabled":true}]}'
WHERE name = 'Plan Anual'; 