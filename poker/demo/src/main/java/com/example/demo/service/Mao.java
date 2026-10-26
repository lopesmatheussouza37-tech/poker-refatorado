package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

public class Mao {
    private final List<Carta> cartas = new ArrayList<>();

    public void receber(Carta carta) {
        cartas.add(carta);
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public void limpar() {
        cartas.clear();
    }

    @Override
    public String toString() {
        return cartas.toString();
    }
}
