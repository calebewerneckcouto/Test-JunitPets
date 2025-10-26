package br.com.alura.adopet.api.service;

import static org.mockito.ArgumentMatchers.any;
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

import br.com.alura.adopet.api.dto.AbrigoDto;
import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.TipoPet;
import br.com.alura.adopet.api.repository.AbrigoRepository;
import br.com.alura.adopet.api.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
class AbrigoServiceTest {

    @Mock
    private AbrigoRepository abrigoRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private AbrigoService abrigoService;

    // ==================== TESTES DE LISTAR ABRIGOS ====================

    @Test
    @DisplayName("Deveria retornar lista de todos os abrigos")
    void deveriaRetornarListaDeTodosOsAbrigos() {
        // ARRANGE
        Abrigo abrigo1 = new Abrigo(new CadastroAbrigoDto("Abrigo Feliz", "(11)99999-9999", "feliz@email.com"));
        abrigo1.setId(1L);
        Abrigo abrigo2 = new Abrigo(new CadastroAbrigoDto("Abrigo dos Bichos", "(11)88888-8888", "bichos@email.com"));
        abrigo2.setId(2L);

        List<Abrigo> abrigos = Arrays.asList(abrigo1, abrigo2);
        given(abrigoRepository.findAll()).willReturn(abrigos);

        // ACT
        List<AbrigoDto> resultado = abrigoService.listar();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        AbrigoDto primeiroAbrigo = resultado.get(0);
        assertEquals(1L, primeiroAbrigo.id());
        assertEquals("Abrigo Feliz", primeiroAbrigo.nome());
        assertEquals("(11)99999-9999", primeiroAbrigo.telefone());
        assertEquals("feliz@email.com", primeiroAbrigo.email());

        AbrigoDto segundoAbrigo = resultado.get(1);
        assertEquals(2L, segundoAbrigo.id());
        assertEquals("Abrigo dos Bichos", segundoAbrigo.nome());
        assertEquals("(11)88888-8888", segundoAbrigo.telefone());
        assertEquals("bichos@email.com", segundoAbrigo.email());

        then(abrigoRepository).should().findAll();
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando não houver abrigos")
    void deveriaRetornarListaVaziaQuandoNaoHouverAbrigos() {
        // ARRANGE
        given(abrigoRepository.findAll()).willReturn(Arrays.asList());

        // ACT
        List<AbrigoDto> resultado = abrigoService.listar();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        then(abrigoRepository).should().findAll();
    }

    // ==================== TESTES DE CADASTRAR ABRIGO ====================

    @Test
    @DisplayName("Deveria cadastrar abrigo com sucesso")
    void deveriaCadastrarAbrigoComSucesso() {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Novo Abrigo", 
            "(11)77777-7777", 
            "novo@email.com"
        );

        Abrigo abrigoSalvo = new Abrigo(dto);
        abrigoSalvo.setId(1L);

        given(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
            .willReturn(false);
        given(abrigoRepository.save(any(Abrigo.class))).willReturn(abrigoSalvo);

        // ACT
        AbrigoDto resultado = abrigoService.cadatrar(dto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Novo Abrigo", resultado.nome());
        assertEquals("(11)77777-7777", resultado.telefone());
        assertEquals("novo@email.com", resultado.email());

        then(abrigoRepository).should().existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email());
        then(abrigoRepository).should().save(any(Abrigo.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando abrigo já estiver cadastrado")
    void deveriaLancarExcecaoQuandoAbrigoJaEstiverCadastrado() {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Abrigo Existente", 
            "(11)99999-9999", 
            "existente@email.com"
        );

        given(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            abrigoService.cadatrar(dto);
        });

        assertEquals("Dados já cadastrados para outro abrigo!", exception.getMessage());
        then(abrigoRepository).should().existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email());
        then(abrigoRepository).should(never()).save(any(Abrigo.class));
    }

    @Test
    @DisplayName("Deveria verificar duplicação por nome")
    void deveriaVerificarDuplicacaoPorNome() {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Nome Duplicado", 
            "(11)11111-1111", 
            "diferente@email.com"
        );

        given(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        assertThrows(ValidacaoException.class, () -> abrigoService.cadatrar(dto));
        then(abrigoRepository).should().existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email());
    }

