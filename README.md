# 🚢 Battleship Game - Java Implementation 🎯

## 📝 Overview
This is a Java implementation of the classic Battleship game. The game logic is written in a structured manner, using object-oriented programming principles. The program consists of a single Java file, `BattleshipGame.java`, which handles the entire game flow, from initializing the game board to taking user input and determining the winner.

## 📥 Imports & Graphics Implementation
The game features a graphical user interface (GUI) built using **Swing**, allowing for an interactive game experience instead of a purely text-based interface.

### 📌 Imported Packages
- `javax.swing.*` - Used for GUI components like frames, panels, buttons, and labels.
- `java.awt.*` - Provides support for drawing and layout management.
- `java.util.Scanner` - Used to take user input (if applicable in CLI mode).
- `java.util.Random` - Used to randomly place ships on the grid.

### 🎨 GUI Implementation
The game utilizes **JFrame** to create the main window, and **JPanel** for rendering the grid. Ships, hits, and misses are visually represented using **JButtons** or **JLabels** with different colors and symbols. **ActionListeners** handle user interactions, making the game more dynamic.

## 🎮 Game Logic
The game is played on a grid where ships are placed at predefined locations. The player tries to guess the positions of the ships by clicking on grid cells. If a guess hits a ship, it is marked as a hit ✅; otherwise, it is a miss ❌. The game continues until all ships are sunk.

## 🔢 Variables
The following are key variables used in the program:

- 🏗️ `grid[][]` - A 2D array representing the game board.
- 🚢 `ships[][]` - Stores the positions of the ships.
- 🎯 `HIT` - A constant indicating a successful hit.
- ❌ `MISS` - A constant indicating an unsuccessful attempt.
- 🚀 `SHIP` - A constant representing ship locations.
- ⬜ `EMPTY` - A constant representing empty cells.

## 🔧 Functions and Their Purpose
### ⚙️ `initializeGame()`
- 🛠️ Sets up the game board.
- 📌 Places ships randomly or at predefined positions.

### 🖥️ `displayBoard()` (For GUI Mode)
- 🏁 Updates the graphical board to show hits, misses, and remaining ships.
- 🎨 Uses color-coding to differentiate game states.

### 🎯 `makeMove(int x, int y)`
- 🎮 Processes player input.
- ✅ Checks if the guessed coordinates hit or miss a ship.

### 🏆 `checkWinCondition()`
- 🔍 Checks if all ships have been sunk.
- 🏅 Ends the game when the player wins.

### 🔄 `playGame()`
- 🔁 Handles the game loop.
- 🎤 Accepts user input and calls appropriate functions.

## 🗺️ Code Roadmap
1. **🚀 Initialization**
   - 🛠️ Create the game board.
   - 🚢 Place ships on the board.
2. **🔄 Game Loop**
   - 🖥️ Display the board.
   - 🎯 Take player input (click or command-line entry).
   - ✅❌ Check if the move was a hit or a miss.
   - 🔄 Update the board accordingly.
   - 🔁 Repeat until all ships are sunk.
3. **🏆 End of Game**
   - 🎉 Declare victory once all ships are hit.

---

To run the game, use the following command:

```sh
java BattleshipGame.java
```


🎉 Enjoy playing Battleship! 🚢🔥

