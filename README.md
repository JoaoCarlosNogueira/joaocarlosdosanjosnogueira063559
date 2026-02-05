
### **Candidato:** João Carlos dos Anjos Nogueira

### **Vaga:** Desenvolvedor Back-end


# 🎼 Music Catalog API - Documentação Técnica de Arquitetura

Esta API foi desenvolvida seguindo os padrões da **Clean Architecture** e princípios **SOLID**, focando em uma estrutura escalável para o gerenciamento de catálogos musicais e integração com serviços externos.

## 1. Arquitetura e Estrutura

### 🏗️ Padrão de Camadas (API)

 A aplicação é estritamente uma **API RESTful** distribuída em camadas: **Controller** (Entrada), **Service** (Negócio), **Repository** (Dados) e **Infrastructure** (Detalhes).

### 🔒 Segurança e Autenticação Stateless

* **Decisão**: Implementação de segurança via **JWT (JSON Web Token)** com suporte a **Refresh Tokens** persistidos.
* **Justificativa**: Priorizamos a escalabilidade. O uso de JWT torna a API **stateless**, permitindo que o servidor não precise armazenar sessões, facilitando o balanceamento de carga e o uso de containers Docker.

## 2. Observabilidade e Saúde do Sistema (Health Check)

### 🏥 Monitoramento com Spring Actuator

* **Decisão**: Implementação de endpoints de monitoramento e um indicador de saúde personalizado para o storage.
* **Justificativa**: Priorizamos a confiabilidade operacional. Através do **Actuator**, o sistema fornece métricas em tempo real sobre o estado da aplicação e suas dependências.

**Endpoints de Saúde Disponíveis:**

* **Status Geral**: `GET /actuator/health`
* Exibe o estado de saúde da API, Banco de Dados (PostgreSQL) e Storage (MinIO).


* **Detalhes do MinIO**: Incluímos o `MinioHealthIndicator` que valida se o bucket `music-covers` está acessível.
* **Sucesso**: `{ "status": "UP", "details": { "MinioStorage": { "status": "UP", "bucket": "music-covers" } } }`.


* **Métricas e Info**: `GET /actuator/metrics` e `GET /actuator/info` para coleta de dados por sistemas de monitoramento externos.

## 3. Gerenciamento de Arquivos e Infraestrutura

### ☁️ Abstração de Storage (Interface `FileStorage`)

* **Decisão**: Criação do contrato `FileStorage` com implementação concreta via MinIO.
* **Justificativa**: Aplicamos a **Inversão de Dependência**. A lógica de negócio de Álbuns depende apenas da abstração de armazenamento, permitindo trocar o MinIO por AWS S3 ou Azure Blob sem alterar o código do `AlbumService`.

## 4. Evolução do Banco de Dados

### 🧬 Migrations Atômicas (Flyway)

* **Decisão**: Organização de migrations sequenciais (`V1` a `V9`), separando estritamente **DDL** (Estrutura) de **DML** (Dados Iniciais).
* **Justificativa**: Priorizamos a integridade referencial. Arquivos como `V7` e `V8` garantem que os dados base existam antes que as associações em `V9` tentem referenciá-los, evitando falhas de chave estrangeira durante o deploy.

## 5. Resiliência e Qualidade

### 🛡️ Tratamento de Erros e Rate Limit

* **Decisão**: Centralização de exceções em um pacote `exception` e implementação de limite de requisições.
* **Justificativa**:
* **UX**: O `GlobalExceptionHandler` garante que erros retornem um formato JSON fixo: `{ "timestamp", "status", "error", "message" }`.
* **Segurança**: O `RateLimitInterceptor` protege a API contra ataques de força bruta ou excesso de consumo, limitando a 10 requisições por minuto por IP.



### 🧪 Testes de Integração (Conformidade Spring 3.4+)

* **Decisão**: Uso de `@MockitoBean` para mockar infraestrutura (MinIO/WebSocket) em testes de API.
* **Justificativa**: Priorizamos testes rápidos e isolados. Mocks permitem validar o comportamento do `AlbumController` sem precisar de um servidor MinIO real rodando durante o build do CI/CD.

---

**Nota Final**: Esta API prioriza a **padronização técnica** (DTOs, HTTP Status corretos) e a **segurança** (CORS, JWT, Rate Limit), garantindo um produto pronto para produção e de fácil integração.




Para facilitar o acesso e monitorização da **MusicCatalog-API**, aqui estão os links diretos para as interfaces de gestão e observabilidade do projeto:

### 🚀 Interfaces de Desenvolvimento e Documentação

* **Swagger UI (Documentação da API)**: [http://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)
* Utilize este link para testar os endpoints de Álbuns, Artistas e Regionais em tempo real.


* **MinIO Console (Gestão de Storage)**: [http://localhost:9001](https://www.google.com/search?q=http://localhost:9001)
* **User**: `admin` | **Password**: `admin123`
* Aceda para visualizar as capas dos álbuns armazenadas no bucket `music-covers`.



### 🏥 Monitoramento e Saúde (Observabilidade)

* **Health Check Geral**: [http://localhost:8080/actuator/health](https://www.google.com/search?q=http://localhost:8080/actuator/health)
* Fornece o estado do Banco de Dados e da API.


* **Health Check Detalhado (Storage)**: [http://localhost:8080/actuator/health/MinioStorage](https://www.google.com/search?q=http://localhost:8080/actuator/health/MinioStorage)
* Link específico para verificar a conectividade com o servidor de imagens.


* **Métricas de Desempenho**: [http://localhost:8080/actuator/metrics](https://www.google.com/search?q=http://localhost:8080/actuator/metrics)
* Exibe dados técnicos sobre consumo de memória e threads da JVM.



### 🛠️ Configuração de Redes

* **API Base URL**: `http://localhost:8080/v1`
* **MinIO API Endpoint**: `http://localhost:9000`

> **Nota**: Estes links funcionam enquanto os containers Docker estiverem ativos (`docker compose up`). Caso esteja a rodar a aplicação fora do Docker, verifique se as portas 8080 e 9000 estão disponíveis no seu `localhost`.
