package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.dto.AprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.ReprovacaoAdocaoDto;
import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.service.AdocaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adocoes")
@Tag(name = "Adoções", description = "Operações relacionadas a adoções de pets")
public class AdocaoController {

    @Autowired
    private AdocaoService adocaoService;

    @PostMapping
    @Transactional
    @Operation(summary = "Solicitar adoção", description = "Solicita a adoção de um pet por um tutor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adoção solicitada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou validação falhou")
    })
    public ResponseEntity<String> solicitar(@RequestBody @Valid SolicitacaoAdocaoDto dto) {
        try {
            this.adocaoService.solicitar(dto);
            return ResponseEntity.ok("Adoção solicitada com sucesso!");
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/aprovar")
    @Transactional
    @Operation(summary = "Aprovar adoção", description = "Aprova uma solicitação de adoção")
    @ApiResponse(responseCode = "200", description = "Adoção aprovada com sucesso")
    public ResponseEntity<String> aprovar(@RequestBody @Valid AprovacaoAdocaoDto dto) {
        this.adocaoService.aprovar(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reprovar")
    @Transactional
    @Operation(summary = "Reprovar adoção", description = "Reprova uma solicitação de adoção")
    @ApiResponse(responseCode = "200", description = "Adoção reprovada com sucesso")
    public ResponseEntity<String> reprovar(@RequestBody @Valid ReprovacaoAdocaoDto dto) {
        this.adocaoService.reprovar(dto);
        return ResponseEntity.ok().build();
    }
}