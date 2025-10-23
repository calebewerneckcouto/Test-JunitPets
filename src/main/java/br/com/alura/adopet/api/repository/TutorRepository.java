package br.com.alura.adopet.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.adopet.api.model.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    
    @Query("SELECT COUNT(t) > 0 FROM Tutor t WHERE t.telefone = ?1 OR t.email = ?2")
    boolean existsByTelefoneOrEmail(String telefone, String email);
    
    Optional<Tutor> findByTelefone(String telefone);
    Optional<Tutor> findByEmail(String email);
    
    // Método customizado para paginação nativa no PostgreSQL
    @Query(value = "SELECT * FROM tutores ORDER BY nome LIMIT :limit OFFSET :offset", 
           nativeQuery = true)
    List<Tutor> findAllWithPagination(@Param("offset") int offset, @Param("limit") int limit);
    
    // Contagem total para paginação
    @Query(value = "SELECT COUNT(*) FROM tutores", nativeQuery = true)
    long countTotal();
}