package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Regras de uma mesa de Texas Hold'em simplificada: o jogador humano age,
 * os bots sempre igualam a aposta (ou vão all-in), e a rodada fecha em seguida.
 */
public class Mesa {
    private Baralho baralho = new Baralho();
    private final List<Carta> comunitarias = new ArrayList<>();
    private final List<Jogador> jogadores = new ArrayList<>();
    private final AvaliadorMao avaliador = new AvaliadorMao();
    private FaseJogo fase = FaseJogo.PRE_FLOP;
    private int pote;
    private int apostaAtual;
    private List<Jogador> vencedores = new ArrayList<>();
    private String descricaoVencedora = "";
    private boolean maoEmAndamento;

    public void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
    }

    public void iniciarNovaMao() {
        baralho = new Baralho();
        baralho.embaralhar();
        comunitarias.clear();
        vencedores = new ArrayList<>();
        descricaoVencedora = "";
        pote = 0;
        apostaAtual = 0;
        fase = FaseJogo.PRE_FLOP;
        for (Jogador j : jogadores) {
            j.resetarParaNovaMao();
        }
        if (jogadores.stream().filter(Jogador::isAtivo).count() < 2) {
            maoEmAndamento = false;
            return;
        }
        for (int i = 0; i < 2; i++) {
            for (Jogador j : jogadores) {
                if (j.isAtivo()) {
                    j.getMao().receber(baralho.comprar());
                }
            }
        }
        maoEmAndamento = true;
    }

    /** Aumenta a aposta da rodada para {@code novoTotal} (ou o máximo possível). */
    public void apostar(Jogador jogador, int novoTotal) {
        exigirMaoEmAndamento();
        int falta = Math.max(0, novoTotal - jogador.getApostaRodada());
        pote += jogador.apostar(falta);
        apostaAtual = Math.max(apostaAtual, jogador.getApostaRodada());
    }

    /** Iguala a maior aposta da rodada (ou passa, se não houver aposta). */
    public void igualar(Jogador jogador) {
        apostar(jogador, apostaAtual);
    }

    public void desistir(Jogador jogador) {
        exigirMaoEmAndamento();
        jogador.desistir();
        if (ativos().size() == 1) {
            encerrarSemShowdown();
        }
    }

    /** Fecha a rodada: todos os ativos igualam e a próxima fase é revelada. */
    public void fecharRodada() {
        exigirMaoEmAndamento();
        for (Jogador j : ativos()) {
            igualar(j);
        }
        avancarFase();
    }

    private void avancarFase() {
        for (Jogador j : jogadores) {
            j.resetarApostaRodada();
        }
        apostaAtual = 0;
        switch (fase) {
            case PRE_FLOP -> {
                comprarComunitarias(3);
                fase = FaseJogo.FLOP;
            }
            case FLOP -> {
                comprarComunitarias(1);
                fase = FaseJogo.TURN;
            }
            case TURN -> {
                comprarComunitarias(1);
                fase = FaseJogo.RIVER;
            }
            case RIVER -> {
                fase = FaseJogo.SHOWDOWN;
                showdown();
            }
            default -> { }
        }
    }

    private void comprarComunitarias(int qtd) {
        for (int i = 0; i < qtd; i++) {
            comunitarias.add(baralho.comprar());
        }
    }

    private void encerrarSemShowdown() {
        Jogador unico = ativos().get(0);
        vencedores = List.of(unico);
        descricaoVencedora = "Todos desistiram";
        unico.receberPremio(pote);
        pote = 0;
        maoEmAndamento = false;
        fase = FaseJogo.SHOWDOWN;
    }

    private void showdown() {
        ResultadoMao melhor = null;
        List<Jogador> empatados = new ArrayList<>();
        for (Jogador j : ativos()) {
            List<Carta> sete = new ArrayList<>(j.getMao().getCartas());
            sete.addAll(comunitarias);
            ResultadoMao r = avaliador.avaliar(sete);
            int cmp = melhor == null ? 1 : r.compareTo(melhor);
            if (cmp > 0) {
                melhor = r;
                empatados.clear();
                empatados.add(j);
            } else if (cmp == 0) {
                empatados.add(j);
            }
        }
        vencedores = empatados;
        descricaoVencedora = melhor == null ? "" : melhor.getDescricao();
        int parte = pote / empatados.size();
        int resto = pote % empatados.size();
        for (int i = 0; i < empatados.size(); i++) {
            empatados.get(i).receberPremio(parte + (i == 0 ? resto : 0));
        }
        pote = 0;
        maoEmAndamento = false;
    }

    private List<Jogador> ativos() {
        return jogadores.stream().filter(Jogador::isAtivo).toList();
    }

    private void exigirMaoEmAndamento() {
        if (!maoEmAndamento) {
            throw new IllegalStateException("Não há mão em andamento");
        }
    }

    public List<Carta> getComunitarias() { return comunitarias; }
    public List<Jogador> getJogadores() { return jogadores; }
    public FaseJogo getFase() { return fase; }
    public int getPote() { return pote; }
    public int getApostaAtual() { return apostaAtual; }
    public List<Jogador> getVencedores() { return vencedores; }
    public String getDescricaoVencedora() { return descricaoVencedora; }
    public boolean isMaoEmAndamento() { return maoEmAndamento; }
}
