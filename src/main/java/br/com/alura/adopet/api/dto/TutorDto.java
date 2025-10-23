package br.com.alura.adopet.api.dto;

import br.com.alura.adopet.api.model.Tutor;

public record TutorDto(
    Long id,
    String nome,
    String telefone,
    String email
) {
    public TutorDto(Tutor tutor) {
        this(
            tutor.getId(),
            tutor.getNome(),
            tutor.getTelefone(),
            tutor.getEmail()
        );
    }
}