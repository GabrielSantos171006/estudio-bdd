package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.Cenario;

@Repository
public interface CenarioRepository extends JpaRepository<Cenario, Long> {

    /** Cenários da história na ordem de posição. */
    @Query("SELECT c FROM Cenario c WHERE c.historiaUsuario.id = :historiaId ORDER BY c.posicao ASC NULLS LAST, c.id ASC")
    List<Cenario> findByHistoriaUsuarioId(@Param("historiaId") Long historiaId);

    /** Regra 2: título único dentro da história. */
    @Query("SELECT COUNT(c) > 0 FROM Cenario c WHERE c.titulo = :titulo AND c.historiaUsuario.id = :historiaId")
    boolean existsByTituloAndHistoriaUsuarioId(@Param("titulo") String titulo, @Param("historiaId") Long historiaId);

    /** Usado na edição: existe OUTRO cenário com este título na história? */
    @Query("SELECT COUNT(c) > 0 FROM Cenario c WHERE c.titulo = :titulo AND c.historiaUsuario.id = :historiaId AND c.id <> :cenarioId")
    boolean existsTituloEmOutroCenario(@Param("titulo") String titulo,
                                       @Param("historiaId") Long historiaId,
                                       @Param("cenarioId") Long cenarioId);

    @Query("SELECT COUNT(c) FROM Cenario c WHERE c.historiaUsuario.id = :historiaId")
    long contarPorHistoria(@Param("historiaId") Long historiaId);

    @Query("SELECT COALESCE(MAX(c.posicao), 0) FROM Cenario c WHERE c.historiaUsuario.id = :historiaId")
    Integer maiorPosicao(@Param("historiaId") Long historiaId);

    /** Resumo (regra 7): cada linha = [OrigemCenario, Long quantidade]. */
    @Query("SELECT c.origem, COUNT(c) FROM Cenario c WHERE c.historiaUsuario.projeto.id = :projetoId GROUP BY c.origem")
    List<Object[]> contarPorOrigemNoProjeto(@Param("projetoId") Long projetoId);
}
