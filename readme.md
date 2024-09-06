<h1 align="center">Planets Explorer API</h1>

> Essa API foi desenvolvida para um desafio backend de nível pleno para a empresa Elo7. Ela simula um case de negócio onde é possível controlar sondas espaciais em outros planetas por meio de comandos.

![img.png](img.png)

# Detalhes do Projeto
* <a href="https://gist.github.com/elo7-developer/f0b91a7a98e5e65288b875ac6d376875">`https://gist.github.com/elo7-developer/f0b91a7a98e5e65288b875ac6d376875`</a>
####

# Como Executar Este Projeto

Esta API é containerizada com Docker e Docker Compose, então você precisará das seguintes ferramentas instaladas na sua máquina:

### 1. Docker

**Docker** é uma plataforma para construir, rodar e gerenciar **<a href="https://cloud.google.com/learn/what-are-containers?hl=pt-BR">contêineres</a>**.

- **Instalação:**
  - **[Windows](https://docs.docker.com/desktop/install/windows-install/)** (no caso do Windows, precisa instalar o **<a href="https://learn.microsoft.com/pt-br/windows/wsl/about">WSL2</a>** antes)

    - Para instalar o WSL2, basta abrir o PowerShell e digitar `wsl --install`, após a instalação terminar, reinicie o computador. Quando o computador ligar novamente, o terminal do WSL já abrirá sozinho, e então basta configurar um usuário e senha e fechá-lo.
  - **[macOS](https://docs.docker.com/desktop/install/mac-install/)**
  - **[Linux](https://docs.docker.com/engine/install/)**

### 2. Docker Compose

**Docker Compose** é uma ferramenta para definir e executar aplicativos multi-contêineres Docker (casos em que a aplicação tem mais de um contêiner). No caso dessa API, tem um contêiner para a aplicação em si e um contêiner para o banco de dados MySQL.

- **Instalação:**
  - Docker Desktop (para Windows e macOS) - **já inclui o Docker Compose**.
  - No Linux, instale o Docker Compose separadamente seguindo **[estas instruções](https://docs.docker.com/compose/install/)**.


### AS PRÓXIMAS ETAPAS SÃO OPCIONAIS, DEVEM SER SEGUIDAS APENAS SE FOR NECESSÁRIO DESENVOLVER.

### 3. Java Development Kit (JDK) (para desenvolvimento se necessário)

Se você precisar desenvolver ou testar localmente fora do Docker, você precisará do **<a href="https://www.devmedia.com.br/introducao-ao-java-jdk/28896">JDK</a>** correspondente à versão dessa aplicação.

- **Instalação:**
  - **[JDK 21](https://www.oracle.com/java/technologies/downloads/?er=221886#jdk21-windows)**

### 4. Maven (para desenvolvimento se necessário)

Se você precisar compilar ou rodar testes localmente fora do Docker, você precisará do **<a href="https://www.devmedia.com.br/introducao-ao-maven/25128">Apache Maven</a>**.

- **Instalação:**
  - **[Apache Maven](https://maven.apache.org/install.html)**

### 5. MySQL (para desenvolvimento se necessário)

Se você precisar interagir diretamente com o banco de dados localmente, você precisará do **<a href="https://www.oracle.com/br/mysql/what-is-mysql/">MySQL</a>** instalado ou de uma ferramenta de gerenciamento de banco de dados.

- **Instalação:**
  - **[MySQL Community Server](https://dev.mysql.com/downloads/mysql/)**

### 6. Ferramentas de Linha de Comando

- **Git**: Para controle de versão e clonar repositórios.
  - **[Instalação Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)**

- **Postman**: Para testar suas APIs.
  - **[Postman](https://www.postman.com/downloads/)**

## Executando a Aplicação

1. **Clone o repositório:**

    ```bash
    git clone https://github.com/linikersilva/planets-explorer-api.git
    cd planets-explorer-api
    ```

2. **Entre na pasta do projeto:**

    ```bash
    cd planets-explorer-api
    ```

3. **Inicie os contêineres:**

    ```bash
    docker-compose up --build
    ```

   Isso irá construir e iniciar os contêineres definidos no arquivo `docker-compose.yml`.


4. **Com o projeto rodando, crie seu usuário no endpoint **_POST /users_**.  Informe o seguinte payload na request (mudando seu email e senha):**
  
       { 
           "email": "seuemail@gmail.com",
           "password": "suasenha",
           "roleId": 2,
           "creatorId": 1
       }

5. **Agora, efetue login com o email e senha desse usuário criado no endpoint **_POST /users/login_**. Informe o seguinte payload na request (mudando seu email e senha):**

       {
           "email": "seuemail@gmail.com",
           "password": "suasenha"
       }
* Copie o bearer token recebido no response e use-o para fazer as demais requisições no sistema.


