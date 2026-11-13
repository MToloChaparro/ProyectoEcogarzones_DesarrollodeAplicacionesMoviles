package com.ecogarzones.backend.service;

import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.EstadoIncidencia;
import com.ecogarzones.backend.model.Incidencia;
import com.ecogarzones.backend.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private final IncidenciaRepository repo;

    @Transactional(readOnly = true)
    public List<Incidencia> listar(Long solicitudId) {
        return solicitudId == null ? repo.findAllByOrderByFechaDesc()
                : repo.findBySolicitudIdOrderByFechaDesc(solicitudId);
    }

    @Transactional
    public Incidencia crear(Incidencia i) {
        i.setId(null);
        if (i.getEstado() == null) i.setEstado(EstadoIncidencia.REPORTADA);
        if (i.getFecha() == null) i.setFecha(System.currentTimeMillis());
        return repo.save(i);
    }

    @Transactional
    public Incidencia actualizar(Long id, Incidencia datos) {
        Incidencia actual = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Incidencia " + id + " no encontrada"));
        datos.setId(id);
        if (datos.getFecha() == null) datos.setFecha(actual.getFecha());
        return repo.save(datos);
    }
}
