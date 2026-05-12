import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner; // Import Scanner untuk input pengguna
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

// ==========================================
// CLASS: User
// ==========================================
class User {
    private int id;
    private String username;
    private String password;
    private boolean isLoggedIn;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }

    public int getId() {
        return id;
    }

    public boolean login(String inputUsername, String inputPassword) {
        if (this.username.equals(inputUsername) && this.password.equals(inputPassword)) {
            this.isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        this.isLoggedIn = false;
    }
}

// ==========================================
// CLASS: Product
// ==========================================
class Product {
    // Menggunakan static Map sebagai simulasi tabel database 'PRODUCTS'
    public static Map<Integer, Product> dbProducts = new HashMap<>();

    private int id;
    private String nama;
    private double harga;
    private int stok;

    public Product(int id, String nama, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    // Getters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }

    // Fitur 1: Tambah Produk
    public static boolean tambahProduk(Product product) {
        if (!dbProducts.containsKey(product.getId())) {
            dbProducts.put(product.getId(), product);
            return true;
        }
        return false;
    }

    // Fitur 2: Ubah Produk
    public static boolean ubahProduk(int idProduct, String namaBaru, double hargaBaru, int stokBaru) {
        if (dbProducts.containsKey(idProduct)) {
            Product prod = dbProducts.get(idProduct);
            if (namaBaru != null && !namaBaru.isEmpty()) prod.nama = namaBaru;
            if (hargaBaru >= 0) prod.harga = hargaBaru;
            if (stokBaru >= 0) prod.stok = stokBaru;
            return true;
        }
        return false;
    }

    // Fitur 3: Hapus Produk
    public static boolean hapusProduk(int idProduct) {
        if (dbProducts.containsKey(idProduct)) {
            dbProducts.remove(idProduct);
            return true;
        }
        return false;
    }

    // Metode pendukung untuk pesanan
    public boolean kurangiStok(int qty) {
        if (this.stok >= qty) {
            this.stok -= qty;
            return true;
        }
        return false;
    }
}

// ==========================================
// CLASS: Order
// ==========================================
class Order {
    // Menggunakan static List sebagai simulasi tabel database 'ORDERS'
    public static List<Order> dbOrders = new ArrayList<>();

    private int id;
    private int userId;
    private int productId;
    private int qty;
    private double totalHarga;
    private String tanggalPesanan;

    public Order(int id, int userId, int productId, int qty, double totalHarga) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.qty = qty;
        this.totalHarga = totalHarga;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        this.tanggalPesanan = dtf.format(LocalDateTime.now());
    }

    // Fitur 4: Buat Pesanan
    public static Order buatPesanan(int userId, int productId, int qty) {
        // Cek apakah produk ada di database
        if (Product.dbProducts.containsKey(productId)) {
            Product prod = Product.dbProducts.get(productId);
            
            // Coba kurangi stok
            if (prod.kurangiStok(qty)) {
                // Jika stok cukup, hitung harga dan buat pesanan
                double total = prod.getHarga() * qty;
                int newId = dbOrders.size() + 1;
                Order newOrder = new Order(newId, userId, productId, qty, total);
                
                dbOrders.add(newOrder);
                return newOrder;
            } else {
                System.out.println("[GAGAL] Stok tidak mencukupi untuk " + prod.getNama());
            }
        } else {
            System.out.println("[GAGAL] Produk dengan ID " + productId + " tidak ditemukan.");
        }
        return null;
    }

    // Fitur 5 (Bagian 1): Lihat Semua Pesanan
    public static List<Order> lihatSemuaPesanan() {
        return dbOrders;
    }

    // Fitur 5 (Bagian 2): Hitung Total Pendapatan
    public static double hitungTotalPendapatan() {
        double total = 0;
        for (Order order : dbOrders) {
            total += order.totalHarga;
        }
        return total;
    }

