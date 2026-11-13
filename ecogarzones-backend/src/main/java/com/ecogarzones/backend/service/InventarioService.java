package com.ecogarzones.backend.service;

import com.ecogarzones.backend.exception.ApiException;
import com.ecogarzones.backend.model.Inventario;
import com.ecogarzones.backend.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository repo;

    @Transactional(readOnly = true)
    public List<Inventario> listar() {
        return repo.findAllByOrderByCategoriaAscNombreItemAsc();
    }

    @Transactional
    public Inventario crear(Inventario item) {
        item.setId(null);
        return repo.save(item);
    }

    @Transactional
    public Inventario actualizar(Long id, Inventario datos) {
        if (!repo.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Ítem " + id + " no encontrado");
        }
        datos.setId(id);
        return repo.save(datos);
    }
}
