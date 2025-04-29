import java.util.Random;

public class LotreBoard {
    private char[][] board;
    private boolean[][] revealed;
    private int[][] data;
    private int safeOpened;

    public LotreBoard() {
        board = new char[4][5];
        revealed = new boolean[4][5];
        data = new int[4][5];
        safeOpened = 0;
        generateBoard();
    }

    public void generateBoard() {
        // Initialize all cells as safe (0)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                data[i][j] = 0;
                revealed[i][j] = false;
                board[i][j] = '*';
            }
        }

        // Place 2 bombs randomly
        Random rand = new Random();
        int bombsPlaced = 0;
        while (bombsPlaced < 2) {
            int row = rand.nextInt(4);
            int col = rand.nextInt(5);
            if (data[row][col] == 0) {
                data[row][col] = 1;
                bombsPlaced++;
            }
        }
    }

    public void displayBoard() {
        System.out.println("\n==LOTRE GOSOK==");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(" ");
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("===============");
    }

    public boolean guess(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 5 || revealed[row][col]) {
            System.out.println("Posisi tidak valid atau sudah dibuka!");
            return true;
        }

        revealed[row][col] = true;

        if (data[row][col] == 1) { // Bomb
            board[row][col] = 'X';
            return false;
        } else { // Safe
            board[row][col] = 'O';
            safeOpened++;
            return true;
        }
    }

    public boolean isGameOver() {
        return safeOpened == 18; // All safe cells opened
    }
}
