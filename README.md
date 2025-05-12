# UTS Pemrograman Berorientasi Obyek 2
<ul>
  <li>Mata Kuliah: Pemrograman Berorientasi Obyek 2</li>
  <li>Dosen Pengampu: <a href="https://github.com/Muhammad-Ikhwan-Fathulloh">Muhammad Ikhwan Fathulloh</a></li>
</ul>

## Profil
<ul>
  <li>Nama: Samara Buana Tungga</li>
  <li>NIM: 23552011126</li>
  <li>Studi Kasus: Sistem Todo List Fullstack (Spring Boot + Thymeleaf)</li>
</ul>

## Judul Studi Kasus
<p>Sistem Todo List Fullstack (Spring Boot + Thymeleaf)</p>

## Penjelasan Studi Kasus
<p>Universitas Teknologi Bandung (UTB) ingin membangun sebuah sistem manajemen tugas (Todo List)
berbasis web. Sistem ini akan digunakan oleh mahasiswa dan dosen untuk mencatat dan mengelola daftar
tugas pribadi mereka secara online.
Sistem akan dibangun menggunakan teknologi Java Spring Boot untuk backend dan Thymeleaf untuk
frontend, dengan pendekatan fullstack berbasis MVC (Model-View-Controller).</p>

## Penjelasan 4 Pilar OOP dalam Studi Kasus

### 1. Inheritance
<p>Spring menyediakan pewarisan otomatis melalui class dan interface seperti JpaRepository, memungkinkan penggunaan ulang metode standar CRUD tanpa harus ditulis ulang.</p>

### 2. Encapsulation
<p>
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
</p>

### 3. Polymorphism
<p>
Controller, service, dan konfigurasi keamanan mengimplementasikan dependensi melalui interface (seperti UserDetailsService) agar fleksibel dan mudah diuji atau diganti implementasinya.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    // Implementasi polymorphic dari loadUserByUsername()
}
```
</p>

### 4. Abstract
<p>
Logika kompleks seperti penyimpanan data dan otentikasi disembunyikan di balik layer service dan repository, memisahkan logika bisnis dari logika presentasi (MVC).

```java
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(User user);
}
```
</p>

## Demo Proyek
<ul>
  <li>Github: <a href="https://github.com/SamaraBuanaTungga/ToDo_List_Springboot">Github</a></li>
  <li>Youtube: <a href="">Youtube</a></li>
</ul>
