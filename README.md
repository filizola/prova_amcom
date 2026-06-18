# Serviço Order

Serviço Java Spring Boot para gerenciamento de pedidos, integração com Produto Externo A (recebimento) e Produto Externo B (consulta), com cálculo automático do valor total dos produtos.

## Requisitos

- Java 26 (JDK local; bytecode `release 25` por compatibilidade com Spring Boot)
- Maven 3.9+
- Docker (para PostgreSQL local)

## Execução local

```bash
# 1. Subir banco de dados
docker compose up -d

# 2. Compilar e executar
mvn spring-boot:run

# 3. Verificar saúde
curl http://localhost:8080/actuator/health
```

A documentação interativa da API fica em: http://localhost:8080/swagger-ui.html

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `order_db` | Nome do banco |
| `DB_USER` | `order_user` | Usuário do banco |
| `DB_PASSWORD` | `order_pass` | Senha do banco |
| `SYSTEM_A_API_KEY` | `dev-system-a-key` | Chave do Produto Externo A |
| `SYSTEM_B_API_KEY` | `dev-system-b-key` | Chave do Produto Externo B |

## Endpoints principais

### Produto Externo A — Envio de pedidos

```http
POST /api/v1/integration/system-a/orders
X-API-Key: dev-system-a-key
Content-Type: application/json

{
  "externalId": "PED-12345",
  "customerName": "Cliente ABC",
  "currency": "BRL",
  "items": [
    {
      "productCode": "SKU-001",
      "productName": "Produto Exemplo",
      "quantity": 3,
      "unitPrice": 12.50
    }
  ]
}
```

### Produto Externo B — Consulta de pedidos

```http
GET /api/v1/integration/system-b/orders?status=PROCESSED&page=0&size=20
X-API-Key: dev-system-b-key

GET /api/v1/integration/system-b/orders/{id}
GET /api/v1/integration/system-b/orders/external/{externalId}
GET /api/v1/integration/system-b/orders/{id}/status
```
