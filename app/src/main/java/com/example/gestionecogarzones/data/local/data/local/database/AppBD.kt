package com.example.gestionecogarzones.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gestionecogarzones.data.local.dao.IncidenciaDao
import com.example.gestionecogarzones.data.local.dao.InventarioDao
import com.example.gestionecogarzones.data.local.dao.SolicitudDao
import com.example.gestionecogarzones.data.local.entity.IncidenciaEntity
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import com.example.gestionecogarzones.data.local.entity.SolicitudEntity

@Database(
    entities = [SolicitudEntity::class, IncidenciaEntity::class, InventarioEntity::class],
    version = 14,
    exportSchema = false
)
abstract class AppBD : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao
    abstract fun incidenciaDao(): IncidenciaDao
    abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppBD? = null

        fun getDatabase(context: Context): AppBD {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppBD::class.java,
                    "ecogarzones_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}