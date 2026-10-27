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
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cenarios")
public class Cenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemCenario origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "historia_usuario_id", nullable = false)
    private HistoriaUsuario historiaUsuario;

    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passo> passos = new ArrayList<>();

    public Cenario() {
    }

    public Cenario(String titulo, OrigemCenario origem, HistoriaUsuario historiaUsuario) {
        this.titulo = titulo;
        this.origem = origem;
        this.historiaUsuario = historiaUsuario;
    }

    public Cenario(Long id, String titulo, OrigemCenario origem, HistoriaUsuario historiaUsuario) {
        this.id = id;
        this.titulo = titulo;
        this.origem = origem;
        this.historiaUsuario = historiaUsuario;
    }

    // Método auxiliar para sincronizar a lista bidirecional de passos
    public void adicionarPasso(Passo passo) {
        passos.add(passo);
        passo.setCenario(this);
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

    public OrigemCenario getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemCenario origem) {
        this.origem = origem;
    }

    public HistoriaUsuario getHistoriaUsuario() {
        return historiaUsuario;
    }

    public void setHistoriaUsuario(HistoriaUsuario historiaUsuario) {
        this.historiaUsuario = historiaUsuario;
    }

    public List<Passo> getPassos() {
        return passos;
    }

    public void setPassos(List<Passo> passos) {
        this.passos = passos;
    }
}