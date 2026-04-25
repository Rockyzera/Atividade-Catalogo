package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    // O Spring Security vai usar este método para achar quem está logando
    Optional<UsuarioModel> findByUsername(String username);
}