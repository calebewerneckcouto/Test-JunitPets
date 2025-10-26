package br.com.alura.adopet.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.adopet.api.dto.AbrigoDto;
import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.dto.CadastroPetDto;
import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.service.AbrigoService;
import br.com.alura.adopet.api.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/abrigos")
@Tag(name = "Abrigos", description = "Operações relacionadas a abrigos de animais")
public class AbrigoController {

    @Autowired
    private AbrigoService abrigoService;

    @Autowired
    private PetService petService;

    @GetMapping
    @Operation(summary = "Listar todos os abrigos", description = "Retorna uma lista com todos os abrigos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de abrigos retornada com sucesso")
    public ResponseEntity<List<AbrigoDto>> listar() {
        List<AbrigoDto> abrigos = abrigoService.listar();
        return ResponseEntity.ok(abrigos);
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar novo abrigo", description = "Cadastra um novo abrigo no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abrigo cadastrado com sucesso", 
                    content = @Content(schema = @Schema(implementation = AbrigoDto.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou já cadastrados")
    })
    public ResponseEntity<?> cadastrar(@RequestBody @Valid CadastroAbrigoDto dto) {
        try {
            AbrigoDto abrigoCadastrado = abrigoService.cadatrar(dto);
            return ResponseEntity.ok(abrigoCadastrado);
        } catch (ValidacaoException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/{idOuNome}/pets")
    @Operation(summary = "Listar pets de um abrigo", description = "Retorna todos os pets de um abrigo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pets retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<List<PetDto>> listarPets(
            @Parameter(description = "ID ou nome do abrigo", example = "1 ou 'Abrigo dos Bichinhos'") 
            @PathVariable String idOuNome) {
        try {
            List<PetDto> petsDoAbrigo = abrigoService.listarPetsDoAbrigo(idOuNome);
            return ResponseEntity.ok(petsDoAbrigo);
        } catch (ValidacaoException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{idOuNome}/pets")
    @Transactional
    @Operation(summary = "Cadastrar pet em um abrigo", description = "Cadastra um novo pet em um abrigo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pet cadastrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<String> cadastrarPet(
            @Parameter(description = "ID ou nome do abrigo", example = "1 ou 'Abrigo dos Bichinhos'") 
            @PathVariable String idOuNome, 
            @RequestBody @Valid CadastroPetDto dto) {
        try {
            Abrigo abrigo = abrigoService.carregarAbrigo(idOuNome);
            petService.cadastrarPet(abrigo, dto);
            return ResponseEntity.ok().build();
        } catch (ValidacaoException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}