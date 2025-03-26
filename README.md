Username buat login :

Email :dany@val.id 
Password : 123456

Email : aji@lacak.io 
Password : 123456

Email : nurafifah@lacak.io 
Password : 123456

1. Setup Instructions :
   A. Menjalankan Aplikasinya : Ketika aplikasi dijalankan akan muncul halaman login dimana muncul pop up untuk memberikan izin terlebih dahulu seperti camera dan lokasi agar bisa di gunakan. Setelah itu login dengan akun yang sudah saya berikan, dan ketika berhasil login ada muncul toast login sukses lalu menuju ke halaman home. di halaman home ini langsung membuka maps dimana lokasi kita berada serta bisa berjalan. fitur di halaman home ini ada 3 yaitu menu home, menu dashboard dan menu keluar. ketika kita pilih menu dashboard berisi speed, engine dan door dimana sudah di setting di dalam aplikasinya. untuk fitur keluar ini akan muncul popup konfirmasi keluar ? Apakah anda yakin ingin keluar ? tombolnya Tidak atau Ya, dimana ketika keluar dia langsung keluar akun yang telah login tadi missal dengan email ignatiusadi62@gmail.com langsung menuju ke halaman loginscreen. Ketika sudah login dengan akun email tersebut lalu aplikasi tidak sengaja di kill lalu buka aplikasi lagi dia tidak ke halaman loginscreen tetapi langsung menuju ke halaman home karena saya sudah setting didalam aplikasinya untuk menyimpan email dan password kedalam SharedPreferences. 

2. Project Structure Overview : project saya menggunakan MVVM dimana foldernya berisi :
	- Helper -> SessionManager = untuk menyimpan data email dan password kedalam SharedPreferences.
	- Model  -> UserModel = Untuk menyimpan informasi pengguna, seperti username, email, password, alamat, telepon, dan photo.
		 -> Vehicle = Untuk menyimpan Data Kendaraan, Memudahkan Pengolahan Data GPS & Sensor dan digunakan untuk merepresentasikan status atau informasi terkini dari suatu kendaraan.
	- Repository -> FleetRepository = Menyediakan Data Simulasi Kendaraan, Menyimpan daftar koordinat GPS kendaraan (gpsCoordinates) sebagai data statis, digunakan untuk mensimulasikan pergerakan kendaraan dalam sistem fleet management, Mengembalikan Data Kendaraan Secara Bergantian dan Menambahkan Elemen Acak pada Kecepatan (speed).
	- Service -> FleetService = Simulasi Pembaruan Data Kendaraan
	- UI -> LoginActivity = Untuk login user aplikasi yang sudah didaftarkan. disini saya menggunakan firebase
	     -> MainActivity = Menampilkan peta Google Maps dengan lokasi kendaraan, Mengambil data pengguna dari Firebase, Mengamati data kendaraan secara real-time menggunakan ViewModel, Memiliki navigasi bawah untuk berpindah halaman, Menyediakan fitur logout dengan konfirmasi, Mencegah tombol "Back" agar pengguna tidak keluar tanpa logout
	     -> DashboardActivity = Menampilkan data kendaraan secara real-time (kecepatan, status mesin, status pintu), Memantau peringatan atau notifikasi dari ViewModel, Navigasi ke halaman lain dengan Bottom Navigation, Menyediakan fitur logout dengan konfirmasi, Mencegah tombol "Back" agar pengguna tidak keluar tanpa logout.
	- dependency = Retrofit, gms play services maps, dagger, room, firebase, firebase-auth


3. Class Explanation
   Fungsi dari masing - masing class sebagai berikut :

A. LoginActivity.kt
   1. Gunakan ViewModel untuk Manajemen UI Logic :
      Pisahkan logika autentikasi dari Activity dengan menggunakan ViewModel, sehingga lebih mudah diuji dan dikelola.

   2. Gunakan MutableLiveData untuk Status Loading dan Error :
      Daripada menggunakan ProgressDialog, gunakan LiveData atau StateFlow agar UI bisa memperbarui status secara otomatis.

   3. Gunakan Coroutine untuk Firebase Authentication :
      Alih-alih menggunakan OnCompleteListener, lebih baik gunakan coroutine agar lebih bersih dan lebih readable.

   4. Pisahkan Permission Handling ke Kelas Terpisah :
      Buatlah utilitas untuk menangani izin agar tidak mengotori LoginActivity.

