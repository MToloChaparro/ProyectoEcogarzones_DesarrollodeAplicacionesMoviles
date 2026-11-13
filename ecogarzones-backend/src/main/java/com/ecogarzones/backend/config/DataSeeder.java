package com.ecogarzones.backend.config;

import com.ecogarzones.backend.model.*;
import com.ecogarzones.backend.repository.InventarioRepository;
import com.ecogarzones.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/** Carga los mismos datos de prueba que tenía la app Android (solo si las tablas están vacías). */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final InventarioRepository inventario;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarios.count() == 0) {
            usuarios.saveAll(List.of(
                    usuario("Matias", "matias@adminecogarzones.com", "matias123", Rol.ADMINISTRADOR, "+56 9 1234 5678", "Av. Providencia 1234, Santiago", "Director General"),
                    usuario("Marcela", "marcela@gmail.com", "marcela123", Rol.CLIENTE, "+56 9 8765 4321", "Av. Las Condes 5678, Santiago", "Cliente VIP"),
                    usuario("Alan", "alan@personalecogarzones.com", "alan123", Rol.TRABAJADOR, "+56 9 5555 1111", "Calle San Martín 88, Santiago", "Garzón Principal"),
                    usuario("Gabriela", "gabriela@personalecogarzones.com", "gabriela123", Rol.TRABAJADOR, "+56 9 5555 2222", "Av. Italia 432, Providencia", "Bartender Senior"),
                    usuario("Pim", "pim@personalecogarzones.com", "pim123", Rol.TRABAJADOR, "+56 9 5555 3333", "Calle O'Higgins 99, Santiago", "Bartender Coctelería")
            ));
        }
        if (inventario.count() == 0) {
            inventario.saveAll(List.of(
                    item("Platos principales", 150, "Vajilla", "Unidades", 1500.0, "Local"),
                    item("Copas de vino", 200, "Cristalería", "Unidades", 1000.0, "Importadora X"),
                    item("Sillas Tiffany", 100, "Mobiliario", "Unidades", 15000.0, "Arriendos SPA"),
                    item("Manteles blancos", 30, "Textiles", "Paquetes", 5000.0, "Textiles Sur")
            ));
        }
    }

    private Usuario usuario(String nombre, String email, String pass, Rol rol, String tel, String dir, String cargo) {
        return new Usuario(null, nombre, email, encoder.encode(pass), rol, null, tel, dir, cargo);
    }

    private Inventario item(String nombre, int cant, String cat, String unidad, double costo, String prov) {
        Inventario i = new Inventario();
        i.setNombreItem(nombre); i.setCantidad(cant); i.setCategoria(cat);
        i.setUnidad(unidad); i.setCostoUnidad(costo); i.setProveedor(prov);
        return i;
    }
}
