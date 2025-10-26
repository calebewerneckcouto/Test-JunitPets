package br.com.alura.adopet.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.alura.adopet.api.dto.AtualizacaoTutorDto;
import br.com.alura.adopet.api.dto.CadastroTutorDto;
import br.com.alura.adopet.api.dto.TutorDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private TutorService tutorService;

    // ==================== TESTES DE LISTAR TUTORES ====================

    @Test
    @DisplayName("Deveria retornar página de tutores com sucesso")
    void deveriaRetornarPaginaDeTutoresComSucesso() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(0, 10);
        
        Tutor tutor1 = criarTutor(1L, "João Silva", "(11)99999-9999", "joao@email.com");
        Tutor tutor2 = criarTutor(2L, "Maria Santos", "(11)88888-8888", "maria@email.com");
        
        List<Tutor> tutores = Arrays.asList(tutor1, tutor2);
        long total = 2L;

        given(tutorRepository.findAllWithPagination(0, 10)).willReturn(tutores);
        given(tutorRepository.count()).willReturn(total);

        // ACT
        Page<TutorDto> resultado = tutorService.listar(paginacao);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals(2, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
        assertEquals(0, resultado.getNumber());
        
        TutorDto primeiroTutor = resultado.getContent().get(0);
        assertEquals(1L, primeiroTutor.id());
        assertEquals("João Silva", primeiroTutor.nome());
        assertEquals("(11)99999-9999", primeiroTutor.telefone());
        assertEquals("joao@email.com", primeiroTutor.email());

        TutorDto segundoTutor = resultado.getContent().get(1);
        assertEquals(2L, segundoTutor.id());
        assertEquals("Maria Santos", segundoTutor.nome());
        assertEquals("(11)88888-8888", segundoTutor.telefone());
        assertEquals("maria@email.com", segundoTutor.email());

        then(tutorRepository).should().findAllWithPagination(0, 10);
        then(tutorRepository).should().count();
    }

    @Test
    @DisplayName("Deveria retornar página vazia quando não houver tutores")
    void deveriaRetornarPaginaVaziaQuandoNaoHouverTutores() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(0, 10);
        
        given(tutorRepository.findAllWithPagination(0, 10)).willReturn(Arrays.asList());
        given(tutorRepository.count()).willReturn(0L);

        // ACT
        Page<TutorDto> resultado = tutorService.listar(paginacao);

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
        assertEquals(0, resultado.getTotalPages());
        
        then(tutorRepository).should().findAllWithPagination(0, 10);
        then(tutorRepository).should().count();
    }

    @Test
    @DisplayName("Deveria calcular offset corretamente para diferentes páginas")
    void deveriaCalcularOffsetCorretamenteParaDiferentesPaginas() {
        // ARRANGE
        Pageable primeiraPagina = PageRequest.of(0, 5);
        Pageable segundaPagina = PageRequest.of(1, 5);
        Pageable terceiraPagina = PageRequest.of(2, 5);

        given(tutorRepository.findAllWithPagination(0, 5)).willReturn(Arrays.asList());
        given(tutorRepository.findAllWithPagination(5, 5)).willReturn(Arrays.asList());
        given(tutorRepository.findAllWithPagination(10, 5)).willReturn(Arrays.asList());
        given(tutorRepository.count()).willReturn(15L);

        // ACT & ASSERT - Primeira página
        Page<TutorDto> resultado1 = tutorService.listar(primeiraPagina);
        assertEquals(0, resultado1.getNumber());

        // Segunda página
        Page<TutorDto> resultado2 = tutorService.listar(segundaPagina);
        assertEquals(1, resultado2.getNumber());

        // Terceira página
        Page<TutorDto> resultado3 = tutorService.listar(terceiraPagina);
        assertEquals(2, resultado3.getNumber());

        then(tutorRepository).should(times(1)).findAllWithPagination(0, 5);
        then(tutorRepository).should(times(1)).findAllWithPagination(5, 5);
        then(tutorRepository).should(times(1)).findAllWithPagination(10, 5);
        then(tutorRepository).should(times(3)).count();
    }

    // ==================== TESTES DE CADASTRAR TUTOR ====================

    @Test
    @DisplayName("Deveria cadastrar tutor com sucesso")
    void deveriaCadastrarTutorComSucesso() {
        // ARRANGE
        CadastroTutorDto dto = new CadastroTutorDto(
            "Carlos Oliveira", 
            "(11)77777-7777", 
            "carlos@email.com"
        );

        given(tutorRepository.existsByTelefoneOrEmail(dto.telefone(), dto.email()))
            .willReturn(false);

        // ACT
        tutorService.cadastrar(dto);

        // ASSERT
        then(tutorRepository).should().existsByTelefoneOrEmail(dto.telefone(), dto.email());
        then(tutorRepository).should().save(any(Tutor.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando tutor já estiver cadastrado por telefone")
    void deveriaLancarExcecaoQuandoTutorJaEstiverCadastradoPorTelefone() {
        // ARRANGE
        CadastroTutorDto dto = new CadastroTutorDto(
            "Novo Tutor", 
            "(11)99999-9999", // Telefone já cadastrado
            "novo@email.com"
        );

        given(tutorRepository.existsByTelefoneOrEmail(dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tutorService.cadastrar(dto);
        });

        assertEquals("Dados já cadastrados para outro tutor!", exception.getMessage());
        then(tutorRepository).should().existsByTelefoneOrEmail(dto.telefone(), dto.email());
        then(tutorRepository).should(never()).save(any(Tutor.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando tutor já estiver cadastrado por email")
    void deveriaLancarExcecaoQuandoTutorJaEstiverCadastradoPorEmail() {
        // ARRANGE
        CadastroTutorDto dto = new CadastroTutorDto(
            "Novo Tutor", 
            "(11)11111-1111", 
            "existente@email.com" // Email já cadastrado
        );

        given(tutorRepository.existsByTelefoneOrEmail(dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tutorService.cadastrar(dto);
        });

        assertEquals("Dados já cadastrados para outro tutor!", exception.getMessage());
        then(tutorRepository).should().existsByTelefoneOrEmail(dto.telefone(), dto.email());
        then(tutorRepository).should(never()).save(any(Tutor.class));
    }

    // ==================== TESTES DE ATUALIZAR TUTOR ====================

    @Test
    @DisplayName("Deveria atualizar tutor com sucesso")
    void deveriaAtualizarTutorComSucesso() {
        // ARRANGE
        AtualizacaoTutorDto dto = new AtualizacaoTutorDto(
            1L, 
            "João Silva Atualizado", 
            "(11)98888-8888", 
            "joao.novo@email.com"
        );

        Tutor tutorExistente = criarTutor(1L, "João Silva", "(11)99999-9999", "joao@email.com");
        
        given(tutorRepository.findById(1L)).willReturn(Optional.of(tutorExistente));

        // ACT
        tutorService.atualizar(dto);

        // ASSERT
        then(tutorRepository).should().findById(1L);
        // Verifica se o método atualizarDados foi chamado (implícito pelo mock)
    }

    @Test
    @DisplayName("Deveria lançar exceção quando tutor não for encontrado na atualização")
    void deveriaLancarExcecaoQuandoTutorNaoForEncontradoNaAtualizacao() {
        // ARRANGE
        AtualizacaoTutorDto dto = new AtualizacaoTutorDto(
            999L, 
            "Tutor Inexistente", 
            "(11)99999-9999", 
            "inexistente@email.com"
        );

        given(tutorRepository.findById(999L)).willReturn(Optional.empty());

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tutorService.atualizar(dto);
        });

        assertEquals("Tutor não encontrado!", exception.getMessage());
        then(tutorRepository).should().findById(999L);
    }

    // ==================== TESTES DE EXCLUIR TUTOR ====================

    @Test
    @DisplayName("Deveria excluir tutor com sucesso quando não possui pets")
    void deveriaExcluirTutorComSucessoQuandoNaoPossuiPets() {
        // ARRANGE
        Long idTutor = 1L;
        Tutor tutor = criarTutor(1L, "Tutor para Excluir", "(11)99999-9999", "excluir@email.com");
        // A lista de pets está vazia por padrão, então possuiPets() retorna false
        
        given(tutorRepository.findById(idTutor)).willReturn(Optional.of(tutor));

        // ACT
        tutorService.excluir(idTutor);

        // ASSERT
        then(tutorRepository).should().findById(idTutor);
        then(tutorRepository).should().delete(tutor);
    }

    @Test
    @DisplayName("Deveria lançar exceção quando tutor não for encontrado na exclusão")
    void deveriaLancarExcecaoQuandoTutorNaoForEncontradoNaExclusao() {
        // ARRANGE
        Long idTutor = 999L;
        given(tutorRepository.findById(idTutor)).willReturn(Optional.empty());

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tutorService.excluir(idTutor);
        });

        assertEquals("Tutor não encontrado!", exception.getMessage());
        then(tutorRepository).should().findById(idTutor);
        then(tutorRepository).should(never()).delete(any(Tutor.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando tutor possuir pets associados")
    void deveriaLancarExcecaoQuandoTutorPossuirPetsAssociados() {
        // ARRANGE
        Long idTutor = 1L;
        Tutor tutor = criarTutorComPets(idTutor, "Tutor com Pets", "(11)99999-9999", "compets@email.com");
        
        given(tutorRepository.findById(idTutor)).willReturn(Optional.of(tutor));

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            tutorService.excluir(idTutor);
        });

        assertEquals("Não é possível excluir tutor com pets associados!", exception.getMessage());
        then(tutorRepository).should().findById(idTutor);
        then(tutorRepository).should(never()).delete(any(Tutor.class));
    }

    // ==================== TESTES DE VALIDAÇÃO DE DADOS ====================

    @Test
    @DisplayName("Deveria converter corretamente todos os campos para DTO")
    void deveriaConverterCorretamenteTodosOsCamposParaDto() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(0, 10);
        
        Tutor tutor = criarTutor(1L, "Tutor Completo", "(11)99999-9999", "completo@email.com");

        given(tutorRepository.findAllWithPagination(0, 10)).willReturn(Arrays.asList(tutor));
        given(tutorRepository.count()).willReturn(1L);

        // ACT
        Page<TutorDto> resultado = tutorService.listar(paginacao);

        // ASSERT
        TutorDto tutorDto = resultado.getContent().get(0);
        assertEquals(1L, tutorDto.id());
        assertEquals("Tutor Completo", tutorDto.nome());
        assertEquals("(11)99999-9999", tutorDto.telefone());
        assertEquals("completo@email.com", tutorDto.email());
        
        then(tutorRepository).should().findAllWithPagination(0, 10);
        then(tutorRepository).should().count();
    }

    @Test
    @DisplayName("Deveria lidar com paginação de página única")
    void deveriaLidarComPaginacaoDePaginaUnica() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(0, 5);
        
        List<Tutor> tutores = Arrays.asList(
            criarTutor(1L, "Tutor 1", "(11)11111-1111", "tutor1@email.com"),
            criarTutor(2L, "Tutor 2", "(11)22222-2222", "tutor2@email.com"),
            criarTutor(3L, "Tutor 3", "(11)33333-3333", "tutor3@email.com")
        );

        given(tutorRepository.findAllWithPagination(0, 5)).willReturn(tutores);
        given(tutorRepository.count()).willReturn(3L);

        // ACT
        Page<TutorDto> resultado = tutorService.listar(paginacao);

        // ASSERT
        assertEquals(3, resultado.getContent().size());
        assertEquals(3, resultado.getTotalElements());
        assertEquals(1, resultado.getTotalPages());
        assertEquals(0, resultado.getNumber());
        
        then(tutorRepository).should().findAllWithPagination(0, 5);
        then(tutorRepository).should().count();
    }

    @Test
    @DisplayName("Deveria lidar com paginação de múltiplas páginas")
    void deveriaLidarComPaginacaoDeMultiplasPaginas() {
        // ARRANGE
        Pageable paginacao = PageRequest.of(1, 2); // Segunda página, 2 itens por página
        
        List<Tutor> tutoresPagina2 = Arrays.asList(
            criarTutor(3L, "Tutor 3", "(11)33333-3333", "tutor3@email.com"),
            criarTutor(4L, "Tutor 4", "(11)44444-4444", "tutor4@email.com")
        );

        given(tutorRepository.findAllWithPagination(2, 2)).willReturn(tutoresPagina2);
        given(tutorRepository.count()).willReturn(5L);

        // ACT
        Page<TutorDto> resultado = tutorService.listar(paginacao);

        // ASSERT
        assertEquals(2, resultado.getContent().size());
        assertEquals(5, resultado.getTotalElements());
        assertEquals(3, resultado.getTotalPages()); // 5 elementos / 2 por página = 3 páginas
        assertEquals(1, resultado.getNumber()); // Segunda página (index 1)
        
        then(tutorRepository).should().findAllWithPagination(2, 2);
        then(tutorRepository).should().count();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Tutor criarTutor(Long id, String nome, String telefone, String email) {
        Tutor tutor = new Tutor(new CadastroTutorDto(nome, telefone, email));
        tutor.setId(id);
        return tutor;
    }

    private Tutor criarTutorComPets(Long id, String nome, String telefone, String email) {
        Tutor tutor = criarTutor(id, nome, telefone, email);
        
        // Criar um pet mock e adicionar à lista de pets do tutor
        Pet pet = new Pet();
        pet.setId(1L);
        tutor.getPets().add(pet); // Isso fará com que possuiPets() retorne true
        
        return tutor;
    }
}