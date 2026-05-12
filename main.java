import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

// ==========================================
// MODELS (Data Layer)
// ==========================================

class User {
    private int id;
    private String username;
    private String password;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public boolean authenticate(String user, String pass) {
        return this.username.equals(user) && this.password.equals(pass);
    }
}

class Product {
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

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public void update(String nama, double harga, int stok) {
        if (nama != null && !nama.isEmpty())
            this.nama = nama;
        if (harga >= 0)
            this.harga = harga;
        if (stok >= 0)
            this.stok = stok;
    }

    public boolean reduceStock(int qty) {
        if (this.stok >= qty) {
            this.stok -= qty;
            return true;
        }
        return false;
    }
}

class Order {
    private int id;
    private String productName;
    private int qty;
    private double totalHarga;
    private String timestamp;

    public Order(int id, String productName, int qty, double total) {
        this.id = id;
        this.productName = productName;
        this.qty = qty;
        this.totalHarga = total;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    @Override
    public String toString() {
        return String.format("Item: %-18s | Qty: %-3d | Total: %-15s | Waktu: %s",
                productName, qty, Main.formatRupiah(totalHarga), timestamp);
    }
}

// ==========================================
// SERVICE (Business Logic Layer)
// ==========================================

class StoreManager {
    private Map<Integer, Product> products = new HashMap<>();
    private List<Order> orders = new ArrayList<>();

    public void seedInitialData() {
        addProduct(new Product(101, "Laptop ASUS", 10000000, 10));
        addProduct(new Product(102, "Mouse Wireless", 150000, 25));
    }

    public boolean addProduct(Product p) {
        if (products.containsKey(p.getId()))
            return false;
        products.put(p.getId(), p);
        return true;
    }

    public Product getProduct(int id) {
        return products.get(id);
    }

    public boolean deleteProduct(int id) {
        return products.remove(id) != null;
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Order createOrder(int userId, int productId, int qty) {
        Product p = products.get(productId);
        if (p != null && p.reduceStock(qty)) {
            Order newOrder = new Order(orders.size() + 1, p.getNama(), qty, p.getHarga() * qty);
            orders.add(newOrder);
            return newOrder;
        }
        return null;
    }

    public double calculateTotalRevenue() {
        return orders.stream().mapToDouble(Order::getTotalHarga).sum();
    }
}

// ==========================================
// VIEW / UI (Presentation Layer)
// ==========================================

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StoreManager store = new StoreManager();

    public static void main(String[] args) {
        store.seedInitialData();
        User admin = new User(1, "admin", "1234");

        System.out.println("=============================================");
        System.out.println("      SISTEM MANAJEMEN TOKO ONLINE           ");
        System.out.println("=============================================");

        loginFlow(admin);
        mainMenuFlow(admin);

        scanner.close();
    }

    private static void loginFlow(User admin) {
        while (true) {
            System.out.println("\n--- SILAKAN LOGIN ---");
            System.out.print("Username : ");
            String u = scanner.nextLine();
            System.out.print("Password : ");
            String p = scanner.nextLine();

            if (admin.authenticate(u, p)) {
                System.out.println("[SUKSES] Selamat datang, Admin!");
                break;
            }
            System.out.println("[GAGAL] Login salah, coba lagi.");
        }
    }

    private static void mainMenuFlow(User admin) {
        boolean running = true;
        while (running) {
            printMenuHeader();
            int choice = readInt("Pilih aksi (1-7): ");
            System.out.println();

            switch (choice) {
                case 1 -> addProductAction();
                case 2 -> updateProductAction();
                case 3 -> deleteProductAction();
                case 4 -> placeOrderAction(admin);
                case 5 -> showSalesReport();
                case 6 -> showInventory();
                case 7 -> {
                    System.out.println("Keluar... Terima kasih!");
                    running = false;
                }
                default -> System.out.println("[!] Pilihan tidak tersedia.");
            }
        }
    }

    private static void printMenuHeader() {
        System.out.println("\n" + "=".repeat(33));
        System.out.println("           MENU UTAMA            ");
        System.out.println("=".repeat(33));
        System.out.println("1. Tambah | 2. Ubah    | 3. Hapus");
        System.out.println("4. Pesan  | 5. Laporan | 6. Stok");
        System.out.println("7. Keluar");
    }

    // --- ACTIONS ---

    private static void addProductAction() {
        int id = readInt("Masukkan ID Produk: ");
        System.out.print("Nama Produk       : ");
        String nama = scanner.nextLine();
        double harga = readDouble("Harga Produk      : ");
        int stok = readInt("Jumlah Stok       : ");

        if (store.addProduct(new Product(id, nama, harga, stok)))
            System.out.println("[SUKSES] Produk ditambahkan.");
        else
            System.out.println("[GAGAL] ID sudah ada.");
    }

    private static void updateProductAction() {
        while (true) {
            int id = readInt("Masukkan ID Produk yang diubah (0 untuk batal): ");
            if (id == 0)
                break;

            Product p = store.getProduct(id);
            if (p != null) {
                System.out.println("Mengubah: " + p.getNama());
                System.out.print("Nama Baru  : ");
                String nama = scanner.nextLine();
                double harga = readDouble("Harga Baru : ");
                int stok = readInt("Stok Baru  : ");
                p.update(nama, harga, stok);
                System.out.println("[SUKSES] Produk diperbarui.");
                break;
            }
            System.out.println("[GAGAL] ID tidak ditemukan.");
        }
    }

    private static void deleteProductAction() {
        int id = readInt("ID Produk yang dihapus: ");
        if (store.deleteProduct(id))
            System.out.println("[SUKSES] Produk dihapus.");
        else
            System.out.println("[GAGAL] ID tidak ditemukan.");
    }

    private static void placeOrderAction(User admin) {
        int pid = readInt("ID Produk yang dibeli: ");
        int qty = readInt("Jumlah (Qty)         : ");

        if (store.createOrder(admin.getId(), pid, qty) != null)
            System.out.println("[SUKSES] Pesanan dibuat.");
        else
            System.out.println("[GAGAL] Stok tidak cukup atau ID salah.");
    }

    private static void showSalesReport() {
        List<Order> orders = store.getOrders();
        if (orders.isEmpty()) {
            System.out.println("Belum ada transaksi.");
        } else {
            orders.forEach(System.out::println);
            System.out.println("\n>> TOTAL PENDAPATAN: " + formatRupiah(store.calculateTotalRevenue()));
        }
    }

    private static void showInventory() {
        Collection<Product> prods = store.getAllProducts();
        if (prods.isEmpty()) {
            System.out.println("Gudang kosong.");
        } else {
            prods.forEach(p -> System.out.printf("ID: %-4d | %-15s | %-12s | Stok: %d\n",
                    p.getId(), p.getNama(), formatRupiah(p.getHarga()), p.getStok()));
        }
    }

    // --- UTILS ---

    public static String formatRupiah(double nominal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        return "Rp. " + new DecimalFormat("###,###", symbols).format(nominal);
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("[!] Harus angka.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("[!] Harus angka.");
            }
        }
    }
}
