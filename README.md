# 📝 To-Do List Web App

Aplikasi web **To-Do List** ini dikembangkan menggunakan **Spring Boot** dan **Thymeleaf**, serta menerapkan prinsip-prinsip **Object-Oriented Programming (OOP)** untuk menjaga struktur kode yang bersih, modular, dan mudah dikembangkan.

---

## 🎯 Fitur Utama

- ✅ **Registrasi dan Login** dengan autentikasi berbasis **Spring Security**.
- 📋 **Tambah**, **edit**, **tandai selesai/belum**, dan **hapus** tugas.
- 🔍 **Filter tugas** berdasarkan status: *semua*, *selesai*, *belum selesai*.
- 👤 **Personalisasi tugas** berdasarkan user login.

---

## 🧱 Arsitektur dan Prinsip OOP

### 1. **Encapsulation**
Setiap entitas seperti `User` dan `Todo` dikapsulasi sebagai class Java dengan atribut dan metode tersendiri. Akses ke field dilakukan melalui getter dan setter untuk menjaga integritas data.

```java
public class Todo {
    private Long id;
    private String task;
    private boolean completed;
    private User user;
    // Getter dan Setter
}
```

### 2. **Abstraction**
Logika kompleks seperti penyimpanan data dan otentikasi disembunyikan di balik layer service dan repository, memisahkan logika bisnis dari logika presentasi (MVC).

```java
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(User user);
}
```

### 3. **Inheritance**
Spring menyediakan pewarisan otomatis melalui class dan interface seperti JpaRepository, memungkinkan penggunaan ulang metode standar CRUD tanpa harus ditulis ulang.

### 4. **Polymorphism**
Controller, service, dan konfigurasi keamanan mengimplementasikan dependensi melalui interface (seperti UserDetailsService) agar fleksibel dan mudah diuji atau diganti implementasinya.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implementasi polymorphic dari loadUserByUsername()
}
```

## 🛠️ Teknologi
```
Java 17+
Spring Boot
Spring Security
Spring Data JPA
Thymeleaf
Database MySQL
Bootstrap 5
```

## 🚀 Cara Menjalankan
Clone repositori:
```
git clone https://github.com/SamaraBuanaTungga/ToDo_List_Springboot.git
```
Jalankan aplikasi:
```
./mvnw spring-boot:run
```
Akses aplikasi di `http://localhost:8080`

## 🔐 Akun & Keamanan
Otentikasi dan otorisasi pengguna diterapkan menggunakan Spring Security.

Password disimpan dengan hashing menggunakan BCrypt.

## 📁 Struktur Folder Penting
```src
├── controller       → Web controller (Login, Register, Todo)
├── model            → Kelas-kelas entitas (User, Todo)
├── repository       → Interface JPA untuk database
├── service          → Logika bisnis dan pemrosesan
├── config           → Konfigurasi keamanan
└── templates        → Halaman HTML dengan Thymeleaf
```
