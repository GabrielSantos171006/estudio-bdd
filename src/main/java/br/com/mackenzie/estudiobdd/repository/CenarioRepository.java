package br.com.mackenzie.estudiobdd.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import br.com.mackenzie.estudiobdd.model.Cenario;

@Repository
public interface CenarioRepository extends JpaRepository<Cenario, Long> {

    // Lista os cenários da história
    @Query("SELECT c FROM Cenario c WHERE c.historiaUsuario.id = :historiaId")
    List<Cenario> findByHistoriaUsuarioId(@Param("historiaId") Long historiaId);

    // Valida se já existe o título na mesma história de forma direta e segura
    @Query("SELECT COUNT(c) > 0 FROM Cenario c WHERE c.titulo = :titulo AND c.historiaUsuario.id = :historiaId")
    boolean existsByTituloAndHistoriaUsuarioId(@Param("titulo") String titulo, @Param("historiaId") Long historiaId);
}