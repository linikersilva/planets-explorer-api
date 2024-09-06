CREATE DATABASE IF NOT EXISTS planets_explorer;
USE planets_explorer;

CREATE TABLE tipo_de_acesso
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(30) NOT NULL UNIQUE,
    data_de_criacao DATETIME NOT NULL,
    data_de_atualizacao DATETIME NOT NULL
);

CREATE TABLE usuario
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_de_acesso_fk INT NOT NULL,
    usuario_criador INT,
    data_de_criacao DATETIME NOT NULL,
    data_de_atualizacao DATETIME NOT NULL,
    ultimo_usuario_que_atualizou INT,
    FOREIGN KEY (tipo_de_acesso_fk) REFERENCES tipo_de_acesso (id),
    FOREIGN KEY (usuario_criador) REFERENCES usuario (id),
    FOREIGN KEY (ultimo_usuario_que_atualizou) REFERENCES usuario (id)
);

CREATE TABLE planeta
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    largura INT NOT NULL,
    altura INT NOT NULL,
    ocupacao_maxima INT NOT NULL,
    usuario_criador INT NOT NULL,
    data_de_criacao DATETIME NOT NULL,
    data_de_atualizacao DATETIME NOT NULL,
    ultimo_usuario_que_atualizou INT NOT NULL,
    FOREIGN KEY (usuario_criador) REFERENCES usuario (id),
    FOREIGN KEY (ultimo_usuario_que_atualizou) REFERENCES usuario (id)
);

CREATE TABLE sonda
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    x INT,
    y INT,
    direcao INT,
    planeta_fk INT,
    usuario_dono INT NOT NULL,
    usuario_criador INT NOT NULL,
    data_de_criacao DATETIME NOT NULL,
    data_de_atualizacao DATETIME NOT NULL,
    ultimo_usuario_que_atualizou INT NOT NULL,
    FOREIGN KEY (planeta_fk) REFERENCES planeta (id),
    FOREIGN KEY (usuario_dono) REFERENCES usuario (id),
    FOREIGN KEY (usuario_criador) REFERENCES usuario (id),
    FOREIGN KEY (ultimo_usuario_que_atualizou) REFERENCES usuario (id),
    CHECK (direcao IN (1, 2, 3, 4))
);

INSERT INTO tipo_de_acesso
(nome, data_de_criacao, data_de_atualizacao)
VALUES
('ROLE_BASIC', NOW(), NOW()),
('ROLE_ADMIN', NOW(), NOW());

INSERT INTO usuario
(email, senha, tipo_de_acesso_fk, usuario_criador, data_de_criacao, data_de_atualizacao, ultimo_usuario_que_atualizou)
VALUES
('admin@gmail.com', '$2a$10$XBa35smON.L5jgorQDx8xOix5C5qI7U.7YyP35dQFSep2bDyujpAa', 2, null, NOW(), NOW(), null),
('basico1@gmail.com', '$2a$10$XBa35smON.L5jgorQDx8xOix5C5qI7U.7YyP35dQFSep2bDyujpAa', 1, 1, NOW(), NOW(), 1),
('basico2@gmail.com', '$2a$10$XBa35smON.L5jgorQDx8xOix5C5qI7U.7YyP35dQFSep2bDyujpAa', 1, 1, NOW(), NOW(), 1);

INSERT INTO planeta
(nome, largura, altura, ocupacao_maxima, usuario_criador, data_de_criacao, data_de_atualizacao, ultimo_usuario_que_atualizou)
VALUES
('TERRA', 5, 5, 25, 1, NOW(), NOW(), 1),
('MARTE', 8, 3, 24, 1, NOW(), NOW(), 1);

INSERT INTO sonda
(x, y, direcao, planeta_fk, usuario_dono, usuario_criador, data_de_criacao, data_de_atualizacao, ultimo_usuario_que_atualizou)
VALUES
(1, 2, 1, 1, 1, 1, NOW(), NOW(), 1),
(3, 3, 2, 1, 2, 1, NOW(), NOW(), 1),
(8, 2, 3, 2, 3, 1, NOW(), NOW(), 1);