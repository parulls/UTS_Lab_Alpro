import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int totalKendaraan = 0;
        double totalBiayaSemua = 0;
        boolean lanjut = true;

        System.out.println("====== Selamat Datang di Parkir Chan ======");

        while (lanjut) {
            // Input jenis kendaraan
            System.out.print("\nMasukkan jenis kendaraan (Mobil/Motor/Truk): ");
            String jenis = scanner.nextLine();

            // Buat objek kendaraan
            Kendaraan kendaraan = new Kendaraan(jenis);

            // Pilih metode input durasi
            System.out.print("\nPilih metode input durasi (manual/jam):");
            String metode = scanner.nextLine().toLowerCase();

            if (metode.equals("manual")) {
                System.out.print("Masukkan lama parkir (jam): ");
                int jam = scanner.nextInt();
                scanner.nextLine(); // Membersihkan newline
                kendaraan.hitungBiayaParkir(jam);
            } else if (metode.equals("jam")) {
                System.out.print("Masukkan jam masuk (format 24 jam): ");
                int jamMasuk = scanner.nextInt();
                System.out.print("Masukkan jam keluar (format 24 jam): ");
                int jamKeluar = scanner.nextInt();
                scanner.nextLine(); // Membersihkan newline
                kendaraan.hitungBiayaParkir(jamMasuk, jamKeluar);
            } else {
                System.out.println("Pilihan tidak valid!");
                continue;
            }

            // Tampilkan ringkasan
            kendaraan.tampilkanRingkasan();

            // Akumulasi total
            totalKendaraan++;
            totalBiayaSemua += kendaraan.getTotalBiaya();

            // Tanya apakah ingin menambah kendaraan
            System.out.print("\nTambah kendaraan lain? (y/n): ");
            String jawaban = scanner.nextLine();
            if (!jawaban.equalsIgnoreCase("y")) {
                lanjut = false;
            }
        }

        // Tampilkan ringkasan akhir
        System.out.println("\nRingkasan Akhir\t\t\t:");
        System.out.println("Total Kendaraan\t\t\t: " + totalKendaraan);
        System.out.println("Total Semua Biaya Parkir: Rp" + totalBiayaSemua);

        scanner.close();
    }
}
