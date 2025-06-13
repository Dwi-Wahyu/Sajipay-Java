# Sajipay - Aplikasi Point of Sale (POS)

Sajipay adalah aplikasi Point of Sale (POS) berbasis desktop yang dibangun menggunakan JavaFX. Aplikasi ini dirancang untuk mengelola penjualan, produk, dan pengguna (pelanggan dan karyawan) untuk bisnis skala kecil seperti kafe atau restoran. Aplikasi ini memiliki dasbor yang berbeda untuk setiap peran pengguna (Manajer, Koki, dan Pelanggan), masing-masing dengan fungsionalitas spesifik.

---

## 📋 Fitur Utama

Aplikasi Sajipay menyediakan berbagai fitur yang terbagi berdasarkan hak akses setiap pengguna:

- **Sistem Otentikasi Pengguna**

  - Terdapat sistem login yang aman untuk Pelanggan dan Karyawan (Manajer, Koki).
  - Pengguna dapat mendaftar sebagai pelanggan baru.

- **Kontrol Akses Berbasis Peran (Role-Based Access Control)**

  - **Dasbor Manajer**:
    - Melihat daftar karyawan beserta detailnya seperti pengalaman dan gaji.
    - Managemen karyawan (Manajer/Koki), menu CRUD karyawan.
  - **Dasbor Koki**:
    - Menambahkan produk baru (makanan atau minuman) dengan atribut yang spesifik.
    - Melihat daftar semua produk beserta stok, harga, dan biaya.
    - Menghapus produk yang sudah ada dari daftar.
  - **Dasbor Pelanggan**:
    - Melihat daftar produk yang tersedia untuk dibeli.
    - Menambahkan produk ke dalam keranjang belanja.
    - Melakukan proses _checkout_ dan pembayaran. Saldo pelanggan akan otomatis terpotong setelah pembayaran berhasil.
    - Melihat riwayat transaksi atau pesanan yang pernah dilakukan.
    - Melakukan _top-up_ saldo dengan verifikasi PIN.

- **Manajemen Produk**

  - Mendukung dua tipe produk: **Makanan (Food)** dan **Minuman (Beverage)**.
  - Atribut produk makanan mencakup kalori dan status vegetarian.
  - Atribut produk minuman mencakup ukuran, level gula, dan status dingin
  - Sistem dapat melacak detail produk seperti nama, deskripsi, harga jual, biaya produksi, dan stok.

- **Proses Pesanan dan Transaksi**

  - Fungsionalitas keranjang belanja yang memungkinkan pelanggan menambah atau menghapus item sebelum _checkout_.
  - Popup ringkasan pesanan ditampilkan sebelum konfirmasi pembayaran untuk verifikasi akhir.
  - Sistem secara otomatis menghitung total harga termasuk pajak. Tarif pajak ditentukan oleh metode pembayaran yang dipilih pelanggan.
  - Setiap transaksi yang berhasil akan mengurangi stok produk yang terjual dan dicatat dalam riwayat pesanan.

- **Persistensi Data**
  - Menggunakan database **SQLite** untuk menyimpan data pengguna, produk, dan riwayat pesanan secara persisten.
  - Jika database belum ada saat aplikasi pertama kali dijalankan, sistem akan secara otomatis membuat tabel-tabel yang diperlukan dan mengimpor data awal dari file `.csv` yang tersedia di `src/main/resources/data/`.

---

## 🛠️ Teknologi yang Digunakan

- **Bahasa Pemrograman**: Java
- **Framework GUI**: JavaFX
- **Database**: SQLite
- **Struktur Proyek**: Gradle

## 📁 Struktur Folder

```
Sajipay/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── sajipay/
│           │       ├── components/
│           │       │   └── AvatarComponent.java
│           │       ├── controllers/
│           │       │   ├── AddEmployeeController.java
│           │       │   ├── AddProductController.java
│           │       │   ├── ChefDashboardController.java
│           │       │   ├── ListProductController.java
│           │       │   ├── LoginController.java
│           │       │   ├── ManagerDashboardController.java
│           │       │   ├── OrderHistoryController.java
│           │       │   ├── OrderSummaryController.java
│           │       │   ├── ProfileController.java
│           │       │   ├── RegisterController.java
│           │       │   └── UpdateEmployeeController.java
│           │       ├── enums/
│           │       │   ├── BeverageSize.java
│           │       │   ├── Payment.java
│           │       │   └── Role.java
│           │       ├── exceptions/
│           │       │   ├── CustomerNotFoundException.java
│           │       │   ├── ProductNotFoundException.java
│           │       │   └── UpdateFailedException.java
│           │       ├── helper/
│           │       │   ├── AlertHelper.java
│           │       │   ├── EmployeesCheck.java
│           │       │   ├── FormHelper.java
│           │       │   ├── ProductIdGenerator.java
│           │       │   └── StyleHelper.java
│           │       ├── interfaces/
│           │       │   └── IProduct.java
│           │       ├── models/
│           │       │   ├── Beverage.java
│           │       │   ├── Customer.java
│           │       │   ├── Employee.java
│           │       │   ├── Food.java
│           │       │   ├── Management.java
│           │       │   ├── Order.java
│           │       │   ├── OrderItem.java
│           │       │   ├── Product.java
│           │       │   └── User.java
│           │       ├── services/
│           │       │   ├── AuthService.java
│           │       │   ├── DatabaseService.java
│           │       │   ├── NavigationManager.java
│           │       │   └── ProductService.java
│           │       ├── utils/
│           │       │   └── DatabaseConnection.java
│           │       ├── views/
│           │       │   └── OrderSummaryPopup.java
│           │       └── App.java
│           └── resources/
│               ├── css/
│               │   └── avatar.css
│               └── data/
│                   ├── customers.csv
│                   ├── employees.csv
│                   ├── order_history.csv
│                   └── products.csv
├── gradle/
│   ├── wrapper/
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml
└── README.md
```

## 🚀 Panduan Menjalankan Aplikasi

1.  **Prasyarat**:

    - Java Development Kit (JDK) versi 11 atau yang lebih baru.
    - JavaFX SDK.
    - IDE seperti IntelliJ IDEA atau Eclipse.

2.  **Konfigurasi**:

    - Pastikan JavaFX SDK telah terkonfigurasi dengan benar di IDE Anda.
    - Atur VM options untuk menyertakan modul JavaFX. Contoh:
      ```
      --module-path /path/to/your/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml
      ```

3.  **Jalankan Aplikasi**:

    - Titik masuk utama (main entry point) aplikasi adalah kelas `sajipay.App`.
    - Jalankan metode `main` di dalam file `App.java` untuk memulai aplikasi.
    - Aplikasi akan secara otomatis membuat file database SQLite (`database.db`) di direktori `src/main/resources/` dan mengisinya dengan data awal dari file CSV jika ini adalah pertama kalinya aplikasi dijalankan.

4.  **Informasi Login Default**:
    - **Pelanggan**:
      - Username: `guest`
      - Password: `guest123`
    - **Manajer**:
      - Username: `manager`
      - Password: `manager123`
    - **Koki**:
      - Username: `chef`
      - Password: `chef123`

---
