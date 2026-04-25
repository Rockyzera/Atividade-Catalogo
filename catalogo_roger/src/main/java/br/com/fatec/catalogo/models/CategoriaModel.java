package br.com.fatec.catalogo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "TB_CATEGORIA") // Padronizado com TB_ igual ao TB_PRODUTO
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BUG CORRIGIDO: sem @NotBlank, era possível salvar categoria com nome vazio/nulo.
    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(min = 2, max = 60, message = "O nome deve ter entre 2 e 60 caracteres.")
    @Column(nullable = false, unique = true)
    private String nome;

    // Relacionamento inverso: Uma categoria para muitos produtos.
    // orphanRemoval = false por segurança — impede exclusão em cascata acidental de produtos.
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<ProdutoModel> produtos;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<ProdutoModel> getProdutos() { return produtos; }
    public void setProdutos(List<ProdutoModel> produtos) { this.produtos = produtos; }
}