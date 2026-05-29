package br.com.fatec.catalogo;

import br.com.fatec.catalogo.models.CategoriaModel;
import br.com.fatec.catalogo.models.UsuarioModel;
import br.com.fatec.catalogo.repositories.CategoriaRepository;
import br.com.fatec.catalogo.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(
			UsuarioRepository repository,
			CategoriaRepository cRepo,       // CORREÇÃO: declarar como parâmetro do método
			PasswordEncoder passwordEncoder  // O Spring injeta automaticamente ao ver aqui
	) {
		return args -> {
			if (repository.findByUsername("admin2").isEmpty()) {
				UsuarioModel admin = new UsuarioModel();
				admin.setNome("Administrador Chefe");
				admin.setUsername("admin2");
				admin.setPassword(passwordEncoder.encode("123456"));
				admin.setRole("ADMIN");
				repository.save(admin);
				System.out.println("==== NOVO ADMIN CRIADO! ====");
			}

			if (cRepo.count() == 0) {
				CategoriaModel c1 = new CategoriaModel();
				c1.setNome("Eletrônicos");
				cRepo.save(c1);

				CategoriaModel c2 = new CategoriaModel();
				c2.setNome("Eletrodomésticos");
				cRepo.save(c2);

				System.out.println("==== CATEGORIAS INICIAIS CRIADAS! ====");
			}
		};
	}
}