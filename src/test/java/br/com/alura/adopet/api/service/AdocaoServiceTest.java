package br.com.alura.adopet.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.ReprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Adocao;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.PetRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import br.com.alura.adopet.api.validacoes.ValidacaoSolicitacaoAdocao;

@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    @InjectMocks
    private AdocaoService service;

    @Mock
    private AdocaoRepository repository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private EmailService emailService;

    @Spy
    private List<ValidacaoSolicitacaoAdocao> validacoes = new ArrayList<>();

    @Mock
    private ValidacaoSolicitacaoAdocao validador1;

    @Mock
    private ValidacaoSolicitacaoAdocao validador2;

    @Mock
    private Pet pet;

    @Mock
    private Tutor tutor;

    @Mock
    private Abrigo abrigo;

    @Mock
    private Adocao adocao;

    private SolicitacaoAdocaoDto solicitacaoDto;
    private AprovacaoAdocaoDto aprovacaoDto;
    private ReprovacaoAdocaoDto reprovacaoDto;

    @Captor
    private ArgumentCaptor<Adocao> adocaoCaptor;

    @BeforeEach
    void setUp() {
        this.solicitacaoDto = new SolicitacaoAdocaoDto(10L, 20L, "Motivo qualquer");
        this.aprovacaoDto = new AprovacaoAdocaoDto(1L);
        this.reprovacaoDto = new ReprovacaoAdocaoDto(1L, "Justificativa de reprovação");
    }

    // ==================== TESTES DE SOLICITAÇÃO ====================

    @Test
    void deveriaSalvarAdocaoAoSolicitar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");

        // ACT
        service.solicitar(solicitacaoDto);

        // ASSERT
        then(repository).should().save(adocaoCaptor.capture());
        Adocao adocaoSalva = adocaoCaptor.getValue();
        Assertions.assertEquals(pet, adocaoSalva.getPet());
        Assertions.assertEquals(tutor, adocaoSalva.getTutor());
        Assertions.assertEquals(solicitacaoDto.motivo(), adocaoSalva.getMotivo());
    }

    @Test
    void deveriaChamarValidadoresDeAdocaoAoSolicitar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        validacoes.add(validador1);
        validacoes.add(validador2);

        // ACT
        service.solicitar(solicitacaoDto);

        // ASSERT
        BDDMockito.then(validador1).should().validar(solicitacaoDto);
        BDDMockito.then(validador2).should().validar(solicitacaoDto);
    }

    @Test
    void deveriaEnviarEmailAoAbrigoAoSolicitar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(pet.getNome()).willReturn("Rex");

        // ACT
        service.solicitar(solicitacaoDto);

        // ASSERT
        then(emailService).should().enviarEmail(
            eq("abrigo@email.com"),
            eq("Solicitação de adoção"),
            anyString()
        );
    }

    @Test
    void deveriaBuscarPetEtutorPorIdAoSolicitar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");

        // ACT
        service.solicitar(solicitacaoDto);

        // ASSERT
        then(petRepository).should().getReferenceById(solicitacaoDto.idPet());
        then(tutorRepository).should().getReferenceById(solicitacaoDto.idTutor());
    }

    // ==================== TESTES DE APROVAÇÃO ====================

    @Test
    void deveriaMarcarAdocaoComoAprovada() {
        // ARRANGE
        given(repository.getReferenceById(aprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        // ACT
        service.aprovar(aprovacaoDto);

        // ASSERT
        then(adocao).should().marcarComoAprovada();
    }

    @Test
    void deveriaEnviarEmailAoAbrigoAoAprovar() {
        // ARRANGE
        given(repository.getReferenceById(aprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        // ACT
        service.aprovar(aprovacaoDto);

        // ASSERT
        then(emailService).should().enviarEmail(
            eq("abrigo@email.com"),
            eq("Adoção aprovada"),
            anyString()
        );
    }

    @Test
    void deveriaBuscarAdocaoPorIdAoAprovar() {
        // ARRANGE
        given(repository.getReferenceById(aprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        // ACT
        service.aprovar(aprovacaoDto);

        // ASSERT
        then(repository).should().getReferenceById(aprovacaoDto.idAdocao());
    }

    @Test
    void deveriaIncluirNomeDoTutorNoEmailDeAprovacao() {
        // ARRANGE
        String nomeTutor = "Maria Santos";
        given(repository.getReferenceById(aprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn(nomeTutor);
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());

        // ACT
        service.aprovar(aprovacaoDto);

        // ASSERT
        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);
        then(emailService).should().enviarEmail(
            anyString(),
            anyString(),
            mensagemCaptor.capture()
        );
        String mensagem = mensagemCaptor.getValue();
        Assertions.assertTrue(mensagem.contains(nomeTutor));
    }

    // ==================== TESTES DE REPROVAÇÃO ====================

    @Test
    void deveriaMarcarAdocaoComoReprovadaComJustificativa() {
        // ARRANGE
        given(repository.getReferenceById(reprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());
        given(adocao.getJustificativaStatus()).willReturn(reprovacaoDto.justificativa());

        // ACT
        service.reprovar(reprovacaoDto);

        // ASSERT
        then(adocao).should().marcarComoReprovada(reprovacaoDto.justificativa());
    }

    @Test
    void deveriaEnviarEmailAoAbrigoAoReprovar() {
        // ARRANGE
        given(repository.getReferenceById(reprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());
        given(adocao.getJustificativaStatus()).willReturn(reprovacaoDto.justificativa());

        // ACT
        service.reprovar(reprovacaoDto);

        // ASSERT
        then(emailService).should().enviarEmail(
            eq("abrigo@email.com"),
            eq("Solicitação de adoção"),
            anyString()
        );
    }

    @Test
    void deveriaBuscarAdocaoPorIdAoReprovar() {
        // ARRANGE
        given(repository.getReferenceById(reprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());
        given(adocao.getJustificativaStatus()).willReturn(reprovacaoDto.justificativa());

        // ACT
        service.reprovar(reprovacaoDto);

        // ASSERT
        then(repository).should().getReferenceById(reprovacaoDto.idAdocao());
    }

    @Test
    void deveriaIncluirJustificativaNoEmailDeReprovacao() {
        // ARRANGE
        String justificativa = "Não possui espaço adequado";
        ReprovacaoAdocaoDto dtoComJustificativa = new ReprovacaoAdocaoDto(1L, justificativa);
        
        given(repository.getReferenceById(dtoComJustificativa.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn("João Silva");
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());
        given(adocao.getJustificativaStatus()).willReturn(justificativa);

        // ACT
        service.reprovar(dtoComJustificativa);

        // ASSERT
        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);
        then(emailService).should().enviarEmail(
            anyString(),
            anyString(),
            mensagemCaptor.capture()
        );
        String mensagem = mensagemCaptor.getValue();
        Assertions.assertTrue(mensagem.contains(justificativa));
    }

    @Test
    void deveriaIncluirNomeDoTutorNoEmailDeReprovacao() {
        // ARRANGE
        String nomeTutor = "Carlos Oliveira";
        given(repository.getReferenceById(reprovacaoDto.idAdocao())).willReturn(adocao);
        given(adocao.getPet()).willReturn(pet);
        given(adocao.getTutor()).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(tutor.getNome()).willReturn(nomeTutor);
        given(pet.getNome()).willReturn("Rex");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(adocao.getData()).willReturn(LocalDateTime.now());
        given(adocao.getJustificativaStatus()).willReturn(reprovacaoDto.justificativa());

        // ACT
        service.reprovar(reprovacaoDto);

        // ASSERT
        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);
        then(emailService).should().enviarEmail(
            anyString(),
            anyString(),
            mensagemCaptor.capture()
        );
        String mensagem = mensagemCaptor.getValue();
        Assertions.assertTrue(mensagem.contains(nomeTutor));
    }

    // ==================== TESTES DE INTEGRAÇÃO ENTRE COMPONENTES ====================

    @Test
    void deveriaExecutarFluxoCompletoAoSolicitar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        given(pet.getAbrigo()).willReturn(abrigo);
        given(abrigo.getEmail()).willReturn("abrigo@email.com");
        given(abrigo.getNome()).willReturn("Abrigo Feliz");
        given(pet.getNome()).willReturn("Rex");
        validacoes.add(validador1);

        // ACT
        service.solicitar(solicitacaoDto);

        // ASSERT
        then(petRepository).should().getReferenceById(solicitacaoDto.idPet());
        then(tutorRepository).should().getReferenceById(solicitacaoDto.idTutor());
        then(validador1).should().validar(solicitacaoDto);
        then(repository).should().save(any(Adocao.class));
        then(emailService).should().enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void naoDeveriaSalvarAdocaoSeValidacaoFalhar() {
        // ARRANGE
        given(petRepository.getReferenceById(solicitacaoDto.idPet())).willReturn(pet);
        given(tutorRepository.getReferenceById(solicitacaoDto.idTutor())).willReturn(tutor);
        validacoes.add(validador1);
        BDDMockito.willThrow(new RuntimeException("Validação falhou"))
            .given(validador1).validar(solicitacaoDto);

        // ACT & ASSERT
        Assertions.assertThrows(RuntimeException.class, () -> service.solicitar(solicitacaoDto));
        then(repository).should(times(0)).save(any(Adocao.class));
    }
}