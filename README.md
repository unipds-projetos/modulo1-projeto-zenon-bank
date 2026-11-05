<div align="center">

# 🛡️ Zenon Fraud Detector

**Detecção e análise de fraudes em transações financeiras com Java moderno**

Projeto prático do Módulo 1 da pós em Java, construído sobre o dataset **PaySim** (~6,3 milhões de transações).

![Java](https://img.shields.io/badge/Java-25_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## 📌 Sobre o projeto

O **Zenon Bank** precisa processar milhões de transações e identificar padrões de fraude. Este repositório evolui, tarefa a tarefa, de um simples leitor de CSV até um pipeline de ingestão **concorrente**, **resiliente** e com **baixo consumo de memória**, passando por:

- Modelagem imutável com **Records** e **enums**
- I/O clássico (`java.io`) e eficiente (`java.nio`)
- Tratamento de exceções e **Optional**
- Análise de dados com **Stream API**
- Benchmark de estruturas de dados (**List O(n) × Map O(1)**)
- Internacionalização (**i18n**) com `ResourceBundle` e `NumberFormat`
- Persistência com **JDBC** + **MySQL** em Docker, com inserções em lote
- Concorrência com **ExecutorService** e **Virtual Threads**

> Fork de [unipds-projetos/modulo1-projeto-zenon-bank](https://github.com/unipds-projetos/modulo1-projeto-zenon-bank).

---

## 🧰 Tecnologias

| Categoria        | Ferramenta                                  |
|------------------|---------------------------------------------|
| Linguagem        | Java 25 (LTS)                               |
| Build            | Maven                                       |
| IDE              | IntelliJ IDEA ou Eclipse                    |
| Banco de dados   | MySQL (via Docker Compose)                  |
| Acesso a dados   | JDBC puro                                   |
| Dataset          | [PaySim (Kaggle)](https://www.kaggle.com/datasets/ealaxi/paysim1) |

---

## 📂 Estrutura

```
.
└── zenon-fraud-detector/
    ├── data/                          # CSVs (ignorado pelo Git)
    │   └── PS_20174392719_1491204439457_log.csv
    ├── pom.xml
    └── src/main/java/br/com/zenon/
        ├── Main.java
        ├── Transaction.java
        ├── TransactionCustomer.java
        └── TransactionType.java
```

> A estrutura mostra o que existe hoje e é atualizada a cada tarefa concluída.

---

## 🚀 Como executar

### Pré-requisitos

- JDK 25
- Maven
- Docker e Docker Compose (a partir da tarefa 09)

### 1. Clonar

```bash
git clone https://github.com/DevMireski/projeto-zenon-bank.git
cd projeto-zenon-bank
```

### 2. Baixar os dados

1. Baixe o dataset PaySim no [Kaggle](https://www.kaggle.com/datasets/ealaxi/paysim1) (CSV de ~493 MB) e extraia em `zenon-fraud-detector/data/`.
2. Baixe o [CSV com dados corrompidos](https://gist.githubusercontent.com/alexandreaquiles/5b78c8ce49699b69e40ea248d560ff86/raw/635b63fede16b07bbf7bfa4735fd6f6e89e40fb2/paysim_with_bad_data.csv) para a mesma pasta (usado na tarefa 04).

> ⚠️ Os CSVs ficam **fora** de `src/main/resources` para não inflar o build nem o JAR. A pasta `data/` já está no `.gitignore`.

### 3. Compilar

```bash
cd zenon-fraud-detector

mvn clean package
```

As classes `*Main` podem ser executadas direto pela IDE.

### 4. Subir o banco (tarefas 09 e 10)

```bash
docker compose up -d
```

Em seguida, execute o `script.sql` para criar a tabela `TRANSACTIONS`.

### 5. Testar com pouca memória (tarefa 07)

Na configuração de execução da IDE, adicione o VM option:

```
-Xmx128m
```

---

## 🌿 Fluxo de trabalho

Cada tarefa é desenvolvida em sua própria branch `feature/XX-nome`, nunca diretamente na `main`. Depois de concluída, ela é integrada à `main` **deste fork** via Pull Request (base: `DevMireski/projeto-zenon-bank`, não o repositório original).

```bash
git checkout main
git pull
git checkout -b feature/XX-nome
# ... desenvolvimento ...
git add .
git commit -m "feat: descrição da tarefa"
git push -u origin feature/XX-nome
```

> As branches usam o prefixo `feature/` no lugar do `tarefa/` sugerido no enunciado. A numeração e o nome de cada uma seguem os do enunciado.

---

## 🗺️ Roadmap

| #  | Tarefa                                    | Branch                     | Status |
|----|-------------------------------------------|----------------------------|:------:|
| 01 | Configuração do ambiente e dados          | `feature/01-setup`          | ✅ |
| 02 | Modelagem com Records                     | `feature/02-records`        | ✅ |
| 03 | Ingestão de dados e I/O básico            | `feature/03-ingestao-dados` | ⬜ |
| 04 | Tratamento de erros e Optional            | `feature/04-tratamento-erros` | ⬜ |
| 05 | Análise de fraudes com Stream API         | `feature/05-streams`        | ⬜ |
| 06 | Benchmark de busca: List × Map            | `feature/06-benchmark`      | ⬜ |
| 07 | Relatório eficiente com Java NIO          | `feature/07-nio`            | ⬜ |
| 08 | Internacionalização do relatório          | `feature/08-i18n`           | ⬜ |
| 09 | Persistência com JDBC                     | `feature/09-jdbc`           | ⬜ |
| 10 | Ingestão completa, eficiente e concorrente | `feature/10-concorrencia`  | ⬜ |

---

## 📝 Detalhamento das tarefas

<details>
<summary><strong>01 · Configuração do Ambiente e Dados (PaySim)</strong></summary>

**Objetivo:** configurar IDE e ferramenta de build, e preparar o manuseio de grandes volumes de dados reais.

**Referência:** Prof. Isidro (Aula 8: IntelliJ IDEA e Eclipse; Aula 9: Maven) e Prof. Alexandre Aquiles (Aula Extra 1: Gradle).

1. Fazer fork do repositório base e clonar localmente.
2. Criar a branch `feature/01-setup`.
3. Garantir o JDK 25 (última LTS).
4. Escolher a IDE: IntelliJ IDEA ou Eclipse.
5. Criar o projeto no diretório `zenon-fraud-detector`, usando Maven, com:
   - `groupId`: `br.com.zenon`
   - `artifactId`: `zenon-fraud-detector`
6. Baixar o dataset PaySim (~493 MB), baseado em logs de uma empresa de pagamentos móveis presente em 14 países.
7. Colocar o CSV no diretório externo `data/`.

</details>

<details>
<summary><strong>02 · Modelagem de Transações com Records</strong></summary>

**Objetivo:** modelar dados imutáveis com Java Records, garantindo integridade desde a criação e escolhendo os tipos corretos.

**Referência:** Prof. Isidro (Aula 1: Por que Java?) e Prof. Alexandre Aquiles (Aula Extra 1: Gradle, trecho sobre Records).

1. Criar o record `Transaction` no pacote `br.com.zenon.fraud`, baseado nas colunas do CSV:
   `step`, `type`, `amount`, `nameOrig`, `oldbalanceOrg`, `newbalanceOrig`, `nameDest`, `oldbalanceDest`, `newbalanceDest`, `isFraud`, `isFlaggedFraud`.
2. Usar `camelCase` e tipos adequados (`int`, `boolean`, `BigDecimal`, etc.).
3. Representar `type` como enum: `CASH_IN`, `CASH_OUT`, `DEBIT`, `PAYMENT`, `TRANSFER`.
4. Extrair a abstração de **cliente**: as colunas `*Orig` descrevem o cliente de origem e as `*Dest`, o cliente de destino (`TransactionCustomer`).
5. Na `Main`, instanciar e imprimir duas transações de exemplo:

| Campo            | Transação 1   | Transação 2   |
|------------------|---------------|---------------|
| `step`           | 1             | 743           |
| `type`           | PAYMENT       | CASH_OUT      |
| `amount`         | 9839.64       | 850002.52     |
| `nameOrig`       | C1231006815   | C1280323807   |
| `oldbalanceOrg`  | 170136.0      | 850002.52     |
| `newbalanceOrig` | 160296.36     | 0.0           |
| `nameDest`       | M1979787155   | C873221189    |
| `oldbalanceDest` | 0.0           | 6510099.11    |
| `newbalanceDest` | 0.0           | 7360101.63    |
| `isFraud`        | 0             | 1             |
| `isFlaggedFraud` | 0             | 0             |

</details>

<details>
<summary><strong>03 · Ingestão de Dados e I/O Básico</strong></summary>

**Objetivo:** ler arquivos de texto com `java.io` e transformar dados brutos em objetos Java via parsing manual.

**Referência:** Prof. Isidro (Aula 5: I/O, parte sobre o antigo `java.io`).

1. Criar a classe `TransactionIngestor`, que recebe o nome do arquivo e retorna `List<Transaction>`.
2. Ler apenas as primeiras **1.000** linhas do CSV.
3. Fazer o parsing manual de cada linha e instanciar `Transaction`.
4. Na `Main`, imprimir as 10 primeiras transações importadas:

```text
Transaction[step=1, type=PAYMENT, amount=9839.64, origin=TransactionCustomer[name=C1231006815, oldBalance=170136.0, newBalance=160296.36], recipient=TransactionCustomer[name=M1979787155, oldBalance=0.0, newBalance=0.0], isFraud=false, isFlaggedFraud=false]
Transaction[step=1, type=PAYMENT, amount=1864.28, origin=TransactionCustomer[name=C1666544295, oldBalance=21249.0, newBalance=19384.72], recipient=TransactionCustomer[name=M2044282225, oldBalance=0.0, newBalance=0.0], isFraud=false, isFlaggedFraud=false]
Transaction[step=1, type=TRANSFER, amount=181.0, origin=TransactionCustomer[name=C1305486145, oldBalance=181.0, newBalance=0.0], recipient=TransactionCustomer[name=C553264065, oldBalance=0.0, newBalance=0.0], isFraud=true, isFlaggedFraud=false]
...
```

</details>

<details>
<summary><strong>04 · Tratamento de Erros e Optional</strong></summary>

**Objetivo:** garantir robustez tratando exceções corretamente (sem crashes) e usando `Optional` para evitar `NullPointerException`, lidando com dados sujos do mundo real.

**Referência:** Prof. Isidro (Aula 4: APIs Essenciais do Java, trecho sobre Optional; Aula 6: Tratamento de Exceções) e Prof. Alexandre Aquiles (Aula Extra 3: Aprofundando em Coleções parte 2, trecho sobre Optional).

1. Copiar `paysim_with_bad_data.csv` para `data/`.
2. Validar o record `Transaction` no construtor compacto, impedindo instâncias inválidas:
   - nenhum valor pode ser nulo;
   - `step` deve ser **≥ 1**;
   - demais números **não podem ser negativos** (zero é permitido);
   - `type` deve ser um dos valores do enum.
3. Na `Main`, ler o arquivo com o `TransactionIngestor` e imprimir a quantidade e todas as transações válidas.
4. Para cada linha inválida, capturar a exceção, registrar em `System.err` no formato `Erro: <linha> | <exceção>` e **continuar** a leitura.

```text
Erro: -1,PAYMENT,1000.00,C10001,1000.00,0.00,M10001,0.00,0.00,0,0 | java.lang.IllegalArgumentException: step should be positive: -1
Erro: 1,PAYMENT,-50.00,C10002,100.00,50.00,M10002,0.00,0.00,0,0 | java.lang.IllegalArgumentException: amount should be positive: -50.00
Erro: 1,COMPRA_INVALIDA,100.00,C10006,100.00,0.00,M10006,0.00,0.00,0,0 | java.lang.IllegalArgumentException: No enum constant br.com.zenon.fraud.TransactionType.COMPRA_INVALIDA
Erro: 1,PAYMENT,100.00,,100.00,0.00,M10007,0.00,0.00,0,0 | java.lang.IllegalArgumentException: name should not be empty
Erro: 1,PAYMENT,,C10008,100.00,0.00,M10008,0.00,0.00,0,0 | java.lang.NumberFormatException
...
8
Transaction[step=1, type=PAYMENT, amount=5000.00, origin=TransactionCustomer[name=C1000001, ...], ...]
...
```

</details>

<details>
<summary><strong>05 · Análise de Fraudes com Stream API</strong></summary>

**Objetivo:** praticar programação funcional e declarativa: filtragem, ordenação, agregação e agrupamento.

**Referência:** Prof. Isidro (Aula 7: Stream API) e Prof. Alexandre Aquiles (Aula Extra 2: Aprofundando em Coleções parte 2, trecho sobre abordagem funcional com Streams).

Carregar **50.000** linhas e criar a classe `FraudAnalyzer` para:

1. Filtrar as transações com `isFraud == true` e exibir o total.
2. Exibir as 3 fraudes de maior `amount`.
3. Listar, sem repetição, os 5 maiores clientes de origem suspeitos.
4. Calcular o prejuízo total (soma dos `amount` das fraudes).
5. Contar fraudes por tipo de transação.

```text
1. Total de Fraudes: 100
2. Top 3 Fraudes de Maior Valor:
10000000.00
10000000.00
5460002.91
3. Clientes Suspeitos:
C7162498
C351297720
C666654362
C1588880909
C2047521920
4. Prejuízo Total: 57393771.84
5. Fraudes por Tipo:
 - CASH_OUT: 51
 - TRANSFER: 49
```

> 💡 Streams não podem ser reutilizados: crie um novo stream a partir da lista para cada cálculo.

</details>

<details>
<summary><strong>06 · Benchmark de Busca: List × Map</strong></summary>

**Objetivo:** demonstrar na prática a diferença entre busca linear **O(n)** em listas e busca em tempo constante **O(1)** em mapas.

**Referência:** Prof. Isidro (Aula 3: Estrutura de Dados, trecho sobre Collections e Benchmark O(n) × O(1)) e Prof. Alexandre Aquiles (Aula Extra 3: Aprofundando em Coleções parte 2, trecho sobre otimização da busca: List × Map).

1. Ajustar o `TransactionIngestor` para carregar **100.000** transações.
2. Criar `TransactionListRepository` com busca pelo cliente de origem retornando `Optional<Transaction>`.
3. Testar na `Main` com o arquivo PaySim completo, com clientes existentes (`C1231006815`) e inexistentes (`C12345`):

```text
Transação não encontrada para o cliente C12345
Transaction[step=1, type=PAYMENT, amount=9839.64, origin=TransactionCustomer[name=C1231006815, ...], ...]
```

4. Buscar o pior caso, a última transação da lista (`C1868032458`), medindo com `System.nanoTime()`.
5. Extrair a interface `TransactionRepository`, tendo a versão com lista como implementação.
6. Criar uma implementação com `Map<String, Transaction>`, repetir a busca por `C1868032458` e comparar os tempos.

</details>

<details>
<summary><strong>07 · Relatório Eficiente com Java NIO</strong></summary>

**Objetivo:** processar arquivos grandes sem `OutOfMemoryError`, comparando carregar tudo na RAM com processamento lazy.

**Referência:** Prof. Isidro (Aula 5: I/O, trecho sobre Java NIO × NIO.2 e Benchmark).

1. Remover temporariamente o limite de 100 mil linhas do `TransactionIngestor` e rodar a `Main` com `-Xmx128m` para observar o `OutOfMemoryError`. **Depois, restaurar o limite de 100 mil.**
2. Criar `TransactionReport` usando `Files.lines(Path)`, que devolve um `Stream<String>` lido linha a linha.
3. Processar o arquivo inteiro sem armazenar as transações, calculando apenas: total de linhas, total de fraudes e valor total transacionado.
4. Criar a classe `ReportMain` e executá-la com o arquivo original e `-Xmx128m`:

```text
Total de linhas: 6362620
Total de fraudes: 8213
Valor total transacionado: 1144392944759.77
```

</details>

<details>
<summary><strong>08 · Internacionalização do Relatório</strong></summary>

**Objetivo:** criar aplicações globais (i18n), formatando dados sensíveis à cultura e traduzindo textos conforme a preferência do usuário.

**Referência:** Prof. Alexandre Aquiles (Aula Extra 15: Internacionalização e Localização).

1. Criar os bundles `report_pt.properties` e `report_en.properties` e usar `ResourceBundle` nos rótulos (ex.: "Valor Total" / "Total Value").
2. Formatar valores monetários com `NumberFormat` conforme o `Locale`.
3. Permitir que o usuário escolha o idioma (português ou inglês) por argumento de execução.

</details>

<details>
<summary><strong>09 · Persistência em Banco com JDBC</strong></summary>

**Objetivo:** conectar a aplicação ao MySQL via JDBC e entender a importância de inserções em lote para ingestão massiva.

**Referência:** Prof. Alexandre Aquiles (Aulas Extras 12 e 13, JDBC partes 1 e 2: Banco de Dados, Docker e JDBC).

1. Definir um `docker-compose.yml` e subir o MySQL.
2. Criar o `script.sql` com a tabela `TRANSACTIONS`.
3. Adicionar o driver do MySQL no `pom.xml`.
4. Criar `TransactionSQLRepository`, implementando `findByOriginName`.
5. Adicionar `save(Transaction)` à interface `TransactionRepository` e implementá-lo em todas as classes.
6. Criar `DBMain`: ler **10 mil** transações, inserir no banco medindo o tempo, e buscar `C1231006815` (existente) e `C12345` (inexistente).

</details>

<details>
<summary><strong>10 · Ingestão Completa, Eficiente e Concorrente</strong></summary>

**Objetivo:** limitar o uso de memória com streams e aproveitar o hardware com programação concorrente e paralela.

**Referência:** Prof. Isidro (Aula 1: trecho sobre Virtual Threads) e Prof. Alexandre Aquiles (Aulas Extras 8 e 9: Concurrency partes 1 e 2, Threads e ExecutorService).

1. Mantendo 10 mil transações, identificar os gargalos da inserção e aplicar **batch** no JDBC. Limpar o banco, rodar de novo e comparar os tempos.
2. Criar `EfficientTransactionIngestor` com `readAsStream(String, Consumer<Transaction>)`, processando cada transação sem mantê-las em memória (inicialmente limitado a 10 mil).
3. Criar `IngestionMain` para inserir as milhões de transações no banco, limpando a tabela antes e medindo o tempo total.
4. Criar `readBatch(String, Consumer<List<Transaction>>)`, acumulando lotes de **10 mil** antes de chamar o consumer, e salvar em lote na `IngestionMain`.
5. Usar um `ExecutorService` no `readBatch` para processar os lotes em paralelo. Testar diferentes quantidades de threads e, opcionalmente, **Virtual Threads**.
6. Processar o CSV completo, medir os tempos e validar o resultado pelo total de transações, de fraudes e do valor transacionado.

</details>

---

## 📊 Resultados de referência

| Métrica (arquivo completo) | Valor esperado        |
|----------------------------|-----------------------|
| Total de linhas            | 6.362.620             |
| Total de fraudes           | 8.213                 |
| Valor total transacionado  | 1.144.392.944.759,77  |

<!-- Benchmarks: preencher quando as tarefas 06 e 10 estiverem concluidas
Benchmarks medidos por mim (preencher):

| Cenário                                    | Tempo |
|
-->

--------------------------------------------|-------|
| Busca pior caso, `List` (100 mil)         |       |
| Busca pior caso, `Map` (100 mil)          |       |
| Inserção JDBC um a um (10 mil)             |       |
| Inserção JDBC em batch (10 mil)            |       |
| Ingestão completa, sequencial             |       |
| Ingestão completa, `ExecutorService` (N threads) |  |
| Ingestão completa, Virtual Threads        |       |

---

## 🙏 Créditos

- **Prof. Isidro** e **Prof. Alexandre Aquiles**: enunciados e aulas de referência
- [Repositório base](https://github.com/unipds-projetos/modulo1-projeto-zenon-bank) (unipds-projetos)
- [PaySim](https://www.kaggle.com/datasets/ealaxi/paysim1): dataset sintético de transações financeiras (Kaggle)

<div align="center">

Desenvolvido por **Gustavo Mireski Demétrio**

</div>
