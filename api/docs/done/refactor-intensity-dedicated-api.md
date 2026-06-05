# Refatoração multi-projeto → API Intensity dedicada

**Data:** 2026-06-05  
**Status:** Concluída

## Objetivo

Transformar o monorepo multi-produto (Narvane API) em uma API dedicada exclusivamente ao **Intensity**, eliminando a estrutura modular em `/projects`.

## Estado anterior

- Maven reactor com 6 módulos: `app`, `hour-manager`, `pandora-box`, `between-us`, `intensity` (legado), `intensity2` (ativo).
- O módulo `intensity2` era a implementação canônica, exposta em `/intensity2/api/v1/*` com schema PostgreSQL `intensity2`.
- Não havia carregamento dinâmico de módulos — apenas dependências Maven + component scan Spring.

## Decisões

| Tema | Decisão |
|------|---------|
| Base de código | `intensity2` movido para `app/src/main/kotlin` |
| Rotas HTTP | `/intensity2/` → `/intensity/` (alinha com OpenAPI existente) |
| Schema DB | Migração `V225` renomeia `intensity2` → `intensity`; entidades JPA usam `schema = "intensity"` |
| Migrações históricas | V221–V224 mantidas inalteradas (Flyway); referências a `intensity2` são intencionais |
| Migrações legado (V1–V6) | Mantidas para não quebrar validação Flyway em bancos existentes |
| Módulos removidos | `hour-manager`, `pandora-box`, `between-us`, `intensity`, `intensity2` |
| Bean name generator | Removido `FullyQualifiedAnnotationBeanNameGenerator` (não há mais colisão entre módulos) |
| Artifact Maven | `narvane-api` → `intensity-api`; JAR final: `intensity-api.jar` |

## Estrutura final

```
intensity-api/
├── pom.xml                 # Parent (único módulo filho: app)
├── app/
│   ├── pom.xml             # Spring Boot + Kotlin + JWT
│   └── src/main/
│       ├── java/...        # Boot, CORS, Swagger, exception handler
│       ├── kotlin/br/com/narvane/intensity/...  # Domínio Intensity
│       └── resources/
│           ├── application.yml
│           ├── META-INF/openapi.yaml
│           └── db/migration/  # V1–V6 (legado) + V221–V225 (Intensity)
└── docs/done/              # Documentação autoritativa
```

## Breaking changes (operacionais)

1. **URL:** clientes que chamavam `/intensity2/api/v1/*` devem migrar para `/intensity/api/v1/*`.
2. **Docker image:** JAR renomeado para `intensity-api.jar`; atualizar `docker-compose.prod.yml` / GHCR se necessário.
3. **Schema DB:** após deploy, Flyway executa V225 e renomeia o schema. Se existir schema legado `intensity` (módulo antigo), ele é removido antes do rename.

## Validação

- [x] `mvn clean compile` — SUCCESS
- [x] `mvn package -DskipTests` — SUCCESS, artefato `app/target/intensity-api.jar`
- [ ] Startup com PostgreSQL (requer banco local/prod)
- [ ] Testes manuais dos fluxos CURATE e CONNECT

## Residual conhecido

- Migrações Flyway V221–V224 e V1–V6 ainda contêm referências históricas a schemas de outros produtos (`hour_manager`, `pandora`, `intensity2`). Isso é esperado para compatibilidade com histórico Flyway.
- `docs/DEPLOY-SERVER.md` ainda referencia `narvane-api` — atualizar em deploy futuro.
- CI (`.github/workflows/build-and-push.yml`) e `docker-compose.prod.yml` ainda usam imagem `ghcr.io/narvane/narvane-api` — atualizar registry/tag conforme estratégia de deploy.
