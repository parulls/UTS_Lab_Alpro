import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LotreBoard game = new LotreBoard();
        boolean gameRunning = true;

        System.out.println("=== SELAMAT DATANG DI LOTRE GOSOK ===");
        System.out.println("Tebak posisi (format input: baris kolom, mulai dari 0 0)");
        System.out.println("Simbol: * = belum dibuka, O = aman, X = bom");
        System.out.println("Temukan semua kotak aman untuk menang!\n");

        while (gameRunning) {
            game.displayBoard();

            System.out.print("Masukkan baris dan kolom (misal: 0 0): ");
            String input = scanner.nextLine(); // Mengambil input satu baris
            String[] parts = input.split(" "); // Memisahkan input menjadi baris dan kolom
            if (parts.length != 2) {
                System.out.println("Input tidak valid. Harap masukkan dua angka.");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);

                if (row < 0 || row >= 4 || col < 0 || col >= 5) {
                    System.out.println("Posisi tidak valid! Harap masukkan angka antara 0 dan 3 untuk baris, dan 0 hingga 4 untuk kolom.");
                    continue;
                }

                if (!game.guess(row, col)) {
                    game.displayBoard();
                    System.out.println("BOOM! Kamu kena bom. Game over!");
                    gameRunning = false;
                } else if (game.isGameOver()) {
                    game.displayBoard();
                    System.out.println("SELAMAT! Kamu berhasil membuka semua kotak aman!");
                    gameRunning = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Harap masukkan angka yang benar.");
            }
        }

        System.out.println("\nTerima kasih telah bermain Lotre Gosok!"); 
        scanner.close();
    }
}
