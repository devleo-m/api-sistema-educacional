package com.study.apisistemaeducacional.Service.Impl;

import com.study.apisistemaeducacional.Controller.dto.request.AlunoRequest;
import com.study.apisistemaeducacional.Controller.dto.response.AlunoResponse;
import com.study.apisistemaeducacional.Entity.AlunoEntity;
import com.study.apisistemaeducacional.Entity.UsuarioEntity;
import com.study.apisistemaeducacional.Exception.NotFoundException;
import com.study.apisistemaeducacional.Repository.AlunoRepository;
import com.study.apisistemaeducacional.Repository.UsuarioRepository;
import com.study.apisistemaeducacional.Service.AlunoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlunoServiceImpl implements AlunoService {
    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Método para criar aluno.
     * @param request a ser adicionado.
     * @return aluno criado.
     */
    @Override
    public AlunoResponse criarAluno(AlunoRequest request) {
        log.info("Criando novo Aluno: {}", request);

        UsuarioEntity usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        AlunoEntity alunoEntity = new AlunoEntity();
        alunoEntity.setNome(request.nome());

        Date dataEntrada = request.nascimento() != null ? request.nascimento() : null;
        alunoEntity.setData(dataEntrada);

        alunoEntity.setUsuario(usuario);

        AlunoEntity aluno = alunoRepository.save(alunoEntity);
        AlunoResponse response = new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getData(),
                aluno.getUsuario().getLogin(),
                aluno.getUsuario().getPapel().getNome()
        );
        return response;
    }


    /**
     * Método para obter Docente pelo ID.
     * @param id ID do aluno a ser obtido.
     * @return O aluno encontrado.
     * @throws NotFoundException Se o aluno não for encontrado.
     */
    @Override
    public AlunoResponse obterAlunoPorId(Long id) {
        log.info("Obtendo aluno por ID: {}", id);
        Optional<AlunoEntity> AlunoOptional = alunoRepository.findById(id);
        AlunoEntity aluno = AlunoOptional.orElseThrow(() -> {
            log.warn("Aluno não encontrado pelo ID: {}", id);
            return new NotFoundException("Aluno não encontrado com o ID: " + id);
        });
        AlunoResponse response = new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getData(),
                aluno.getUsuario().getLogin(),
                aluno.getUsuario().getPapel().getNome()
        );
        return response;
    }


    /**
     * Método para atualizar aluno pelo id.
     * @param id id do aluno para ser atualizado
     * @param request novos dados do aluno
     * @verificarExistenciaAluno metodo para verificar se o aluno existe
     * @return o aluno atualizado.
     */
    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest request) {
        log.info("Atualizando aluno pelo ID: {}", id);
        verificarExistenciaAluno(id);

        UsuarioEntity usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        AlunoEntity aluno = alunoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aluno não encontrado pelo ID: {}", id);
                    throw new NotFoundException("Aluno não encontrado com o ID: " + id);
                });

        aluno.setNome(request.nome());
        aluno.setUsuario(usuario);
        aluno.setData(request.nascimento());

        AlunoEntity alunoAtualizado = alunoRepository.save(aluno);

        return new AlunoResponse(
                alunoAtualizado.getId(),
                alunoAtualizado.getNome(),
                alunoAtualizado.getData(),
                alunoAtualizado.getUsuario().getLogin(),
                alunoAtualizado.getUsuario().getPapel().getNome());
    }

    /**
     * Método para listar todos os alunos.
     * @return Lista de todos os alunos.
     */
    @Override
    public List<AlunoResponse> listarTodosAlunos() {
        log.info("Listando todos os Docentes!");
        List<AlunoEntity> alunos = alunoRepository.findAll();
        List<AlunoResponse> alunoResponses = alunos.stream()
                .map(aluno -> new AlunoResponse(
                        aluno.getId(),
                        aluno.getNome(),
                        aluno.getData(),
                        aluno.getUsuario().getLogin(),
                        aluno.getUsuario().getPapel().getNome()))
                .collect(Collectors.toList());
        return alunoResponses;
    }

    /**
     * Deleta um aluno existente.
     * @verificarExistenciaDocente metodo para verificar se o aluno existe
     * @param id ID do aluno a ser deletado.
     */
    @Override
    public void deletarAluno(Long id) {
        log.info("Deletando aluno com o ID: {}", id);
        verificarExistenciaAluno(id);
        alunoRepository.deleteById(id);
    }

    /**
     * Verifica se o docente existe.
     * @param id ID do docente a ser verificado.
     * @throws NotFoundException Se o docente não for encontrado.
     */
    private void verificarExistenciaAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            log.warn("Aluno não encontrado com o ID: {}", id);
            throw new NotFoundException("Aluno não encontrado com o ID: " + id);
        } else {
            log.info("Aluno encontrado com o ID: {}", id);
        }
    }
}
