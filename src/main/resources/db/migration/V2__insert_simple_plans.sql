-- Actualizar planes con características básicas
UPDATE membership_plans SET features = '{"features":[{"name":"Acceso completo a la plataforma","icon":"check","enabled":true}]}' WHERE name = 'Plan Mensual';

UPDATE membership_plans SET features = '{"features":[{"name":"Acceso completo a la plataforma","icon":"check","enabled":true},{"name":"Soporte prioritario","icon":"support","enabled":true}]}' WHERE name = 'Plan Anual'; 