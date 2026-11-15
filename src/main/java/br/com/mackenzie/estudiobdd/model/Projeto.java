package br.com.mackenzie.estudiobdd.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "projetos", uniqueConstraints = @UniqueConstraint(name = "uk_projeto_nome", columnNames = "nome"))
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    /** Glossário e informações do negócio (seção 3 do enunciado). */
    @Column(name = "contexto_dominio", columnDefinition = "TEXT")
    private String contextoDominio;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    /** Usuário que criou o projeto. LAZY: só é carregado quando acessado dentro da transação. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** 1:N com HistoriaUsuario. Excluir o projeto remove suas histórias (e, em cascata, cenários e sugestões). */
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<HistoriaUsuario> historias = new ArrayList<>();

    public Projeto() {
    }

    public Projeto(String nome, String descricao, Usuario usuario) {
        this.nome = nome;
        this.descricao = descricao;
        this.usuario = usuario;
    }

    @PrePersist
    void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getContextoDominio() { return contextoDominio; }
    public void setContextoDominio(String contextoDominio) { this.contextoDominio = contextoDominio; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<HistoriaUsuario> getHistorias() { return historias; }
    public void setHistorias(List<HistoriaUsuario> historias) { this.historias = historias; }
}
