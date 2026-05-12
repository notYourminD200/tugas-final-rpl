// ==========================================
// KELAS PENGUJIAN: OrderTest
// Menguji metode createOrder() pada StoreManager
// berdasarkan CFG (Control Flow Graph)
// ==========================================
public class OrderTest {

    // Metode untuk menyiapkan kondisi awal (Pre-condition) sebelum tiap tes
    // Karena menggunakan StoreManager, kita buat instance baru agar bersih (fresh state)
    public static StoreManager setUp() {
        StoreManager store = new StoreManager();
        
        // Masukkan data awal sesuai dokumen pengujian
        store.addProduct(new Product(101, "Laptop ASUS", 10000000, 10));
        
        return store;
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  MENJALANKAN PENGUJIAN UNIT (UNIT TESTING)       ");
        System.out.println("  Metode: StoreManager.createOrder()              ");
        System.out.println("==================================================");

        jalankanTC01();
        jalankanTC02();
        jalankanTC03();
        
        System.out.println("\n==================================================");
        System.out.println("  PENGUJIAN UNIT SELESAI                          ");
        System.out.println("==================================================");
    }

    // Menguji Jalur 1: Produk Tidak Ditemukan
    public static void jalankanTC01() {
        StoreManager store = setUp(); // Panggil data awal
        
        System.out.println("\n--- [TC-01] Menguji Jalur 1 ---");
        System.out.println("Input: userId=1, productId=999, qty=1");
        System.out.print("Sistem   : ");
        
        // Coba buat pesanan dengan ID produk yang tidak ada (999)
        Order hasil = store.createOrder(1, 999, 1);
        
        System.out.print("Hasil Cek: ");
        if (hasil == null) {
            System.out.println("PASS (Return null sesuai harapan karena produk tidak ada)");
        } else {
            System.out.println("FAIL (Seharusnya return null)");
        }
    }

    // Menguji Jalur 2: Produk Ada, tetapi Stok Tidak Cukup
    public static void jalankanTC02() {
        StoreManager store = setUp(); // Panggil data awal
        
        System.out.println("\n--- [TC-02] Menguji Jalur 2 ---");
        System.out.println("Input: userId=1, productId=101, qty=20");
        System.out.print("Sistem   : ");
        
        // Coba beli 20 qty, padahal stok cuma 10
        Order hasil = store.createOrder(1, 101, 20);
        
        System.out.print("Hasil Cek: ");
        if (hasil == null) {
            System.out.println("PASS (Return null sesuai harapan karena stok tidak cukup)");
        } else {
            System.out.println("FAIL (Seharusnya return null)");
        }
    }

    // Menguji Jalur 3: Produk Ada dan Stok Cukup (Sukses)
    public static void jalankanTC03() {
        StoreManager store = setUp(); // Panggil data awal
        
        System.out.println("\n--- [TC-03] Menguji Jalur 3 ---");
        System.out.println("Input: userId=1, productId=101, qty=2");
        System.out.print("Sistem   : "); 
        
        // Coba beli 2 qty, stok 10 (Harusnya berhasil)
        Order hasil = store.createOrder(1, 101, 2);
        
        System.out.print("\nHasil Cek: ");
        boolean isPass = true;
        
        // Verifikasi Return Object
        if (hasil == null) {
            System.out.println("FAIL (Pesanan gagal dibuat, return null)");
            isPass = false;
        } else {
            // Verifikasi perhitungan total harga (2 * 10.000.000 = 20.000.000)
            if (hasil.getTotalHarga() != 20000000) {
                System.out.println("FAIL (Total harga salah: " + hasil.getTotalHarga() + ")");
                isPass = false;
            }
            
            // Verifikasi stok produk berkurang (10 - 2 = 8)
            Product p = store.getProduct(101);
            if (p.getStok() != 8) {
                System.out.println("FAIL (Stok tidak berkurang dengan benar. Sisa: " + p.getStok() + ")");
                isPass = false;
            }
        }

        if (isPass) {
            System.out.println("PASS (Order berhasil dibuat, Harga=20.000.000, Sisa Stok=8)");
        }
    }
}