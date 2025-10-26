package br.com.alura.adopet.api.validacoes;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.StatusAdocao;
import br.com.alura.adopet.api.repository.AdocaoRepository;

@ExtendWith(MockitoExtension.class)
class ValidacaoPetComAdocaoEmAndamentoTest {

    @Mock
    private AdocaoRepository adocaoRepository;

    @InjectMocks
    private ValidacaoPetComAdocaoEmAndamento validacao;

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deveria permitir adoção quando pet não tem adoção em andamento")
    void deveriaPermitirAdocaoQuandoPetNaoTemAdocaoEmAndamento() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria lançar exceção quando pet já tem adoção em andamento")
    void deveriaLancarExcecaoQuandoPetJaTemAdocaoEmAndamento() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(true);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            validacao.validar(dto);
        });

        assertEquals("Pet já possui uma adoção em andamento!", exception.getMessage());
        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria verificar pet correto no repositório")
    void deveriaVerificarPetCorretoNoRepositorio() {
        // ARRANGE
        Long idPet = 5L;
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(idPet, 10L, "Motivo de teste");
        
        given(adocaoRepository.existsByPetIdAndStatus(idPet, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(adocaoRepository).should().existsByPetIdAndStatus(idPet, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria usar status AGUARDANDO_AVALIACAO na verificação")
    void deveriaUsarStatusAguardandoAvaliacaoNaVerificacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(adocaoRepository).should().existsByPetIdAndStatus(eq(1L), eq(StatusAdocao.AGUARDANDO_AVALIACAO));
    }

    @Test
    @DisplayName("Deveria funcionar com diferentes IDs de pet")
    void deveriaFuncionarComDiferentesIdsDePet() {
        // ARRANGE
        SolicitacaoAdocaoDto dto1 = new SolicitacaoAdocaoDto(10L, 20L, "Motivo 1");
        SolicitacaoAdocaoDto dto2 = new SolicitacaoAdocaoDto(30L, 40L, "Motivo 2");
        
        given(adocaoRepository.existsByPetIdAndStatus(10L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);
        given(adocaoRepository.existsByPetIdAndStatus(30L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT - Primeira validação
        assertDoesNotThrow(() -> validacao.validar(dto1));
        then(adocaoRepository).should().existsByPetIdAndStatus(10L, StatusAdocao.AGUARDANDO_AVALIACAO);

        // ACT & ASSERT - Segunda validação
        assertDoesNotThrow(() -> validacao.validar(dto2));
        then(adocaoRepository).should().existsByPetIdAndStatus(30L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria lançar exceção para múltiplos pets com adoção em andamento")
    void deveriaLancarExcecaoParaMultiplosPetsComAdocaoEmAndamento() {
        // ARRANGE
        SolicitacaoAdocaoDto dto1 = new SolicitacaoAdocaoDto(1L, 10L, "Motivo 1");
        SolicitacaoAdocaoDto dto2 = new SolicitacaoAdocaoDto(2L, 20L, "Motivo 2");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(true);
        given(adocaoRepository.existsByPetIdAndStatus(2L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(true);

        // ACT & ASSERT - Primeira validação
        ValidacaoException exception1 = assertThrows(ValidacaoException.class, () -> {
            validacao.validar(dto1);
        });
        assertEquals("Pet já possui uma adoção em andamento!", exception1.getMessage());

        // ACT & ASSERT - Segunda validação
        ValidacaoException exception2 = assertThrows(ValidacaoException.class, () -> {
            validacao.validar(dto2);
        });
        assertEquals("Pet já possui uma adoção em andamento!", exception2.getMessage());

        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
        then(adocaoRepository).should().existsByPetIdAndStatus(2L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria ignorar outros status de adoção")
    void deveriaIgnorarOutrosStatusDeAdocao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "Motivo qualquer");
        
        // Configurar para retornar false mesmo se houver adoções com outros status
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria chamar repository apenas uma vez por validação")
    void deveriaChamarRepositoryApenasUmaVezPorValidacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria funcionar com ID de pet zero")
    void deveriaFuncionarComIdDePetZero() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(0L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(0L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(0L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria funcionar com ID de pet negativo")
    void deveriaFuncionarComIdDePetNegativo() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(-1L, 2L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(-1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(-1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    // ==================== TESTES DE INTEGRAÇÃO COM DTO ====================

    @Test
    @DisplayName("Deveria extrair ID do pet corretamente do DTO")
    void deveriaExtrairIdDoPetCorretamenteDoDto() {
        // ARRANGE
        Long idPetEsperado = 15L;
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(idPetEsperado, 25L, "Motivo específico");
        
        given(adocaoRepository.existsByPetIdAndStatus(idPetEsperado, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT
        validacao.validar(dto);

        // ASSERT
        then(adocaoRepository).should().existsByPetIdAndStatus(idPetEsperado, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria não depender do ID do tutor na validação")
    void deveriaNaoDependerDoIdDoTutorNaValidacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 999L, "Motivo qualquer");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    @Test
    @DisplayName("Deveria não depender do motivo na validação")
    void deveriaNaoDependerDoMotivoNaValidacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 2L, "");
        
        given(adocaoRepository.existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO))
            .willReturn(false);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));

        then(adocaoRepository).should().existsByPetIdAndStatus(1L, StatusAdocao.AGUARDANDO_AVALIACAO);
    }

    // ==================== MÉTODO AUXILIAR ====================

    private static void assertEquals(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}