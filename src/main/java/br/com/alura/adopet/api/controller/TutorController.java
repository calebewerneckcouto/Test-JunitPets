package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.dto.AtualizacaoTutorDto;
import br.com.alura.adopet.api.dto.CadastroTutorDto;
import br.com.alura.adopet.api.dto.TutorDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores")
@Tag(name = "Tutores", description = "Operações relacionadas a tutores")
public class TutorController {

    @Autowired
    private TutorService service;

    @GetMapping
    @Operation(summary = "Listar tutores", description = "Retorna uma lista paginada de todos os tutores")
    @ApiResponse(responseCode = "200", description = "Lista de tutores retornada com sucesso")
    public ResponseEntity<Page<TutorDto>> listar(
            @Parameter(description = "Parâmetros de paginação") 
            @PageableDefault(size = 10) Pageable paginacao) {
        Page<TutorDto> tutores = service.listar(paginacao);
        return ResponseEntity.ok(tutores);
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar tutor", description = "Cadastra um novo tutor no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tutor cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou já cadastrados")
    })
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CadastroTutorDto dto) {
        try {
            service.cadastrar(dto);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualizar tutor", description = "Atualiza os dados de um tutor existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tutor atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou tutor não encontrado")
    })
    public ResponseEntity<String> atualizar(@RequestBody @Valid AtualizacaoTutorDto dto) {
        try {
            service.atualizar(dto);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir tutor", description = "Exclui um tutor do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tutor excluído com sucesso"),
        @ApiResponse(responseCode = "400", description = "Tutor não encontrado ou possui pets associados")
    })
    public ResponseEntity<String> excluir(
            @Parameter(description = "ID do tutor", example = "1") 
            @PathVariable Long id) {
        try {
            service.excluir(id);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}