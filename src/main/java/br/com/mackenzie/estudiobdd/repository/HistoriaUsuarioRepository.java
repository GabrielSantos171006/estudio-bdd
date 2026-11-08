package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.HistoriaUsuario;

@Repository
public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {

    List<HistoriaUsuario> findByProjetoId(Long projetoId);

    // Valida se já existe uma história com esse título no mesmo projeto
    boolean existsByTituloAndProjetoId(String titulo, Long projetoId);
}