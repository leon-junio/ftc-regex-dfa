
# FTC Minimização de AFD

Um autômato finito determinístico (AFD) é denominado mínimo se não existir nenhum outro AFD
com menos estados que reconheça a mesma linguagem. O objetivo deste trabalho é implementar um programa de obtenha um AFD mínimo para determinada linguagem.




## Objetivos

- Minimizar AFD com dois métodos (n^2 e n log n)
- Ler e escrever o formato JFF (JFLAP simulator)
- Gerar AFDs com N estados para teste
- Medir o tempo de execução e performance dos algoritmos


## Requisitos

- JDK 17 ou Java SE 17 instalado na máquina. [Baixar JDK 17](https://www.oracle.com/br/java/technologies/downloads/#jdk17-windows)



## Execução

Para executar a aplicação apenas clique no executável *dfa-minimization.bat*
ou execute manualmente em um terminal

```bash
  # Na pasta Root do programa
  java -jar afd-minimization.jar
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

- [Norbert Blum. 1996. An O(n log n) implementation of the standard method for minimizing n-state finite automata. Information Processing Letters 57, 2 (Jan. 29, 1996), 65–69](https://doi.org/10.1016/0020-0190(95)00199-9)
- [Hopcroft, John. An n log n algorithm for minimizing states in a finite automaton.Theory of machines and computations (Proc. Internat. Sympos., Technion, Haifa, 1971), New York: Academic Press, pp. 189–196, 1971.](http://i.stanford.edu/pub/cstr/reports/cs/tr/71/190/CS-TR-71-190.pdf)


## Autores

- [Edmar](https://www.github.com/Lexizz7)
- [Leon](https://www.github.com/leon-junio)

