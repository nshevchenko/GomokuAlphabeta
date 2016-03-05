import java.awt.*;
import java.util.*;


/*
    Move class
*/
class Move {
    public int row, col;
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

/*
    Player class
*/
public class Player130708081 extends GomokuPlayer {


    private static Color blak = Color.BLACK;
    private static Color whit = Color.WHITE;
    public static Color MAX_Color;

    //Color.BLACK
    //Color.WHITE
    public static void main(String[] args){
        Color[][] board = new Color[][] {
            {whit, null, null, null, null, null, null, null},
            {null, whit, null, null, null, null, null, null},
            {null, null, whit, null, null, null, null, null},
            {null, null, null, whit, blak, blak, null, null},
            {null, null, null, blak, blak, null, null, null},
            {null, null, null, null, null, whit, null, null},
            {null, null, blak, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
        };
        Player130708081 player = new Player130708081();
        // int score = AI.evaluateColor(blak, board);
        // boolean adj1 = AI.haveAdjacentNodes(board, 1,0);
        // boolean adj2 = AI.haveAdjacentNodes(board, 0,4);
        // boolean adj3 = AI.haveAdjacentNodes(board, 7,7);
        // System.out.println("score " + adj1 + " " + adj2 + " " + adj3);

        Move move = player.chooseMove(board, blak);
        System.out.println("best move is :" + move.row + " " + move.col);
    }


    public Move chooseMove(Color[][] board, Color myColour){
        Random rand = new Random();
        int x = rand.nextInt(4) + 2;
        int y = rand.nextInt(4) + 2;
        if(AI.boardEmpty(board))
            return new Move(x,y);
        Move bestMove = null;
        MAX_Color = myColour;
        // System.out.println("myColour MAX : " String.toString(myColour;
        int bestValue = -10000;

        for (Move move : AI.getAvailableMoves(board)) {
            Color[][] tempBoard = AI.copyBoard(board);
            tempBoard[move.row][move.col] = myColour;
            int score =  AI.minimax(tempBoard, 10, myColour, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestValue) {
                bestValue = score;
                // System.out.println("so far best is : " + bestValue);
                bestMove = new Move(move.row, move.col);
            }
        }

        if(bestMove == null )
            System.out.println("NOOOOO !!!! so sorry :( ");
        return bestMove;
    }
}


/*
    AI class
*/
class AI {

    // board size
    public static final int BOARD_SIZE = 8;
    // store available moves temporary
    private static ArrayList<Move> availableMoves;
    // store evaluations of board (translated into string)
    private static String[] rowsEvaluation;


    // MINIMAX implementation
    public static int minimax(Color[][] board, int depth, Color player, int alpha, int beta){
        int score;
        Color[][] tempBoard = new Color[BOARD_SIZE][BOARD_SIZE];
        ArrayList<Move> moves = getAvailableMoves(board);

        if(moves.size() == 0 || depth == 0){
            int evaluation = evaluate(board);
            score = evaluation;
            return score;
        }

        for(Move move : moves) {
            tempBoard = copyBoard(board);
            tempBoard[move.row][move.col] = player;
            score = minimax(tempBoard, depth-1, changePlayer(player), alpha, beta);
            if(player == Player130708081.MAX_Color) { // MAX
                if (score > alpha)
                    alpha = score;
                if (alpha >= beta)
                    return alpha;
                return alpha;
            } else {    // MIN
                if (score < beta)
                    beta = score;
                if (beta <= alpha)
                    return beta;
                return beta;
            }
        }
        if (player == Player130708081.MAX_Color)
           return alpha;
        return beta;
    }

    // Evaluate the board
    // create rows for current board state and find patterns with relateive scores
    private static int evaluate(Color[][] board){
        return evaluateColor(Player130708081.MAX_Color, board) - evaluateColor(changePlayer(Player130708081.MAX_Color), board);
    }

    // print board with the board as parameter
    public static void printBoard(Color[][] board){
        System.out.println("___________________");
        for(int i = 0; i < BOARD_SIZE; i++) {
            String line = "";
            for(int j = 0; j < BOARD_SIZE; j++) {
                if(board[i][j] == Color.BLACK)
                    line += "[B] ";
                else if(board[i][j] == Color.WHITE)
                    line += "[W] ";
                else line += "[ ] ";
            }
            System.out.println(line);
        }
        System.out.println("___________________");
    }


    // evaluate color
    // translate the board into sequence of astring in order to find combos
    public static int evaluateColor(Color colorToEvaluate, Color[][] board){

        int score = 0;

        // actual ROWS
        rowsEvaluation = new String[BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j =0; j < BOARD_SIZE; j++)
                translateCellToStr(colorToEvaluate, i, board[i][j]);
            // System.out.println(rowsEvaluation[i] + " ");
            score += evaluateRow(rowsEvaluation[i]);
        }


        // COLOUMNS
        rowsEvaluation = new String[BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j =0; j < BOARD_SIZE; j++)
                translateCellToStr(colorToEvaluate, i, board[j][i]);
            // System.out.println(rowsEvaluation[i] + " ");
            score += evaluateRow(rowsEvaluation[i]);
        }

        // DIAGONAL LEFT TOP TO RIGHT DOWN

