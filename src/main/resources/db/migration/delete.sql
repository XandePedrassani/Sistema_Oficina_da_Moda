DELETE FROM flyway_schema_history WHERE version = '1'; -- ou o número da migração problemática
-- Comando para dropar todas as tabelas (execute em uma única transação)
BEGIN;
DROP TABLE IF EXISTS servico_produto CASCADE;
DROP TABLE IF EXISTS servico CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
COMMIT;