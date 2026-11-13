package com.ecogarzones.backend.controller;

import com.ecogarzones.backend.model.Incidencia;
import com.ecogarzones.backend.service.IncidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService service;

    @GetMapping
    public List<Incidencia> listar(@RequestParam(required = false) Long solicitudId) {
        return service.listar(solicitudId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incidencia crear(@Valid @RequestBody Incidencia body) { return service.crear(body); }

    @PutMapping("/{id}")
    public Incidencia actualizar(@PathVariable Long id, @Valid @RequestBody Incidencia body) {
        return service.actualizar(id, body);
    }
}
