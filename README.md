# 🎵 Gerenciador de Artistas e Álbuns

## Processo Seletivo SEPLAG/MT 2026 - Projeto Prático

---

### 📋 Informações do Candidato

| Campo | Valor |
|-------|-------|
| **Candidato** | Jader Fellipe de Oliveira e Silva Golembiouski |
| **CPF (6 primeiros dígitos)** | 012.389 |
| **Cargo** | Analista de TI - Perfil Engenheiro da Computação (Sênior) |
| **Perfil do Projeto** | Full Stack |

---

## 📖 Sumário

1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Estrutura de Dados (Schemas)](#2-estrutura-de-dados-schemas)
3. [Funcionalidades Sênior Implementadas](#3-funcionalidades-sênior-implementadas)
4. [Orquestração e Execução](#4-orquestração-e-execução)
5. [Qualidade e Monitoramento](#5-qualidade-e-monitoramento)

---

## 1. Visão Geral da Arquitetura

### 🔧 Back-end: Java Spring Boot 4.x

A aplicação segue uma **arquitetura em camadas** bem definida, garantindo separação de responsabilidades, manutenibilidade e testabilidade:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CONTROLLER LAYER                            │
│  - Recebe requisições HTTP e valida entrada                         │
│  - Endpoints RESTful: /api/v1/artistas, /api/v1/albums, /api/v1/auth│
│  - Retorna DTOs formatados (Presenter DTOs)                         │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          SERVICE LAYER                              │
│  - Regras de negócio e orquestração                                 │
│  - Transações (@Transactional)                                      │
│  - Integração com MinIO para upload de arquivos                     │
│  - Geração de Presigned URLs com expiração de 30 minutos            │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        REPOSITORY LAYER                             │
│  - Abstração de acesso a dados (JPA/Hibernate)                      │
│  - Queries customizadas com @Query (JPQL)                           │
│  - Paginação nativa com Pageable                                    │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                   MODEL / DTO / ENTITY LAYER                        │
│  - Entities: Artist, Album, UserEntity (JPA)                        │
│  - DTOs de entrada: ArtistDTO, AlbumDTO, LoginRequestDTO            │
│  - DTOs de saída: ArtistPresenterDTO, AlbumPresenterDTO             │
│  - Mapeamento via MapStruct (compile-time)                          │
└─────────────────────────────────────────────────────────────────────┘
```

#### Tecnologias do Back-end

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 17 | Linguagem principal |
| Spring Boot | 4.0.2 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 4.x | Persistência de dados |
| Flyway | 10.x | Migrations de banco de dados |
| MapStruct | 1.6.3 | Mapeamento DTO ↔ Entity |
| MinIO SDK | 8.5.7 | Integração com Object Storage |
| JJWT | 0.12.3 | Geração e validação de tokens JWT |
| PostgreSQL | 16 | Banco de dados relacional |
| Lombok | 1.18.32 | Redução de boilerplate |
| SpringDoc OpenAPI | 2.8.5 | Documentação Swagger/OpenAPI |

---

### 🎨 Front-end: Angular 19+

O front-end utiliza **Angular moderno** com gerenciamento de estado baseado em **Signals** e padrão **Facade** para encapsular a lógica de negócio:

```
┌─────────────────────────────────────────────────────────────────────┐
│                          COMPONENTS                                 │
│  - Componentes visuais (Smart & Dumb)                               │
│  - Injetam o Facade correspondente                                  │
│  - Reatividade via Signals (loading, artists, albums)               │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         FACADE PATTERN                              │
│  - Encapsula regras de negócio do front-end                         │
│  - Gerencia estado local com Angular Signals                        │
│  - Orquestra chamadas a múltiplos Services                          │
│  - Expõe apenas o necessário para os componentes                    │
│  Exemplos: ArtistsFacade, AlbumsFacade, LoginFacade                 │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          SERVICES                                   │
│  - Comunicação HTTP com a API REST                                  │
│  - Métodos tipados (findAll, create, update, delete)                │
│  - Injetam HttpClient                                               │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       CONNECTORS / HTTP                             │
│  - HttpClient para chamadas REST                                    │
│  - Interceptors para injeção de JWT                                 │
│  - Refresh automático de tokens                                     │
└─────────────────────────────────────────────────────────────────────┘
```

#### Gestão de Estado com Signals

O Angular Signals substitui o uso tradicional de BehaviorSubject/RxJS para estado local, oferecendo:

```typescript
// Exemplo do ArtistsFacade
@Injectable()
export class ArtistsFacade {
  // Estado reativo com Signals
  artists = signal<Artist[]>([]);
  loading = signal(false);
  currentPage = signal(0);
  pageSize = signal(10);
  totalPages = signal(0);
  
  // Atualização reativa
  loadArtists(): void {
    this.loading.set(true);
    this.artistsService.findAllPaginated(...).subscribe({
      next: (response) => {
        this.artists.set(response.content);
        this.loading.set(false);
      }
    });
  }
}
```

#### Tecnologias do Front-end

| Tecnologia | Propósito |
|------------|-----------|
| Angular 19+ | Framework SPA |
| Angular Signals | Gestão de estado reativo |
| TypeScript | Tipagem estática |
| TailwindCSS | Estilização |
| Nginx | Servidor web em produção |

---

## 2. Estrutura de Dados (Schemas)

### 📊 Modelagem do Banco de Dados

O banco de dados foi modelado utilizando **Flyway Migrations** para versionamento e controle de alterações. A estrutura suporta o relacionamento **N:N (muitos-para-muitos)** entre Artistas e Álbuns.

#### Diagrama Entidade-Relacionamento

```
┌──────────────────────┐       ┌──────────────────────┐       ┌──────────────────────┐
│       ARTIST         │       │     ARTIST_ALBUM     │       │        ALBUM         │
├──────────────────────┤       ├──────────────────────┤       ├──────────────────────┤
│ id (PK) BIGSERIAL    │───────│ artist_id (PK, FK)   │───────│ id (PK) BIGSERIAL    │
│ name VARCHAR(255)    │       │ album_id (PK, FK)    │       │ title VARCHAR(255)   │
│ music_genre VARCHAR  │       └──────────────────────┘       │ release_year INTEGER │
│ biography TEXT       │                                      │ record_label VARCHAR │
│ country_of_origin    │                                      │ track_count INTEGER  │
│ photo_url VARCHAR    │                                      │ cover_url VARCHAR    │
│ storage_id VARCHAR   │                                      │ storage_id VARCHAR   │
│ created_at TIMESTAMP │                                      │ description TEXT     │
│ updated_at TIMESTAMP │                                      │ created_at TIMESTAMP │
└──────────────────────┘                                      │ updated_at TIMESTAMP │
                                                              └──────────────────────┘
```

### 📜 Migrations Flyway

#### V1__create_table_artist.sql
```sql
CREATE TABLE artist (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    music_genre VARCHAR(100),
    biography TEXT,
    country_of_origin VARCHAR(100),
    photo_url VARCHAR(500),
    storage_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_artist_name ON artist(name);
CREATE INDEX idx_artist_music_genre ON artist(music_genre);
```

#### V2__create_table_album.sql
```sql
CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INTEGER,
    record_label VARCHAR(255),
    track_count INTEGER,
    cover_url VARCHAR(500),
    storage_id VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_album_title ON album(title);
CREATE INDEX idx_album_release_year ON album(release_year);
```

#### V3__create_table_artist_album.sql
```sql
CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_artist_album_artist
        FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    CONSTRAINT fk_artist_album_album
        FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);

CREATE INDEX idx_artist_album_artist ON artist_album(artist_id);
CREATE INDEX idx_artist_album_album ON artist_album(album_id);
```

### 🔍 Justificativa da Modelagem

| Decisão | Justificativa |
|---------|---------------|
| **Tabela Associativa `artist_album`** | Permite relacionamento N:N onde um artista pode ter múltiplos álbuns e um álbum pode ter múltiplos artistas (bandas, colaborações). |
| **Índices em `name` e `title`** | Otimiza consultas parametrizadas de busca textual (`LIKE '%termo%'`). |
| **Índices em FKs** | Acelera JOINs entre as tabelas para listagem de álbuns por artista e vice-versa. |
| **`ON DELETE CASCADE`** | Garante integridade referencial, removendo associações automaticamente ao deletar artista ou álbum. |
| **Campos `storage_id`** | Identificador único para organização de arquivos no MinIO (pastas separadas por entidade). |
| **Campos `created_at/updated_at`** | Auditoria e ordenação cronológica. |

### 📡 Queries Suportadas

A modelagem suporta as seguintes consultas parametrizadas:

```java
// Por nome do artista (ordenável)
List<Artist> findByNameContainingIgnoreCase(String name, Sort sort);

// Por gênero musical
List<Artist> findByMusicGenreIgnoreCase(String musicGenre);

// Álbuns por ano de lançamento
List<Album> findByReleaseYear(Integer releaseYear);

// Álbuns por ID do artista (JOIN)
@Query("SELECT DISTINCT alb FROM Album alb JOIN alb.artists art WHERE art.id = :artistId")
List<Album> findByArtistId(@Param("artistId") Long artistId);

// Álbuns por nome do artista (busca parcial + JOIN)
@Query("SELECT DISTINCT alb FROM Album alb JOIN alb.artists art WHERE LOWER(art.name) LIKE LOWER(CONCAT('%', :artistName, '%'))")
List<Album> findByArtistNameContaining(@Param("artistName") String artistName);
```

---

## 3. Funcionalidades Sênior Implementadas

### 🔐 Segurança Avançada

#### Autenticação JWT com Access Token + Refresh Token

| Parâmetro | Valor | Descrição |
|-----------|-------|-----------|
| **Access Token TTL** | 5 minutos (300.000 ms) | Token de curta duração para requisições autenticadas |
| **Refresh Token TTL** | 24 horas (86.400.000 ms) | Token de longa duração para renovação |
| **Algoritmo** | HS256 | HMAC-SHA256 para assinatura |
| **Biblioteca** | JJWT 0.12.3 | Implementação robusta e atualizada |

**Fluxo de Autenticação:**

```
┌──────────┐     POST /api/v1/auth/login     ┌──────────┐
│  Client  │ ─────────────────────────────►  │   API    │
│          │  { username, password }         │          │
│          │ ◄─────────────────────────────  │          │
│          │  { accessToken, refreshToken }  │          │
└──────────┘                                 └──────────┘
     │
     │  (após 5 min, accessToken expira)
     │
     ▼
┌──────────┐   POST /api/v1/auth/refresh     ┌──────────┐
│  Client  │ ─────────────────────────────►  │   API    │
│          │  { refreshToken }               │          │
│          │ ◄─────────────────────────────  │          │
│          │  { newAccessToken }             │          │
└──────────┘                                 └──────────┘
```

**Implementação no `JwtTokenProvider`:**

```java
// Geração do Access Token (5 min)
public String generateAccessToken(String username) {
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs); // 300.000 ms
    return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256)
            .compact();
}

// Geração do Refresh Token (24h) com claim diferenciador
public String generateRefreshToken(String username) {
    Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs); // 86.400.000 ms
    return Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256)
            .compact();
}
```

#### Configuração de CORS

CORS configurado para bloquear origens não autorizadas:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins; // http://localhost:3000,4200,8080

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

#### Configuração de Segurança por Endpoint

```java
// Endpoints públicos
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers("/api/health", "/api/info").permitAll()
.requestMatchers("/actuator/**").permitAll()
.requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
.requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
.requestMatchers("/api/v1/artistas/**").permitAll()

