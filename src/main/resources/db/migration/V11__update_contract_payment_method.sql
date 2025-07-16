-- V11: Actualizar pagos de contratos para usar CONTRACT_PAYMENT

-- Actualizar todos los pagos que corresponden a membresías de contrato
UPDATE membership_payments mp
INNER JOIN user_memberships um ON mp.membership_id = um.id
SET mp.payment_method = 'CONTRACT_PAYMENT',
    mp.description = 'Pago de contrato con entrenador',
    mp.updated_at = NOW()
WHERE um.membership_type = 'CONTRACT'
  AND mp.payment_method = 'OTHER'
  AND mp.description LIKE '%contrato%'; 