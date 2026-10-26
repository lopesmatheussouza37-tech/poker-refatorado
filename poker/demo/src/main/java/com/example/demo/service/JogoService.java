package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class JogoService {
    private static final int FICHAS_INICIAIS = 1000;
    private final Mesa mesa = new Mesa();

    public JogoService() {
        mesa.adicionarJogador(new Jogador("Você", FICHAS_INICIAIS, true));
        for (String bot : new String[]{"Ana", "Bruno", "Carla", "Diego", "Elisa"}) {
            mesa.adicionarJogador(new Jogador(bot, FICHAS_INICIAIS, false));
        }
        mesa.iniciarNovaMao();
    }

    public synchronized EstadoMesa estado() {
        return EstadoMesa.de(mesa);
    }

    public synchronized EstadoMesa novaMao() {
        mesa.iniciarNovaMao();
        return EstadoMesa.de(mesa);
    }

    public synchronized EstadoMesa agir(String acao, int valor) {
        Jogador eu = mesa.getJogadores().get(0);
        switch (acao) {
            case "desistir" -> {
                mesa.desistir(eu);
                while (mesa.isMaoEmAndamento()) { // bots seguem até o showdown
                    mesa.fecharRodada();
                }
            }
            case "aumentar" -> {
                if (valor <= 0) {
                    throw new IllegalArgumentException("Valor inválido");
                }
                mesa.apostar(eu, Math.max(mesa.getApostaAtual(), eu.getApostaRodada()) + valor);
                mesa.fecharRodada();
            }
            case "igualar" -> {
                mesa.igualar(eu);
                mesa.fecharRodada();
            }
            default -> throw new IllegalArgumentException("Ação desconhecida: " + acao);
        }
        return EstadoMesa.de(mesa);
    }
}
