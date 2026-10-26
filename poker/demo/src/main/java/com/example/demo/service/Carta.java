package com.example.demo.service;

/** Carta imutável. Valor: 1 = Ás, 11 = Valete, 12 = Dama, 13 = Rei. */
public record Carta(Naipe naipe, int valor) {

    /** Valor para comparação (Ás vale 14). */
    public int valorAlto() {
        return valor == 1 ? 14 : valor;
    }

    public String rotulo() {
        return switch (valor) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(valor);
        };
    }

    @Override
    public String toString() {
        return rotulo() + naipe.getSimbolo();
    }
}
