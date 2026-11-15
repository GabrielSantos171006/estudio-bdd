package br.com.mackenzie.estudiobdd.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "historias_usuario",
       uniqueConstraints = @UniqueConstraint(name = "uk_historia_titulo_projeto", columnNames = { "projeto_id", "titulo" }))
public class HistoriaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String papel; // "Como [papel]"

    @Column(nullable = false)
    private String acao; // "Quero [objetivo]"

    @Column(nullable = false)
    private String beneficio; // "Para [benefício]"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    /** Composição: excluir a história remove seus cenários (e os passos deles). */
    @OneToMany(mappedBy = "historiaUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicao ASC, id ASC")
    private List<Cenario> cenarios = new ArrayList<>();

    /** Excluir a história remove o registro das sugestões associadas. */
    @OneToMany(mappedBy = "historiaUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Sugestao> sugestoes = new ArrayList<>();

    public HistoriaUsuario() {
    }

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

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (dataCriacao == null) {
            dataCriacao = agora;
        }
        dataAtualizacao = agora;
    }

    @PreUpdate
    void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getBeneficio() { return beneficio; }
    public void setBeneficio(String beneficio) { this.beneficio = beneficio; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public List<Cenario> getCenarios() { return cenarios; }
    public void setCenarios(List<Cenario> cenarios) { this.cenarios = cenarios; }

    public List<Sugestao> getSugestoes() { return sugestoes; }
    public void setSugestoes(List<Sugestao> sugestoes) { this.sugestoes = sugestoes; }
}
