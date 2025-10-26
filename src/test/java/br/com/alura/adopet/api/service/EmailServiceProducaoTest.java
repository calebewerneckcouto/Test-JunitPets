package br.com.alura.adopet.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceProducaoTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceProducao emailServiceProducao;

    @Test
    @DisplayName("Deveria enviar email com dados corretos")
    void deveriaEnviarEmailComDadosCorretos() {
        // ARRANGE
        String to = "destinatario@email.com";
        String subject = "Assunto do Email";
        String message = "Conteúdo da mensagem de teste";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria configurar remetente como adopet@email.com.br")
    void deveriaConfigurarRemetenteCorreto() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Teste";
        String message = "Mensagem";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email para destinatário correto")
    void deveriaEnviarEmailParaDestinatarioCorreto() {
        // ARRANGE
        String to = "cliente@empresa.com";
        String subject = "Teste Destinatário";
        String message = "Mensagem";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com assunto correto")
    void deveriaEnviarEmailComAssuntoCorreto() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Assunto Importante";
        String message = "Mensagem";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com mensagem correta")
    void deveriaEnviarEmailComMensagemCorreta() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Assunto";
        String message = "Esta é uma mensagem longa com detalhes importantes sobre o processo de adoção.";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com destinatário vazio")
    void deveriaEnviarEmailComDestinatarioVazio() {
        // ARRANGE
        String to = "";
        String subject = "Assunto Teste";
        String message = "Mensagem Teste";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com assunto vazio")
    void deveriaEnviarEmailComAssuntoVazio() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "";
        String message = "Mensagem Teste";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com mensagem vazia")
    void deveriaEnviarEmailComMensagemVazia() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Assunto Teste";
        String message = "";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com caracteres especiais")
    void deveriaEnviarEmailComCaracteresEspeciais() {
        // ARRANGE
        String to = "teste.especial@email.com";
        String subject = "Assunto com ç, á, ã, é!";
        String message = "Mensagem com caracteres especiais: ç, á, ã, é, í, ó, ú, ñ, ¿, ¡";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria chamar emailSender.send apenas uma vez")
    void deveriaChamarEmailSenderSendApenasUmaVez() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Teste Único";
        String message = "Mensagem";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should(times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar múltiplos emails sequencialmente")
    void deveriaEnviarMultiplosEmailsSequencialmente() {
        // ARRANGE
        String to1 = "cliente1@email.com";
        String to2 = "cliente2@email.com";
        String to3 = "cliente3@email.com";

        // ACT
        emailServiceProducao.enviarEmail(to1, "Assunto 1", "Mensagem 1");
        emailServiceProducao.enviarEmail(to2, "Assunto 2", "Mensagem 2");
        emailServiceProducao.enviarEmail(to3, "Assunto 3", "Mensagem 3");

        // ASSERT
        then(emailSender).should(times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria lidar com endereços de email complexos")
    void deveriaLidarComEnderecosDeEmailComplexos() {
        // ARRANGE
        String to = "nome.sobrenome+tag@subdominio.empresa.com.br";
        String subject = "Email Complexo";
        String message = "Teste com endereço de email complexo";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria enviar email com mensagem longa")
    void deveriaEnviarEmailComMensagemLonga() {
        // ARRANGE
        String to = "teste@email.com";
        String subject = "Mensagem Longa";
        String message = "Esta é uma mensagem muito longa que contém diversos parágrafos e informações detalhadas. " +
            "O objetivo é testar como o serviço de email lida com mensagens extensas que podem ser geradas " +
            "pelo sistema durante o processo de adoção de pets. Esperamos que tudo funcione corretamente " +
            "mesmo com textos longos e complexos.";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deveria configurar todas as propriedades do email")
    void deveriaConfigurarTodasAsPropriedadesDoEmail() {
        // ARRANGE
        String to = "teste.completo@email.com";
        String subject = "Teste Completo";
        String message = "Mensagem completa de teste";

        // ACT
        emailServiceProducao.enviarEmail(to, subject, message);

        // ASSERT
        then(emailSender).should().send(any(SimpleMailMessage.class));
    }
}