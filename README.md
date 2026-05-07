#Catálogo de Produtos com Spring Security

Um sistema completo de gerenciamento de catálogo de produtos construído com **Spring Boot**, **Thymeleaf** e **Spring Security**. O projeto inclui autenticação de usuários, controle de acesso baseado em perfis (Role-Based Access Control - RBAC) e relacionamento de entidades utilizando **PostgreSQL**.

## Tecnologias e Arquitetura
* **Java 17+** * **Spring Boot 3**
* **Spring Security** (Criptografia BCrypt e Proteção de Rotas)
* **Spring Data JPA / Hibernate** (Mapeamento @ManyToOne)
* **Thymeleaf** & Thymeleaf Extras Security (Renderização e UI Dinâmica)
* **PostgreSQL** (Banco de Dados Relacional)
* **Bootstrap 5** 

##  Pré-requisitos
Antes de rodar o projeto, você precisará ter instalado em sua máquina:
* [JDK 17 ou superior](https://adoptium.net/)
* [PostgreSQL](https://www.postgresql.org/download/)
* Maven

##  Configuração do Banco de Dados (PostgreSQL)

Para que o projeto funcione localmente, é necessário configurar a conexão com o banco de dados. O Hibernate (JPA) cuidará de criar as tabelas automaticamente para você.


1. Abra o seu PostgreSQL (via pgAdmin, DBeaver ou terminal) e crie um banco de dados vazio. Recomendamos o nome `catalogo_db`:
   ```sql
   CREATE DATABASE catalogo_db;
   

2. No projeto, navegue até a pasta src/main/resources/ e abra (ou crie) o arquivo application.properties.

Adicione as seguintes configurações. Atenção: Lembre-se de substituir seu_usuario e sua_senha pelas credenciais reais do seu PostgreSQL instalado na sua máquina:

# ----------------------------------------
# CONEXÃO COM O BANCO DE DADOS POSTGRESQL
# ----------------------------------------
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver

# ----------------------------------------
# CONFIGURAÇÕES DO JPA / HIBERNATE
# ----------------------------------------
# O comando "update" cria as tabelas automaticamente se não existirem
spring.jpa.hibernate.ddl-auto=update
# Exibe os comandos SQL no console para debug
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
"# Projeto_Academico_JumpNet" 