    @Test
    @DisplayName("Deveria verificar duplicação por telefone")
    void deveriaVerificarDuplicacaoPorTelefone() {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Nome Diferente", 
            "(11)99999-9999", 
            "diferente@email.com"
        );

        given(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        assertThrows(ValidacaoException.class, () -> abrigoService.cadatrar(dto));
        then(abrigoRepository).should().existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email());
    }

    @Test
    @DisplayName("Deveria verificar duplicação por email")
    void deveriaVerificarDuplicacaoPorEmail() {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Nome Diferente", 
            "(11)11111-1111", 
            "existente@email.com"
        );

        given(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
            .willReturn(true);

        // ACT & ASSERT
        assertThrows(ValidacaoException.class, () -> abrigoService.cadatrar(dto));
        then(abrigoRepository).should().existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email());
    }

    // ==================== TESTES DE LISTAR PETS DO ABRIGO ====================

    @Test
    @DisplayName("Deveria listar pets de um abrigo por ID")
    void deveriaListarPetsDeUmAbrigoPorId() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo Teste", "(11)99999-9999", "teste@email.com"));
        abrigo.setId(1L);

        Pet pet1 = criarPet(1L, "Rex", TipoPet.CACHORRO, "Vira-lata", 2, abrigo);
        Pet pet2 = criarPet(2L, "Mimi", TipoPet.GATO, "Siamês", 1, abrigo);

        List<Pet> pets = Arrays.asList(pet1, pet2);

        given(abrigoRepository.findById(1L)).willReturn(Optional.of(abrigo));
        given(petRepository.findByAbrigo(abrigo)).willReturn(pets);

        // ACT
        List<PetDto> resultado = abrigoService.listarPetsDoAbrigo("1");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Rex", resultado.get(0).nome());
        assertEquals("Mimi", resultado.get(1).nome());

        then(abrigoRepository).should().findById(1L);
        then(petRepository).should().findByAbrigo(abrigo);
    }

    @Test
    @DisplayName("Deveria listar pets de um abrigo por nome")
    void deveriaListarPetsDeUmAbrigoPorNome() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo por Nome", "(11)99999-9999", "teste@email.com"));
        abrigo.setId(1L);

        Pet pet = criarPet(1L, "Buddy", TipoPet.CACHORRO, "Labrador", 3, abrigo);
        List<Pet> pets = Arrays.asList(pet);

        given(abrigoRepository.findByNome("Abrigo por Nome")).willReturn(Optional.of(abrigo));
        given(petRepository.findByAbrigo(abrigo)).willReturn(pets);

        // ACT
        List<PetDto> resultado = abrigoService.listarPetsDoAbrigo("Abrigo por Nome");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Buddy", resultado.get(0).nome());

        then(abrigoRepository).should().findByNome("Abrigo por Nome");
        then(petRepository).should().findByAbrigo(abrigo);
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando abrigo não tiver pets")
    void deveriaRetornarListaVaziaQuandoAbrigoNaoTiverPets() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo Vazio", "(11)99999-9999", "vazio@email.com"));
        abrigo.setId(1L);

        given(abrigoRepository.findById(1L)).willReturn(Optional.of(abrigo));
        given(petRepository.findByAbrigo(abrigo)).willReturn(Arrays.asList());

        // ACT
        List<PetDto> resultado = abrigoService.listarPetsDoAbrigo("1");

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        then(abrigoRepository).should().findById(1L);
        then(petRepository).should().findByAbrigo(abrigo);
    }

    @Test
    @DisplayName("Deveria lançar exceção quando abrigo não for encontrado na listagem de pets")
    void deveriaLancarExcecaoQuandoAbrigoNaoForEncontradoNaListagemDePets() {
        // ARRANGE
        given(abrigoRepository.findById(999L)).willReturn(Optional.empty());
        given(abrigoRepository.findByNome("Abrigo Inexistente")).willReturn(Optional.empty());

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            abrigoService.listarPetsDoAbrigo("999");
        });

        assertEquals("Abrigo não encontrado", exception.getMessage());

        // Teste com nome também
        ValidacaoException exceptionPorNome = assertThrows(ValidacaoException.class, () -> {
            abrigoService.listarPetsDoAbrigo("Abrigo Inexistente");
        });

        assertEquals("Abrigo não encontrado", exceptionPorNome.getMessage());
    }

    // ==================== TESTES DE CARREGAR ABRIGO ====================

    @Test
    @DisplayName("Deveria carregar abrigo por ID numérico")
    void deveriaCarregarAbrigoPorIdNumerico() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo ID", "(11)99999-9999", "id@email.com"));
        abrigo.setId(1L);

        given(abrigoRepository.findById(1L)).willReturn(Optional.of(abrigo));

        // ACT
        Abrigo resultado = abrigoService.carregarAbrigo("1");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Abrigo ID", resultado.getNome());
        then(abrigoRepository).should().findById(1L);
        then(abrigoRepository).should(never()).findByNome(anyString());
    }

    @Test
    @DisplayName("Deveria carregar abrigo por nome")
    void deveriaCarregarAbrigoPorNome() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo por Nome", "(11)99999-9999", "nome@email.com"));
        abrigo.setId(2L);

        given(abrigoRepository.findByNome("Abrigo por Nome")).willReturn(Optional.of(abrigo));

        // ACT
        Abrigo resultado = abrigoService.carregarAbrigo("Abrigo por Nome");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals("Abrigo por Nome", resultado.getNome());
        then(abrigoRepository).should().findByNome("Abrigo por Nome");
        then(abrigoRepository).should(never()).findById(any(Long.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando ID não for encontrado")
    void deveriaLancarExcecaoQuandoIdNaoForEncontrado() {
        // ARRANGE
        given(abrigoRepository.findById(999L)).willReturn(Optional.empty());

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            abrigoService.carregarAbrigo("999");
        });

        assertEquals("Abrigo não encontrado", exception.getMessage());
        then(abrigoRepository).should().findById(999L);
    }

    @Test
    @DisplayName("Deveria lançar exceção quando nome não for encontrado")
    void deveriaLancarExcecaoQuandoNomeNaoForEncontrado() {
        // ARRANGE
        given(abrigoRepository.findByNome("Nome Inexistente")).willReturn(Optional.empty());

        // ACT & ASSERT
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> {
            abrigoService.carregarAbrigo("Nome Inexistente");
        });

        assertEquals("Abrigo não encontrado", exception.getMessage());
        then(abrigoRepository).should().findByNome("Nome Inexistente");
    }

    @Test
    @DisplayName("Deveria tratar NumberFormatException e buscar por nome")
    void deveriaTratarNumberFormatExceptionEBuscarPorNome() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo com ID Inválido", "(11)99999-9999", "invalido@email.com"));
        abrigo.setId(3L);

        given(abrigoRepository.findByNome("123abc")).willReturn(Optional.of(abrigo));

        // ACT
        Abrigo resultado = abrigoService.carregarAbrigo("123abc");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Abrigo com ID Inválido", resultado.getNome());
        then(abrigoRepository).should().findByNome("123abc");
    }

    // ==================== TESTES DE INTEGRAÇÃO ENTRE MÉTODOS ====================

    @Test
    @DisplayName("Deveria usar abrigo carregado para listar pets")
    void deveriaUsarAbrigoCarregadoParaListarPets() {
        // ARRANGE
        Abrigo abrigo = new Abrigo(new CadastroAbrigoDto("Abrigo Integrado", "(11)99999-9999", "integrado@email.com"));
        abrigo.setId(1L);

        Pet pet = criarPet(1L, "Pet Integrado", TipoPet.CACHORRO, "SRD", 1, abrigo);

        given(abrigoRepository.findById(1L)).willReturn(Optional.of(abrigo));
        given(petRepository.findByAbrigo(abrigo)).willReturn(Arrays.asList(pet));

        // ACT
        List<PetDto> resultado = abrigoService.listarPetsDoAbrigo("1");

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("Pet Integrado", resultado.get(0).nome());
        then(abrigoRepository).should().findById(1L);
        then(petRepository).should().findByAbrigo(abrigo);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Pet criarPet(Long id, String nome, TipoPet tipo, String raca, Integer idade, Abrigo abrigo) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setNome(nome);
        pet.setTipo(tipo);
        pet.setRaca(raca);
        pet.setIdade(idade);
        pet.setAbrigo(abrigo);
        return pet;
    }
}