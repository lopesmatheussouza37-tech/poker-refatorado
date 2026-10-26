package com.example.demo.service;

public class Jogador {
    private final String nome;
    private final boolean humano;
    private int fichas;
    private int apostaRodada;
    private boolean desistiu;
    private boolean allIn;
    private final Mao mao = new Mao();

    public Jogador(String nome, int fichas, boolean humano) {
        this.nome = nome;
        this.fichas = fichas;
        this.humano = humano;
    }

    /** Aposta até o limite de fichas e devolve o valor realmente apostado. */
    public int apostar(int valor) {
        int real = Math.max(0, Math.min(valor, fichas));
        fichas -= real;
        apostaRodada += real;
        if (fichas == 0) {
            allIn = true;
        }
        return real;
    }

    public void receberPremio(int valor) {
        fichas += valor;
    }

    public void desistir() {
        desistiu = true;
    }

    public void resetarParaNovaMao() {
        mao.limpar();
        apostaRodada = 0;
        desistiu = false;
        allIn = fichas == 0;
        if (fichas == 0) {
            desistiu = true; // sem fichas, fora da mão
        }
    }

    public void resetarApostaRodada() {
        apostaRodada = 0;
    }

    public boolean isAtivo() {
        return !desistiu;
    }

    public String getNome() { return nome; }
    public boolean isHumano() { return humano; }
    public int getFichas() { return fichas; }
    public int getApostaRodada() { return apostaRodada; }
    public boolean isDesistiu() { return desistiu; }
    public boolean isAllIn() { return allIn; }
    public Mao getMao() { return mao; }
}
