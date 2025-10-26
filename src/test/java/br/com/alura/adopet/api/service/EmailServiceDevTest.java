package br.com.alura.adopet.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmailServiceDevTest {

    @InjectMocks
    private EmailServiceDev emailServiceDev;

    @Test
    @DisplayName("Deveria imprimir informações do email no console")
    void deveriaImprimirInformacoesDoEmailNoConsole() {
        // ARRANGE
        String to = "destinatario@email.com";
        String subject = "Assunto do Email";
        String message = "Conteúdo da mensagem de teste";
        
        // Captura a saída do System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("Enviando email fake"), 
                "Deveria conter a mensagem 'Enviando email fake'");
            assertTrue(consoleOutput.contains("Destinatario: " + to), 
                "Deveria conter o destinatário");
            assertTrue(consoleOutput.contains("Assunto: " + subject), 
                "Deveria conter o assunto");
            assertTrue(consoleOutput.contains("Mensagem: " + message), 
                "Deveria conter a mensagem");
            
        } finally {
            // Restaura o System.out original
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria funcionar com destinatário vazio")
    void deveriaFuncionarComDestinatarioVazio() {
        // ARRANGE
        String to = "";
        String subject = "Assunto Teste";
        String message = "Mensagem Teste";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("Destinatario: "), 
                "Deveria lidar com destinatário vazio");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria funcionar com assunto vazio")
    void deveriaFuncionarComAssuntoVazio() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "";
        String message = "Mensagem Teste";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("Assunto: "), 
                "Deveria lidar com assunto vazio");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria funcionar com mensagem vazia")
    void deveriaFuncionarComMensagemVazia() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Assunto Teste";
        String message = "";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("Mensagem: "), 
                "Deveria lidar com mensagem vazia");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria funcionar com mensagem longa")
    void deveriaFuncionarComMensagemLonga() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Assunto Teste";
        String message = "Esta é uma mensagem muito longa que contém diversos detalhes importantes "
            + "sobre o processo de adoção. O pet está disponível e aguarda um lar amoroso. "
            + "Entre em contato para mais informações.";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains(message), 
                "Deveria imprimir mensagem longa completa");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria funcionar com caracteres especiais")
    void deveriaFuncionarComCaracteresEspeciais() {
        // ARRANGE
        String to = "teste.especial@email.com";
        String subject = "Assunto com ç, á, ã, é!";
        String message = "Mensagem com caracteres especiais: ç, á, ã, é, í, ó, ú, ñ, ¿, ¡";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains(subject), 
                "Deveria lidar com caracteres especiais no assunto");
            assertTrue(consoleOutput.contains(message), 
                "Deveria lidar com caracteres especiais na mensagem");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria imprimir formato correto das informações")
    void deveriaImprimirFormatoCorreto() {
        // ARRANGE
        String to = "formato@email.com";
        String subject = "Teste de Formato";
        String message = "Mensagem de teste";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT
            emailServiceDev.enviarEmail(to, subject, message);
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            // Verifica se contém todas as linhas esperadas
            assertTrue(consoleOutput.contains("Enviando email fake"));
            assertTrue(consoleOutput.contains("Destinatario: " + to));
            assertTrue(consoleOutput.contains("Assunto: " + subject));
            assertTrue(consoleOutput.contains("Mensagem: " + message));
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria não lançar exceção com parâmetros nulos")
    void deveriaNaoLancarExcecaoComParametrosNulos() {
        // ARRANGE
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT & ASSERT - Não deve lançar exceção
            emailServiceDev.enviarEmail(null, null, null);
            
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("Enviando email fake"), 
                "Deveria executar mesmo com parâmetros nulos");
            
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Deveria executar método sem problemas múltiplas vezes")
    void deveriaExecutarMultiplasVezesSemProblemas() {
        // ARRANGE
        String to = "multi@email.com";
        String subject = "Teste Múltiplo";
        String message = "Mensagem múltipla";
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // ACT - Executa múltiplas vezes
            emailServiceDev.enviarEmail(to, subject, message);
            emailServiceDev.enviarEmail(to + "2", subject + "2", message + "2");
            emailServiceDev.enviarEmail(to + "3", subject + "3", message + "3");
            
            // ASSERT
            String consoleOutput = outputStream.toString();
            // Deve conter todas as execuções
            assertTrue(consoleOutput.contains("Destinatario: " + to));
            assertTrue(consoleOutput.contains("Destinatario: " + to + "2"));
            assertTrue(consoleOutput.contains("Destinatario: " + to + "3"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
}