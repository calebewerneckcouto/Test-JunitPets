package br.com.alura.adopet.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alura.adopet.api.dto.AtualizacaoTutorDto;
import br.com.alura.adopet.api.dto.CadastroTutorDto;
import br.com.alura.adopet.api.dto.TutorDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.service.TutorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TutorController.class)
@AutoConfigureMockMvc
class TutorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TutorService tutorService;

    // CORRIGIDO: Campos na ordem correta - id, nome, telefone, email
    private final TutorDto tutorDto1 = new TutorDto(1L, "João Silva", "(11)99999-9999", "joao@email.com");
    private final TutorDto tutorDto2 = new TutorDto(2L, "Maria Santos", "(11)88888-8888", "maria@email.com");

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("Deveria retornar lista paginada de tutores com sucesso")
    void deveriaRetornarListaPaginadaDeTutores() throws Exception {
        // ARRANGE
        Page<TutorDto> paginaTutores = new PageImpl<>(
            List.of(tutorDto1, tutorDto2),
            PageRequest.of(0, 10),
            2
        );
        given(tutorService.listar(any(Pageable.class))).willReturn(paginaTutores);

        // ACT & ASSERT
        mvc.perform(get("/tutores")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].nome").value("João Silva"))
            .andExpect(jsonPath("$.content[0].telefone").value("(11)99999-9999"))
            .andExpect(jsonPath("$.content[0].email").value("joao@email.com"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].nome").value("Maria Santos"))
            .andExpect(jsonPath("$.content[1].telefone").value("(11)88888-8888"))
            .andExpect(jsonPath("$.content[1].email").value("maria@email.com"));
    }

    @Test
    @DisplayName("Deveria retornar lista vazia quando não houver tutores")
    void deveriaRetornarListaVazia() throws Exception {
        // ARRANGE
        Page<TutorDto> paginaVazia = new PageImpl<>(
            List.of(),
            PageRequest.of(0, 10),
            0
        );
        given(tutorService.listar(any(Pageable.class))).willReturn(paginaVazia);

        // ACT & ASSERT
        mvc.perform(get("/tutores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(0));
    }

    // ==================== TESTES DE CADASTRO ====================

    @Test
    @DisplayName("Deveria cadastrar tutor com sucesso")
    void deveriaCadastrarTutorComSucesso() throws Exception {
        // ARRANGE
        CadastroTutorDto dto = new CadastroTutorDto(
            "Carlos Oliveira", 
            "(11)77777-7777", 
            "carlos@email.com"
        );

        // ACT & ASSERT
        mvc.perform(post("/tutores")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(tutorService).should().cadastrar(any(CadastroTutorDto.class));
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
        mvc.perform(post("/tutores")
                .content(jsonInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando service lançar ValidacaoException no cadastro")
    void deveriaRetornarErro400QuandoServiceLancarExcecaoNoCadastro() throws Exception {
        // ARRANGE
        CadastroTutorDto dto = new CadastroTutorDto(
            "João Silva", 
            "(11)99999-9999", 
            "joao@email.com"
        );
        String mensagemErro = "E-mail já cadastrado";
        
        doThrow(new ValidacaoException(mensagemErro))
            .when(tutorService).cadastrar(any(CadastroTutorDto.class));

        // ACT & ASSERT
        mvc.perform(post("/tutores")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(mensagemErro));
    }

    // ==================== TESTES DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Deveria atualizar tutor com sucesso")
    void deveriaAtualizarTutorComSucesso() throws Exception {
        // ARRANGE
        AtualizacaoTutorDto dto = new AtualizacaoTutorDto(
            1L, 
            "João Silva Atualizado", 
            "(11)98888-8888",  // CORRIGIDO: telefone no formato correto
            "joao.novo@email.com"
        );

        // ACT & ASSERT
        mvc.perform(put("/tutores")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(tutorService).should().atualizar(any(AtualizacaoTutorDto.class));
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando dados de atualização forem inválidos")
    void deveriaRetornarErro400ParaAtualizacaoComDadosInvalidos() throws Exception {
        // ARRANGE
        String jsonInvalido = """
            {
                "id": null,
                "nome": "",
                "telefone": "123",
                "email": "email-invalido"
            }
            """;

        // ACT & ASSERT
        mvc.perform(put("/tutores")
                .content(jsonInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando service lançar ValidacaoException na atualização")
    void deveriaRetornarErro400QuandoServiceLancarExcecaoNaAtualizacao() throws Exception {
        // ARRANGE
        AtualizacaoTutorDto dto = new AtualizacaoTutorDto(
            1L, 
            "João Silva", 
            "(11)99999-9999",  // CORRIGIDO: telefone no formato correto
            "joao@email.com"
        );
        String mensagemErro = "Tutor não encontrado";
        
        doThrow(new ValidacaoException(mensagemErro))
            .when(tutorService).atualizar(any(AtualizacaoTutorDto.class));

        // ACT & ASSERT
        mvc.perform(put("/tutores")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(mensagemErro));
    }

    // ==================== TESTES DE EXCLUSÃO ====================

    @Test
    @DisplayName("Deveria excluir tutor com sucesso")
    void deveriaExcluirTutorComSucesso() throws Exception {
        // ARRANGE
        Long idTutor = 1L;

        // ACT & ASSERT
        mvc.perform(delete("/tutores/{id}", idTutor)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(tutorService).should().excluir(eq(idTutor));
    }

    @Test
    @DisplayName("Deveria retornar erro 400 quando service lançar ValidacaoException na exclusão")
    void deveriaRetornarErro400QuandoServiceLancarExcecaoNaExclusao() throws Exception {
        // ARRANGE
        Long idTutor = 1L;
        String mensagemErro = "Tutor não encontrado ou possui pets associados";
        
        doThrow(new ValidacaoException(mensagemErro))
            .when(tutorService).excluir(eq(idTutor));

        // ACT & ASSERT
        mvc.perform(delete("/tutores/{id}", idTutor)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(mensagemErro));
    }

    @Test
    @DisplayName("Deveria retornar erro 400 ao tentar excluir tutor com ID inválido")
    void deveriaRetornarErro400ParaExclusaoComIdInvalido() throws Exception {
        // ARRANGE
        String idInvalido = "abc";

        // ACT & ASSERT
        mvc.perform(delete("/tutores/{id}", idInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE VALIDAÇÃO ====================

    @Test
    @DisplayName("Deveria validar campos obrigatórios no cadastro")
    void deveriaValidarCamposObrigatoriosNoCadastro() throws Exception {
        // ARRANGE
        String jsonInvalido = """
            {
                "nome": "",
                "telefone": "",
                "email": ""
            }
            """;

        // ACT & ASSERT
        mvc.perform(post("/tutores")
                .content(jsonInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria validar formato de telefone no cadastro")
    void deveriaValidarFormatoDeTelefoneNoCadastro() throws Exception {
        // ARRANGE
        String jsonTelefoneInvalido = """
            {
                "nome": "Carlos Oliveira",
                "telefone": "123456789",
                "email": "carlos@email.com"
            }
            """;

        // ACT & ASSERT
        mvc.perform(post("/tutores")
                .content(jsonTelefoneInvalido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria aceitar telefone com formato correto")
    void deveriaAceitarTelefoneComFormatoCorreto() throws Exception {
        // ARRANGE
        String jsonTelefoneValido = """
            {
                "nome": "Carlos Oliveira",
                "telefone": "(11)99999-9999",
                "email": "carlos@email.com"
            }
            """;

        // ACT & ASSERT
        mvc.perform(post("/tutores")
                .content(jsonTelefoneValido)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    // ==================== TESTES DE PAGINAÇÃO ====================

    @Test
    @DisplayName("Deveria usar paginação padrão quando não especificada")
    void deveriaUsarPaginacaoPadrao() throws Exception {
        // ARRANGE
        Page<TutorDto> paginaTutores = new PageImpl<>(
            List.of(tutorDto1),
            PageRequest.of(0, 10),
            1
        );
        given(tutorService.listar(any(Pageable.class))).willReturn(paginaTutores);

        // ACT & ASSERT
        mvc.perform(get("/tutores")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(tutorService).should().listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deveria aceitar parâmetros de paginação personalizados")
    void deveriaAceitarParametrosDePaginacaoPersonalizados() throws Exception {
        // ARRANGE
        Page<TutorDto> paginaTutores = new PageImpl<>(
            List.of(tutorDto1),
            PageRequest.of(2, 5),
            1
        );
        given(tutorService.listar(any(Pageable.class))).willReturn(paginaTutores);

        // ACT & ASSERT
        mvc.perform(get("/tutores")
                .param("page", "2")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        then(tutorService).should().listar(any(Pageable.class));
    }
}