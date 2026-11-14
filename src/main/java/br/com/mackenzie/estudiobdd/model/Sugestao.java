package br.com.mackenzie.estudiobdd.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "sugestoes")
public class Sugestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ferramenta; // Ex: "Gemini", "ChatGPT"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo; // Texto ou JSON estruturado do cenário sugerido pela IA

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisaoSugestao decisao;

    @Enumerated(EnumType.STRING)
    private MotivoRejeicao motivoRejeicao; // Obrigatório apenas se decisao == REJEITADA

    @Column(columnDefinition = "TEXT")
    private String justificativa; // Explicação humana da curadoria

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_decisao")
    private LocalDateTime dataDecisao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "historia_usuario_id", nullable = false)
    private HistoriaUsuario historiaUsuario;

    public Sugestao() {
    }

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.decisao == null) {
            this.decisao = DecisaoSugestao.PENDENTE;
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFerramenta() {
        return ferramenta;
    }

    public void setFerramenta(String ferramenta) {
        this.ferramenta = ferramenta;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public DecisaoSugestao getDecisao() {
        return decisao;
    }

    public void setDecisao(DecisaoSugestao decisao) {
        this.decisao = decisao;
    }

    public MotivoRejeicao getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(MotivoRejeicao motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataDecisao() {
        return dataDecisao;
    }

    public void setDataDecisao(LocalDateTime dataDecisao) {
        this.dataDecisao = dataDecisao;
    }

    public HistoriaUsuario getHistoriaUsuario() {
        return historiaUsuario;
    }

    public void setHistoriaUsuario(HistoriaUsuario historiaUsuario) {
        this.historiaUsuario = historiaUsuario;
    }
}