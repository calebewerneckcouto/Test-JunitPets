# Adopet API

API REST para sistema de adoção de pets, desenvolvida para conectar abrigos de animais com tutores interessados em adotar.

## 📋 Sobre o Projeto

A Adopet API é um sistema completo que gerencia o processo de adoção de pets, permitindo o cadastro de abrigos, pets, tutores e o controle do fluxo de solicitações de adoção.

## 🚀 Tecnologias

- Java
- Spring Boot
- OpenAPI 3.0

## 🔧 Configuração

### Servidor de Desenvolvimento
```
URL Base: http://localhost:8080
```

## 📚 Documentação da API

### Abrigos

#### Listar todos os abrigos
```http
GET /abrigos
```

**Resposta:** Lista de todos os abrigos cadastrados

#### Cadastrar novo abrigo
```http
POST /abrigos
```

**Body:**
```json
{
  "nome": "string",
  "telefone": "string",
  "email": "string"
}
```

#### Listar pets de um abrigo
```http
GET /abrigos/{idOuNome}/pets
```

**Parâmetros:**
- `idOuNome` (path) - ID ou nome do abrigo

#### Cadastrar pet em um abrigo
```http
POST /abrigos/{idOuNome}/pets
```

**Body:**
```json
{
  "tipo": "GATO | CACHORRO",
  "nome": "string",
  "raca": "string",
  "idade": 0,
  "cor": "string",
  "peso": 0.0
}
```

### Tutores

#### Listar tutores
```http
GET /tutores?page=0&size=10
```

**Parâmetros de paginação:**
- `page` - Número da página (mínimo: 0)
- `size` - Tamanho da página (mínimo: 1)
- `sort` - Ordenação (opcional)

#### Cadastrar tutor
```http
POST /tutores
```

**Body:**
```json
{
  "nome": "string",
  "telefone": "string",
  "email": "string"
}
```

#### Atualizar tutor
```http
PUT /tutores
```

**Body:**
```json
{
  "id": 0,
  "nome": "string",
  "telefone": "string",
  "email": "string"
}
```

#### Excluir tutor
```http
DELETE /tutores/{id}
```

### Pets

#### Listar pets disponíveis
```http
GET /pets
```

**Resposta:** Lista de todos os pets disponíveis para adoção

### Adoções

#### Solicitar adoção
```http
POST /adocoes
```

**Body:**
```json
{
  "idPet": 0,
  "idTutor": 0,
  "motivo": "string"
}
```

#### Aprovar adoção
```http
PUT /adocoes/aprovar
```

**Body:**
```json
{
  "idAdocao": 0
}
```

#### Reprovar adoção
```http
PUT /adocoes/reprovar
```

**Body:**
```json
{
  "idAdocao": 0,
  "justificativa": "string"
}
```

## 📝 Regras de Validação

### Telefone
Formato aceito: `(XX)XXXXX-XXXX` ou `(XX)XXXX-XXXX`

Padrão regex: `\(?\d{2}\)?\d?\d{4}-?\d{4}`

### Tipos de Pet
- `GATO`
- `CACHORRO`

## 📫 Contato e Suporte

- **Website:** https://www.adopet.com
- **Email:** suporte@adopet.com

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](https://choosealicense.com/licenses/mit/) para mais detalhes.

## 🔍 Status Codes

| Status | Descrição |
|--------|-----------|
| 200 | Requisição bem-sucedida |
| 400 | Dados inválidos ou erro de validação |
| 404 | Recurso não encontrado |

## 💡 Exemplos de Uso

### Cadastrar um abrigo e adicionar um pet
```bash
# 1. Cadastrar abrigo
curl -X POST http://localhost:8080/abrigos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Abrigo Amigo Fiel",
    "telefone": "(11)98765-4321",
    "email": "contato@amigofiel.com"
  }'

# 2. Adicionar pet ao abrigo
curl -X POST http://localhost:8080/abrigos/1/pets \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "CACHORRO",
    "nome": "Rex",
    "raca": "Labrador",
    "idade": 3,
    "cor": "Dourado",
    "peso": 25.5
  }'
```

### Solicitar uma adoção
```bash
# 1. Cadastrar tutor
curl -X POST http://localhost:8080/tutores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Silva",
    "telefone": "(11)91234-5678",
    "email": "maria@email.com"
  }'

# 2. Solicitar adoção
curl -X POST http://localhost:8080/adocoes \
  -H "Content-Type: application/json" \
  -d '{
    "idPet": 1,
    "idTutor": 1,
    "motivo": "Sempre quis ter um cachorro e tenho um quintal espaçoso"
  }'
```
