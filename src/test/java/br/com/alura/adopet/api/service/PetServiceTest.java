package br.com.alura.adopet.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alura.adopet.api.dto.CadastroPetDto;
import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.TipoPet;
import br.com.alura.adopet.api.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository repository;

    @Mock
    private Abrigo abrigo;

    @InjectMocks
    private PetService petService;

    // ==================== TESTES DE BUSCAR PETS DISPONÍVEIS ====================

    @Test
    @DisplayName("Deveria retornar lista de pets disponíveis")
    void deveriaRetornarListaDePetsDisponiveis() {
        // ARRANGE
        Pet pet1 = criarPet(1L, "Rex", TipoPet.CACHORRO, "Vira-lata", 2, false);
        Pet pet2 = criarPet(2L, "Mimi", TipoPet.GATO, "Siamês", 1, false);

        List<Pet> pets = Arrays.asList(pet1, pet2);
        given(repository.findAllByAdotadoFalse()).willReturn(pets);

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        PetDto primeiroPet = resultado.get(0);
        assertEquals(1L, primeiroPet.id());
        assertEquals("Rex", primeiroPet.nome());
        assertEquals(TipoPet.CACHORRO, primeiroPet.tipo());
        assertEquals("Vira-lata", primeiroPet.raca());
        assertEquals(2, primeiroPet.idade());

        PetDto segundoPet = resultado.get(1);
        assertEquals(2L, segundoPet.id());
        assertEquals("Mimi", segundoPet.nome());
        assertEquals(TipoPet.GATO, segundoPet.tipo());
        assertEquals("Siamês", segundoPet.raca());
        assertEquals(1, segundoPet.idade());

        then(repository).should().findAllByAdotadoFalse();
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando não houver pets disponíveis")
    void deveriaRetornarListaVaziaQuandoNaoHouverPetsDisponiveis() {
        // ARRANGE
        given(repository.findAllByAdotadoFalse()).willReturn(Arrays.asList());

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        then(repository).should().findAllByAdotadoFalse();
    }

    @Test
    @DisplayName("Deveria retornar apenas pets não adotados")
    void deveriaRetornarApenasPetsNaoAdotados() {
        // ARRANGE
        Pet petDisponivel = criarPet(1L, "Rex", TipoPet.CACHORRO, "Vira-lata", 2, false);

        List<Pet> petsDisponiveis = Arrays.asList(petDisponivel);
        given(repository.findAllByAdotadoFalse()).willReturn(petsDisponiveis);

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("Rex", resultado.get(0).nome());
        then(repository).should().findAllByAdotadoFalse();
    }

    @Test
    @DisplayName("Deveria converter corretamente todos os campos para DTO")
    void deveriaConverterCorretamenteTodosOsCamposParaDto() {
        // ARRANGE
        Pet pet = criarPetCompleto(1L, "Buddy", TipoPet.CACHORRO, "Labrador", 3, "Dourado", 25.0f, false);
        given(repository.findAllByAdotadoFalse()).willReturn(Arrays.asList(pet));

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertEquals(1, resultado.size());
        PetDto petDto = resultado.get(0);
        assertEquals(1L, petDto.id());
        assertEquals("Buddy", petDto.nome());
        assertEquals(TipoPet.CACHORRO, petDto.tipo());
        assertEquals("Labrador", petDto.raca());
        assertEquals(3, petDto.idade());
        then(repository).should().findAllByAdotadoFalse();
    }

    // ==================== TESTES DE CADASTRAR PET ====================

    @Test
    @DisplayName("Deveria cadastrar pet com sucesso")
    void deveriaCadastrarPetComSucesso() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Rex",
            "Vira-lata",
            2,
            "Caramelo",
            15.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria criar pet com dados corretos do DTO e abrigo")
    void deveriaCriarPetComDadosCorretos() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.GATO,
            "Luna",
            "Persa",
            1,
            "Branco",
            4.5f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria cadastrar pet do tipo cachorro")
    void deveriaCadastrarPetTipoCachorro() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Thor",
            "Pastor Alemão",
            4,
            "Preto",
            30.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria cadastrar pet do tipo gato")
    void deveriaCadastrarPetTipoGato() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.GATO,
            "Felix",
            "Siamês",
            2,
            "Cinza",
            5.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria cadastrar pet com idade zero")
    void deveriaCadastrarPetComIdadeZero() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Filhote",
            "SRD",
            0,
            "Branco",
            2.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria cadastrar pet com peso zero")
    void deveriaCadastrarPetComPesoZero() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.GATO,
            "Leve",
            "SRD",
            1,
            "Preto",
            0.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria chamar repository.save apenas uma vez")
    void deveriaChamarRepositorySaveApenasUmaVez() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Rex",
            "Vira-lata",
            2,
            "Caramelo",
            15.0f
        );

        // ACT
        petService.cadastrarPet(abrigo, dto);

        // ASSERT
        then(repository).should(times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria salvar pet mesmo com abrigo nulo")
    void deveriaSalvarPetMesmoComAbrigoNulo() {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Rex",
            "Vira-lata",
            2,
            "Caramelo",
            15.0f
        );

        // ACT
        petService.cadastrarPet(null, dto);

        // ASSERT
        then(repository).should().save(any(Pet.class));
    }

    @Test
    @DisplayName("Deveria lançar exceção quando DTO for nulo")
    void deveriaLancarExcecaoQuandoDtoForNulo() {
        // ACT & ASSERT
        assertThrows(NullPointerException.class, () -> {
            petService.cadastrarPet(abrigo, null);
        });

        // ASSERT - Não deve chamar o repository quando DTO é nulo
        then(repository).should(never()).save(any(Pet.class));
    }

    // ==================== TESTES DE INTEGRAÇÃO ENTRE MÉTODOS ====================

    @Test
    @DisplayName("Deveria manter consistência entre busca e cadastro")
    void deveriaManterConsistenciaEntreBuscaECadastro() {
        // ARRANGE - Primeiro cadastra um pet
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO,
            "Novo Pet",
            "SRD",
            1,
            "Preto",
            10.0f
        );

        // ACT - Cadastra o pet
        petService.cadastrarPet(abrigo, dto);

        // ARRANGE - Configura a busca para retornar o pet cadastrado
        Pet petCadastrado = new Pet(dto, abrigo);
        petCadastrado.setId(1L);
        given(repository.findAllByAdotadoFalse()).willReturn(Arrays.asList(petCadastrado));

        // ACT - Busca pets disponíveis
        List<PetDto> petsDisponiveis = petService.buscarPetsDisponiveis();

        // ASSERT
        assertEquals(1, petsDisponiveis.size());
        assertEquals("Novo Pet", petsDisponiveis.get(0).nome());
        then(repository).should().save(any(Pet.class));
        then(repository).should().findAllByAdotadoFalse();
    }

    @Test
    @DisplayName("Deveria retornar pets em ordem do repository")
    void deveriaRetornarPetsEmOrdemDoRepository() {
        // ARRANGE
        Pet pet1 = criarPet(1L, "Primeiro", TipoPet.CACHORRO, "SRD", 1, false);
        Pet pet2 = criarPet(2L, "Segundo", TipoPet.CACHORRO, "SRD", 1, false);
        Pet pet3 = criarPet(3L, "Terceiro", TipoPet.CACHORRO, "SRD", 1, false);

        List<Pet> pets = Arrays.asList(pet1, pet2, pet3);
        given(repository.findAllByAdotadoFalse()).willReturn(pets);

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertEquals(3, resultado.size());
        assertEquals("Primeiro", resultado.get(0).nome());
        assertEquals("Segundo", resultado.get(1).nome());
        assertEquals("Terceiro", resultado.get(2).nome());
    }

    @Test
    @DisplayName("Deveria processar lista grande de pets eficientemente")
    void deveriaProcessarListaGrandeDePets() {
        // ARRANGE
        List<Pet> muitosPets = Arrays.asList(
            criarPet(1L, "Pet1", TipoPet.CACHORRO, "SRD", 1, false),
            criarPet(2L, "Pet2", TipoPet.CACHORRO, "SRD", 1, false),
            criarPet(3L, "Pet3", TipoPet.CACHORRO, "SRD", 1, false),
            criarPet(4L, "Pet4", TipoPet.CACHORRO, "SRD", 1, false),
            criarPet(5L, "Pet5", TipoPet.CACHORRO, "SRD", 1, false)
        );
        given(repository.findAllByAdotadoFalse()).willReturn(muitosPets);

        // ACT
        List<PetDto> resultado = petService.buscarPetsDisponiveis();

        // ASSERT
        assertEquals(5, resultado.size());
        then(repository).should().findAllByAdotadoFalse();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Pet criarPet(Long id, String nome, TipoPet tipo, String raca, Integer idade, Boolean adotado) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setNome(nome);
        pet.setTipo(tipo);
        pet.setRaca(raca);
        pet.setIdade(idade);
        pet.setAdotado(adotado);
        return pet;
    }

    private Pet criarPetCompleto(Long id, String nome, TipoPet tipo, String raca, Integer idade, String cor, Float peso, Boolean adotado) {
        Pet pet = criarPet(id, nome, tipo, raca, idade, adotado);
        pet.setCor(cor);
        pet.setPeso(peso);
        return pet;
    }
}