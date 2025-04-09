CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    contato VARCHAR(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    data_cadastro DATE NOT NULL,
    cpfcnpj VARCHAR(20) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    preco DECIMAL(8, 2) NOT NULL
);

CREATE TABLE servico (
    id SERIAL PRIMARY KEY,
    dt_movimento DATE NOT NULL,
    dt_entrega DATE NOT NULL,
    observacao VARCHAR(255),
    id_cliente INTEGER NOT NULL REFERENCES clientes(id),
    id_usuario INTEGER NOT NULL REFERENCES usuarios(id),
    status VARCHAR(20) NOT NULL DEFAULT 'pendente'
);

CREATE TABLE servico_produto (
    id SERIAL PRIMARY KEY,
    id_servico INTEGER NOT NULL REFERENCES servico(id),
    id_produto INTEGER NOT NULL REFERENCES produto(id),
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(8, 2) NOT NULL,
    observacao VARCHAR(255),
    sequencia INTEGER NOT NULL
);
