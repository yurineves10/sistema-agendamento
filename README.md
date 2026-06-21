# Sistema de Agendamento 📅

API REST para gerenciamento de agendamentos com autenticação JWT.

## Documentação da API

![Swagger](https://raw.githubusercontent.com/yurineves10/sistema-agendamento/main/docs/images/swagger.png)

## Tecnologias

- Java 21 + Spring Boot 4
- Spring Security + JWT
- PostgreSQL + Spring Data JPA
- Swagger/OpenAPI
- JUnit 5 + Mockito

## Funcionalidades

- Cadastro e autenticação de clientes e profissionais com JWT
- Criação de agendamentos com validação de conflito de horários
- Confirmação e cancelamento de agendamentos
- Validação de dados de entrada com mensagens claras
- Documentação automática com Swagger
- Testes unitários com 19 testes cobrindo os principais fluxos

## Como rodar

### Pré-requisitos
- Java 21+
- PostgreSQL
- Maven

### Configuração

1. Clone o repositório
   git clone https://github.com/yurineves10/sistema-agendamento.git

2. Configure as variáveis de ambiente
   DB_PASSWORD=sua_senha_postgres
   JWT_SECRET=sua_chave_jwt_256bits

3. Crie o banco de dados
   CREATE DATABASE agendamento_db;

4. Rode o projeto
   ./mvnw spring-boot:run

5. Acesse a documentação
   http://localhost:8080/swagger-ui/index.html

## Endpoints

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | /auth/registro | Cadastro de cliente | ❌ |
| POST | /auth/login | Login de cliente | ❌ |
| POST | /profissionais/auth/registro | Cadastro de profissional | ❌ |
| POST | /profissionais/auth/login | Login de profissional | ❌ |
| GET | /profissionais | Listar profissionais ativos | ✅ |
| POST | /agendamentos | Criar agendamento | ✅ |
| GET | /agendamentos/meus | Meus agendamentos | ✅ |
| PUT | /agendamentos/{id}/confirmar | Confirmar agendamento | ✅ |
| PUT | /agendamentos/{id}/cancelar | Cancelar agendamento | ✅ |