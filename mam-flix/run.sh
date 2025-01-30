#!/bin/bash

# Diretórios das funções Azure
FUNC1_DIR="/Users/marcio/Development/JOB/AZURE/AZ204/mam-flix/fnSaveMovie/fnmamflix/"
FUNC2_DIR="/Users/marcio/Development/JOB/AZURE/AZ204/mam-flix/fnGetAllMovies/fnmamflix"
FUNC3_DIR="/Users/marcio/Development/JOB/AZURE/AZ204/mam-flix/fnGetMovieDetails/fnmamflix"
FUNC4_DIR="/Users/marcio/Development/JOB/AZURE/AZ204/mam-flix/fnPostDataStorage/"

# Matar processos que estão usando as portas 7071 até 7074
for port in {7071..7074}; do
    kill -9 $(lsof -t -i :$port) 2>/dev/null
done

# Executar as funções simultaneamente
(
    cd "$FUNC1_DIR" || exit
    mvn azure-functions:run &
)

(
    cd "$FUNC2_DIR" || exit
    mvn azure-functions:run &
)

(
    cd "$FUNC3_DIR" || exit
    mvn azure-functions:run &
)

(
    cd "$FUNC4_DIR" || exit
    func start &
)

# Aguardar que todas as funções terminem
wait