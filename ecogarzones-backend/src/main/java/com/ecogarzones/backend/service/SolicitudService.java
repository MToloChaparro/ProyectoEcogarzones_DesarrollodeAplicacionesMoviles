package com.ecogarzones.backend.service;

import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.EstadoEvento;
import com.ecogarzones.backend.model.Solicitud;
import com.ecogarzones.backend.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository repo;

    @Transactional(readOnly = true)
    public List<Solicitud> listar() {
        return repo.findAllByOrderByCreadoEnDesc();
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Solicitud " + id + " no encontrada"));
    }

    @Transactional
    public Solicitud crear(Solicitud s) {
        s.setId(null);
        if (s.getEstado() == null) s.setEstado(EstadoEvento.PENDIENTE);
        if (s.getCreadoEn() == null) s.setCreadoEn(System.currentTimeMillis());
        return repo.save(s);
    }

    @Transactional
    public Solicitud actualizar(Long id, Solicitud datos) {
        Solicitud actual = obtener(id);
        datos.setId(id);
        if (datos.getCreadoEn() == null) datos.setCreadoEn(actual.getCreadoEn());
        if (datos.getEstado() == null) datos.setEstado(actual.getEstado());
        return repo.save(datos);
    }
}
