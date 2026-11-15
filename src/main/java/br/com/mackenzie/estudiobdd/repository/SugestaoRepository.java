package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.DecisaoSugestao;
import br.com.mackenzie.estudiobdd.model.Sugestao;

@Repository
public interface SugestaoRepository extends JpaRepository<Sugestao, Long> {

    @Query("SELECT s FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId ORDER BY s.id ASC")
    List<Sugestao> findByHistoriaUsuarioId(@Param("historiaId") Long historiaId);

    @Query("SELECT s FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId AND s.decisao = :decisao ORDER BY s.id ASC")
    List<Sugestao> findByHistoriaUsuarioIdAndDecisao(@Param("historiaId") Long historiaId,
                                                     @Param("decisao") DecisaoSugestao decisao);

    /** Regra 5: existe sugestão com a decisão informada (ex.: PENDENTE)? */
    @Query("SELECT COUNT(s) > 0 FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId AND s.decisao = :decisao")
    boolean existsByHistoriaUsuarioIdAndDecisao(@Param("historiaId") Long historiaId,
                                                @Param("decisao") DecisaoSugestao decisao);

    @Query("SELECT COUNT(s) FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId AND s.decisao = :decisao")
    long contarPorHistoriaEDecisao(@Param("historiaId") Long historiaId,
                                   @Param("decisao") DecisaoSugestao decisao);

    /** Resumo (regra 7): cada linha = [DecisaoSugestao, Long quantidade]. */
    @Query("SELECT s.decisao, COUNT(s) FROM Sugestao s WHERE s.historiaUsuario.projeto.id = :projetoId GROUP BY s.decisao")
    List<Object[]> contarPorDecisaoNoProjeto(@Param("projetoId") Long projetoId);

    /** Resumo (regra 7): cada linha = [MotivoRejeicao, Long quantidade], do mais para o menos frequente. */
    @Query("SELECT s.motivoRejeicao, COUNT(s) FROM Sugestao s "
         + "WHERE s.historiaUsuario.projeto.id = :projetoId AND s.motivoRejeicao IS NOT NULL "
         + "GROUP BY s.motivoRejeicao ORDER BY COUNT(s) DESC")
    List<Object[]> contarMotivosRejeicaoNoProjeto(@Param("projetoId") Long projetoId);
}