        rowsEvaluation = new String[BOARD_SIZE];    // left edge
        for(int i = 0; i < BOARD_SIZE / 2; i++) {
            int h = i;
            for(int j = 0; j < BOARD_SIZE - i; j++, h++)
                translateCellToStr(colorToEvaluate, i, board[h][j]);
            score += evaluateRow(rowsEvaluation[i]);
        }

        rowsEvaluation = new String[BOARD_SIZE];    // top edge (1st part)
        for(int i = 1; i < BOARD_SIZE / 2; i++) {
            int h = i;
            for(int j = 0; j < BOARD_SIZE - i ; j++, h++)
                translateCellToStr(colorToEvaluate, i, board[j][h]);
            // System.out.println(rowsEvaluation[i] + " ");
            score += evaluateRow(rowsEvaluation[i]);
        }


        // DIAGONAL RIGHT TOP TO LEFT DOWN

        rowsEvaluation = new String[BOARD_SIZE];    // top edge (2nd part)
        for(int i = 0; i < BOARD_SIZE / 2; i++) {
            int h = 0;
            for(int j = 7 - i; j >= 0; j--, h++)
                translateCellToStr(colorToEvaluate, i, board[h][j]);
            // System.out.println(rowsEvaluation[i] + " ");
            score += evaluateRow(rowsEvaluation[i]);
        }

        rowsEvaluation = new String[BOARD_SIZE];    // right edge
        for(int i = 1; i < BOARD_SIZE / 2; i++) {
            int h = i;
            for(int j = BOARD_SIZE - 1; j > i - 1; j--, h++)
                translateCellToStr(colorToEvaluate, i, board[h][j]);
            // System.out.println(rowsEvaluation[i] + " ");
            score += evaluateRow(rowsEvaluation[i]);
        }

        return score;
    }

    // evaluate a string sequence of either orw, columns, diagonals
    private static int evaluateRow(String row){
        int score = 0;

        // X5

        // max scores for most dangerous situations
		if (row.indexOf("GGGGG") >= 0)
			return 1000;
        // less dangeours


        // x4
        if (row.indexOf("_GGGG_") >= 0)
            score += 500;

        if (row.indexOf("GGG_G") >= 0)
			score += 100;
        if (row.indexOf("GG_GG") >= 0)
			score += 100;
        if (row.indexOf("G_GGG") >= 0)
			score += 100;
        if (row.indexOf("_GGGG") >= 0)
			score += 100;
		if (row.indexOf("GGGG_") >= 0)
			score += 100;

        // x3
        if (row.indexOf("_GG_G_") >= 0)
			score += 10;
		if (row.indexOf("_G_GG_") >= 0)
			score += 10;
		if (row.indexOf("_GGG_") >= 0)
			score += 10;
        if (row.indexOf("GGG_") >= 0)
			score += 10;
        if (row.indexOf("_GGG") >= 0)
            score += 10;

        // x2
		if (row.indexOf("_G_G_") >= 0)
			score += 2;
        if (row.indexOf("_GG_") >= 0)
			score += 2;
		if (row.indexOf("_GG") >= 0)
			score += 1;
        if (row.indexOf("GG_") >= 0)
			score += 1;

		return score;
    }


    // Translate a real cell to char (in order to find patterns)
    private static void translateCellToStr(Color colorToEvaluate, int i, Color cell){
        if(cell == colorToEvaluate)
            rowsEvaluation[i] += 'G'; // no reason for being G, just a char to identify the current user
        else if(cell == null)
            rowsEvaluation[i] += '_';
        else
            rowsEvaluation[i] += 'O'; // literally can be anything (doesnt matter)
    }


    // change opposite color of the playre ( MAX / MIN )
    private static Color changePlayer(Color color){
        if(color == Color.BLACK)
            return Color.WHITE;
        return Color.BLACK;
    }


    // Copy board in order to achieve a deconstructive method
    public static Color[][] copyBoard(Color[][] board){
        Color[][] temp = new Color[BOARD_SIZE][BOARD_SIZE];
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j =0; j < BOARD_SIZE; j++)
                temp[i][j] = board[i][j];
        }
        return temp;
    }


    // Get available moves
    public static ArrayList<Move> getAvailableMoves(Color[][] board){
        availableMoves = new ArrayList<Move>();
        for(int i =0; i < BOARD_SIZE; i++) {
            for(int j =0; j < BOARD_SIZE; j++)
                if(board[i][j] != Color.BLACK && board[i][j] != Color.WHITE && haveAdjacentNodes(board, i, j))
                    // select only moves adjacent to nodes
                    availableMoves.add(new Move(i, j)); // [row, col]
        }
        return availableMoves;
    }


    // check if board in position [i, j] is an adjacent cell in the board
    public static boolean haveAdjacentNodes(Color[][] board, int i, int j){
        for(int x=-1; x <= 1; x++) {
            for(int y=-1; y <= 1; y++){
                try{
                    if(board[i + x][j + y] != null) {
                        return true;
                    }
                } catch(ArrayIndexOutOfBoundsException e){
                }
            }
        }
        return false;
    }


    // Is board empty
    public static boolean boardEmpty(Color[][] board){

        for(int i =0; i < BOARD_SIZE; i++) {
            for(int j =0; j < BOARD_SIZE; j++)
                if(board[i][j] == Color.BLACK || board[i][j] == Color.WHITE)
                    return false;
        }
        return true;
    }



}
