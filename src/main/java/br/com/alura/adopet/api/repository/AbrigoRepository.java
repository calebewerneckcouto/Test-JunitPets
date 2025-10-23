package br.com.alura.adopet.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.adopet.api.model.Abrigo;

public interface AbrigoRepository extends JpaRepository<Abrigo, Long> {
    
    @Query("SELECT a FROM Abrigo a WHERE a.nome = ?1")
    Optional<Abrigo> findByNome(String nome);
    
    @Query("SELECT COUNT(a) > 0 FROM Abrigo a WHERE a.nome = ?1 OR a.telefone = ?2 OR a.email = ?3")
    boolean existsByNomeOrTelefoneOrEmail(String nome, String telefone, String email);
}