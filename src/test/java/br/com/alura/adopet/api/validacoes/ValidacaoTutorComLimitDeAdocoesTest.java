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
class ValidacaoTutorComLimiteDeAdocoesTest {

    @InjectMocks
    private ValidacaoTutorComLimiteDeAdocoes validacao;

    @Mock
    private AdocaoRepository adocaoRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private Tutor tutor;

    @Mock
    private Tutor outroTutor;

    @Mock
    private Adocao adocaoAprovada1, adocaoAprovada2, adocaoAprovada3, adocaoAprovada4, adocaoAprovada5;

    @Mock
    private Adocao adocaoAguardando;

    @Mock
    private Adocao adocaoReprovada;

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorTemMenosDe5AdocoesAprovadas() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoAprovada1);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoAprovada1.getTutor()).willReturn(tutor);
        given(adocaoAprovada1.getStatus()).willReturn(StatusAdocao.APROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorTem4AdocoesAprovadas() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        
        List<Adocao> adocoes = Arrays.asList(
            adocaoAprovada1, adocaoAprovada2, adocaoAprovada3, adocaoAprovada4
        );
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        
        given(adocaoAprovada1.getTutor()).willReturn(tutor);
        given(adocaoAprovada1.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada2.getTutor()).willReturn(tutor);
        given(adocaoAprovada2.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada3.getTutor()).willReturn(tutor);
        given(adocaoAprovada3.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada4.getTutor()).willReturn(tutor);
        given(adocaoAprovada4.getStatus()).willReturn(StatusAdocao.APROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaLancarExcecaoQuandoTutorTem5AdocoesAprovadas() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        
        List<Adocao> adocoes = Arrays.asList(
            adocaoAprovada1, adocaoAprovada2, adocaoAprovada3, adocaoAprovada4, adocaoAprovada5
        );
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        
        given(adocaoAprovada1.getTutor()).willReturn(tutor);
        given(adocaoAprovada1.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada2.getTutor()).willReturn(tutor);
        given(adocaoAprovada2.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada3.getTutor()).willReturn(tutor);
        given(adocaoAprovada3.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada4.getTutor()).willReturn(tutor);
        given(adocaoAprovada4.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada5.getTutor()).willReturn(tutor);
        given(adocaoAprovada5.getStatus()).willReturn(StatusAdocao.APROVADO);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> validacao.validar(dto));
        assertEquals("Tutor chegou ao limite máximo de 5 adoções!", exception.getMessage());
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoTutorTemAdocoesComStatusDiferenteDeAprovado() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoAguardando, adocaoReprovada);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        given(adocaoAguardando.getTutor()).willReturn(tutor);
        given(adocaoAguardando.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);
        given(adocaoReprovada.getTutor()).willReturn(tutor);
        given(adocaoReprovada.getStatus()).willReturn(StatusAdocao.REPROVADO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }

    @Test
    void deveriaPermitirSolicitacaoQuandoAdocoesAprovadasSaoDeOutrosTutores() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        List<Adocao> adocoes = Arrays.asList(adocaoAprovada1, adocaoAprovada2, adocaoAprovada3);
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        
        given(adocaoAprovada1.getTutor()).willReturn(outroTutor);
        given(adocaoAprovada2.getTutor()).willReturn(outroTutor);
        given(adocaoAprovada3.getTutor()).willReturn(outroTutor);
        // Não é necessário stubar o status pois quando o tutor é diferente, não verifica o status

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
    void deveriaContarApenasAdocoesAprovadasDoMesmoTutor() {
        // ARRANGE
        SolicitacaoAdocaoDto dto = new SolicitacaoAdocaoDto(1L, 1L, "motivo");
        
        // 3 aprovadas do mesmo tutor + 1 aprovada de outro tutor + 1 do mesmo tutor com outro status
        List<Adocao> adocoes = Arrays.asList(
            adocaoAprovada1, adocaoAprovada2, adocaoAprovada3, // do mesmo tutor, aprovadas
            adocaoAprovada4, // de outro tutor, aprovada
            adocaoAguardando // do mesmo tutor, outro status
        );
        
        given(tutorRepository.getReferenceById(dto.idTutor())).willReturn(tutor);
        given(adocaoRepository.findAll()).willReturn(adocoes);
        
        // Primeiras 3 adoções: mesmo tutor, aprovadas
        given(adocaoAprovada1.getTutor()).willReturn(tutor);
        given(adocaoAprovada1.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada2.getTutor()).willReturn(tutor);
        given(adocaoAprovada2.getStatus()).willReturn(StatusAdocao.APROVADO);
        given(adocaoAprovada3.getTutor()).willReturn(tutor);
        given(adocaoAprovada3.getStatus()).willReturn(StatusAdocao.APROVADO);
        
        // Próxima adoção: outro tutor, aprovada
        given(adocaoAprovada4.getTutor()).willReturn(outroTutor);
        // REMOVIDO: given(adocaoAprovada4.getStatus()).willReturn(StatusAdocao.APROVADO);
        // O status não é verificado quando o tutor é diferente
        
        // Última adoção: mesmo tutor, outro status
        given(adocaoAguardando.getTutor()).willReturn(tutor);
        given(adocaoAguardando.getStatus()).willReturn(StatusAdocao.AGUARDANDO_AVALIACAO);

        // ACT & ASSERT
        assertDoesNotThrow(() -> validacao.validar(dto));
    }
}