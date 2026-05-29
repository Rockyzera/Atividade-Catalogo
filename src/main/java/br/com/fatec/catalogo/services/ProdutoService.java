package br.com.fatec.catalogo.services;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.ProdutoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public List<ProdutoModel> listarTodos(String nome, Long categoriaId) {
        boolean temNome = (nome != null && !nome.isBlank());
        boolean temCategoria = (categoriaId != null);

        if (temNome && temCategoria) {
            return repository.findByNomeContainingIgnoreCaseAndCategoriaId(nome, categoriaId);
        } else if (temNome) {
            return repository.findByNomeContainingIgnoreCase(nome);
        } else if (temCategoria) {
            return repository.findByCategoriaId(categoriaId);
        }
        return repository.findAll();
    }

    // PONTO 2: Lista todos os produtos ordenados pela data de atualização mais recente
    public List<ProdutoModel> listarParaAuditoria() {
        return repository.findAllByOrderByDataAtualizacaoDesc();
    }

    public ProdutoModel buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    @Transactional
    public void salvar(ProdutoModel produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            return;
        }

        // PONTO 1: Validação de negócio — quantidade não pode ser negativa
        if (produto.getQuantidade() != null && produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }

        // Impede cadastro de produtos com nome duplicado (apenas para novos produtos)
        if (produto.getIdProduto() == null && repository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new IllegalArgumentException("Já existe um produto cadastrado com o nome: " + produto.getNome());
        }

        repository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
