package br.com.alura.adopet.api.validacoes;

import br.com.alura.adopet.api.dto.SolicitacaoAdocaoDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.StatusAdocao;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoPetComAdocaoEmAndamento implements ValidacaoSolicitacaoAdocao {

    @Autowired
    private AdocaoRepository adocaoRepository;

    @Override
    public void validar(SolicitacaoAdocaoDto dto) {
        // Use o método correto com StatusAdocao
        boolean adocaoEmAndamento = adocaoRepository.existsByPetIdAndStatus(
            dto.idPet(), 
            StatusAdocao.AGUARDANDO_AVALIACAO
        );
        
        if (adocaoEmAndamento) {
            throw new ValidacaoException("Pet já possui uma adoção em andamento!");
        }
    }
}