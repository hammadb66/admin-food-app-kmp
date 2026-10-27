package org.example.adminfoodapp.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

actual class DriverFactory actual constructor() {
    // 👇 YEH CACHE HAI: Taake baar baar database create na ho
    private var cachedDriver: SqlDriver? = null

    @Synchronized
    actual fun createDriver(): SqlDriver {
        // Agar driver pehle se bana hua hai, toh wapas wahi return karo
        cachedDriver?.let { return it }

        val place = DataPlace.resolve()
        val dbFile = File(place.dataFolder, "restaurant.db")
        val backup = DbBackup(dbFile, place)

        // 1) Agar asli database delete/kharab ho gaya ho, toh backup se wapas le aao
        backup.restoreIfNeeded()

        // 2) Database open karo
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // 3) Schema create karo sirf pehli dafa, aur usko try-catch mein wrap karo
        try {
            if (!dbFile.exists() || dbFile.length() == 0L) {
                RestaurantDatabase.Schema.create(driver)
            }
        } catch (e: Exception) {
            println("⚠️ Schema already exists or error: ${e.message}")
        }

        // 4) Background mein backup ka process chalu karo
        backup.startMirroring()

        // 5) Driver ko cache mein save karo taake next time wahi use ho
        cachedDriver = driver
        return driver
    }
}

/** Database kahan rakhna hai aur backup kaise karna hai */
private class DataPlace(
    val dataFolder: File,
    val legacyDb: File?, // Purana data migrate karne ke liye
    val backupName: String,
    val mode: String,
) {
    companion object {
        fun resolve(): DataPlace {
            val home = File(System.getProperty("user.home"), ".adminfoodapp")
            val install = installDir()

            if (install != null) {
                val data = File(install, "data")
                if (isWritable(data)) {
                    return DataPlace(data, File(home, "restaurant.db"), "AdminFoodBackup", "INSTALLED_DATA_FOLDER")
                }
                home.mkdirs()
                return DataPlace(home, null, "AdminFoodBackup", "INSTALLED_HOME_FALLBACK")
            }
            // IDE se chalane par
            home.mkdirs()
            return DataPlace(home, null, "Backup_AdminFood", "IDE")
        }

        private fun installDir(): File? {
            System.getProperty("jpackage.app-path")?.let { path ->
                File(path).parentFile?.takeIf { it.isDirectory }?.let { return it }
            }
            val javaHome = File(System.getProperty("java.home") ?: "")
            if (javaHome.name == "runtime") {
                val parent = javaHome.parentFile
                if (parent != null && File(parent, "app").isDirectory) return parent
            }
            runCatching {
                val cmd = ProcessHandle.current().info().command().orElse(null)
                val parent = if (cmd != null) File(cmd).parentFile else null
                if (parent != null && File(parent, "app").isDirectory && File(parent, "runtime").isDirectory) {
                    return parent
                }
            }
            return null
        }

        private fun isWritable(dir: File): Boolean = runCatching {
            if (!dir.exists()) dir.mkdirs()
            if (!dir.isDirectory) return@runCatching false
            val testFile = File(dir, "write_test.tmp")
            if (testFile.exists()) testFile.delete()
            val success = testFile.createNewFile()
            if (success) { testFile.delete(); true } else { false }
        }.getOrDefault(false)
    }
}

/** Backup ka poora kaam: Live copy aur Daily copies */
private class DbBackup(private val dbFile: File, private val place: DataPlace) {
    private val dataFolder: File = dbFile.parentFile
    private val backupDir: File = chooseBackupDir()
    private val liveFile = File(backupDir, "restaurant_live.db")
    private val dailyDir = File(backupDir, "daily")

    @Volatile
    private var lastSignature = ""

