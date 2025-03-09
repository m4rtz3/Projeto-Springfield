package com.springfield.springfield_marty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(service.cadastrarUsuario(usuario));
    }

    @GetMapping("/{username}")
    public ResponseEntity<Usuario> buscarPorUsername(@PathVariable String username) {
        Optional<Usuario> usuario = service.buscarPorUsername(username);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/trocar-senha")
    public ResponseEntity<Void> trocarSenha(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String novaSenha = payload.get("senha");
        service.trocarSenha(id, novaSenha);
        return ResponseEntity.ok().build();
    }

    

    @PutMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquear(@PathVariable Long id) {
        service.desbloquearUsuario(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/senha-expirada")
    public ResponseEntity<Boolean> verificarSenhaExpirada(@PathVariable String username) {
        Optional<Usuario> usuario = service.buscarPorUsername(username);
        return usuario.map(u -> ResponseEntity.ok(service.senhaExpirada(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
