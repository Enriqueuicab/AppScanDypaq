package com.example.ultimamilla.propuesta.DAO

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// ==========================================
// ENTIDAD
// ==========================================

@Entity(
    tableName = "recolecta_cip",
    indices = [
        Index(
            value = ["FolioRecolecta", "cip"],
            unique = true
        )
    ]
)
data class RecolectaCipEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val FolioEmbarqueR: String,
    val FolioRecolecta: String,
    val latitud: String,
    val longitud: String,
    val usuariocaptura: String,
    val cip: String,

    // Estado de sincronización local
    val sincronizado: Boolean = false
)


// ==========================================
// DAO
// ==========================================

@Dao
interface RecolectaCipDao {

    // Insertar un CIP pendiente
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(recolectaCip: RecolectaCipEntity): Long

    // Obtener todos los registros locales
    @Query("SELECT * FROM recolecta_cip ORDER BY id DESC")
    fun obtenerTodosFlow(): Flow<List<RecolectaCipEntity>>

    // Obtener todos los registros
    @Query("SELECT * FROM recolecta_cip ORDER BY id ASC")
    suspend fun obtenerTodos(): List<RecolectaCipEntity>

    // Obtener CIPs pendientes de sincronización
    @Query("""
        SELECT * FROM recolecta_cip
        WHERE sincronizado = 0
        ORDER BY id ASC
    """)
    suspend fun obtenerPendientes(): List<RecolectaCipEntity>

    // Marcar un CIP como sincronizado
    @Query("""
        UPDATE recolecta_cip
        SET sincronizado = 1
        WHERE id = :id
    """)
    suspend fun marcarSincronizado(id: Int)

    // Eliminar un registro específico
    @Query("DELETE FROM recolecta_cip WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    // Eliminar todos los registros
    @Query("DELETE FROM recolecta_cip")
    suspend fun eliminarTodo()
}


// ==========================================
// BASE DE DATOS
// ==========================================

// Cuando cambie la estructura de las entidades,
// debemos incrementar la versión y gestionar la migración.
@Database(
    entities = [
        RecolectaCipEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class RecolectaCipDatabase : RoomDatabase() {

    abstract fun recolectaCipDao(): RecolectaCipDao

    companion object {

        @Volatile
        private var INSTANCE: RecolectaCipDatabase? = null

        fun getDatabase(context: Context): RecolectaCipDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecolectaCipDatabase::class.java,
                    "UltimaMilla_db"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
