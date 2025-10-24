package br.com.alura.adopet.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.ReprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.service.AdocaoService;
import jakarta.validation.ValidationException;

@SpringBootTest
@AutoConfigureMockMvc
class AdocaoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdocaoService service;

    // ==================== TESTES DE SOLICITAÇÃO ====================
    
    @Test
    void deveriaDevolverCodigo400ParaSolicitacaoDeAdocaoComErros() throws Exception {
        // ARRANGE
        String json = "{}";

        // ACT
        var response = mvc.perform(
            post("/adocoes")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void deveriaDevolverCodigo200ParaSolicitacaoDeAdocaoSemErros() throws Exception {
        // ARRANGE
        String json = """
            {
                "idPet": 1,
                "idTutor": 2,
                "motivo": "Motivo qualquer"
            }
            """;

        // ACT
        var response = mvc.perform(
            post("/adocoes")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void deveriaDevolverCodigo400QuandoServiceLancarValidationException() throws Exception {
        // ARRANGE
        String json = """
            {
                "idPet": 1,
                "idTutor": 2,
                "motivo": "Motivo qualquer"
            }
            """;
        
        doThrow(new ValidationException("Pet já foi adotado"))
            .when(service).solicitar(any(SolicitacaoAdocaoDto.class));

        // ACT
        var response = mvc.perform(
            post("/adocoes")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, response.getStatus());
        Assertions.assertEquals("Pet já foi adotado", response.getContentAsString());
    }

    @Test
    void deveriaRetornarMensagemDeSucessoQuandoSolicitacaoForBemSucedida() throws Exception {
        // ARRANGE
        String json = """
            {
                "idPet": 1,
                "idTutor": 2,
                "motivo": "Motivo qualquer"
            }
            """;
        
        doNothing().when(service).solicitar(any(SolicitacaoAdocaoDto.class));

        // ACT
        var response = mvc.perform(
            post("/adocoes")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Adoção solicitada com sucesso!", response.getContentAsString());
    }

    @Test
    void deveriaChamarServiceAoSolicitarAdocao() throws Exception {
        // ARRANGE
        String json = """
            {
                "idPet": 1,
                "idTutor": 2,
                "motivo": "Motivo qualquer"
            }
            """;

        // ACT
        mvc.perform(
            post("/adocoes")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // ASSERT
        verify(service).solicitar(any(SolicitacaoAdocaoDto.class));
    }

    // ==================== TESTES DE APROVAÇÃO ====================

    @Test
    void deveriaDevolverCodigo200ParaAprovacaoDeAdocao() throws Exception {
        // ARRANGE
        String json = """
            {
                "idAdocao": 1
            }
            """;

        doNothing().when(service).aprovar(any(AprovacaoAdocaoDto.class));

        // ACT
        var response = mvc.perform(
            put("/adocoes/aprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void deveriaDevolverCodigo400ParaAprovacaoComDadosInvalidos() throws Exception {
        // ARRANGE
        String json = "{}";

        // ACT
        var response = mvc.perform(
            put("/adocoes/aprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void deveriaChamarServiceAoAprovarAdocao() throws Exception {
        // ARRANGE
        String json = """
            {
                "idAdocao": 1
            }
            """;

        // ACT
        mvc.perform(
            put("/adocoes/aprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // ASSERT
        verify(service).aprovar(any(AprovacaoAdocaoDto.class));
    }

    // ==================== TESTES DE REPROVAÇÃO ====================

    @Test
    void deveriaDevolverCodigo200ParaReprovacaoDeAdocao() throws Exception {
        // ARRANGE
        String json = """
            {
                "idAdocao": 1,
                "justificativa": "Tutor não possui condições adequadas"
            }
            """;

        doNothing().when(service).reprovar(any(ReprovacaoAdocaoDto.class));

        // ACT
        var response = mvc.perform(
            put("/adocoes/reprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    void deveriaDevolverCodigo400ParaReprovacaoComDadosInvalidos() throws Exception {
        // ARRANGE
        String json = "{}";

        // ACT
        var response = mvc.perform(
            put("/adocoes/reprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void deveriaChamarServiceAoReprovarAdocao() throws Exception {
        // ARRANGE
        String json = """
            {
                "idAdocao": 1,
                "justificativa": "Tutor não possui condições adequadas"
            }
            """;

        // ACT
        mvc.perform(
            put("/adocoes/reprovar")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // ASSERT
        verify(service).reprovar(any(ReprovacaoAdocaoDto.class));
    }
}