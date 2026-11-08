package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.Cenario;

@Repository
public interface CenarioRepository extends JpaRepository<Cenario, Long> {

    List<Cenario> findByHistoriaUsuarioId(Long historiaUsuarioId);

    // verificar título único dentro da história
    boolean existsByTituloAndHistoriaUsuarioId(String titulo, Long historiaUsuarioId);
}