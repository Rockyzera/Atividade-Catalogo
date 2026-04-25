package br.com.fatec.catalogo.controllers;

import org.springframework.stereotype.Controller; // Import necessário
import org.springframework.web.bind.annotation.GetMapping; // Import necessário

@Controller // <--- VOCÊ ESQUECEU ESTA LINHA!
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Retorna o arquivo templates/login.html
    }
}