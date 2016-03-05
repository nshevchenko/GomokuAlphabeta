import java.awt.*;
import java.util.*;


class Base{
    static final int boardSize = 3;
}

class Move {
    public int row, col, heuristic;
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

class AI extends Base {
    static ArrayList<Move> choices;
    static ArrayList<Move> moves;

    public static Move chooseMove(char[][] board, char player){ // player = x or o
        // private int[][] preferredMoves = {
        //     {1, 1}, {0, 0}, {0, 2}, {2, 0}, {2, 2},
        //     {0, 1}, {1, 0}, {1, 2}, {2, 1}
        // };
        choices = new ArrayList<Move>();

        Move bestMove = null;

        int bestValue = -10000;
        for (Move move : getAvailableMoves(board)) {
            char[][] tempBoard = copyBoard(board);
            tempBoard[move.row][move.col] = 'o';
            int score =  minimax(tempBoard, 5, 'x', Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestValue) {
                bestValue = score;
                //start code edit
                System.out.println("so far best is : " + bestValue);
                bestMove = new Move(move.row, move.col);
                //end code edit
            }
        }
        return bestMove;
    }

    public static char changePlayer(char player){
        if (player == 'o')
            return 'x';
        else
            return  'o';
    }

    // int [] contains score, bestRow, bestCol
    public static int minimax(char[][] board, int depth, char player, int alpha, int beta){
        int score;
        char[][] tempBoard = new char[3][3];
        ArrayList<Move> moves = getAvailableMoves(board);

        // System.out.println("size = " + moves.size() );
        if(moves.size() == 0 || depth == 0){
            int evaluation = evaluate(board);
            // TicTacToe.printBoard(board);
            // System.out.println("evaluating = " + evaluation );
            score = evaluation;
            return score;
        }
        for(Move move : moves) {
            if(player == 'o') { // MAX
                tempBoard = copyBoard(board);
                tempBoard[move.row][move.col] = player;
                score = minimax(tempBoard, depth - 1,  changePlayer(player), alpha, beta);

                if (score > alpha)
                    alpha = score;
                if (alpha >= beta)
                 return beta;

            } else if(player == 'x')  {// MIN
                tempBoard = copyBoard(board);
                tempBoard[move.row][move.col] = player;
                score = minimax(tempBoard, depth-1, changePlayer(player), alpha, beta);

                if (score < beta)
                    beta = score;
                if (beta <= alpha)
                  return alpha;
            }
        }
        if (player == 'o')
           return alpha;
        return beta;
    }


    public static char[][] copyBoard(char[][] board){
        char[][] temp = new char[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++) {
            for(int j =0; j < boardSize; j++)
                temp[i][j] = board[i][j];
        }
        return temp;
    }

    public static ArrayList<Move> getAvailableMoves(char[][] board){
        moves = new ArrayList<Move>();
        for(int i =0; i < boardSize; i++) {
            for(int j =0; j < boardSize; j++)
                if(board[i][j] != 'o' && board[i][j] != 'x')
                    moves.add(new Move(i, j)); // [row, col]
        }
        return moves;
    }

    /** The heuristic evaluation function for the current board
    @Return +100, +10, +1 for EACH 3-, 2-, 1-in-a-line for computer.
    -100, -10, -1 for EACH 3-, 2-, 1-in-a-line for opponent.
    0 otherwise
    */
    private static int evaluate(char[][] board) {
        int score = 0;
        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
        score += evaluateLine(board, 0, 0, 0, 1, 0, 2);  // row 0
        score += evaluateLine(board, 1, 0, 1, 1, 1, 2);  // row 1
        score += evaluateLine(board, 2, 0, 2, 1, 2, 2);  // row 2
        score += evaluateLine(board, 0, 0, 1, 0, 2, 0);  // col 0
        score += evaluateLine(board, 0, 1, 1, 1, 2, 1);  // col 1
        score += evaluateLine(board, 0, 2, 1, 2, 2, 2);  // col 2
        score += evaluateLine(board, 0, 0, 1, 1, 2, 2);  // diagonal
        score += evaluateLine(board, 0, 2, 1, 1, 2, 0);  // alternate diagonal
        return score;
    }

    public static int evaluateLine (char[][] board, int row1, int col1, int row2, int col2, int row3, int col3) {

      int score = 0;
      // First cell
      if (board[row1][col1] == 'x') {
         score = 1;
     } else if (board[row1][col1] == 'o') {
         score = -1;
      }

      // Second cell
      if (board[row2][col2] == 'x') {
         if (score == 1) {   // cell1 is mySeed
            score = 10;
         } else if (score == -1) {  // cell1 is oppSeed
            return -10;
         } else {  // cell1 is empty
            score = 1;
         }
      } else if (board[row2][col2] == 'o') {
         if (score == -1) { // cell1 is oppSeed
            score = -10;
         } else if (score == 1) { // cell1 is mySeed
            return 0;
         } else {  // cell1 is empty
            score = -1;
         }
      }

      // Third cell
      if (board[row3][col3] == 'x') {
         if (score > 0) {  // cell1 and/or cell2 is mySeed
            score *= 10;
         } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = 1;
         }
     } else if (board[row3][col3] == 'o') {
         if (score < 0) {  // cell1 and/or cell2 is oppSeed
            score *= 10;
         } else if (score > 1) {  // cell1 and/or cell2 is mySeed
            return 0;
         } else {  // cell1 and cell2 are empty
            score = -1;
         }
      }
      return score *-1;
    }
}



public class TicTacToe extends Base{

    static final int boardSize = 3;
    static char[][] board; // o or x
    static boolean playing = false;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        board = new char[boardSize][boardSize];
        playing = true;
        setupBoard(board);

        printBoard(board);

        System.out.println("start playing");

        while(playing){
            System.out.println("Your move in coordinates: x, y");
            Move myMove = getInputMove(s, board);
            applyMove(myMove, 'x'); // my move
            printBoard(board);
            Move moveAI = AI.chooseMove(board, 'o');
            applyMove(moveAI, 'o');
            printBoard(board);
        }
    }

    public static void setupBoard(char[][] board){
        // board[0] = new char[]{'x', 'o', 'o'};
        // board[1] = new char[]{'\0', 'o', '\0'};
        // board[2] = new char[]{'\0', '\0', '\0'};
    }

    public static Move getInputMove(Scanner scan, char[][] board){
        boolean valid = false;
        Move move = null;

        while(!valid){
            String str = scan.nextLine();
            String[] moveIntStr = str.split(",");

            int row = Integer.parseInt(moveIntStr[1]);
            int col =  Integer.parseInt(moveIntStr[0]);
            if(board[row][col] != 'o' && board[row][col] != 'x'){
                move = new Move(row, col);
                valid = true;
            }
            else
                System.out.println("occupied, try again:)");
        }
        return move;
    }

    public static void applyMove(Move move, char player){
            board[move.row][move.col] = player;
    }

    public static void printBoard(char[][] board){
        for(int i =0; i < boardSize; i++) {
            char[] row = board[i];
            String line = "";

            for(int j =0; j < boardSize; j++) {
                if(row[j] != 'o' && row[j] !='x')
                    line +=  " |";
                else
                    line += row[j] + "|";
            }
            System.out.println(line);
        }
    }
}
