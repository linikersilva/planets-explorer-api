<h1 align="center">Planets Explorer API</h1>

> Essa API foi desenvolvida para um desafio backend de nível pleno para a empresa Elo7. Ela simula um case de negócio onde é possível controlar sondas espaciais em outros planetas por meio de comandos.

![img.png](img.png)

## Detalhes do Projeto
* <a href="https://gist.github.com/elo7-developer/f0b91a7a98e5e65288b875ac6d376875">`https://gist.github.com/elo7-developer/f0b91a7a98e5e65288b875ac6d376875`</a>
####

## Como Executar Este Projeto
* É necessário **Java 21** para executar este projeto.
####
* É necessário **Maven 3.6.3** ou superior para executar este projeto.
####
* É necessário **MySQL 8.0** ou superior para executar este projeto.
####
* É necessário ter o banco de dados já criado na sua máquina para executar este projeto.

  O script de SQL necessário para criar o banco de dados deste projeto é esse:

  <a href="https://drive.google.com/file/d/1k6jJdItsF7lvVALXq9__UxrtA5GETsIx/view">`https://drive.google.com/file/d/1k6jJdItsF7lvVALXq9__UxrtA5GETsIx/view`</a>
####
* Uma vez que atenda aos requisitos anteriores, pode clonar ou baixar o projeto.
####
* Após clonar ou baixar o projeto em sua máquina, é necessário rodá-lo com sua IDE de preferência.
####
* Com o projeto rodando, crie seu usuário no endpoint **_POST /users_**.  Informe o seguinte payload na request (mudando seu email e senha):
  
      { 
          "email": "seuemail@gmail.com",
          "password": "suasenha",
          "roleId": 2,
          "creatorId": 1
      }
####
* Agora, efetue login com o email e senha desse usuário criado no endpoint **_POST /users/login_**. Informe o seguinte payload na request (mudando seu email e senha):

      {
          "email": "seuemail@gmail.com",
          "password": "suasenha"
      }
####
* Copie o bearer token recebido no response e use-o para fazer as demais requisições no sistema.
####
* Se todos os passos forem seguidos corretamente então o projeto executará sem problemas.


