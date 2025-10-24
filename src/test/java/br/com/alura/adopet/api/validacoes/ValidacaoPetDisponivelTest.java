package br.com.alura.adopet.api.validacoes;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
class ValidacaoPetDisponivelTest {

    @InjectMocks
    private ValidacaoPetDisponivel validacao;

    @Mock
    private PetRepository petRepository;

    @Mock
    private Pet pet;

    @Mock
    private SolicitacaoAdocaoDto dto;

    @Test
    @DisplayName("Deve permitir solicitação de adoção quando pet está disponível")
    void deveriaPermitirSolicitacaoDeAdocaoPetDisponivel() {
        // ARRANGE
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(pet.getAdotado()).willReturn(false);

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    @DisplayName("Não deve permitir solicitação de adoção quando pet já foi adotado")
    void naoDeveriaPermitirSolicitacaoDeAdocaoPetJaAdotado() {
        // ARRANGE
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(pet.getAdotado()).willReturn(true);

        // ACT + ASSERT
        Assertions.assertThrows(ValidacaoException.class, () -> validacao.validar(dto));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException com mensagem correta quando pet já foi adotado")
    void deveriaLancarExcecaoComMensagemCorreta() {
        // ARRANGE
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(pet.getAdotado()).willReturn(true);

        // ACT
        ValidacaoException exception = Assertions.assertThrows(
            ValidacaoException.class,
            () -> validacao.validar(dto)
        );

        // ASSERT
        Assertions.assertEquals("Pet já foi adotado!", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar pet pelo ID fornecido no DTO")
    void deveriaBuscarPetPeloIdDoDto() {
        // ARRANGE
        Long idPet = 10L;
        SolicitacaoAdocaoDto dtoReal = new SolicitacaoAdocaoDto(idPet, 20L, "Motivo qualquer");
        given(petRepository.getReferenceById(idPet)).willReturn(pet);
        given(pet.getAdotado()).willReturn(false);

        // ACT
        validacao.validar(dtoReal);

        // ASSERT
        then(petRepository).should().getReferenceById(idPet);
    }

    @Test
    @DisplayName("Deve verificar o status de adotado do pet")
    void deveriaVerificarStatusAdotadoDoPet() {
        // ARRANGE
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(pet.getAdotado()).willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(pet).should().getAdotado();
    }

    @Test
    @DisplayName("Deve buscar pet no repositório antes de validar")
    void deveriaBuscarPetNoRepositorio() {
        // ARRANGE
        given(petRepository.getReferenceById(dto.idPet())).willReturn(pet);
        given(pet.getAdotado()).willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(petRepository).should().getReferenceById(dto.idPet());
    }

    @Test
    @DisplayName("Deve permitir múltiplas validações para pets diferentes disponíveis")
    void deveriaPermitirMultiplasValidacoesPetsDiferentes() {
        // ARRANGE
        SolicitacaoAdocaoDto dto1 = new SolicitacaoAdocaoDto(1L, 10L, "Motivo 1");
        SolicitacaoAdocaoDto dto2 = new SolicitacaoAdocaoDto(2L, 10L, "Motivo 2");
        
        Pet pet1 = org.mockito.Mockito.mock(Pet.class);
        Pet pet2 = org.mockito.Mockito.mock(Pet.class);
        
        given(petRepository.getReferenceById(1L)).willReturn(pet1);
        given(petRepository.getReferenceById(2L)).willReturn(pet2);
        given(pet1.getAdotado()).willReturn(false);
        given(pet2.getAdotado()).willReturn(false);

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validacao.validar(dto1));
        Assertions.assertDoesNotThrow(() -> validacao.validar(dto2));
    }

    @Test
    @DisplayName("Deve validar corretamente quando pet tem status false explicitamente")
    void deveriaValidarCorretamenteComStatusFalseExplicito() {
        // ARRANGE
        SolicitacaoAdocaoDto dtoReal = new SolicitacaoAdocaoDto(5L, 15L, "Quero adotar");
        given(petRepository.getReferenceById(5L)).willReturn(pet);
        given(pet.getAdotado()).willReturn(Boolean.FALSE);

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validacao.validar(dtoReal));
        then(pet).should().getAdotado();
    }

    @Test
    @DisplayName("Deve rejeitar validação quando pet tem status true explicitamente")
    void deveriaRejeitarValidacaoComStatusTrueExplicito() {
        // ARRANGE
        SolicitacaoAdocaoDto dtoReal = new SolicitacaoAdocaoDto(5L, 15L, "Quero adotar");
        given(petRepository.getReferenceById(5L)).willReturn(pet);
        given(pet.getAdotado()).willReturn(Boolean.TRUE);

        // ACT + ASSERT
        ValidacaoException exception = Assertions.assertThrows(
            ValidacaoException.class,
            () -> validacao.validar(dtoReal)
        );
        Assertions.assertNotNull(exception.getMessage());
    }
}