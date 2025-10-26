package br.com.alura.adopet.api.validacoes;

import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Adocao;
import br.com.alura.adopet.api.model.StatusAdocao;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ValidacaoTutorComAdocaoEmAndamentoTest {

    @InjectMocks
    private ValidacaoTutorComAdocaoEmAndamento validacao;

    @Mock
    private AdocaoRepository adocaoRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private Tutor tutor;

    @Mock
    private Adocao adocaoEmAndamento;

    @Mock
    private Adocao adocaoAprovada;

    @Mock
    private Adocao adocaoReprovada;

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorNaoTemAdocaoEmAndamento() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoAprovada);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoAprovada.getTutor()).willReturn(tutor);
        given(adocaoAprovada.getStatus()).willReturn(StatusAdocao.APROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaLancarExcecaoQuandoTutorTemAdocaoEmAndamento() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoEmAndamento);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoEmAndamento.getTutor()).willReturn(tutor);
        given(adocaoEmAndamento.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> validacao.validar(dto));
        assertEquals("Tutor já possui outra adoção aguardando avaliação!", exception.getMessage());
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoAdocaoEmAndamentoEDeOutroTutor() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        Tutor outroTutor = new Tutor();
        List<Adocao> adocoes = Arrays.asList(adocaoEmAndamento);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoEmAndamento.getTutor()).willReturn(outroTutor);
        // REMOVIDO: given(adocaoEmAndamento.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);
        // O status não é verificado quando o tutor é diferente

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoNaoHaAdocoes() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList();
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorTemAdocaoComStatusDiferenteDeAguardandoAvaliacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoReprovada);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoReprovada.getTutor()).willReturn(tutor);
        given(adocaoReprovada.getStatus()).willReturn(StatusAdocao.REPROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorTemMultiplasAdocoesMasNenhumaEmAguardandoAvaliacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoAprovada, adocaoReprovada);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoAprovada.getTutor()).willReturn(tutor);
        given(adocaoAprovada.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoReprovada.getTutor()).willReturn(tutor);
        given(adocaoReprovada.getStatus()).willReturn(StatusAdocao.REPROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaLancarExcecaoQuandoTutorTemMultiplasAdocoesEUmaEstaEmAguardandoAvaliacao() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        
        // Colocando a adoção em andamento PRIMEIRO na lista para garantir que seja encontrada
        List<Adocao> adocoes = Arrays.asList(adocaoEmAndamento, adocaoAprovada, adocaoReprovada);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoEmAndamento.getTutor()).willReturn(tutor);
        given(adocaoEmAndamento.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);
        // REMOVIDOS: os stubbings das outras adoções pois o loop para na primeira que atende a condição

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> validacao.validar(dto));
        assertEquals("Tutor já possui outra adoção aguardando avaliação!", exception.getMessage());
    }

    // Teste alternativo para múltiplas adoções - colocando a adoção problemática no final
    @Test
    void deveriaLancarExcecaoQuandoTutorTemAdocaoEmAndamentoNoFinalDaLista() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        
        // Colocando a adoção em andamento no FINAL da lista
        List<Adocao> adocoes = Arrays.asList(adocaoAprovada, adocaoReprovada, adocaoEmAndamento);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoAprovada.getTutor()).willReturn(tutor);
        given(adocaoAprovada.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoReprovada.getTutor()).willReturn(tutor);
        given(adocaoReprovada.getStatus()).willReturn(StatusAdocao.REPROVADO);
        given(adocaoEmAndamento.getTutor()).willReturn(tutor);
        given(adocaoEmAndamento.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> validacao.validar(dto));
        assertEquals("Tutor já possui outra adoção aguardando avaliação!", exception.getMessage());
    }
}