// Endpoints protegidos (requerem JWT válido)
.requestMatchers("/api/v1/albums/**").authenticated()
.requestMatchers("/api/v1/usuarios/**").authenticated()
```

---

### ☁️ Cloud Storage (MinIO/S3)

#### Arquitetura de Armazenamento

O MinIO é utilizado como Object Storage compatível com S3 para armazenar imagens de artistas e capas de álbuns.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        MinIO Bucket: artistas-media                 │
├─────────────────────────────────────────────────────────────────────┤
│  artists/                                                           │
│    └── {storageId}/                                                 │
│          └── {uuid}.jpg                                             │
│  albums/                                                            │
│    └── {storageId}/                                                 │
│          └── {uuid}.jpg                                             │
└─────────────────────────────────────────────────────────────────────┘
```

#### Fluxo de Upload de Capas/Fotos

```
┌──────────┐   POST /api/v1/artistas (multipart)   ┌──────────┐
│  Client  │ ───────────────────────────────────►  │   API    │
│          │   { file: image.jpg, artistDTO }      │          │
└──────────┘                                       └────┬─────┘
                                                        │
                      ┌─────────────────────────────────┘
                      ▼
             ┌─────────────────┐
             │  FileUpload     │
             │  Service        │
             │  1. Gera UUID   │
             │  2. Upload MinIO│
             │  3. Retorna path│
             └────────┬────────┘
                      │
                      ▼
             ┌─────────────────┐
             │  MinioService   │
             │  - putObject()  │
             │  - Bucket check │
             └────────┬────────┘
                      │
                      ▼
             ┌─────────────────┐
             │     MinIO       │
             │  (S3 compat)    │
             └─────────────────┘
```

