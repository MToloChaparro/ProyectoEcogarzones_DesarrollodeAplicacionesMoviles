package com.ecogarzones.backend.controller;

import com.ecogarzones.backend.model.Solicitud;
import com.ecogarzones.backend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService service;

    @GetMapping
    public List<Solicitud> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@Valid @RequestBody Solicitud body) { return service.crear(body); }

    @PutMapping("/{id}")
    public Solicitud actualizar(@PathVariable Long id, @Valid @RequestBody Solicitud body) {
        return service.actualizar(id, body);
    }
}
