package com.ecogarzones.backend.repository;

import com.ecogarzones.backend.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findAllByOrderByFechaDesc();
    List<Incidencia> findBySolicitudIdOrderByFechaDesc(Long solicitudId);
}
