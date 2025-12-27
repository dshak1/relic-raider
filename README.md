# Relic Raider

```
   _____      _ _      _     _____       _
  |  __ \    (_) |    | |   |  __ \     (_)
  | |__) |___ _| | ___| |_  | |__) |__ _ _ __ ___ ___
  |  _  // _ \ | |/ _ \ __| |  _  // _ \ | '_ \ / _ \
  | | \ \  __/ | |  __/ |_  | | \ \  __/ | | | |  __/
  |_|  \_\___|_|_|\___|\__| |_|  \_\___|_|_| |_|\___|
```

**Tech Stack:** Java | Maven | Object-Oriented Design

---

A Java-based adventure game developed for CMPT 276.

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
