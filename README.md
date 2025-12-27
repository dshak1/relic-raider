Group Project for CMPT 276 Intro to Software Engineering


**Tech Stack:** Java | Maven | Object-Oriented Design


A Java-based adventure game developed for CMPT 276.

## Demo

![Relic Raider Demo](demo.png)

## Team Members
* Lex Camplin
* Sydney Komanac
* Diar Shakimov
* John Araujo

## Tech Stack

- **Language**: Java
- **Build Tool**: Maven
- **Architecture**: Object-oriented design with modular packages

## Project Structure

```
src/main/java/game/
├── behaviour/     # Game entity behaviors
├── core/         # Core game logic
├── entity/       # Game entities (player, enemies, etc.)
├── map/          # Game world and level design
├── reward/       # Items and rewards system
└── ui/           # User interface components
```

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Build the project: `mvn compile`
4. Run the game: `mvn exec:java -Dexec.mainClass="game.core.Main"`

## Features

- Adventure gameplay
- Entity-based architecture
- Modular design for easy extension
- Reward system

## Development

This project follows standard Java development practices with separation of concerns across different packages for maintainability and scalability.

## License

Academic project for CMPT 276 course.
