# Conventional Commits - Guia de Padrão de Commits

Este documento define o padrão de commits para o projeto **Zenon Fraud Detector**.

## Estrutura Básica

```
<tipo>[escopo opcional]: <descrição breve>

[corpo opcional]

[rodapé(s) opcional(is)]
```

## Tipos de Commit

| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| **feat** | Nova funcionalidade | `feat: adiciona detecção de fraude por padrão` |
| **fix** | Correção de bug | `fix: corrige validação de transação` |
| **docs** | Documentação | `docs: atualiza README com instruções` |
| **style** | Formatação, sem alterar lógica | `style: aplica formatação de código` |
| **refactor** | Refatoração sem alterar comportamento | `refactor: simplifica lógica de validação` |
| **perf** | Melhoria de performance | `perf: otimiza busca de transações` |
| **test** | Adição ou atualização de testes | `test: adiciona testes para validação` |
| **chore** | Alterações em build, deps, config | `chore: atualiza dependências` |
| **ci** | Alterações em CI/CD | `ci: configura pipeline GitHub Actions` |
| **revert** | Reverte um commit anterior | `revert: reverte commit abc123` |

## Escopo (Opcional)

Indica a área/módulo afetado. Exemplos para este projeto:

- `core` - Lógica principal de detecção
- `api` - Endpoints REST
- `database` - Operações de banco de dados
- `validation` - Validação de dados
- `security` - Segurança e autenticação
- `config` - Configurações

## Descrição

- Use imperativo, modo presente: "adiciona" em vez de "adicionado" ou "adicionando"
- Não comece com letra maiúscula
- Sem ponto final na descrição breve
- Máximo 50 caracteres

## Corpo (Opcional)

- Separe do título por uma linha em branco
- Descreva **o quê** e **por quê**, não como
- Limite a 72 caracteres por linha
- Use quando necessário explicar o motivo da mudança

## Rodapé (Opcional)

Use para referências:
- `Closes #123` - Fecha a issue #123
- `Fixes #123` - Corrige a issue #123
- `Refs #123` - Referencia a issue #123

## Exemplos Práticos

### Exemplo 1: Nova Funcionalidade
```
feat(core): implementa detector de transações duplicadas

Adiciona validação para identificar transações duplicadas baseado em
timestamp, valor e ID do cliente. O detector utiliza cache em memória
para melhorar performance.

Closes #42
```

### Exemplo 2: Correção de Bug
```
fix(validation): corrige validação de números de cartão

Ajusta regex para aceitar cartões com 16-19 dígitos conforme padrão ISO/IEC 7812.

Refs #35
```

### Exemplo 3: Refatoração
```
refactor: simplifica estrutura de detecção

Remove camada intermediária desnecessária e consolida lógica de
detecção em um único serviço.
```

### Exemplo 4: Documentação
```
docs: adiciona instruções de setup local
```

### Exemplo 5: Atualização de Dependências
```
chore: atualiza Spring Boot para 3.2.0
```

## Breaking Changes

Se seu commit introduz mudanças que quebram compatibilidade, adicione `!` após o tipo:

```
feat!: refatora API de detecção

BREAKING CHANGE: A resposta da API mudou de objeto para array.
```

## Checklist antes de fazer commit

- [ ] Tipo de commit apropriado?
- [ ] Escopo faz sentido?
- [ ] Descrição é clara e concisa?
- [ ] Usa imperativo (adiciona, corrige, refatora)?
- [ ] Tem breaking changes documentadas?
- [ ] Referencia issues relacionadas?
- [ ] Máximo 50 caracteres no título?

## Benefícios

✅ **Histórico legível** - Fácil entender mudanças passadas  
✅ **Automação** - Gerar changelog automaticamente  
✅ **Versionamento Semântico** - Determinar versão automaticamente  
✅ **Rastreabilidade** - Saber por que mudanças foram feitas  
✅ **Code Review** - Facilita revisão de código  

## Referências

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Angular Commit Guidelines](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit)

