# Exemplos de Commits - Zenon Fraud Detector

## ✅ BONS EXEMPLOS

### Nova Feature
```
feat(core): implementa detecção de transações duplicadas

Adiciona validação para identificar transações duplicadas baseado em
timestamp, valor e ID do cliente. O detector utiliza cache em memória
para melhorar performance em 40%.

Closes #42
```

### Correção de Bug
```
fix(validation): corrige validação de CVV

O validador estava aceitando CVV com menos de 3 dígitos. Ajustado
para aceitar apenas 3-4 dígitos conforme padrão de cartões.

Fixes #88
```

### Refatoração
```
refactor(database): consolida queries de transações

Remove duplicação de SQL consolidando 3 métodos em 1 com
parâmetros opcionais. Sem mudanças de comportamento.
```

### Atualização de Documentação
```
docs: adiciona guia de setup local no README
```

### Atualização de Dependência
```
chore(deps): atualiza Spring Boot 3.1.0 -> 3.2.0
```

### Teste Novo
```
test(validation): adiciona casos de teste para CVV

- Testa CVV com menos de 3 dígitos
- Testa CVV com mais de 4 dígitos
- Testa CVV com caracteres especiais
```

---

## ❌ RUINS - O QUE EVITAR

### ❌ Sem tipo
```
corrige validação
```
Deveria ser: `fix(validation): corrige validação`

### ❌ Tipo genérico demais
```
feat: mudanças
```
Deveria ser: `feat(core): implementa detecção de duplicatas`

### ❌ Descrição muito vaga
```
feat: ajusta coisas
```
Deveria ser: `feat(api): adiciona endpoint de análise de transações`

### ❌ Começa com maiúscula
```
Feat(core): Adiciona validação
```
Deveria ser: `feat(core): adiciona validação`

### ❌ Tempo passado
```
feat(core): adicionado detector de fraude
```
Deveria ser: `feat(core): adiciona detector de fraude`

### ❌ Muito longo (> 50 caracteres)
```
feat(core): implementa sistema muito complexo de detecção automática
```
Deveria ser: `feat(core): implementa detector automático`

### ❌ Sem contexto
```
fix: corrige bug
```
Deveria ser: `fix(validation): corrige validação de números de cartão`

### ❌ Mistura múltiplas mudanças
```
feat: adiciona validação, corrige bug, atualiza docs
```
Deveria ser: múltiplos commits
- `feat(validation): adiciona validação de cartão`
- `fix(api): corrige resposta de erro`
- `docs: atualiza README`

---

## 📊 Estatísticas de Bom Commits

| Aspecto | Recomendado |
|---------|------------|
| Comprimento do título | Máx. 50 caracteres |
| Comprimento do corpo | Máx. 72 caracteres/linha |
| Frequência | 1 mudança lógica = 1 commit |
| Escopo | Sempre que possível |
| Referência a issues | Sempre que faz sentido |

---

## 🚀 Comandos Úteis

### Usar o template ao fazer commit
```bash
git commit
```

### Ver commits com o padrão
```bash
git log --oneline
```

### Ver commits com mais detalhe
```bash
git log --pretty=format:"%H %s"
```

### Filtrar por tipo
```bash
git log --grep="^feat" --oneline
git log --grep="^fix" --oneline
```

---

## 💡 Dica Final

Lembre-se: **Um commit bem feito economiza horas no futuro!**

Quando você voltar a este código em 3 meses, um bom histórico de commits
vai te poupar tempo para entender por que cada mudança foi feita.

