-- Criar tabela de status
CREATE TABLE status (
                        id SERIAL PRIMARY KEY,
                        nome VARCHAR(50) NOT NULL UNIQUE,
                        ordem INT NOT NULL,
                        cor VARCHAR(20)
);

-- Inserir os status padrão
INSERT INTO status (nome, ordem, cor) VALUES ('pendente', 1, '#FFA500');
INSERT INTO status (nome, ordem, cor) VALUES ('pronto', 2, '#008000');
INSERT INTO status (nome, ordem, cor) VALUES ('pendente pagamento', 3, '#0000FF');

-- Adicionar a nova coluna SEM o NOT NULL
ALTER TABLE servico ADD COLUMN id_status INTEGER REFERENCES status(id);

-- Atualizar os dados com base na coluna antiga
UPDATE servico s
SET id_status = (SELECT id FROM status WHERE nome = s.status)
WHERE s.status IS NOT NULL;

-- Agora sim: tornar a coluna NOT NULL, pois já foi preenchida
ALTER TABLE servico ALTER COLUMN id_status SET NOT NULL;

-- Opcional: remover a coluna antiga, se não for mais necessária
-- ALTER TABLE servico DROP COLUMN status;
