package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listarProdutos(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "categoriaId", required = false) Long categoriaId,
            Model model) {

        model.addAttribute("produtos", service.listarTodos(nome, categoriaId));
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("nomePesquisado", nome);
        model.addAttribute("categoriaPesquisada", categoriaId);
        return "lista-produtos";
    }

    // PONTO 2: Painel de auditoria — exclusivo para ADMIN (protegido também no SecurityConfig)
    @GetMapping("/auditoria")
    public String painelAuditoria(Model model) {
        model.addAttribute("produtos", service.listarParaAuditoria());
        return "auditoria-produtos";
    }

    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "cadastro-produto";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@Valid @ModelAttribute("produto") ProdutoModel produto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
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

        // PONTO 3: Mensagem de sucesso com horário
        String horario = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Produto salvo com sucesso! Horário da operação: " + horario);
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String exibirEdicao(@PathVariable("id") Long id, Model model) {
        ProdutoModel produto = service.buscarPorId(id);
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "editar-produto";
    }

    @PostMapping("/editar/{id}")
    public String atualizarProduto(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("produto") ProdutoModel produto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "editar-produto";
        }
        produto.setIdProduto(id);

        try {
            service.salvar(produto);
        } catch (IllegalArgumentException e) {
            result.rejectValue("nome", "error.produto", e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "editar-produto";
        }

        // PONTO 3: Mensagem de confirmação de edição com horário
        String horario = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Produto atualizado com sucesso! Horário da modificação: " + horario);
        return "redirect:/produtos";
    }

    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        String horario = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Produto excluído com sucesso! Horário da operação: " + horario);
        return "redirect:/produtos";
    }
}
