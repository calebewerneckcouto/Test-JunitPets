package br.com.alura.adopet.api.repository;

import br.com.alura.adopet.api.model.Adocao;
import br.com.alura.adopet.api.model.StatusAdocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {
    
    // Use StatusAdocao (enum) como parâmetro, não String
    @Query("SELECT COUNT(a) > 0 FROM Adocao a WHERE a.pet.id = :petId AND a.status = :status")
    boolean existsByPetIdAndStatus(@Param("petId") Long petId, @Param("status") StatusAdocao status);
    
    // Ou se preferir uma versão mais específica para adoções em andamento:
    @Query("SELECT COUNT(a) > 0 FROM Adocao a WHERE a.pet.id = :petId AND a.status = br.com.alura.adopet.api.model.StatusAdocao.AGUARDANDO_AVALIACAO")
    boolean existsByPetWithAdocaoEmAndamento(@Param("petId") Long petId);
    
    // Métodos adicionais úteis
    Optional<Adocao> findByPetId(Long petId);
    
    @Query("SELECT a FROM Adocao a WHERE a.pet.id = :petId AND a.status = :status")
    Optional<Adocao> findByPetIdAndStatus(@Param("petId") Long petId, @Param("status") StatusAdocao status);
}