#### Presigned URLs com Expiração de 30 Minutos

As URLs de acesso às imagens são **temporárias e assinadas**, expirando automaticamente após 30 minutos por segurança:

```java
public String getPresignedUrl(String fileName, int expiryMinutes) {
    return publicMinioClient.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucketName)
            .object(fileName)
            .expiry(expiryMinutes, TimeUnit.MINUTES)  // 30 minutos
            .build()
    );
}
```

**Renovação Automática de URLs:**

Cada vez que um artista ou álbum é consultado, o Service renova automaticamente a Presigned URL:

```java
private ArtistPresenterDTO refreshUrls(ArtistPresenterDTO dto) {
    String refreshedPhotoUrl = dto.photoUrl() != null
        ? fileUploadService.refreshPresignedUrl(dto.photoUrl(), 30).presignedUrl()
        : null;
    // ... retorna DTO com URL renovada
}
```

---

## 4. Orquestração e Execução

### 🐳 Docker Compose - Um Comando Para Subir Tudo

O projeto utiliza Docker Compose para orquestrar todos os serviços. Com **um único comando** é possível inicializar o ecossistema completo:

```bash
docker-compose up --build
```

### Arquitetura dos Containers

```
┌─────────────────────────────────────────────────────────────────────┐
│                         docker-compose.yml                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐             │
│  │  frontend   │    │   backend   │    │   postgres  │             │
│  │  (nginx)    │───►│ (Spring)    │───►│   (DB)      │             │
│  │  :80        │    │  :8080      │    │   :5432     │             │
│  └─────────────┘    └──────┬──────┘    └─────────────┘             │
│                            │                                        │
│                            ▼                                        │
│                     ┌─────────────┐                                 │
│                     │    minio    │                                 │
│                     │  (storage)  │                                 │
│                     │ :9000/:9001 │                                 │
│                     └─────────────┘                                 │
│                                                                     │
│  Network: app-network (bridge)                                      │
│  Volumes: postgres_data, minio_data                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### Serviços e Portas

| Serviço | Container | Porta | Descrição |
|---------|-----------|-------|-----------|
| **Frontend** | `frontend` | `80` | Aplicação Angular servida via Nginx |
| **Backend** | `gerenciador-artistas-api` | `8080` | API REST Spring Boot |
| **PostgreSQL** | `gerenciador-artistas-db` | `5432` | Banco de dados |
| **MinIO** | `gerenciador-artistas-minio` | `9000` (API) / `9001` (Console) | Object Storage |

### Passo a Passo para Execução

#### 1. Clonar o repositório
```bash
git clone https://github.com/xjfelli/jaderfellipedeoliveiraesilvagolembiouski012389
cd seplag-projeto/
```

#### 2. Configurar variáveis de ambiente
```bash
# Copiar arquivo de exemplo
cp .env.example .env

