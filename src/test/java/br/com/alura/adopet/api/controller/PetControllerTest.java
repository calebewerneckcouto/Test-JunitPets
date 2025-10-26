package br.com.alura.adopet.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.alura.adopet.api.dto.PetDto;
import br.com.alura.adopet.api.model.TipoPet;
import br.com.alura.adopet.api.service.PetService;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private PetDto pet1;
    private PetDto pet2;
    private PetDto pet3;

    @BeforeEach
    void setUp() {
        pet1 = new PetDto(1L, TipoPet.CACHORRO, "Rex", "Vira-lata", 2);
        pet2 = new PetDto(2L, TipoPet.GATO, "Mimi", "Siamês", 1);
        pet3 = new PetDto(3L, TipoPet.CACHORRO, "Bob", "Labrador", 3);
    }

    @Test
    void deveriaListarTodosOsPetsDisponiveis() {
        // Arrange
        List<PetDto> pets = Arrays.asList(pet1, pet2, pet3);
        given(petService.buscarPetsDisponiveis()).willReturn(pets);

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("Rex", response.getBody().get(0).nome());
        assertEquals("Mimi", response.getBody().get(1).nome());
        assertEquals("Bob", response.getBody().get(2).nome());
        assertEquals(TipoPet.CACHORRO, response.getBody().get(0).tipo());
        assertEquals(TipoPet.GATO, response.getBody().get(1).tipo());
        
        verify(petService).buscarPetsDisponiveis();
    }

    @Test
    void deveriaRetornarListaVaziaQuandoNaoHouverPetsDisponiveis() {
        // Arrange
        given(petService.buscarPetsDisponiveis()).willReturn(Arrays.asList());

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        
        verify(petService).buscarPetsDisponiveis();
    }

    @Test
    void deveriaRetornarStatus200() {
        // Arrange
        List<PetDto> pets = Arrays.asList(pet1);
        given(petService.buscarPetsDisponiveis()).willReturn(pets);

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveriaRetornarTodosOsCamposDosPets() {
        // Arrange
        List<PetDto> pets = Arrays.asList(pet1);
        given(petService.buscarPetsDisponiveis()).willReturn(pets);

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        PetDto petRetornado = response.getBody().get(0);
        assertEquals(1L, petRetornado.id());
        assertEquals(TipoPet.CACHORRO, petRetornado.tipo());
        assertEquals("Rex", petRetornado.nome());
        assertEquals("Vira-lata", petRetornado.raca());
        assertEquals(2, petRetornado.idade());
    }

    @Test
    void deveriaRetornarApenasPetsDoTipoCachorro() {
        // Arrange
        List<PetDto> pets = Arrays.asList(pet1, pet3);
        given(petService.buscarPetsDisponiveis()).willReturn(pets);

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertEquals(2, response.getBody().size());
        assertEquals(TipoPet.CACHORRO, response.getBody().get(0).tipo());
        assertEquals(TipoPet.CACHORRO, response.getBody().get(1).tipo());
    }

    @Test
    void deveriaRetornarApenasPetsDoTipoGato() {
        // Arrange
        List<PetDto> pets = Arrays.asList(pet2);
        given(petService.buscarPetsDisponiveis()).willReturn(pets);

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals(TipoPet.GATO, response.getBody().get(0).tipo());
        assertEquals("Mimi", response.getBody().get(0).nome());
    }

    @Test
    void deveriaChamarOServiceUmaVez() {
        // Arrange
        given(petService.buscarPetsDisponiveis()).willReturn(Arrays.asList());

        // Act
        petController.listarTodosDisponiveis();

        // Assert
        verify(petService).buscarPetsDisponiveis();
    }

    @Test
    void deveriaRetornarBodyNaoNulo() {
        // Arrange
        given(petService.buscarPetsDisponiveis()).willReturn(Arrays.asList());

        // Act
        ResponseEntity<List<PetDto>> response = petController.listarTodosDisponiveis();

        // Assert
        assertNotNull(response.getBody());
    }
}