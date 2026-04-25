package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.CategoriaModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository repository;

    // Rota pública para usuários logados (USER e ADMIN)
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", repository.findAll());
        return "lista-categoria";
    }

    // Rotas protegidas (Apenas ADMIN chega aqui graças ao SecurityConfig)
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("categoria", new CategoriaModel());
        return "cadastro-categoria";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("categoria") CategoriaModel categoria, BindingResult result) {
        if (result.hasErrors()) {
            return "cadastro-categoria";
        }
        repository.save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String exibirEdicao(@PathVariable("id") Long id, Model model) {
        CategoriaModel categoria = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida:" + id));
        model.addAttribute("categoria", categoria);
        return "cadastro-categoria"; // Reutiliza a tela de cadastro para edição
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            // Se tentar deletar uma categoria que tem produtos, o banco bloqueia.
            // Para um sistema profissional, trataríamos esse erro na tela, mas por enquanto redirecionamos.
            System.out.println("Erro ao deletar: A categoria possui produtos vinculados.");
        }
        return "redirect:/categorias";
    }
}