```

#### 3. Subir o ecossistema
```bash
docker-compose up --build
```

#### 4. Acessar os serviços

| Serviço | URL |
|---------|-----|
| **Frontend** | http://localhost |
| **API** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **MinIO Console** | http://localhost:9001 |
| **Health Check** | http://localhost:8080/api/health |


### Comandos Úteis

```bash
# Subir em background
docker-compose up -d --build

# Ver logs
docker-compose logs -f backend

# Parar todos os containers
docker-compose down

# Remover volumes (reset completo)
docker-compose down -v

# Rebuild apenas o backend
docker-compose up -d --build backend
```

---

## 5. Qualidade e Monitoramento

### 🏥 Health Checks (Liveness/Readiness)

#### Health Checks dos Containers (Docker)

Cada serviço possui health check configurado no `docker-compose.yml`:

**PostgreSQL:**
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
  interval: 10s
  timeout: 5s
  retries: 5
```

**MinIO:**
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
  interval: 30s
  timeout: 20s
  retries: 3
```

**Backend (dependências):**
```yaml
depends_on:
  postgres:
    condition: service_healthy
  minio:
    condition: service_healthy
```

#### Health Checks da API (Spring Actuator)

```properties
# application.properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

**Endpoints disponíveis:**

| Endpoint | Propósito |
|----------|-----------|
| `GET /api/health` | Health check customizado (Liveness) |
| `GET /api/info` | Informações da API |
| `GET /actuator/health` | Spring Actuator Health (Readiness) |

