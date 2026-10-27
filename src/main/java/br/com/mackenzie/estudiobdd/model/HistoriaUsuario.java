package br.com.mackenzie.estudiobdd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "historias_usuario")
public class HistoriaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String papel; // "Como [papel]"

    @Column(nullable = false)
    private String acao; // "Quero [ação]"

    @Column(nullable = false)
    private String beneficio; // "Para [benefício]"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Construtor padrão obrigatório para o JPA
    public HistoriaUsuario() {
    }

    // Construtor com parâmetros para criação de instâncias e testes
    public HistoriaUsuario(String titulo, String papel, String acao, String beneficio, 
                           Prioridade prioridade, Projeto projeto, Usuario autor) {
        this.titulo = titulo;
        this.papel = papel;
        this.acao = acao;
        this.beneficio = beneficio;
        this.prioridade = prioridade;
        this.projeto = projeto;
        this.autor = autor;
    }

    public HistoriaUsuario(Long id, String titulo, String papel, String acao, String beneficio, 
                           Prioridade prioridade, Projeto projeto, Usuario autor) {
        this.id = id;
        this.titulo = titulo;
        this.papel = papel;
        this.acao = acao;
        this.beneficio = beneficio;
        this.prioridade = prioridade;
        this.projeto = projeto;
        this.autor = autor;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
}