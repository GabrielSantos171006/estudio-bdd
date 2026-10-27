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
@Table(name = "passos")
public class Passo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPasso tipo; // DADO, QUANDO, ENTAO, E, MAS

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto; // O texto da cláusula BDD

    @Column(nullable = false)
    private Integer ordem; // Posição de execução dentro do cenário (1, 2, 3...)

    @ManyToOne(optional = false)
    @JoinColumn(name = "cenario_id", nullable = false)
    private Cenario cenario;

    public Passo() {
    }

    public Passo(TipoPasso tipo, String texto, Integer ordem, Cenario cenario) {
        this.tipo = tipo;
        this.texto = texto;
        this.ordem = ordem;
        this.cenario = cenario;
    }

    public Passo(Long id, TipoPasso tipo, String texto, Integer ordem, Cenario cenario) {
        this.id = id;
        this.tipo = tipo;
        this.texto = texto;
        this.ordem = ordem;
        this.cenario = cenario;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPasso getTipo() {
        return tipo;
    }

    public void setTipo(TipoPasso tipo) {
        this.tipo = tipo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Cenario getCenario() {
        return cenario;
    }

    public void setCenario(Cenario cenario) {
        this.cenario = cenario;
    }
}