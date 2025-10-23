package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pets", description = "Operações relacionadas a pets")
public class PetController {

    @Autowired
    private PetService service;

    @GetMapping
    @Operation(summary = "Listar pets disponíveis", description = "Retorna todos os pets disponíveis para adoção")
    @ApiResponse(responseCode = "200", description = "Lista de pets disponíveis retornada com sucesso")
    public ResponseEntity<List<PetDto>> listarTodosDisponiveis() {
        List<PetDto> pets = service.buscarPetsDisponiveis();
        return ResponseEntity.ok(pets);
    }
}