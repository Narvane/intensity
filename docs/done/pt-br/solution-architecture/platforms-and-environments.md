# Plataformas e Ambientes

Este documento descreve onde o Intensity roda — as plataformas de execução, a topologia de implantação e quantas instâncias de cada componente existem em produção.

**Público:** arquitetos e engenheiros seniores que precisam entender o layout estrutural da solução sem detalhe de implementação ou operação.

---

## Curta

O Intensity roda em **duas plataformas**: um **client mobile** nos celulares dos participantes e uma **API centralizada** em um servidor. O client é implantado em **muitos dispositivos**; a API roda como **instância única** em **um ambiente de servidor**. Um **banco de dados** está conectado a esse ambiente e é acessado apenas pela API.

---

## Média

### Plataformas de execução

| Plataforma | Papel | Instâncias |
|------------|-------|------------|
| **Mobile** | Hospeda o aplicativo client — interface, fluxos de interação e comportamento central do produto | Uma instalação por dispositivo do participante |
| **Servidor** | Hospeda a API e o banco de dados conectado | Um ambiente centralizado |

Não há client web na arquitetura atual. O produto é entregue exclusivamente pelo aplicativo mobile.

### Topologia de implantação

```
┌─────────────────────────────────────────────────────────┐
│  Ambiente de servidor (instância única)                 │
│  ┌─────────┐      ┌──────────────┐                        │
│  │   API   │ ───► │ Banco de dados│                        │
│  └────▲────┘      └──────────────┘                        │
└───────┼─────────────────────────────────────────────────┘
        │ REST
        │
   ┌────┴────┬──────────┬──────────┐
   │         │          │          │
┌──▼──┐  ┌──▼──┐   ┌──▼──┐   ┌──▼──┐
│Cel. │  │Cel. │   │Cel. │   │Cel. │   ... (muitos clients)
│Client│  │Client│   │Client│   │Client│
└─────┘  └─────┘   └─────┘   └─────┘
```

### Modelo de ambientes

- **Ambiente do client:** cada dispositivo mobile do participante. A mesma build do client roda de forma independente em cada celular.
- **Ambiente de servidor:** um runtime centralizado único onde a API e o banco de dados coexistem. Todos os clients convergem nesse ambiente como fonte da verdade dos dados persistidos.

A assimetria é intencional: **muitos clients, uma API**. O registro individual de experiências por cada participante exige uma camada de persistência compartilhada, enquanto a experiência do produto em si vive em cada dispositivo.

---

## Detalhada

### Plataforma mobile

A plataforma mobile é onde os participantes interagem com o Intensity. Hospeda:

- A interface completa e a estrutura de navegação
- Telas de onboarding, autenticação e fluxos de criação
- O ritual do momento compartilhado (sorteio, alinhamento, revelação de card)
- Preferências do client não persistidas no modelo de domínio (como idioma da interface)

Cada celular executa sua própria instância do client. Não há exigência de que todos os participantes usem o mesmo modelo de aparelho ou versão de sistema operacional além do que o aplicativo mobile suporta.

No **modo Experiências**, cada participante tipicamente usa seu próprio celular para registrar experiências individualmente. No **modo Caixa de Experiências**, o ritual do grupo — navegar caixinhas, sortear, revelar — acontece em **um celular compartilhado**, enquanto as contribuições podem ter sido registradas a partir de dispositivos separados.

### Plataforma de servidor

A plataforma de servidor existe para centralizar dados persistidos. Hospeda:

- A **API** — o único ponto de entrada na camada de aplicação para leitura e escrita de dados de domínio
- O **banco de dados** — armazenamento de persistência exclusivo do modelo de domínio

A API roda em **instância única** dentro de um ambiente de servidor. Não há topologia multi-região ou API escalada horizontalmente na arquitetura atual.

### O que roda onde

| Responsabilidade | Client mobile | Servidor (API + banco) |
|------------------|---------------|-------------------------|
| Interface e fluxos de UX | ✓ | — |
| Ritual de sorteio e revelação | ✓ | — |
| Persistência de registro de experiências | invoca API | ✓ |
| Dados de participante, grupo, caixinha, experiência | lê via API | ✓ (fonte da verdade) |
| Autenticação contra credenciais persistidas | invoca API | ✓ |
| Preferência de idioma da interface | ✓ (local) | — |
| Pacotes de sugestões pré-definidas | ✓ (embutidos) | — |

### Limites fora de escopo

A arquitetura atual não inclui:

- Aplicativo web ou client baseado em navegador
- Topologia separada de staging ou multi-ambiente (além do ambiente de servidor único descrito aqui)
- Operação offline do client (identificada como direção futura em outro documento)

Especificidades operacionais — provedor de hospedagem, containerização, pipelines de CI/CD, monitoramento — pertencem à camada de Engenharia e Operação.
