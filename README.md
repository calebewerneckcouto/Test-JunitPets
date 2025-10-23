🐕 Adopet API - Sistema de Adoção de Pets
<div align="center">
https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java
https://img.shields.io/badge/Spring_Boot-3.1.0-brightgreen?style=for-the-badge&logo=springboot
https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql
https://img.shields.io/badge/Swagger-3.0-green?style=for-the-badge&logo=swagger

API RESTful para gerenciamento completo de adoção de animais

Documentação da API •
Como Usar •
Endpoints •
Desenvolvimento

</div>
📋 Índice
Visão Geral

Funcionalidades

Tecnologias

Como Usar

Endpoints

Documentação da API

Modelos de Dados

Desenvolvimento

Adaptações Realizadas

Deploy

🚀 Visão Geral
A Adopet API é uma solução completa para gerenciar o processo de adoção de animais. Desenvolvida em Java com Spring Boot, oferece endpoints RESTful para cadastro de abrigos, tutores, pets e todo o fluxo de adoções.

✨ Destaques
✅ API RESTful completa e documentada

✅ PostgreSQL com consultas otimizadas

✅ Paginação avançada com ordenação

✅ Validações robustas de negócio

✅ Documentação interativa com Swagger UI

✅ Pronta para produção com tratamento de erros

🎯 Funcionalidades
🏠 Gestão de Abrigos
✅ Cadastro e listagem de abrigos

✅ Gestão de pets por abrigo

✅ Busca por ID ou nome

👥 Gestão de Tutores
✅ Cadastro, atualização e listagem de tutores

✅ Paginação avançada com ordenação

✅ Exclusão segura com validações

✅ Validação de dados únicos (email, telefone)

🐾 Gestão de Pets
✅ Cadastro de pets em abrigos

✅ Listagem de pets disponíveis para adoção

✅ Diferenciação por tipo (Cachorro/Gato)

❤️ Processo de Adoção
✅ Solicitação de adoção

✅ Aprovação/reprovação de adoções

✅ Validação de regras de negócio

✅ Impedimento de adoções duplicadas

🛠 Tecnologias
Backend:

Java 17 - Linguagem principal

Spring Boot 3.1.0 - Framework web

Spring Data JPA - Persistência de dados

Spring Validation - Validações de entrada

SpringDoc OpenAPI 3 - Documentação da API

Banco de Dados:

PostgreSQL 15 - Banco relacional

Hibernate - ORM

Flyway - Migrations (opcional)

Ferramentas:

Maven - Gerenciamento de dependências

Swagger UI - Documentação interativa

Postman - Testes de API

🚀 Como Usar
Pré-requisitos
Java 17 ou superior

PostgreSQL 12+

Maven 3.6+

📥 Instalação
Clone o repositório:

bash
git clone https://github.com/seu-usuario/adopet-api.git
cd adopet-api
Configure o banco de dados:

sql
CREATE DATABASE adopet;
Configure as variáveis de ambiente:

properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/adopet
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
Execute a aplicação:

bash
mvn spring-boot:run
🐳 Docker (Opcional)
bash
# Subir PostgreSQL
docker-compose up -d postgres

# Executar aplicação
mvn spring-boot:run
📡 Endpoints
🏠 Abrigos
Método	Endpoint	Descrição
GET	/abrigos	Lista todos os abrigos
POST	/abrigos	Cadastra novo abrigo
GET	/abrigos/{idOuNome}/pets	Lista pets do abrigo
POST	/abrigos/{idOuNome}/pets	Cadastra pet no abrigo
👥 Tutores
Método	Endpoint	Descrição
GET	/tutores	Lista tutores (paginado)
POST	/tutores	Cadastra novo tutor
PUT	/tutores	Atualiza tutor existente
DELETE	/tutores/{id}	Exclui tutor
🐾 Pets
Método	Endpoint	Descrição
GET	/pets	Lista pets disponíveis
❤️ Adoções
Método	Endpoint	Descrição
POST	/adocoes	Solicita adoção
PUT	/adocoes/aprovar	Aprova adoção
PUT	/adocoes/reprovar	Reprova adoção
📚 Documentação da API
Acesse a Documentação Interativa
Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI JSON: http://localhost:8080/api-docs

