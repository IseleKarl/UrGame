//Royal Game of Ur - programmed by X4R1
package urgame;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class UrGame {
	static int courseLen = 14; //length of the course
	//pieces finish the course when they reach location courseLen+1
	static int startSafeZone = 4; //location of the last square of the starting safe zone
	static int combatZone = 12; //location of the last square of the combat zone
	static int combatRosLoc = 8; //location of the rosette in the combat zone
	static int startRosLoc = 4; //location of the rosette at the start of the course
	static int endRosLoc = 14; //location of the rosette at the end of the course
	
	//equivalent to tossing 4 coins and counting the number of heads
	static int roll() {
		int roll = 0;
		Random rand = new Random();
		for (int i=0; i<4; i++) { roll += rand.nextInt(2); }
		return roll;
	}
	static void testRoll(int sampleSize) {
		int zero = 0, one = 0, two = 0, three = 0, four = 0, total = 0;
		int roll;
		for (int i=0; i<sampleSize; i++) {
			roll = roll();
			switch (roll) {
			case 0:
				zero++;
				break;
			case 1:
				one++;
				break;
			case 2:
				two++;
				break;
			case 3:
				three++;
				break;
			case 4:
				four++;
				break;
			}
		}
		total = zero + one + two + three + four;
		System.out.println((double)zero/total);
		System.out.println((double)one/total);
		System.out.println((double)two/total);
		System.out.println((double)three/total);
		System.out.println((double)four/total);
	}
	
	static void printPieceLocations(int[] player) {
		for (int i=0; i<7; i++) {
			System.out.print(" " + player[i]);
		}
	}
	
	//evaluate which pieces the player can move given the current game state
	static int[] options(int roll, int[] player, int[] opponent) {
		int[] options = {1,1,1,1,1,1,1};
		for (int i=0; i<7; i++) {//iterating through pieces to move by roll
			if (player[i]==courseLen+1) {
				options[i] = 0;
			} else {
				for (int j=0; j<7; j++) {//iterating through pieces possibly being moved to
					if (i!=j && player[j]!=0 && player[j]!=courseLen+1 && player[i]+roll==player[j] 
							|| player[i]+roll>courseLen+1 || player[i]+roll==combatRosLoc && opponent[j]==combatRosLoc) {
						options[i] = 0;
					}
				}
			}
		}
		return options;
	}	
	static void printOptions(int[] options) {
		for (int i=0; i<7; i++) {
			if (options[i] == 1) {
				System.out.print(" " + i);
			}
		}
	}
	static boolean isValidOption(int move, int[] options) {
		for (int i=0; i<7; i++) {
			if (options[i]==1 && i == move) {
				return true;
			}
		}
		return false;
	}
	
	//check whether player has won
	static boolean hasWon(int[] player) {
		boolean gameOver = true;
		for (int i=0; i<7 && gameOver; i++) {
			if (player[i]!=courseLen+1) {
				gameOver = false;
			}
		}
		return gameOver;
	}
	
	//queries player for what piece they want to move, validates input
	static int queryMove(int[] options, int turns, Scanner kbd) throws IOException {
		int move;
		System.out.print("Player " + turns%2 + " can move pieces ");
		printOptions(options);
		System.out.println();
		System.out.print("Which piece will player " + turns%2 + " move? ");
		move = kbd.nextInt();
		while (!isValidOption(move, options)) {
			System.out.print("Invalid input. Please enter one of these options");
			printOptions(options);
			System.out.print(": ");
			move = kbd.nextInt();
		}
		return move;
	}
	
	//determines the ID number of the opponent's piece that was captured at position, -1 if none captured
	static int captured(int[] opponent, int position) {
		int captured = -1;
		for (int i=0; i<7; i++) {
			if (opponent[i]>startSafeZone && opponent[i]<=combatZone && opponent[i]==position) {
				captured = i;
			}
		}
		return captured;
	}
	
	static void turn(int[][] players, int turns, Scanner kbd) throws IOException {
		int roll = roll(); //stores result of dice rolls
		System.out.println("Roll: " + roll);
		if (roll>0) {
			int[] options = options(roll, players[turns%2], players[(turns+1)%2]);
			int numOptions = 0;
			for (int i=0; i<7; i++) { if (options[i]==1) { numOptions++; } }
			if (numOptions > 0) {
				int pieceToMove = queryMove(options, turns, kbd);
				players[turns%2][pieceToMove] += roll;
				if (players[turns%2][pieceToMove]==startRosLoc || players[turns%2][pieceToMove]==combatRosLoc 
						|| players[turns%2][pieceToMove]==endRosLoc) {
					turn(players, turns, kbd);
				}
				int captured = captured(players[(turns+1)%2], players[turns%2][pieceToMove]);
				if (captured > -1) {
					players[(turns+1)%2][captured] = 0;
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		int[] playerZero = {0, 0, 0, 0, 0, 0, 0};
		int[] playerOne = {0, 0, 0, 0, 0, 0, 0};
		int[][] players = {playerZero, playerOne};
		int turns = 0; //used to determine whose turn it is
		Scanner kbd = new Scanner(System.in);
		while (!hasWon(playerZero) && !hasWon(playerOne)) {
			turn(players, turns, kbd);
			System.out.println();
			System.out.print("Player 0's piece locations: ");
			printPieceLocations(players[0]);
			System.out.println();
			System.out.print("Player 1's piece locations: ");
			printPieceLocations(players[1]);
			System.out.println();
			System.out.println();
			turns++;
		}
		kbd.close();
	}
}
