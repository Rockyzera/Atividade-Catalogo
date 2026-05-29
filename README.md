# Catálogo de Produtos — FATEC

Sistema web de gerenciamento de produtos com controle de estoque, auditoria e segurança por perfil de usuário.

**Stack:** Java 17 · Spring Boot 3.4 · PostgreSQL · Thymeleaf · Bootstrap 5 · Spring Security

---

## Como os Dados Fluem: do Formulário HTML ao Banco de Dados

Quando o usuário preenche um formulário e clica em **Salvar**, os dados passam por 4 camadas antes de chegar no banco.

### Exemplo: Cadastrar um novo produto

#### 1. Formulário HTML (`cadastro-produto.html`)
O usuário preenche os campos. Cada `name` do input corresponde a um atributo do objeto Java:

```html
<form th:action="@{/produtos/salvar}" th:object="${produto}" method="post">
    <input type="text"   th:field="*{nome}"       name="nome">
    <input type="number" th:field="*{valor}"      name="valor">
    <input type="number" th:field="*{quantidade}" name="quantidade" min="0">
    <select th:field="*{categoria.id}">...</select>
</form>
```

O atributo `th:field` do Thymeleaf vincula cada campo diretamente ao objeto `ProdutoModel`.

---

#### 2. Controller recebe os dados (`ProdutoController.java`)
O Spring pega todos os campos do formulário e **monta o objeto automaticamente** via `@ModelAttribute`:

```java
@PostMapping("/salvar")
public String salvarProduto(
    @Valid @ModelAttribute("produto") ProdutoModel produto,
    BindingResult result,
    RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        return "cadastro-produto"; // volta com erros se inválido
    }

    service.salvar(produto); // passa para o Service
    redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto salvo!");
    return "redirect:/produtos";
}
```

> O `@Valid` dispara todas as validações do Model antes de executar o método.  
> O `BindingResult` captura os erros sem lançar exceção.

---

#### 3. Service valida as regras de negócio (`ProdutoService.java`)
Antes de salvar, o Service aplica as regras que vão além das annotations:

```java
@Transactional
public void salvar(ProdutoModel produto) {

    // Regra de negócio: quantidade não pode ser negativa
    if (produto.getQuantidade() != null && produto.getQuantidade() < 0) {
        throw new IllegalArgumentException("A quantidade não pode ser negativa.");
    }

    // Regra de negócio: nome duplicado não é permitido
    if (produto.getIdProduto() == null &&
        repository.existsByNomeIgnoreCase(produto.getNome())) {
        throw new IllegalArgumentException("Produto com esse nome já existe.");
    }

    repository.save(produto); // passa para o Repository
}
```

---

#### 4. Repository persiste no banco (`ProdutoRepository.java`)
O Spring Data JPA executa o SQL automaticamente:

```java
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
    // repository.save(produto) gera:
    // INSERT INTO tb_produto (nome, valor, quantidade, categoria_id, data_cadastro)
    // VALUES ('Notebook', 2999.90, 10, 1, NOW())
}
```

---

#### 5. Model define a estrutura da tabela (`ProdutoModel.java`)
As annotations JPA mapeiam os atributos Java para colunas SQL:

```java
@Entity
@Table(name = "TB_PRODUTO")
public class ProdutoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;         // coluna: id (auto-incremento)

    @NotBlank
    private String nome;            // coluna: nome

    @NotNull @Positive
    private BigDecimal valor;       // coluna: valor

    @Min(0)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer quantidade;     // coluna: quantidade

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaModel categoria; // coluna: categoria_id (FK)

    @PrePersist
    protected void onCreate() {
        this.dataCadastro    = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now(); // atualizado a cada edição
    }
}
```

---

### Resumo Visual do Fluxo

```
[HTML Form]
    │  input name="nome", name="valor", name="quantidade"
    ▼
[Controller]  @ModelAttribute monta o objeto ProdutoModel
    │  @Valid valida as annotations (@NotBlank, @Min, etc.)
    ▼
[Service]     Aplica regras de negócio (quantidade < 0, nome duplicado)
    │  @Transactional garante que tudo ou nada é salvo
    ▼
[Repository]  repository.save(produto)
    │  Hibernate gera o SQL automaticamente
    ▼
[PostgreSQL]  INSERT INTO tb_produto (...) VALUES (...)
```

---

##  Estrutura do Projeto

```
src/main/java/br/com/fatec/catalogo/
├── models/
│   ├── ProdutoModel.java       ← @Entity, mapeamento da tabela
│   ├── CategoriaModel.java
│   └── UsuarioModel.java
├── repositories/
│   ├── ProdutoRepository.java  ← interface JPA, queries automáticas
│   └── CategoriaRepository.java
├── services/
│   └── ProdutoService.java     ← regras de negócio e validações
├── controllers/
│   ├── ProdutoController.java  ← rotas /produtos/**
│   ├── CategoriaController.java
│   ├── UsuarioController.java
│   └── LoginController.java
└── Security/
    └── SecurityConfig.java     ← permissões ADMIN/USER

src/main/resources/templates/
├── lista-produtos.html         ← listagem com filtros
├── cadastro-produto.html       ← formulário de criação
├── editar-produto.html         ← formulário de edição
├── auditoria-produtos.html     ← painel ADMIN (estoque baixo)
└── login.html
```

---

## ⚙️ Como Rodar

### Pré-requisitos
- Java 17+
- PostgreSQL rodando na porta 5432
- Maven

### Configuração do banco (`application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### Executar
```bash
./mvnw spring-boot:run
```

Acesse: `http://localhost:8080/login`  
Login padrão: `admin` / `123456`

---

## Perfis de Acesso

| Perfil | O que pode fazer |
|--------|-----------------|
| `ADMIN` | Criar, editar, excluir produtos e categorias, acessar auditoria |
| `USER`  | Apenas visualizar e filtrar produtos e categorias |

---

## Funcionalidades Implementadas

- [x] CRUD completo de produtos e categorias
- [x] Controle de estoque com validação de quantidade negativa
- [x] Painel de auditoria ordenado por última modificação
- [x] Destaque visual para estoque baixo (quantidade < 5)
- [x] Flash messages com horário da operação
- [x] Filtro por nome e categoria
- [x] Controle de acesso por perfil (ADMIN/USER)
- [x] Login com Spring Security + BCrypt