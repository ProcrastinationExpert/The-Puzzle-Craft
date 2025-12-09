# 🧩 The Puzzle Craft

Aplikasi Desktop Java berbasis Swing yang memungkinkan pengguna untuk membuat akun, mendesain level game (Level Editor), dan menyelesaikan puzzle logika dengan mekanisme interaksi unik. Data progres dan level tersimpan secara online menggunakan Firebase.

## 📋 Daftar Isi
- [Fitur Utama](#-fitur-utama)
- [Mekanisme Permainan](#-mekanisme-permainan)
- [Prasyarat Sistem](#-prasyarat-sistem)
- [Instalasi & Setup](#-instalasi--setup)
- [Cara Menjalankan (Run)](#-cara-menjalankan-run)
- [Struktur Project](#-struktur-project)

---

## 🚀 Fitur Utama
* **User Authentication:** Registrasi, Login, dan Lupa Password menggunakan Firebase Auth REST API.
* **Cloud Save:** Data profil pengguna (level terakhir, waktu bermain) tersimpan di Cloud Firestore.
* **Level Editor:** Fitur drag-and-drop untuk membuat dan mengedit level game puzzle Anda sendiri.
* **Online Level Database:** Level yang dibuat tersimpan di Firebase Realtime Database.
* **Unique Username:** Sistem validasi untuk memastikan username unik antar pengguna.

---

## 🎮 Mekanisme Permainan
Inti dari **The Puzzle Craft** adalah eksplorasi dan interaksi objek untuk membuka jalan:

1.  **Trigger Interaction (Interaksi Pemicu):**
    Pemain tidak perlu menggeser balok. Cukup dengan **berjalan melewati/menyentuh** balok pemicu tertentu, pemain dapat mengubah status objek lain di dalam peta.
    
2.  **Phasing Mechanics (Mekanisme Tembus):**
    Beberapa balok atau dinding yang awalnya padat (menghalangi jalan) akan berubah sifat menjadi **bisa ditembus (passable)** setelah pemain mengaktifkan pemicunya. Ini memungkinkan pemain menembus rintangan untuk mencapai area baru.

3.  **Open Goal (Tujuan Terbuka):**
    Pintu finish tidak dalam keadaan terkunci. Tantangan utama adalah menemukan rute yang benar dan memanipulasi rintangan agar bisa mencapai titik finish tersebut.

---

## 💻 Prasyarat Sistem
Sebelum menjalankan aplikasi ini, pastikan komputer Anda memiliki:

1.  **Java Development Kit (JDK):** Versi **11** atau yang lebih baru (Wajib).
2.  **IDE Java:** Disarankan menggunakan **NetBeans**, IntelliJ IDEA, atau Eclipse.
3.  **Koneksi Internet:** Diperlukan untuk terhubung ke server Firebase.

---

## 🛠 Instalasi & Setup

1.  **Clone atau Download Repository ini:**
    ```bash
    git clone https://github.com/ProcrastinationExpert/The-Puzzle-Craft.git
    ```

2.  **Buka Project di IDE:**
    * Buka NetBeans / IntelliJ.
    * Pilih `File` > `Open Project`.
    * Arahkan ke folder hasil download tadi.

3.  **Tambahkan Library (JAR):**
    * Pastikan file **`gson-2.10.1.jar`** sudah ada di folder `Libraries` project Anda.

---

## ▶️ Cara Menjalankan (Run)

Aplikasi ini harus dijalankan melalui IDE:

1.  Buka Project di IDE Anda.
2.  Buka paket: `src` > `main`
3.  Cari file **`Launcher.java`**.
4.  Klik kanan > **Run File** (Shift + F6).

> **⚠️ Catatan Penting:**
> Selalu jalankan dari `Launcher.java` untuk memastikan inisialisasi sesi dan koneksi database berjalan benar.

---

## 📂 Struktur Project

```text
src/
├── controller/       # Logika komunikasi ke Database (DatabaseController, AuthController)
├── gameLogic/        # Logika inti permainan (Trigger, Phasing Block, Collision) & Editor
├── main/             # Entry Point aplikasi & UI Utama (Launcher, Dialogs)
├── model/            # Representasi data (LevelData, UserData)
└── utils/            # Konstanta API dan Helper (ValidationUtils, SessionManager)
