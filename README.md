# Game of Life

Implementation of the classic Game of Life in Java with JavaFX GUI and PostgreSQL database support.


<div align="center">
  <img src="ss/Zrzut ekranu 2026-01-8 o 12.00.15.png" alt="Configuration" width="500"/>
  <p><em>Configuration screen</em></p>
  
  <img src="ss/Zrzut ekranu 2026-01-8 o 12.00.40.png" alt="Simulation" width="500"/>
  <p><em>Game of Life simulation</em></p>
</div>

## Demo

📹 [Watch video](ss/Nagranie%20z%20ekranu%202026-01-8%20o%2012.35.16.mov)

## Quick Start

### Requirements
- Java 21+
- Maven 3.6+
- Docker (optional, for database)

### Installation and Run

```bash
# Build whole project :)
mvn clean install -DskipTests

# Run It
mvn -pl View exec:java

# Run database if u want to have acces to loading ur board into database
docker-compose up -d
```

## Features

-  Interactive Game of Life simulation
-  Board editing (click cells to toggle)
-  Save/load to file
-  Save/load from PostgreSQL database
-  PL/EN language support
-  Configure board size (4-14) and fill density

