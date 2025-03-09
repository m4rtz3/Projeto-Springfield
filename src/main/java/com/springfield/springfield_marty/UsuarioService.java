package com.springfield.springfield_marty;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final CidadaoRepository cidadaoRepository;

    public UsuarioService(UsuarioRepository repository, CidadaoRepository cidadaoRepository) {
        this.repository = repository;
        this.cidadaoRepository = cidadaoRepository;
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (repository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("Usuário já existe.");
        }

        if (!cidadaoRepository.existsById(usuario.getCidadao().getId())) {
            throw new RuntimeException("Cidadão não encontrado.");
        }

        usuario.setTentativasFalhas(0);
        usuario.setBloqueado(false);
        usuario.setUltimaAlteracaoSenha(LocalDateTime.now());
        return repository.save(usuario);
    }

    @Transactional
    public void trocarSenha(Long id, String novaSenha) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.setSenha(novaSenha);
        usuario.setUltimaAlteracaoSenha(LocalDateTime.now());
        repository.save(usuario);
    }

    @Transactional
    public void registrarTentativaFalha(String username) {
        Usuario usuario = repository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.setTentativasFalhas(usuario.getTentativasFalhas() + 1);
        if (usuario.getTentativasFalhas() >= 3) {
            usuario.setBloqueado(true);
        }
        repository.save(usuario);
    }

    @Transactional
    public void desbloquearUsuario(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.setBloqueado(false);
        usuario.setTentativasFalhas(0);
        repository.save(usuario);
    }

    public boolean senhaExpirada(Usuario usuario) {
        return usuario.getUltimaAlteracaoSenha().isBefore(LocalDateTime.now().minusDays(30));
    }
}