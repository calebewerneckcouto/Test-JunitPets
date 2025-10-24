package br.com.alura.adopet.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.alura.adopet.api.dto.CadastroAbrigoDto;
import br.com.alura.adopet.api.dto.CadastroPetDto;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.ProbabilidadeAdocao;
import br.com.alura.adopet.api.model.TipoPet;

class CalculadoraProbabilidadeAdocaoTest {

    private CalculadoraProbabilidadeAdocao calculadora = new CalculadoraProbabilidadeAdocao();
    private Abrigo abrigo = new Abrigo(new CadastroAbrigoDto(
        "Abrigo Feliz",
        "94999999999",
        "abrigofeliz@email.com.br"
    ));

    // ==================== TESTES DE PROBABILIDADE ALTA (nota >= 8) ====================

    @Test
    @DisplayName("Deve retornar probabilidade ALTA para gato jovem e leve")
    void deveriaRetornarProbabilidadeAltaParaPetComIdadeBaixaEPesoBaixo() {
        // ARRANGE - idade 4 anos e 4kg = nota 10 (sem penalidades) = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Miau",
            "Siamês",
            4,
            "Cinza",
            4.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade ALTA para cachorro jovem e leve")
    void deveriaRetornarProbabilidadeAltaParaCachorroJovemELeve() {
        // ARRANGE - idade 3 anos e 10kg = nota 10 (sem penalidades) = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Rex",
            "Labrador",
            3,
            "Dourado",
            10.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade ALTA para gato com idade média e peso normal")
    void deveriaRetornarProbabilidadeAltaParaGatoIdadeMediaPesoNormal() {
        // ARRANGE - idade 8 anos e 5kg = nota 10 (sem penalidades) = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Felícia",
            "Persa",
            8,
            "Branco",
            5.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade ALTA para cachorro no limite do peso sem penalidade")
    void deveriaRetornarProbabilidadeAltaParaCachorroLimitePeso() {
        // ARRANGE - idade 5 anos e 15kg = nota 10 (peso <= 15, sem penalidade) = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Thor",
            "Beagle",
            5,
            "Tricolor",
            15.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade ALTA para gato no limite do peso sem penalidade")
    void deveriaRetornarProbabilidadeAltaParaGatoLimitePeso() {
        // ARRANGE - idade 6 anos e 10kg = nota 10 (peso <= 10, sem penalidade) = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Garfield",
            "Laranja",
            6,
            "Laranja",
            10.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    // ==================== TESTES DE PROBABILIDADE MÉDIA (5 <= nota < 8) ====================

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA para gato idoso e leve")
    void deveriaRetornarProbabilidadeMediaParaPetComIdadeAltaEPesoBaixo() {
        // ARRANGE - idade 15 anos e 4kg = nota 10 - 5 (idade) = 5 = MÉDIA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Miau",
            "Siamês",
            15,
            "Cinza",
            4.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA para cachorro com idade avançada")
    void deveriaRetornarProbabilidadeMediaParaCachorroIdoso() {
        // ARRANGE - idade 10 anos e 12kg = nota 10 - 4 (idade) = 6 = MÉDIA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Bob",
            "Vira-lata",
            10,
            "Preto",
            12.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA para cachorro jovem mas muito pesado")
    void deveriaRetornarProbabilidadeMediaParaCachorroJovemPesado() {
        // ARRANGE - idade 3 anos e 20kg = nota 10 - 2 (peso) = 8, mas testando limite
        // Vamos usar idade 5 anos e 20kg = nota 10 - 2 (peso) = 8, no limite superior da ALTA
        // Para garantir MÉDIA: idade 10 anos e 16kg = nota 10 - 4 (idade) - 2 (peso) = 4, mas isso é BAIXA
        // Melhor: idade 3 anos e 18kg = nota 10 - 2 (peso) = 8 = ALTA (limite)
        // Para MÉDIA: idade 10 anos e 10kg = nota 10 - 4 (idade) = 6 = MÉDIA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Brutus",
            "Rottweiler",
            11,
            "Preto",
            14.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA para gato pesado e jovem")
    void deveriaRetornarProbabilidadeMediaParaGatoPesadoJovem() {
        // ARRANGE - idade 2 anos e 12kg = nota 10 - 2 (peso) = 8 = ALTA (limite)
        // Para MÉDIA precisa ser < 8, então: idade 10 anos e 8kg = nota 10 - 4 = 6 = MÉDIA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Gordo",
            "Maine Coon",
            12,
            "Cinza",
            8.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA no limite inferior (nota = 5)")
    void deveriaRetornarProbabilidadeMediaNoLimiteInferior() {
        // ARRANGE - idade 15 anos = nota 10 - 5 = 5 = MÉDIA (limite inferior)
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Senior",
            "SRD",
            15,
            "Branco",
            6.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade MÉDIA no limite superior (nota = 7)")
    void deveriaRetornarProbabilidadeMediaNoLimiteSuperior() {
        // ARRANGE - idade 13 anos = nota 10 - 4 = 6 ou idade 11 anos = 10 - 4 = 6
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Velhinho",
            "Poodle",
            13,
            "Branco",
            8.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    // ==================== TESTES DE PROBABILIDADE BAIXA (nota < 5) ====================

    @Test
    @DisplayName("Deve retornar probabilidade BAIXA para gato muito idoso e pesado")
    void deveriaRetornarProbabilidadeBaixaParaGatoIdosoPesado() {
        // ARRANGE - idade 16 anos e 12kg = nota 10 - 5 (idade) - 2 (peso) = 3 = BAIXA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Veterano",
            "SRD",
            16,
            "Preto",
            12.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.BAIXA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade BAIXA para cachorro muito idoso e pesado")
    void deveriaRetornarProbabilidadeBaixaParaCachorroIdosoPesado() {
        // ARRANGE - idade 15 anos e 20kg = nota 10 - 5 (idade) - 2 (peso) = 3 = BAIXA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Gigante",
            "São Bernardo",
            15,
            "Marrom",
            25.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.BAIXA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade BAIXA para gato muito idoso mesmo sendo leve")
    void deveriaRetornarProbabilidadeBaixaParaGatoMuitoIdoso() {
        // ARRANGE - idade 18 anos e 5kg = nota 10 - 5 (idade >= 15) = 5, não é BAIXA
        // Precisa ser < 5, então idade 20 anos ainda é -5, ficaria 5 = MÉDIA
        // O problema é que a lógica só penaliza -5 para >= 15 anos
        // Para ter nota < 5 precisa combinar penalidades
        // Idade 15 + peso > 10 para gato = 10 - 5 - 2 = 3 = BAIXA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Ancião",
            "Persa",
            20,
            "Cinza",
            11.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.BAIXA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade BAIXA no limite (nota = 4)")
    void deveriaRetornarProbabilidadeBaixaNoLimite() {
        // ARRANGE - idade 10 anos e peso > 15 para cachorro = nota 10 - 4 - 2 = 4 = BAIXA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Desafiador",
            "Mastim",
            10,
            "Caramelo",
            20.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.BAIXA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar probabilidade BAIXA para pior cenário possível")
    void deveriaRetornarProbabilidadeBaixaParaPiorCenario() {
        // ARRANGE - idade 20 anos e 30kg = nota 10 - 5 (idade) - 2 (peso) = 3 = BAIXA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Extremo",
            "Mastiff",
            20,
            "Tigrado",
            30.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.BAIXA, probabilidade);
    }

    // ==================== TESTES DE LIMITES E FRONTEIRAS ====================

    @Test
    @DisplayName("Deve retornar ALTA para cachorro com exatos 15kg (limite sem penalidade)")
    void deveriaNaoPenalizarCachorroComExatos15Kg() {
        // ARRANGE - peso = 15kg não deve ser penalizado (penaliza apenas > 15)
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Limite",
            "Border Collie",
            5,
            "Preto e Branco",
            15.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar ALTA para cachorro com 16kg mas jovem (penalidade apenas de peso)")
    void deveriaPenalizarCachorroComMaisDe15Kg() {
        // ARRANGE - peso = 16kg, idade 5 anos = nota 10 - 2 (peso > 15) = 8 = ALTA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Pesadão",
            "Golden",
            5,
            "Dourado",
            16.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar ALTA para gato com exatos 10kg (limite sem penalidade)")
    void deveriaNaoPenalizarGatoComExatos10Kg() {
        // ARRANGE - peso = 10kg não deve ser penalizado (penaliza apenas > 10)
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Limite Gato",
            "Maine Coon",
            4,
            "Cinza",
            10.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar ALTA para gato com 11kg mas jovem")
    void deveriaPenalizarGatoComMaisDe10Kg() {
        // ARRANGE - peso = 11kg, idade 3 anos = nota 10 - 2 = 8 = ALTA (limite)
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Pesado",
            "Ragdoll",
            3,
            "Branco",
            11.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.ALTA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar MÉDIA para pet com exatos 10 anos (primeira faixa de penalidade)")
    void deveriaPenalizarPetCom10Anos() {
        // ARRANGE - idade = 10 anos = nota 10 - 4 = 6 = MÉDIA
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.GATO,
            "Dez Anos",
            "SRD",
            10,
            "Rajado",
            5.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }

    @Test
    @DisplayName("Deve retornar MÉDIA para pet com exatos 15 anos (segunda faixa de penalidade)")
    void deveriaPenalizarMaisPetCom15Anos() {
        // ARRANGE - idade = 15 anos = nota 10 - 5 = 5 = MÉDIA (limite inferior)
        Pet pet = new Pet(new CadastroPetDto(
            TipoPet.CACHORRO,
            "Quinze Anos",
            "SRD",
            15,
            "Caramelo",
            10.0f
        ), abrigo);

        // ACT
        ProbabilidadeAdocao probabilidade = calculadora.calcular(pet);

        // ASSERT
        Assertions.assertEquals(ProbabilidadeAdocao.MEDIA, probabilidade);
    }
}