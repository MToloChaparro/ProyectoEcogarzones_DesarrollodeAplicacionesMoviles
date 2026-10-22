package com.example.gestionecogarzones.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestionecogarzones.data.local.entity.InventarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarioDao {
    @Query("SELECT * FROM inventario ORDER BY categoria ASC, nombreItem ASC")
    fun obtenerTodo(): Flow<List<InventarioEntity>>

    @Insert
    suspend fun insertar(item: InventarioEntity)

    @Insert
    suspend fun insertarTodo(items: List<InventarioEntity>)

    @Update
    suspend fun actualizar(item: InventarioEntity)

    @Query("SELECT COUNT(*) FROM inventario")
    suspend fun contarItems(): Int
}