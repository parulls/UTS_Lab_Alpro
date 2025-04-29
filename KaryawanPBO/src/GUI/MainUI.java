package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainUI {
    private static Map<String, Karyawan> daftarKaryawan = new HashMap<>();
    private static JFrame frame;
    private static JTable table;
    private static final String DATA_FILE = "karyawan.txt";

    static class Karyawan implements Serializable {
        private final String id;
        private String nama;
        private String posisi;
        private double gaji;
        private Date tanggalBergabung;
        private String divisi;

        public Karyawan(String id, String nama, String posisi, double gaji,
                        Date tanggalBergabung, String divisi) {
            this.id = id;
            this.nama = nama;
            this.posisi = posisi;
            this.gaji = gaji;
            this.tanggalBergabung = tanggalBergabung;
            this.divisi = divisi;
        }

        // Getters
        public String getId() { return id; }
        public String getNama() { return nama; }
        public String getPosisi() { return posisi; }
        public double getGaji() { return gaji; }
        public Date getTanggalBergabung() { return tanggalBergabung; }
        public String getDivisi() { return divisi; }

        // Setters
        public void setNama(String nama) { this.nama = nama; }
        public void setPosisi(String posisi) { this.posisi = posisi; }
        public void setGaji(double gaji) { this.gaji = gaji; }
        public void setTanggalBergabung(Date tanggalBergabung) { this.tanggalBergabung = tanggalBergabung; }
        public void setDivisi(String divisi) { this.divisi = divisi; }
    }

    public static void main(String[] args) {
        muatData();
        inisialisasiGUI();
    }

    private static void inisialisasiGUI() {
        frame = new JFrame("Sistem Manajemen Karyawan");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);

        try {
            frame.setIconImage(new ImageIcon("icon.png").getImage());
        } catch (Exception e) {
            System.err.println("Icon tidak ditemukan");
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Sistem Manajemen Karyawan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Panel Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = buatTombolToolbar("Tambah", Color.GREEN.darker());
        JButton searchButton = buatTombolToolbar("Cari", Color.BLUE);
        JButton filterButton = buatTombolToolbar("Filter", Color.CYAN.darker());
        JButton reportButton = buatTombolToolbar("Laporan", Color.MAGENTA);
        JButton saveButton = buatTombolToolbar("Simpan Data", Color.ORANGE);
        JButton loadButton = buatTombolToolbar("Muat Data", Color.PINK);
        JButton exitButton = buatTombolToolbar("Keluar", Color.RED);

        toolbarPanel.add(addButton);
        toolbarPanel.add(searchButton);
        toolbarPanel.add(filterButton);
        toolbarPanel.add(reportButton);
        toolbarPanel.add(saveButton);
        toolbarPanel.add(loadButton);
        toolbarPanel.add(exitButton);

        headerPanel.add(toolbarPanel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabel
        String[] columns = {"ID", "Nama", "Posisi", "Gaji", "Tanggal Bergabung", "Divisi", "Aksi"};
        table = new JTable(new DefaultTableModel(columns, 0));
        table.setRowHeight(30);

        // Tambah tombol aksi ke tabel
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Status Bar
        JLabel statusLabel = new JLabel("Total Karyawan: " + daftarKaryawan.size());
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Aksi Tombol
        addButton.addActionListener(e -> tampilkanDialogTambahKaryawan());
        searchButton.addActionListener(e -> tampilkanDialogCari());
        filterButton.addActionListener(e -> tampilkanDialogFilter());
        reportButton.addActionListener(e -> tampilkanDialogLaporan());
        saveButton.addActionListener(e -> simpanData());
        loadButton.addActionListener(e -> muatData());
        exitButton.addActionListener(e -> System.exit(0));

        perbaruiTabel();
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JButton buatTombolToolbar(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private static void tampilkanDialogTambahKaryawan() {
        JTextField idField = new JTextField();
        JTextField namaField = new JTextField();
        JTextField posisiField = new JTextField();
        JTextField gajiField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField deptField = new JTextField();

        JPanel panel = buatPanelInput(
                new String[]{"ID:", "Nama:", "Posisi:", "Gaji:", "Tanggal Bergabung (yyyy-mm-dd):", "Divisi:"},
                new JTextField[]{idField, namaField, posisiField, gajiField, dateField, deptField}
        );

        int result = JOptionPane.showConfirmDialog(frame, panel, "Tambah Karyawan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim();
                if (daftarKaryawan.containsKey(id)) {
                    tampilkanPesan("ID Karyawan sudah ada!");
                    return;
                }

                String nama = namaField.getText().trim();
                String posisi = posisiField.getText().trim();
                double gaji = Double.parseDouble(gajiField.getText().trim());
                if (gaji < 0) {
                    tampilkanPesan("Gaji tidak boleh negatif!");
                    return;
                }

                Date tanggalBergabung = Date.valueOf(dateField.getText().trim());
                String divisi = deptField.getText().trim();

                if (nama.isEmpty() || posisi.isEmpty() || divisi.isEmpty()) {
                    tampilkanPesan("Semua field harus diisi!");
                    return;
                }

                Karyawan newKaryawan = new Karyawan(id, nama, posisi, gaji, tanggalBergabung, divisi);
                daftarKaryawan.put(id, newKaryawan);
                perbaruiTabel();
                tampilkanPesan("Karyawan berhasil ditambahkan!");

            } catch (Exception ex) {
                tampilkanPesan("Input tidak valid! Periksa:\n- Gaji harus angka\n- Format tanggal yyyy-mm-dd");
            }
        }
    }

    private static void tampilkanDialogCari() {
        JTextField searchField = new JTextField();
        JComboBox<String> searchBy = new JComboBox<>(new String[]{"ID", "Nama", "Posisi", "Divisi"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Cari berdasarkan:"));
        panel.add(searchBy);
        panel.add(new JLabel("Kata kunci:"));
        panel.add(searchField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Cari Karyawan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String term = searchField.getText().trim().toLowerCase();
            String searchType = (String) searchBy.getSelectedItem();

            List<Karyawan> results = daftarKaryawan.values().stream()
                    .filter(emp -> {
                        switch (searchType) {
                            case "ID": return emp.getId().toLowerCase().contains(term);
                            case "Nama": return emp.getNama().toLowerCase().contains(term);
                            case "Posisi": return emp.getPosisi().toLowerCase().contains(term);
                            case "Divisi": return emp.getDivisi().toLowerCase().contains(term);
                            default: return false;
                        }
                    })
                    .collect(Collectors.toList());

            if (results.isEmpty()) {
                tampilkanPesan("Tidak ditemukan karyawan yang sesuai.");
            } else {
                tampilkanHasilPencarian(results);
            }
        }
    }

    private static void tampilkanHasilPencarian(List<Karyawan> results) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Karyawan emp : results) {
            model.addRow(new Object[]{
                    emp.getId(),
                    emp.getNama(),
                    emp.getPosisi(),
                    emp.getGaji(),
                    emp.getTanggalBergabung(),
                    emp.getDivisi(),
                    "Aksi"
            });
        }

        JOptionPane.showMessageDialog(frame,
                "Ditemukan " + results.size() + " karyawan yang sesuai.",
                "Hasil Pencarian", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void tampilkanDialogFilter() {
        JComboBox<String> filterBy = new JComboBox<>(new String[]{"Posisi", "Divisi"});
        JComboBox<String> filterValue = new JComboBox<>();
        perbaruiNilaiFilter(filterBy.getSelectedItem().toString(), filterValue);

        filterBy.addActionListener(e -> {
            perbaruiNilaiFilter(filterBy.getSelectedItem().toString(), filterValue);
        });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Filter berdasarkan:"));
        panel.add(filterBy);
        panel.add(new JLabel("Nilai filter:"));
        panel.add(filterValue);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Filter Karyawan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String filterType = filterBy.getSelectedItem().toString();
            String value = filterValue.getSelectedItem().toString();

            List<Karyawan> filtered = daftarKaryawan.values().stream()
                    .filter(emp -> {
                        switch (filterType) {
                            case "Posisi": return emp.getPosisi().equals(value);
                            case "Divisi": return emp.getDivisi().equals(value);
                            default: return false;
                        }
                    })
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                tampilkanPesan("Tidak ditemukan karyawan dengan filter yang dipilih.");
            } else {
                tampilkanHasilPencarian(filtered);
            }
        }
    }

    private static void perbaruiNilaiFilter(String filterType, JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        Set<String> values = new TreeSet<>();

        for (Karyawan emp : daftarKaryawan.values()) {
            switch (filterType) {
                case "Posisi": values.add(emp.getPosisi()); break;
                case "Divisi": values.add(emp.getDivisi()); break;
            }
        }

        values.forEach(comboBox::addItem);
    }

    private static void tampilkanDialogLaporan() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Total karyawan
        panel.add(new JLabel("Total Karyawan: " + daftarKaryawan.size()));

        // Total gaji
        double totalGaji = daftarKaryawan.values().stream().mapToDouble(Karyawan::getGaji).sum();
        panel.add(new JLabel("Total Gaji: " + formatMataUang(totalGaji)));

        // Rata-rata gaji
        double avgGaji = daftarKaryawan.isEmpty() ? 0 : totalGaji / daftarKaryawan.size();
        panel.add(new JLabel("Rata-rata Gaji: " + formatMataUang(avgGaji)));

        // Karyawan berdasarkan posisi
        Map<String, Long> byPosition = daftarKaryawan.values().stream()
                .collect(Collectors.groupingBy(Karyawan::getPosisi, Collectors.counting()));

        panel.add(new JLabel("Karyawan berdasarkan Posisi:"));
        byPosition.forEach((pos, count) ->
                panel.add(new JLabel("  " + pos + ": " + count)));

        // Karyawan berdasarkan divisi
        Map<String, Long> byDept = daftarKaryawan.values().stream()
                .collect(Collectors.groupingBy(Karyawan::getDivisi, Collectors.counting()));

        panel.add(new JLabel("Karyawan berdasarkan Divisi:"));
        byDept.forEach((dept, count) ->
                panel.add(new JLabel("  " + dept + ": " + count)));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(frame, scrollPane, "Laporan Karyawan", JOptionPane.PLAIN_MESSAGE);
    }

    private static String formatMataUang(double amount) {
        return new DecimalFormat("Rp #,##0.00").format(amount);
    }

    private static JPanel buatPanelInput(String[] labels, JTextField[] fields) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            panel.add(fields[i]);
        }
        return panel;
    }

    private static void perbaruiTabel() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Karyawan emp : daftarKaryawan.values()) {
            model.addRow(new Object[]{
                    emp.getId(),
                    emp.getNama(),
                    emp.getPosisi(),
                    emp.getGaji(),
                    emp.getTanggalBergabung(),
                    emp.getDivisi(),
                    "Aksi"
            });
        }
    }

    private static void tampilkanPesan(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    private static void simpanData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(daftarKaryawan);
            tampilkanPesan("Data berhasil disimpan!");
        } catch (IOException e) {
            tampilkanPesan("Error saat menyimpan data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void muatData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            daftarKaryawan = new HashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            daftarKaryawan = (Map<String, Karyawan>) ois.readObject();
            tampilkanPesan("Data berhasil dimuat!");
        } catch (IOException | ClassNotFoundException e) {
            tampilkanPesan("Error saat memuat data: " + e.getMessage());
            daftarKaryawan = new HashMap<>();
        }
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton editButton;
        private final JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = buatTombolAksi("Edit", new Color(70, 130, 180));
            deleteButton = buatTombolAksi("Hapus", new Color(220, 20, 60));
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private String currentId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = buatTombolAksi("Edit", new Color(70, 130, 180));
            deleteButton = buatTombolAksi("Hapus", new Color(220, 20, 60));

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> editKaryawan(currentId));
            deleteButton.addActionListener(e -> hapusKaryawan(currentId));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentId = table.getValueAt(row, 0).toString();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Aksi";
        }

        private void editKaryawan(String id) {
            Karyawan emp = daftarKaryawan.get(id);
            if (emp == null) {
                tampilkanPesan("Karyawan tidak ditemukan!");
                return;
            }

            JTextField namaField = new JTextField(emp.getNama());
            JTextField posisiField = new JTextField(emp.getPosisi());
            JTextField gajiField = new JTextField(String.valueOf(emp.getGaji()));
            JTextField dateField = new JTextField(emp.getTanggalBergabung().toString());
            JTextField deptField = new JTextField(emp.getDivisi());

            JPanel panel = buatPanelInput(
                    new String[]{"Nama:", "Posisi:", "Gaji:", "Tanggal Bergabung (yyyy-mm-dd):", "Divisi:"},
                    new JTextField[]{namaField, posisiField, gajiField, dateField, deptField}
            );

            int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Karyawan",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nama = namaField.getText().trim();
                    String posisi = posisiField.getText().trim();
                    double gaji = Double.parseDouble(gajiField.getText().trim());
                    if (gaji < 0) {
                        tampilkanPesan("Gaji tidak boleh negatif!");
                        return;
                    }

                    Date tanggalBergabung = Date.valueOf(dateField.getText().trim());
                    String divisi = deptField.getText().trim();

                    if (nama.isEmpty() || posisi.isEmpty() || divisi.isEmpty()) {
                        tampilkanPesan("Semua field harus diisi!");
                        return;
                    }

                    emp.setNama(nama);
                    emp.setPosisi(posisi);
                    emp.setGaji(gaji);
                    emp.setTanggalBergabung(tanggalBergabung);
                    emp.setDivisi(divisi);

                    perbaruiTabel();
                    tampilkanPesan("Data karyawan berhasil diperbarui!");

                } catch (Exception ex) {
                    tampilkanPesan("Input tidak valid! Periksa:\n- Gaji harus angka\n- Format tanggal yyyy-mm-dd");
                }
            }
        }

        private void hapusKaryawan(String id) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Apakah Anda yakin ingin menghapus karyawan ini?", "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                daftarKaryawan.remove(id);
                perbaruiTabel();
                tampilkanPesan("Karyawan berhasil dihapus!");
            }
        }
    }

    private static JButton buatTombolAksi(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(70, 25));
        return button;
    }
}