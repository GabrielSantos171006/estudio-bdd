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

    // Lista todas as sugestões vinculadas a uma história de usuário
    @Query("SELECT s FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId")
    List<Sugestao> findByHistoriaUsuarioId(@Param("historiaId") Long historiaId);

    // Verifica se ainda existe alguma sugestão PENDENTE
    @Query("SELECT COUNT(s) > 0 FROM Sugestao s WHERE s.historiaUsuario.id = :historiaId AND s.decisao = :decisao")
    boolean existsByHistoriaUsuarioIdAndDecisao(@Param("historiaId") Long historiaId, @Param("decisao") DecisaoSugestao decisao);
}