package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.dto.AtualizacaoTutorDto;
import br.com.alura.adopet.api.dto.CadastroTutorDto;
import br.com.alura.adopet.api.dto.TutorDto;
import br.com.alura.adopet.api.exception.ValidacaoException;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    public Page<TutorDto> listar(Pageable paginacao) {
        // Calcular offset baseado na página e tamanho
        int pageNumber = paginacao.getPageNumber();
        int pageSize = paginacao.getPageSize();
        int offset = pageNumber * pageSize;
        
        // Buscar tutores com paginação manual usando método nativo
        List<Tutor> tutores = tutorRepository.findAllWithPagination(offset, pageSize);
        
        // Obter total de elementos
        long total = tutorRepository.count();
        
        // Converter para DTO
        List<TutorDto> tutoresDto = tutores.stream()
                .map(TutorDto::new)
                .toList();
        
        // Retornar Page com os dados paginados
        return new PageImpl<>(tutoresDto, paginacao, total);
    }

    public void cadastrar(CadastroTutorDto dto) {
        boolean jaCadastrado = tutorRepository.existsByTelefoneOrEmail(dto.telefone(), dto.email());

        if (jaCadastrado) {
            throw new ValidacaoException("Dados já cadastrados para outro tutor!");
        }

        tutorRepository.save(new Tutor(dto));
    }

    public void atualizar(AtualizacaoTutorDto dto) {
        Tutor tutor = tutorRepository.findById(dto.id())
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));

        tutor.atualizarDados(dto);
    }

    public void excluir(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));

        // Verificar se o tutor tem pets associados antes de excluir
        if (tutor.possuiPets()) {
            throw new ValidacaoException("Não é possível excluir tutor com pets associados!");
        }

        tutorRepository.delete(tutor);
    }
}