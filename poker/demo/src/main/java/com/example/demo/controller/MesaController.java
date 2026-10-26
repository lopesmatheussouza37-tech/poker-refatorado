package com.example.demo.controller;

import com.example.demo.service.EstadoMesa;
import com.example.demo.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mesa")
public class MesaController {

    public record Acao(String acao, int valor) {
    }

    private final JogoService jogo;

    public MesaController(JogoService jogo) {
        this.jogo = jogo;
    }

    @GetMapping
    public EstadoMesa estado() {
        return jogo.estado();
    }

    @PostMapping("/nova-mao")
    public EstadoMesa novaMao() {
        return jogo.novaMao();
    }

    @PostMapping("/acao")
    public EstadoMesa agir(@RequestBody Acao acao) {
        return jogo.agir(acao.acao(), acao.valor());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> erro(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}
