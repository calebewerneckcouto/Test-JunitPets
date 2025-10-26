package br.com.alura.adopet.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.adopet.api.dto.AbrigoDto;
import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.dto.CadastroPetDto;
import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.TipoPet;
import br.com.alura.adopet.api.service.AbrigoService;
import br.com.alura.adopet.api.service.PetService;

@WebMvcTest(AbrigoController.class)
@AutoConfigureMockMvc
class AbrigoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AbrigoService abrigoService;

    @MockBean
    private PetService petService;

    @MockBean
    private Abrigo abrigo;

    private final AbrigoDto abrigoDto1 = new AbrigoDto(1L, "Abrigo Feliz", "(11)99999-9999", "abrigo.feliz@email.com");
    private final AbrigoDto abrigoDto2 = new AbrigoDto(2L, "Abrigo dos Bichinhos", "(11)88888-8888", "bichinhos@email.com");
    
    private final PetDto petDto1 = new PetDto(1L, TipoPet.CACHORRO, "Rex", "Vira-lata", 2);
    private final PetDto petDto2 = new PetDto(2L, TipoPet.GATO, "Mimi", "Siamês", 1);

    // ==================== TESTES DE LISTAGEM DE ABRIGOS ====================

    @Test
    @DisplayName("Deveria retornar lista de todos os abrigos com sucesso")
    void deveriaRetornarListaDeAbrigos() throws Exception {
        // ARRANGE
        List<AbrigoDto> abrigos = Arrays.asList(abrigoDto1, abrigoDto2);
        given(abrigoService.listar()).willReturn(abrigos);

        // ACT & ASSERT
        mvc.perform(get("/abrigos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Abrigo Feliz"))
            .andExpect(jsonPath("$[0].telefone").value("(11)99999-9999"))
            .andExpect(jsonPath("$[0].email").value("abrigo.feliz@email.com"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].nome").value("Abrigo dos Bichinhos"))
            .andExpect(jsonPath("$[1].telefone").value("(11)88888-8888"))
            .andExpect(jsonPath("$[1].email").value("bichinhos@email.com"));
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando não houver abrigos")
    void deveriaRetornarListaVaziaDeAbrigos() throws Exception {
        // ARRANGE
        given(abrigoService.listar()).willReturn(Arrays.asList());

        // ACT & ASSERT
        mvc.perform(get("/abrigos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    // ==================== TESTES DE CADASTRO DE ABRIGO ====================

    @Test
    @DisplayName("Deveria cadastrar abrigo com sucesso")
    void deveriaCadastrarAbrigoComSucesso() throws Exception {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Novo Abrigo", 
            "(11)77777-7777", 
            "novo.abrigo@email.com"
        );
        
        AbrigoDto abrigoRetorno = new AbrigoDto(3L, "Novo Abrigo", "(11)77777-7777", "novo.abrigo@email.com");
        given(abrigoService.cadatrar(any(CadastroAbrigoDto.class))).willReturn(abrigoRetorno);

        // ACT & ASSERT
        mvc.perform(post("/abrigos")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.nome").value("Novo Abrigo"))
            .andExpect(jsonPath("$.telefone").value("(11)77777-7777"))
            .andExpect(jsonPath("$.email").value("novo.abrigo@email.com"));

        then(abrigoService).should().cadatrar(any(CadastroAbrigoDto.class));
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando dados de cadastro forem inválidos")
    void deveriaRetornarErro400ParaCadastroComDadosInvalidos() throws Exception {
        // ARRANGE
        String jsonInvalido = """
            {
                "nome": "",
                "telefone": "123",
                "email": "email-invalido"
            }
            """;

        // ACT & ASSERT
        mvc.perform(post("/abrigos")
                .content(jsonInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando service lançar ValidacaoException no cadastro")
    void deveriaRetornarErro400QuandoServiceLancarExcecaoNoCadastro() throws Exception {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Abrigo Existente", 
            "(11)99999-9999", 
            "existente@email.com"
        );
        String mensagemErro = "Já existe um abrigo com este nome ou email";
        
        given(abrigoService.cadatrar(any(CadastroAbrigoDto.class)))
            .willThrow(new ValidacaoException(mensagemErro));

        // ACT & ASSERT
        mvc.perform(post("/abrigos")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(mensagemErro));
    }

    // ==================== TESTES DE LISTAGEM DE PETS ====================

    @Test
    @DisplayName("Deveria listar pets de um abrigo por ID com sucesso")
    void deveriaListarPetsPorIdComSucesso() throws Exception {
        // ARRANGE
        List<PetDto> pets = Arrays.asList(petDto1, petDto2);
        given(abrigoService.listarPetsDoAbrigo("1")).willReturn(pets);

        // ACT & ASSERT
        mvc.perform(get("/abrigos/{idOuNome}/pets", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Rex"))
            .andExpect(jsonPath("$[0].tipo").value("CACHORRO"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].nome").value("Mimi"))
            .andExpect(jsonPath("$[1].tipo").value("GATO"));
    }

    @Test
    @DisplayName("Deveria listar pets de um abrigo por nome com sucesso")
    void deveriaListarPetsPorNomeComSucesso() throws Exception {
        // ARRANGE
        List<PetDto> pets = Arrays.asList(petDto1);
        given(abrigoService.listarPetsDoAbrigo("Abrigo Feliz")).willReturn(pets);

        // ACT & ASSERT
        mvc.perform(get("/abrigos/{idOuNome}/pets", "Abrigo Feliz")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Rex"));
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando abrigo não tiver pets")
    void deveriaRetornarListaVaziaDePets() throws Exception {
        // ARRANGE
        given(abrigoService.listarPetsDoAbrigo("1")).willReturn(Arrays.asList());

        // ACT & ASSERT
        mvc.perform(get("/abrigos/{idOuNome}/pets", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Deveria retornar 404 quando abrigo não for encontrado na listagem de pets")
    void deveriaRetornar404QuandoAbrigoNaoEncontradoNaListagem() throws Exception {
        // ARRANGE
        given(abrigoService.listarPetsDoAbrigo("999"))
            .willThrow(new ValidacaoException("Abrigo não encontrado"));

        // ACT & ASSERT
        mvc.perform(get("/abrigos/{idOuNome}/pets", "999")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // ==================== TESTES DE CADASTRO DE PET ====================

    @Test
    @DisplayName("Deveria cadastrar pet em abrigo por ID com sucesso")
    void deveriaCadastrarPetPorIdComSucesso() throws Exception {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO, 
            "Buddy", 
            "Labrador", 
            3, 
            "Dourado", 
            25.0f
        );
        
        given(abrigoService.carregarAbrigo("1")).willReturn(abrigo);

        // ACT & ASSERT
        mvc.perform(post("/abrigos/{idOuNome}/pets", "1")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(abrigoService).should().carregarAbrigo("1");
        then(petService).should().cadastrarPet(eq(abrigo), any(CadastroPetDto.class));
    }

    @Test
    @DisplayName("Deveria cadastrar pet em abrigo por nome com sucesso")
    void deveriaCadastrarPetPorNomeComSucesso() throws Exception {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.GATO, 
            "Luna", 
            "Persa", 
            2, 
            "Branco", 
            4.5f
        );
        
        given(abrigoService.carregarAbrigo("Abrigo Feliz")).willReturn(abrigo);

        // ACT & ASSERT
        mvc.perform(post("/abrigos/{idOuNome}/pets", "Abrigo Feliz")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(abrigoService).should().carregarAbrigo("Abrigo Feliz");
        then(petService).should().cadastrarPet(eq(abrigo), any(CadastroPetDto.class));
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando dados do pet forem inválidos")
    void deveriaRetornarErro400ParaCadastroPetComDadosInvalidos() throws Exception {
        // ARRANGE
        String jsonInvalido = """
            {
                "tipo": null,
                "nome": "",
                "raca": "",
                "idade": -1,
                "cor": "",
                "peso": 0
            }
            """;

        // ACT & ASSERT
        mvc.perform(post("/abrigos/{idOuNome}/pets", "1")
                .content(jsonInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria retornar 404 quando abrigo não for encontrado no cadastro de pet")
    void deveriaRetornar404QuandoAbrigoNaoEncontradoNoCadastroPet() throws Exception {
        // ARRANGE
        CadastroPetDto dto = new CadastroPetDto(
            TipoPet.CACHORRO, 
            "Rex", 
            "Vira-lata", 
            2, 
            "Caramelo", 
            15.0f
        );
        
        given(abrigoService.carregarAbrigo("999"))
            .willThrow(new ValidacaoException("Abrigo não encontrado"));

        // ACT & ASSERT
        mvc.perform(post("/abrigos/{idOuNome}/pets", "999")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deveria aceitar diferentes formatos de ID ou nome do abrigo")
    void deveriaAceitarDiferentesFormatosDeIdOuNome() throws Exception {
        // ARRANGE
        List<PetDto> pets = Arrays.asList(petDto1);
        
        // Teste com ID numérico
        given(abrigoService.listarPetsDoAbrigo("123")).willReturn(pets);
        
        // ACT & ASSERT para ID numérico
        mvc.perform(get("/abrigos/{idOuNome}/pets", "123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Teste com nome com espaços
        given(abrigoService.listarPetsDoAbrigo("Abrigo dos Bichinhos")).willReturn(pets);
        
        // ACT & ASSERT para nome com espaços
        mvc.perform(get("/abrigos/{idOuNome}/pets", "Abrigo dos Bichinhos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria retornar informações completas do abrigo no cadastro")
    void deveriaRetornarInformacoesCompletasDoAbrigoNoCadastro() throws Exception {
        // ARRANGE
        CadastroAbrigoDto dto = new CadastroAbrigoDto(
            "Abrigo Completo", 
            "(11)55555-5555", 
            "completo@email.com"
        );
        
        AbrigoDto abrigoRetorno = new AbrigoDto(4L, "Abrigo Completo", "(11)55555-5555", "completo@email.com");
        given(abrigoService.cadatrar(any(CadastroAbrigoDto.class))).willReturn(abrigoRetorno);

        // ACT & ASSERT
        mvc.perform(post("/abrigos")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.nome").exists())
            .andExpect(jsonPath("$.telefone").exists())
            .andExpect(jsonPath("$.email").exists());
    }
}