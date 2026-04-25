package br.com.fatec.catalogo.repositories;

import java.util.List;
import br.com.fatec.catalogo.models.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
    List<ProdutoModel> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);

    List<ProdutoModel> findByCategoriaId(Long categoriaId);
    List<ProdutoModel> findByNomeContainingIgnoreCaseAndCategoriaId(String nome, Long categoriaId);
}