B. MainActivity.kt
   1. Pengguna Sudah Login Sebelum Mengakses MainActivity
	- Periksa apakah pengguna telah login saat MainActivity dibuka.
	- Jika belum, arahkan ke LoginActivity dan tutup MainActivity.

   2. Inisialisasi Firebase :
	- Gunakan FirebaseAuth untuk mendapatkan pengguna yang sedang login. 
	- Gunakan FirebaseDatabase untuk mengambil data pengguna berdasarkan UID.

   3. Inisialisasi Google Maps :
	- Ambil referensi dari SupportMapFragment menggunakan findFragmentById.
	- Gunakan getMapAsync(this) untuk mempersiapkan Google Maps.

   4. Ambil Data Pengguna dari Firebase
	- Baca data pengguna dari Firebase Realtime Database berdasarkan firebaseUser.uid.
	- Jika data tersedia, log informasi pengguna menggunakan Log.d().

   5. Tampilkan Lokasi Kendaraan di Peta
	- Pantau perubahan data kendaraan dari viewModel.vehicleData.observe().
	- Jika marker belum ada, tambahkan marker baru.
	- Jika marker sudah ada, perbarui posisinya.
	- Geser kamera ke lokasi kendaraan dengan moveCamera().

   6. Tangani Navigasi Bottom Navigation
	- Navigasi ke halaman Dashboard atau Settings sesuai item yang dipilih.
	- Jika memilih Home, tetap berada di halaman ini.
	- Jika memilih Settings, tampilkan dialog konfirmasi logout.

   7. Tampilkan Dialog Logout
	- Gunakan AlertDialog.Builder untuk meminta konfirmasi sebelum logout.
	- Jika pengguna menekan "Ya", lakukan FirebaseAuth.getInstance().signOut(), lalu kembali ke LoginActivity.
	
   8. Tangani Tombol Back
	- Cegah tombol Back dengan dispatchKeyEvent() agar pengguna tidak bisa keluar dengan tombol back.


C. DashboardActivity.kt
   1. Inisialisasi View Binding
	- Gunakan ActivityDashboardBinding untuk mengakses elemen UI di XML.
	- Setel setContentView(binding.root) setelah inisialisasi.

   2. Set Navigasi Bottom Navigation
	- Tandai menu Dashboard sebagai halaman yang sedang aktif.
	- Perbarui tampilan UI sesuai data kendaraan:
           . Home → Pindah ke MainActivity.
           . Dashboard → Tetap di halaman ini.
           . Setting → Menampilkan dialog konfirmasi logout.

   3. Ambil Data Kendaraan dari ViewModel
	- Pantau data kendaraan dari viewModel.vehicleData.observe().
	- Perbarui tampilan UI sesuai data kendaraan:
          . Kecepatan kendaraan
          . Status mesin (On/Off).
          . Status pintu (Open/Closed).

   4. Tangani Logout dari Bottom Navigation
	- Tampilkan AlertDialog saat menu Setting ditekan.
	- Jika pengguna memilih Ya, lakukan logout dari Firebase dan pindah ke LoginActivity.
	- Jika memilih Tidak, dialog ditutup.

   5. Tampilkan Notifikasi Alert dari ViewModel
	- Pantau event notifikasi dari viewModel.alertEvent.observe().
	- Tampilkan pesan peringatan dalam bentuk Toast.

   6. Cegah Pengguna Menekan Tombol Back
	- Override dispatchKeyEvent() untuk menangani tombol Back.
	- Jika tombol Back ditekan, cegah aksi dengan mengembalikan true.

D. UserModel.kt
   1. Definisi Data Class
	- UserModel adalah data class yang merepresentasikan data pengguna dalam aplikasi.
	- Memiliki enam properti dengan nilai default berupa string kosong ("").

   2. Properti dalam UserModel
	- username → Nama pengguna.
	- email → Alamat email pengguna.
	- password → Kata sandi pengguna.
	- alamat → Alamat pengguna.
	- telepon → Nomor telepon pengguna.
	- photo → URL atau path foto profil pengguna.

   3. Override toString()
	- toString() mengembalikan representasi string dari objek UserModel.
	- format output mencantumkan semua properti dalam bentuk "key='value'".

E. Vehicle.kt
   1. Definisi Data Class
	- Vehicle adalah data class yang merepresentasikan data kendaraan dalam aplikasi.
	- Memiliki lima properti utama yang digunakan untuk menyimpan informasi kendaraan.

   2. Properti dalam UserModel
	- lat → Koordinat latitude kendaraan (Double).
	- lng → Koordinat longitude kendaraan (Double).
	- speed → Kecepatan kendaraan dalam km/h (Int).
	- speed → Kecepatan kendaraan dalam km/h (Int).
	- doorOpen → Status pintu kendaraan (Boolean), true jika terbuka.

   3. Penggunaan Model
	- Digunakan untuk menyimpan dan mengelola data kendaraan dalam aplikasi.
	- Biasanya data ini diambil dari backend, Firebase, atau sensor GPS.
	- Dapat digunakan dalam ViewModel untuk mengupdate UI secara real-time.

