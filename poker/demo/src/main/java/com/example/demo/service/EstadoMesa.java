package com.example.demo.service;

import java.util.List;

/** Visão serializável da mesa enviada ao front-end. */
public record EstadoMesa(
        FaseJogo fase,
        int pote,
        int apostaAtual,
        boolean maoEmAndamento,
        List<CartaDto> comunitarias,
        List<JogadorDto> jogadores,
        List<String> vencedores,
        String descricaoVencedora) {

    public record CartaDto(String rotulo, String simbolo, boolean vermelho) {
        static CartaDto de(Carta c) {
            return new CartaDto(c.rotulo(), c.naipe().getSimbolo(), c.naipe().isVermelho());
        }
    }

    public record JogadorDto(String nome, boolean humano, int fichas, int aposta,
                             boolean desistiu, boolean allIn, List<CartaDto> cartas) {
    }

    public static EstadoMesa de(Mesa mesa) {
        boolean revelar = mesa.getFase() == FaseJogo.SHOWDOWN;
        List<JogadorDto> js = mesa.getJogadores().stream().map(j -> {
            boolean mostrar = j.isHumano() || (revelar && !j.isDesistiu());
            List<CartaDto> cartas = mostrar
                    ? j.getMao().getCartas().stream().map(CartaDto::de).toList()
                    : null; // null = cartas viradas para baixo
            int qtd = j.getMao().getCartas().size();
            return new JogadorDto(j.getNome(), j.isHumano(), j.getFichas(), j.getApostaRodada(),
                    j.isDesistiu(), j.isAllIn(),
                    cartas != null ? cartas : java.util.Collections.nCopies(qtd, null));
        }).toList();
        return new EstadoMesa(
                mesa.getFase(), mesa.getPote(), mesa.getApostaAtual(), mesa.isMaoEmAndamento(),
                mesa.getComunitarias().stream().map(CartaDto::de).toList(), js,
                mesa.getVencedores().stream().map(Jogador::getNome).toList(),
                mesa.getDescricaoVencedora());
    }
}
