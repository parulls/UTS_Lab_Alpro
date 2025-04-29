public class Kendaraan {
    private String jenis;
    private int lamaParkir;
    private double biayaPerJam;

    // Constructor
    public Kendaraan(String jenis) {
        this.jenis = jenis;
        // Set biaya per jam berdasarkan jenis kendaraan
        switch (jenis.toLowerCase()) {
            case "mobil":
                this.biayaPerJam = 5000;
                break;
            case "motor":
                this.biayaPerJam = 3000;
                break;
            case "truk":
                this.biayaPerJam = 10000;
                break;
            default:
                this.biayaPerJam = 0;
        }
    }

    // Overloading method untuk menghitung biaya parkir
    // Versi 1: Input langsung lama parkir
    public void hitungBiayaParkir(int jam) {
        this.lamaParkir = jam;
    }

    // Versi 2: Input jam masuk dan jam keluar
    public void hitungBiayaParkir(int jamMasuk, int jamKeluar) {
        this.lamaParkir = jamKeluar - jamMasuk;
    }

    // Method menghitung total biaya dengan diskon
    public double hitungTotalBiaya() {
        double total = lamaParkir * biayaPerJam;
        if (lamaParkir > 5) {
            total *= 0.9; // Diskon 10%
        }
        return total;
    }

    // Method menampilkan ringkasan
    public void tampilkanRingkasan() {
        System.out.println("\n------Ringkasan Parkir------");
        System.out.println("Jenis Kendaraan\t\t: " + jenis);
        System.out.println("Lama Parkir\t\t\t: " + lamaParkir + " jam");
        System.out.println("Total Biaya\t\t\t: Rp" + hitungTotalBiaya());
    }

    // Getter untuk jenis kendaraan
    public String getJenis() {
        return jenis;
    }

    // Getter untuk lama parkir
    public int getLamaParkir() {
        return lamaParkir;
    }

    // Getter untuk total biaya
    public double getTotalBiaya() {
        return hitungTotalBiaya();
    }
}
