package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    // Útil para validar duplicatas antes de salvar
    Optional<CategoriaModel> findByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}