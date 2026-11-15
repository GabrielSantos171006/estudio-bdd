package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;
import br.com.mackenzie.estudiobdd.model.Prioridade;

@Repository
public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {

    List<HistoriaUsuario> findByProjetoIdOrderByIdAsc(Long projetoId);

    List<HistoriaUsuario> findByProjetoIdAndPrioridadeOrderByIdAsc(Long projetoId, Prioridade prioridade);

    Optional<HistoriaUsuario> findByTituloAndProjetoId(String titulo, Long projetoId);

    /** Regra 1: título único dentro do projeto. */
    boolean existsByTituloAndProjetoId(String titulo, Long projetoId);

    /** Usado na edição: existe OUTRA história com este título no projeto? */
    boolean existsByTituloAndProjetoIdAndIdNot(String titulo, Long projetoId, Long id);

    long countByProjetoId(Long projetoId);
}
