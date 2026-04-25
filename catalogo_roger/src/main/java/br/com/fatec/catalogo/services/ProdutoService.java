package br.com.fatec.catalogo.services;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.ProdutoRepository;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
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
            // Se o usuário preencheu nome E escolheu uma categoria
            return repository.findByNomeContainingIgnoreCaseAndCategoriaId(nome, categoriaId);
        } else if (temNome) {
            // Se preencheu SÓ o nome
            return repository.findByNomeContainingIgnoreCase(nome);
        } else if (temCategoria) {
            // Se escolheu SÓ a categoria
            return repository.findByCategoriaId(categoriaId);
        }

        // Se não preencheu nada, traz tudo
        return repository.findAll();
    }

    // BUG CORRIGIDO: parâmetro era 'long' primitivo mas o controller envia 'Long' (wrapper).
    // Usar Long evita NullPointerException em auto-unboxing se o id vier nulo.
    public ProdutoModel buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    @Transactional
    public void salvar(ProdutoModel produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            return;
        }
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