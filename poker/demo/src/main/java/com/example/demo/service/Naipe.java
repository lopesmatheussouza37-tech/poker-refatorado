package com.example.demo.service;

public enum Naipe {
    OURO("♦", "Ouro"), PAUS("♣", "Paus"), COPAS("♥", "Copas"), ESPADAS("♠", "Espadas");

    private final String simbolo;
    private final String nome;

    Naipe(String simbolo, String nome) {
        this.simbolo = simbolo;
        this.nome = nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public boolean isVermelho() {
        return this == OURO || this == COPAS;
    }

    @Override
    public String toString() {
        return nome;
    }
}
