package com.ecogarzones.backend.repository;

import com.ecogarzones.backend.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findAllByOrderByCategoriaAscNombreItemAsc();
}
