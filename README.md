# Nabil's Game Collection

## Run
####  Compile
javac MyGames.java HillHeroes/*.java Snake/*.java BattleSnake/*.java TetrisWorld/*.java Chess/*.java Rocketman/*.java

#### Run
java -cp . MyGames
OR
java MyGames

(Working with JDK 8)

## Descriptions

### Hill Heroes
##### ~ May 2015, start of summer break
###### Controls: Click to select a piece, click to move it
First programmed game and my first experience with GUIs. Hill
Heroes is about conquering a hill with 5 of your team's pieces. Fire can 
knock grass back, water can knock fire back, and grass can knock water back.
It's a combat triangle implemented in a board game, and I decided to make it
a GUI! A status bar in the bottom gives information to the user on what to do.

### Snake
##### ~ January 2016, Winter break
###### Controls: Arrow keys/WASD to move snake
A different take on the classic snake game, in which you are able to
level up after eating enough foods. A higher level means a faster game and a
higher score per food, so quick reaction times and strategy are crucial!

### BattleSnake
##### ~ January 2016, post-snake
###### Controls: WASD to move red, Arrow keys to move blue, YGHJ to move green, PL;' to move yellow. R to restart
After loving how my Snake game turned out, an idea came into my head of implementing a two player snake game, something that I have never
played before. I made the logic similar to that of Snake's, except I included
power-ups rather than just foods, which can do things such as slow time,
speed up time, make the snake much larger, etc...


### Tetris World
##### ~ May-June 2016, summer break
###### Controls: Arrow keys to move piece, opposite arrow key of current shift to rotate. R can also rotate. Left shift to turn the shift. Right shift to hold piece. ESC to pause. 
A 'draft' of Tetriworld, contains the program that
started what I would do for the duration of the month of June. The objective
of Tetris World is the same as TetriWorld, which is to fill in the edges
of a square whose center is the middle. Doing so would push ever tile inside the square
outwards. Numerous things in this game's algorithm have been rethought and
ameliorated for the actual TetriWorld game.

### Chess
##### ~ December 2016, winter break
###### Controls: Click to select a piece, click to move it
This was more of a refresher I imposed on myself. I got into playing
the world-famous chess game, and figured it will be easy to program it given
my experience. I programmed this game, as well as designed all pieces (which 
are just letters :D) in less than 6 hours. The game is won by eating the king.

### Rocket man
##### ~ December 2016, winter break
###### Controls: Arrow keys/WASD to move, N to skip a level (was a debug, but kept it)
This was my 2016-17 Winter Break's project. The idea stemmed from
watching the "World's Hardest Game" being completed on YouTube, and I figured
I am able to design a similar game myself. I designed icons that have a space
theme, and proceeded to program the logic for fireball
movement (lines, circles, or rectangles), user movement, objects that can be
encountered, etc... The objective of the game is to reach the portal, avoiding
all obstacles on the way, unlocking doors when necessary.

#### If you have any questions/feedback about the games, or the collection as a whole, don't hesitate to share it with me! My email is ndarwich@gmu.edu
