package br.com.mackenzie.estudiobdd.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cenarios",
       uniqueConstraints = @UniqueConstraint(name = "uk_cenario_titulo_historia", columnNames = { "historia_usuario_id", "titulo" }))
public class Cenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    /** Posição do cenário dentro da história (1, 2, 3...). */
    @Column(name = "posicao")
    private Integer posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigemCenario origem;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_usuario_id", nullable = false)
    private HistoriaUsuario historiaUsuario;

    /** Composição: passos são removidos junto com o cenário e sempre lidos em ordem. */
    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Passo> passos = new ArrayList<>();

    public Cenario() {
    }

    public Cenario(String titulo, OrigemCenario origem, HistoriaUsuario historiaUsuario) {
        this.titulo = titulo;
        this.origem = origem;
        this.historiaUsuario = historiaUsuario;
    }

    /** Mantém a associação bidirecional sincronizada. */
    public void adicionarPasso(Passo passo) {
        passos.add(passo);
        passo.setCenario(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getPosicao() { return posicao; }
    public void setPosicao(Integer posicao) { this.posicao = posicao; }

    public OrigemCenario getOrigem() { return origem; }
    public void setOrigem(OrigemCenario origem) { this.origem = origem; }

    public HistoriaUsuario getHistoriaUsuario() { return historiaUsuario; }
    public void setHistoriaUsuario(HistoriaUsuario historiaUsuario) { this.historiaUsuario = historiaUsuario; }

    public List<Passo> getPassos() { return passos; }
    public void setPassos(List<Passo> passos) { this.passos = passos; }
}