    fun restoreIfNeeded() {
        runCatching {
            if (isHealthy(dbFile)) return

            val fromBackups = restoreFolders()
                .flatMap { dir ->
                    listOf(File(dir, "restaurant_live.db")) + (File(dir, "daily").listFiles()?.toList() ?: emptyList())
                }
                .filter { it.exists() && it.length() > 0L }
                .sortedByDescending { it.lastModified() }
                .firstOrNull { isHealthy(it) }

            val good = fromBackups ?: place.legacyDb?.takeIf { isHealthy(it) } ?: return

            if (dbFile.exists()) {
                val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                runCatching { dbFile.renameTo(File(dataFolder, "restaurant.corrupt-$stamp.db")) }
            }
            listOf("-wal", "-shm", "-journal").forEach { File(dbFile.path + it).delete() }
            good.copyTo(dbFile, overwrite = true)
            println("✅ Database restored from backup!")
        }
    }

    fun startMirroring() {
        thread(name = "admin-food-backup", isDaemon = true) {
            while (true) {
                runCatching { syncIfChanged() }
                try { Thread.sleep(5_000) } catch (e: InterruptedException) { return@thread }
            }
        }
        Runtime.getRuntime().addShutdownHook(Thread { runCatching { backupNow() } })
    }

    private fun syncIfChanged() {
        val sig = signature()
        if (sig == lastSignature) return
        if (backupNow()) lastSignature = sig
    }

    private fun signature(): String {
        val wal = File(dbFile.path + "-wal")
        return "${dbFile.lastModified()}:${dbFile.length()}:${wal.lastModified()}:${wal.length()}"
    }

    @Synchronized
    private fun backupNow(): Boolean {
        if (!dbFile.exists() || dbFile.length() == 0L) return false
        backupDir.mkdirs()

        val tmp = File(backupDir, "restaurant_live.tmp")
        if (tmp.exists()) tmp.delete()

        val target = tmp.absolutePath.replace("'", "''")
        connect(dbFile).use { c ->
            c.createStatement().use { it.execute("VACUUM INTO '$target'") }
        }

        if (!isHealthy(tmp)) {
            tmp.delete()
            return false
        }
        Files.move(tmp.toPath(), liveFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        dailyCopy()
        return true
    }

    private fun dailyCopy() {
        dailyDir.mkdirs()
        val today = File(dailyDir, "restaurant_${LocalDate.now()}.db")
        if (!today.exists()) liveFile.copyTo(today)
        dailyDir.listFiles { f -> f.name.startsWith("restaurant_") && f.name.endsWith(".db") }
            ?.sortedByDescending { it.name }
            ?.drop(30)
            ?.forEach { it.delete() }
    }

    private fun connect(file: File): Connection {
        val c = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")
        c.createStatement().use { it.execute("PRAGMA busy_timeout = 5000") }
        return c
    }

    private fun isHealthy(file: File): Boolean = runCatching {
        if (!file.exists() || file.length() == 0L) return@runCatching false
        connect(file).use { c ->
            c.createStatement().use { st ->
                st.executeQuery("PRAGMA quick_check").use { rs -> rs.next() && rs.getString(1) == "ok" }
            }
        }
    }.getOrDefault(false)

    private fun chooseBackupDir(): File {
        val dir = customDir()
            ?: otherDriveDir()
            ?: File(System.getProperty("user.home"), place.backupName)
        dir.mkdirs()
        return dir
    }

    private fun customDir(): File? = runCatching {
        File(dataFolder, "backup_dir.txt").takeIf { it.exists() }
            ?.readText()?.lines()?.firstOrNull { it.isNotBlank() }?.trim()
            ?.let { File(it) }
    }.getOrNull()

    private fun otherDriveDir(): File? {
        val systemDrive = (System.getenv("SystemDrive") ?: "C:").uppercase()
        val root = File.listRoots().firstOrNull { r ->
            r.path.take(2).uppercase() != systemDrive && r.totalSpace > 0L && r.canWrite()
        }
        return root?.let { File(it, place.backupName) }
    }

    private fun restoreFolders(): List<File> {
        val all = mutableListOf(backupDir, File(System.getProperty("user.home"), place.backupName))
        File.listRoots().forEach { r -> all += File(r, place.backupName) }
        return all.distinct().filter { it.isDirectory }
    }
}