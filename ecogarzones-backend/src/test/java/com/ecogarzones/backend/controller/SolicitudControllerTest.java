package com.ecogarzones.backend.controller;

import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.Solicitud;
import com.ecogarzones.backend.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired MockMvc mvc;
    @MockBean SolicitudService service;

    private static final String JSON_VALIDO = """
            {"nombreCliente":"Ana Pérez","telefono":"12345678","tipoEvento":"Boda",
             "fechaEvento":"20/12/2026","direccion":"Av. Siempre Viva 123",
             "cantidadInvitados":50,"aceptaTerminos":true}""";

    @Test
    void crear_valido_devuelve201ConId() throws Exception {
        when(service.crear(any(Solicitud.class))).thenAnswer(inv -> {
            Solicitud s = inv.getArgument(0);
            s.setId(7L);
            return s;
        });
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crear_sinInvitados_devuelve400() throws Exception {
        String malo = JSON_VALIDO.replace("\"cantidadInvitados\":50", "\"cantidadInvitados\":0");
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(malo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_telefonoInvalido_devuelve400() throws Exception {
        String malo = JSON_VALIDO.replace("12345678", "abc");
        mvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(malo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_inexistente_devuelve404() throws Exception {
        when(service.obtener(99L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "no existe"));
        mvc.perform(get("/api/solicitudes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("no existe"));
    }
}
