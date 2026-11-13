package com.ecogarzones.backend.controller;

import com.ecogarzones.backend.dto.*;
import com.ecogarzones.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> listar() { return service.listar(); }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest body) { return service.registrar(body); }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest body) { return service.autenticar(body); }

    @PutMapping("/perfil")
    public UsuarioResponse perfil(@RequestParam String email, @Valid @RequestBody PerfilUpdateRequest body) {
        return service.actualizarPerfil(email, body);
    }
}