F. FleetRepository.kt
   1. Definisi Repository
	- FleetRepository bertanggung jawab untuk menyediakan data kendaraan dalam aplikasi.
	- Menggunakan Dependency Injection dengan @Inject constructor().

   2. Data Dummy Koordinat GPS
	- gpsCoordinates adalah daftar koordinat kendaraan yang berisi beberapa objek Vehicle.
	- Setiap kendaraan memiliki nilai lat, lng, speed, engineOn, dan doorOpen.

   3. Simulasi Data Kendaraan
	- index digunakan untuk melacak data kendaraan yang dikembalikan.
	- getVehicleData() melakukan:
	  . Mengambil kendaraan berdasarkan indeks.
          . Memperbarui indeks agar berputar dalam daftar.
          . Mengembalikan salinan kendaraan dengan kecepatan acak antara 0-120 km/h.

G. FleetService.kt
   1. Definisi FleetService
	- FleetService adalah Android Service yang berjalan di latar belakang.
	- Digunakan untuk mensimulasikan pembaruan data kendaraan secara berkala.

   2. Inisialisasi Coroutine Scope
	- serviceScope menggunakan CoroutineScope(Dispatchers.IO + Job()) untuk menjalankan tugas di background thread.

   3. Metode onStartCommand()
	- Dipanggil ketika service dimulai.
	- Memulai proses simulasi data kendaraan dengan memanggil startSimulation().
	- START_STICKY → Service akan otomatis dimulai kembali jika dihentikan oleh sistem.

   4. Simulasi Data Kendaraan (startSimulation())
	- Coroutine berjalan dalam loop tak terbatas untuk mensimulasikan pembaruan data.
	- Setiap 3 detik (delay(3000)), mencetak log "Simulating vehicle data update...".

   5. Menghentikan Service (onDestroy())
	- serviceScope.cancel() menghentikan semua coroutine saat service dihentikan.

   6. Metode onBind()
	-Karena service ini tidak terikat (unbound service), mengembalikan null.

   7. Penggunaan FleetService
	- Bisa dijalankan dengan startService(Intent(context, FleetService::class.java)).
	- Bisa dihentikan dengan stopService(Intent(context, FleetService::class.java)).

H. FleetViewModel.kt
   1. Definisi FleetViewModel
	- FleetViewModel bertanggung jawab untuk mengelola data kendaraan dan mendeteksi peringatan.
	- Menggunakan LiveData untuk memperbarui UI secara real-time.
	- Dihubungkan dengan FleetRepository menggunakan Dependency Injection (@Inject).

   2. LiveData & MutableLiveData
	- _vehicleData (MutableLiveData) → Menyimpan informasi kendaraan terbaru.
	   . vehicleData (LiveData, read-only) → Dipakai oleh UI untuk membaca data kendaraan.
	- _alertEvent (MutableLiveData) → Menyimpan pesan peringatan.
	   . alertEvent (LiveData, read-only) → UI bisa membaca peringatan ini.

   3. Variabel Status Terakhir
	- lastSpeedAlert → Mencegah peringatan kecepatan berulang.
	- lastDoorAlert → Mencegah peringatan pintu berulang.
	- lastEngineStatus → Memastikan peringatan mesin hanya muncul saat ada perubahan.

   4. Simulasi Data Kendaraan (startSimulation())
	- Dilakukan di viewModelScope.launch {} untuk berjalan di latar belakang.
	- Looping tak terbatas (while (true)) untuk mengambil data kendaraan.
	- Setiap 3 detik (delay(3000)), data kendaraan diperbarui dari FleetRepository.
	- Log Debug dicetak untuk memantau perubahan data.

   5. Menghentikan Service (onDestroy())
	- Kecepatan melebihi 80 km/h → Mengirim peringatan "⚠️ Speed over 80 km/h!".
	- Pintu terbuka saat kendaraan bergerak → Mengirim peringatan "🚨 Door open while moving!".
	- Perubahan status mesin (ON / OFF) → Mengirim peringatan "✅ Engine turned ON" atau "❌ Engine turned OFF".

   6. Penggunaan di UI
	- UI dapat mengamati vehicleData untuk menampilkan posisi dan status kendaraan.
	- UI dapat mengamati alertEvent untuk menampilkan notifikasi peringatan ke pengguna.
# Mini-Fleet-Tracker