📖 Exemplo de Uso
1. Cadastrar Abrigo
bash
curl -X POST http://localhost:8080/abrigos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Abrigo dos Bichinhos",
    "telefone": "11999999999",
    "email": "abrigo@email.com"
  }'
2. Cadastrar Tutor
bash
curl -X POST http://localhost:8080/tutores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "telefone": "11988887777", 
    "email": "joao@email.com"
  }'
3. Listar Tutores (Paginado)
bash
curl "http://localhost:8080/tutores?page=0&size=5&sort=nome,asc"
📊 Modelos de Dados
Abrigo
json
{
  "id": 1,
  "nome": "Abrigo dos Bichinhos",
  "telefone": "11999999999",
  "email": "abrigo@email.com"
}
Tutor
json
{
  "id": 1,
  "nome": "João Silva", 
  "telefone": "11988887777",
  "email": "joao@email.com"
}
Pet
json
{
  "id": 1,
  "tipo": "CACHORRO",
  "nome": "Rex",
  "raca": "Vira-lata",
  "idade": 3,
  "cor": "Caramelo",
  "peso": 12.5
}
Adoção
json
{
  "idPet": 1,
  "idTutor": 1,
  "motivo": "Tenho muito amor para dar"
}
💻 Desenvolvimento
Estrutura do Projeto
text
src/
├── main/
│   ├── java/br/com/alura/adopet/api/
│   │   ├── controller/     # Controladores REST
│   │   ├── service/        # Lógica de negócio
│   │   ├── repository/     # Acesso a dados
│   │   ├── model/          # Entidades JPA
│   │   ├── dto/            # Objetos de transferência
│   │   └── config/         # Configurações
│   └── resources/
│       └── application.properties
└── test/                   # Testes
🔧 Comandos Úteis
bash
# Compilar projeto
mvn clean compile

# Executar testes
mvn test

# Empacotar aplicação
mvn clean package

# Executar com profile dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
🧪 Testando a API
Use o Swagger UI para testar endpoints interativamente

Coleção Postman incluída na documentação

Exemplos completos disponíveis na documentação

🔄 Adaptações Realizadas
Principais Melhorias Implementadas
1. 🗄️ Migração para PostgreSQL
Otimização de consultas complexas

Compatibilidade completa com Spring Data JPA

2. 📄 Sistema de Paginação
Listagem paginada de tutores

Parâmetros: page, size, sort

Resolução de problemas de compatibilidade

3. 🗑️ Operação de Exclusão Segura
Validação de integridade referencial

Tutor com pets não pode ser excluído

Tratamento adequado de exceções

4. 📚 Documentação com Swagger
Documentação interativa completa

Exemplos de request/response

Interface amigável para desenvolvedores

5. 🔧 Correções Técnicas
Problemas de sintaxe SQL com PostgreSQL

Otimização de consultas de existência

Correção de relacionamentos JPA

⚡ Performance
Consultas nativas otimizadas para PostgreSQL

Paginação eficiente para grandes volumes

Validações no banco para consistência

🚀 Deploy
Ambiente de Produção
properties
# application-prod.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
Variáveis de Ambiente
bash
export DATABASE_URL=jdbc:postgresql://host:5432/adopet
export DATABASE_USERNAME=usuario
export DATABASE_PASSWORD=senha
Health Check
http
GET /actuator/health
🤝 Contribuição
Faça o fork do projeto

Crie uma branch para sua feature (git checkout -b feature/AmazingFeature)

Commit suas mudanças (git commit -m 'Add some AmazingFeature')

Push para a branch (git push origin feature/AmazingFeature)

Abra um Pull Request

📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

📞 Suporte
Documentação: Swagger UI

Issues: GitHub Issues

Email: suporte@adopet.com

<div align="center">
Desenvolvido com ❤️ para ajudar animais a encontrarem um lar

⬆ Voltar ao topo

</div>