    // Getters untuk keperluan pelaporan
    public int getProductId() { return productId; }
    public int getQty() { return qty; }
    public double getTotalHarga() { return totalHarga; }
    public String getTanggalPesanan() { return tanggalPesanan; }
}

// ==========================================
// MAIN CLASS (CONTROLLER / SIMULATOR)
// ==========================================
public class Main {
    // Helper method untuk format uang ke Rupiah
    public static String formatRupiah(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("###,###", symbols);
        return "Rp. " + df.format(nominal);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=============================================");
        System.out.println("  SIMULASI TOKO ONLINE INTERAKTIF (JAVA)     ");
        System.out.println("=============================================\n");

        // 1. Inisialisasi User & Data Awal (Opsional, agar tidak kosong)
        User admin = new User(1, "admin", "1234");
        Product.tambahProduk(new Product(101, "Laptop ASUS", 10000000, 10));
        Product.tambahProduk(new Product(102, "Mouse Wireless", 150000, 25));

        // [WAJIB] Modul Autentikasi: Login loop
        boolean isLogin = false;
        while (!isLogin) {
            System.out.println("--- MENU LOGIN ---");
            System.out.print("Username : ");
            String uname = scanner.nextLine();
            System.out.print("Password : ");
            String pass = scanner.nextLine();

            if (admin.login(uname, pass)) {
                isLogin = true;
                System.out.println("\n[BERHASIL] Selamat datang, Admin!\n");
            } else {
                System.out.println("[GAGAL] Username atau Password salah. Silakan coba lagi.\n");
            }
        }

        // Loop Menu Utama
        boolean running = true;
        while (running) {
            System.out.println("=================================");
            System.out.println("          MENU UTAMA             ");
            System.out.println("=================================");
            System.out.println("1. Tambah Produk");
            System.out.println("2. Ubah Produk");
            System.out.println("3. Hapus Produk");
            System.out.println("4. Buat Pesanan");
            System.out.println("5. Lihat Laporan Penjualan");
            System.out.println("6. Lihat Daftar Produk Saat Ini");
            System.out.println("7. Keluar");
            System.out.print("Pilih aksi (1-7): ");
            
            int pilihan = -1;
            try {
                pilihan = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Masukan tidak valid. Harap masukkan angka.");
                continue;
            }

            System.out.println(); // spasi
            switch (pilihan) {
                case 1: // Tambah Produk
                    System.out.println("--- 1. TAMBAH PRODUK ---");
                    System.out.print("Masukkan ID Produk  : ");
                    int idT = Integer.parseInt(scanner.nextLine());
                    System.out.print("Masukkan Nama       : ");
                    String namaT = scanner.nextLine();
                    System.out.print("Masukkan Harga      : ");
                    double hargaT = Double.parseDouble(scanner.nextLine());
                    System.out.print("Masukkan Stok       : ");
                    int stokT = Integer.parseInt(scanner.nextLine());
                    
                    if (Product.tambahProduk(new Product(idT, namaT, hargaT, stokT))) {
                        System.out.println("[SUKSES] Produk berhasil ditambahkan!");
                    } else {
                        System.out.println("[GAGAL] Produk dengan ID tersebut sudah ada.");
                    }
                    break;

                case 2: // Ubah Produk
                    System.out.println("--- 2. UBAH PRODUK ---");
                    int idU = -1;
                    while (true) {
                        System.out.print("Masukkan ID Produk yang diubah (ketik 0 untuk batal): ");
                        try {
                            idU = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("[GAGAL] Masukan harus berupa angka.");
                            continue;
                        }

                        if (idU == 0) break;

                        if (Product.dbProducts.containsKey(idU)) {
                            Product pSaatIni = Product.dbProducts.get(idU);
                            System.out.println("INFO: Sedang mengubah produk [" + pSaatIni.getNama() + "]");
                            break; 
                        } else {
                            System.out.println("[GAGAL] Produk dengan ID " + idU + " tidak ditemukan.");
                            System.out.println("INFO: Silakan periksa daftar produk di menu nomor 6 untuk melihat ID yang tersedia.");
                        }
                    }

                    if (idU == 0) {
                        System.out.println("Perubahan produk dibatalkan.");
                        break;
                    }

                    System.out.print("Masukkan Nama Baru             : ");
                    String namaU = scanner.nextLine();
                    System.out.print("Masukkan Harga Baru            : ");
                    double hargaU = Double.parseDouble(scanner.nextLine());
                    System.out.print("Masukkan Stok Baru             : ");
                    int stokU = Integer.parseInt(scanner.nextLine());

                    if (Product.ubahProduk(idU, namaU, hargaU, stokU)) {
                        System.out.println("[SUKSES] Produk berhasil diubah!");
                    } else {
                        System.out.println("[GAGAL] Terjadi kesalahan saat mengubah produk.");
                    }
                    break;

                case 3: // Hapus Produk
                    System.out.println("--- 3. HAPUS PRODUK ---");
                    System.out.print("Masukkan ID Produk yang dihapus: ");
                    int idH = Integer.parseInt(scanner.nextLine());
                    
                    if (Product.hapusProduk(idH)) {
                        System.out.println("[SUKSES] Produk berhasil dihapus!");
                    } else {
                        System.out.println("[GAGAL] Produk tidak ditemukan.");
                    }
                    break;

                case 4: // Buat Pesanan
                    System.out.println("--- 4. BUAT PESANAN ---");
                    System.out.print("Masukkan ID Produk yang dibeli: ");
                    int idP = Integer.parseInt(scanner.nextLine());
                    System.out.print("Masukkan Jumlah (Qty)         : ");
                    int qtyP = Integer.parseInt(scanner.nextLine());

                    Order pesananBaru = Order.buatPesanan(admin.getId(), idP, qtyP);
                    if (pesananBaru != null) {
                        System.out.println("[SUKSES] Pesanan berhasil dibuat! Stok otomatis berkurang.");
                    }
                    break;

                case 5: // Lihat Laporan Penjualan
                    System.out.println("--- 5. LAPORAN PENJUALAN ---");
                    List<Order> daftarPesanan = Order.lihatSemuaPesanan();
                    if (daftarPesanan.isEmpty()) {
                        System.out.println("Belum ada transaksi.");
                    } else {
                        int index = 1;
                        for (Order p : daftarPesanan) {
                            String namaProd = Product.dbProducts.containsKey(p.getProductId()) ? 
                                              Product.dbProducts.get(p.getProductId()).getNama() : "Produk Dihapus";
                            System.out.println("Trx-" + index + " | Item: " + namaProd + " | Qty: " + p.getQty() + " | Total: " + formatRupiah(p.getTotalHarga()) + " | Waktu: " + p.getTanggalPesanan());
                            index++;
                        }
                        double totalPendapatan = Order.hitungTotalPendapatan();
                        System.out.println("\n>> TOTAL PENDAPATAN TOKO: " + formatRupiah(totalPendapatan));
                    }
                    break;

                case 6: // Lihat Data Produk (Tambahan agar mudah dicek)
                    System.out.println("--- 6. DAFTAR PRODUK SAAT INI ---");
                    if (Product.dbProducts.isEmpty()) {
                        System.out.println("Belum ada produk.");
                    } else {
                        for (Product p : Product.dbProducts.values()) {
                            System.out.println("ID: " + p.getId() + " | Nama: " + p.getNama() + " | Harga: " + formatRupiah(p.getHarga()) + " | Stok: " + p.getStok());
                        }
                    }
                    break;

                case 7: // Keluar
                    System.out.println("Keluar dari program. Terima kasih!");
                    running = false;
                    break;

                default:
                    System.out.println("Pilihan tidak valid. Silakan pilih 1-7.");
                    break;
            }
            System.out.println();
        }
        
        scanner.close();
    }
}