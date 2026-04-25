package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.CategoriaModel;
import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    // BUG CORRIGIDO: o controller precisava conhecer as categorias para popular o
    // <select> nos formulários. Sem isso, o dropdown de categorias fica vazio.
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listarProdutos(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "categoriaId", required = false) Long categoriaId, // <-- Parâmetro Novo
            Model model) {

        // Envia os produtos filtrados
        model.addAttribute("produtos", service.listarTodos(nome, categoriaId));

        // Envia todas as categorias para popular o <select> do filtro
        model.addAttribute("categorias", categoriaRepository.findAll());

        // Mantém os valores preenchidos na tela após pesquisar
        model.addAttribute("nomePesquisado", nome);
        model.addAttribute("categoriaPesquisada", categoriaId);

        return "lista-produtos";
    }

    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        // BUG CORRIGIDO: sem esta linha o Thymeleaf lança TemplateProcessingException
        // ao tentar iterar a lista de categorias no <select> do formulário.
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "cadastro-produto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoModel produto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            // BUG CORRIGIDO: ao retornar com erro de validação, as categorias precisam
            // ser reenviadas, senão o <select> fica vazio na tela de erro.
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "cadastro-produto";
        }

        try {
            service.salvar(produto);
        } catch (IllegalArgumentException e) {
            result.rejectValue("nome", "error.produto", e.getMessage());
            model.addAttribute("produto", produto);
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "cadastro-produto";
        }

        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String exibirEdicao(@PathVariable("id") Long id, Model model) {
        ProdutoModel produto = service.buscarPorId(id);
        model.addAttribute("produto", produto);
        // BUG CORRIGIDO: formulário de edição também precisa da lista de categorias.
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "editar-produto";
    }

    @PostMapping("/editar/{id}")
    public String atualizarProduto(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("produto") ProdutoModel produto,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "editar-produto";
        }
        produto.setIdProduto(id);
        service.salvar(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable("id") Long id) {
        service.excluir(id);
        return "redirect:/produtos";
    }
}