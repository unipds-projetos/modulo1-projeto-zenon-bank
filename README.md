# 🏦 Zenón Bank - Projeto Prático: Fundamentos Java

Bem-vindo ao repositório base do projeto prático da disciplina de Fundamentos Java. Este projeto simula o backend de uma fintech fictícia, o **Zenón Bank**, onde você aplicará conceitos desde manipulação de tipos básicos até processamento de alta performance e concorrência.

## 📋 Sobre o Projeto

O objetivo é consolidar os conhecimentos adquiridos em aula através de um cenário realista. Você lidará com:
* Modelagem de dados com **Records**.
* API de Collections e Streams.
* Manipulação de I/O de arquivos grandes.
* Tratamento de Exceções.
* Integração com Banco de Dados.
* Concorrência e Paralelismo.

Siga o passo a passo do quadro de atividades (Trello) do projeto prático dessa disciplina, disponível nos vídeos de projeto prático da disciplina.

## 📦 Requisitos

* **Git e GitHub**: para controle de versões e portifólio de repositórios.
* **Java JDK**: 25 (LTS).
* **IDE**: IntelliJ IDEA ou Eclipse.
* **Docker e Docker Compose**: Para a tarefa de Banco de Dados.

## Tarefa 01 — ambiente

Projeto Maven com `groupId` `br.com.zenon` e `artifactId` `zenon-fraud-detector`.
O código fica em `src/main/java`, no pacote `br.com.zenon.frauddetector`.

O editor deste projeto é o Cursor. O JDK 25 está em `~/.jdks`, e o workspace já aponta o terminal e o suporte a Java para essa instalação. Em um terminal novo, dentro desta pasta:

```bash
export JAVA_HOME="$HOME/.jdks/jdk-25.0.4.1+1/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
mvn compile
java -cp target/classes br.com.zenon.frauddetector.Aplicacao
```

O CSV do PaySim fica em `data/`, fora de `src/main/resources`. Esse diretório já está no `.gitignore` e não entra no Git.
