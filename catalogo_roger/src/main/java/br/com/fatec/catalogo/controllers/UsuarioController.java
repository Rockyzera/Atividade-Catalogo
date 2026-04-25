package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.UsuarioModel;
import br.com.fatec.catalogo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // NÃO ESQUEÇA DESTE IMPORT
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/cadastro")
    public String telaCadastro(Model model) {
        // Envia um "molde" vazio para o HTML preencher
        model.addAttribute("usuario", new UsuarioModel());
        return "cadastro-usuario";
    }

    @PostMapping("/cadastro")
    public String salvarUsuario(@ModelAttribute("usuario") UsuarioModel usuario) {

        // TESTE DE MESA: Isso vai imprimir no console do IntelliJ
        System.out.println("==== TENTANDO SALVAR O USUARIO ====");
        System.out.println("Nome: " + usuario.getNome());
        System.out.println("Username: " + usuario.getUsername());
        System.out.println("Perfil: " + usuario.getRole());

        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(senhaCriptografada);

        // Salva no banco de dados
        repository.save(usuario);
        System.out.println("==== SALVO COM SUCESSO! ====");

        return "redirect:/produtos";
    }
}