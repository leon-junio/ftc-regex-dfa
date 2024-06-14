
# FTC - Expressão regular para AFD e simulador de AFD

Um autômato finito determinístico (AFD) é denominado mínimo se não existir nenhum outro AFD com menos estados que reconheça a mesma linguagem. Toda expressão regular pode ser reduzida para um AFD e consequentemente isso define uma linguagem regular. O objetivo deste trabalho é implementar um programa que obtenha uma expressão regular qualquer e gerar seu AFD equivalente. O programa deve ser capaz de ler uma expressão regular e gerar o AFD equivalente utilizando o método de Thompson (converter expresão regular para automato não deterministico com transições lambda). O programa deve ser capaz de converter o AFN lambda para um AFD equivalente. O programa deve ser capaz de simular o AFD gerado. O programa deve ser capaz de gerar um arquivo no formato JFF (JFLAP simulator) com o AFD gerado. O programa deve ser capaz de gerar AFDs com N estados para teste.




## Objetivos

- Converter uma expressão regular para um AFD equivalente.
- Converter um AFN lambda para um AFD equivalente.
- Simular o AFD gerado.
- Gerar um arquivo no formato JFF (JFLAP simulator) com o AFD gerado.


## Requisitos

- JDK 17 ou Java SE 17 instalado na máquina. [Baixar JDK 17](https://www.oracle.com/br/java/technologies/downloads/#jdk17-windows)


## Execução

Para executar a aplicação apenas clique no executável *regex-to-dfa.bat*
ou execute manualmente em um terminal

```bash
  # Na pasta Root do programa
  java -jar regex-to-dfa.jar
```

Para compilar o código e buildar a aplicação execute o seguinte em um terminal
```bash
  # Na pasta Root do programa
   cd src 
  .\mvnw clean
  .\mvnw compile
  .\mvnw build
  .\mvnw package
``` 
## Artigos




## Autores

- [Edmar](https://www.github.com/Lexizz7)
- [Leon](https://www.github.com/leon-junio)
- [Felipe](https://github.com/felagmoura)

