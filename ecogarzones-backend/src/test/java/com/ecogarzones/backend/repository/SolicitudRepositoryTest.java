package com.ecogarzones.backend.repository;

import com.ecogarzones.backend.model.EstadoEvento;
import com.ecogarzones.backend.model.Solicitud;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SolicitudRepositoryTest {

    @Autowired SolicitudRepository repo;

    private Solicitud nueva(String cliente, long creadoEn) {
        Solicitud s = new Solicitud();
        s.setNombreCliente(cliente);
        s.setTelefono("12345678");
        s.setTipoEvento("Cumpleaños");
        s.setFechaEvento("01/01/2027");
        s.setDireccion("Calle 1");
        s.setCantidadInvitados(10);
        s.setCreadoEn(creadoEn);
        return s;
    }

    @Test
    void listaOrdenadaPorFechaDeCreacionDescendente() {
        repo.save(nueva("Antiguo", 1000L));
        repo.save(nueva("Reciente", 2000L));

        List<Solicitud> lista = repo.findAllByOrderByCreadoEnDesc();

        assertEquals(2, lista.size());
        assertEquals("Reciente", lista.get(0).getNombreCliente());
    }

    @Test
    void estadoPorDefectoEsPendiente() {
        Solicitud guardada = repo.save(nueva("X", 1L));
        assertEquals(EstadoEvento.PENDIENTE, repo.findById(guardada.getId()).orElseThrow().getEstado());
    }
}