**Exemplo de resposta `/api/health`:**
```json
{
  "status": "UP",
  "timestamp": "2026-02-04T10:15:30",
  "message": "API Gerenciador de Artistas está funcionando!"
}
```

---

### 🧪 Estratégia de Testes Unitários

O projeto possui cobertura de testes unitários focada nos componentes críticos de segurança:

#### Testes Implementados

```
src/test/java/com/gerenciadorartistas/backend/
├── DemoApplicationTests.java
└── features/auth/
    ├── controller/
    │   └── AuthControllerTest.java
    ├── dto/
    │   ├── AuthPresenterDTOTest.java
    │   ├── LoginRequestDTOTest.java
    │   └── RefreshTokenRequestDTOTest.java
    ├── security/
    │   ├── JwtAuthenticationFilterTest.java
    │   └── JwtTokenProviderTest.java
    └── service/
        ├── AuthServiceTest.java
        └── CustomUserDetailsServiceTest.java
```

#### Executando os Testes

```bash
# Via Maven
cd backend
./mvnw test

# Ou via Docker
docker-compose exec backend ./mvnw test
```

#### Ferramentas de Teste

| Ferramenta | Propósito |
|------------|-----------|
| **JUnit 5** | Framework de testes |
| **Mockito** | Mocking de dependências |
| **Spring Boot Test** | Contexto de teste Spring |
| **H2 Database** | Banco em memória para testes |

#### Foco dos Testes

| Componente | Aspectos Testados |
|------------|-------------------|
| `JwtTokenProvider` | Geração, validação, expiração de tokens |
| `JwtAuthenticationFilter` | Extração de token, autenticação |
| `AuthService` | Login, refresh, validação de credenciais |
| `AuthController` | Endpoints REST, responses HTTP |
| `CustomUserDetailsService` | Carregamento de usuários |

---

## 📚 Documentação Adicional

### Swagger/OpenAPI

A documentação interativa da API está disponível em:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

### Endpoints da API

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| `POST` | `/api/v1/auth/login` | Autenticação | ❌ |
| `POST` | `/api/v1/usuarios` | Registrar usuário | ❌ |
| `POST` | `/api/v1/auth/refresh` | Renovar token | ✅ |
| `GET` | `/api/v1/artistas` | Listar artistas | ✅ |
| `POST` | `/api/v1/artistas` | Criar artista | ✅ |
| `GET` | `/api/v1/artistas/{id}` | Buscar artista | ✅ |
| `PUT` | `/api/v1/artistas/{id}` | Atualizar artista | ✅ |
| `DELETE` | `/api/v1/artistas/{id}` | Remover artista | ✅ |
| `GET` | `/api/v1/albums` | Listar álbuns | ✅ |
| `POST` | `/api/v1/albums` | Criar álbum | ✅ |
| `GET` | `/api/v1/albums/{id}` | Buscar álbum | ✅ |
| `PUT` | `/api/v1/albums/{id}` | Atualizar álbum | ✅ |
| `DELETE` | `/api/v1/albums/{id}` | Remover álbum | ✅ |


---

## 📄 Licença

Este projeto foi desenvolvido como parte do **Processo Seletivo SEPLAG/MT 2026** e é de uso exclusivo para avaliação técnica.

---

**Desenvolvido por:** Jader Fellipe de Oliveira e Silva Golembiouski  
**Cargo:** Analista de TI - Perfil Engenheiro da Computação (Sênior)  
**Data:** Fevereiro de 2026
