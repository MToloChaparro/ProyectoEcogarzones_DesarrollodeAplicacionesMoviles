package com.ecogarzones.backend.controller;

import com.ecogarzones.backend.model.Inventario;
import com.ecogarzones.backend.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @GetMapping
    public List<Inventario> listar() { return service.listar(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventario crear(@Valid @RequestBody Inventario body) { return service.crear(body); }

    @PutMapping("/{id}")
    public Inventario actualizar(@PathVariable Long id, @Valid @RequestBody Inventario body) {
        return service.actualizar(id, body);
    }
}
