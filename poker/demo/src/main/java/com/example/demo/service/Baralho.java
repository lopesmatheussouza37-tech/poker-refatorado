package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {
    private final List<Carta> cartas = new ArrayList<>();

    public Baralho() {
        for (Naipe n : Naipe.values()) {
            for (int v = 1; v <= 13; v++) {
                cartas.add(new Carta(n, v));
            }
        }
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public Carta comprar() {
        if (cartas.isEmpty()) {
            throw new IllegalStateException("Baralho vazio");
        }
        return cartas.remove(cartas.size() - 1);
    }

    public int restantes() {
        return cartas.size();
    }
}
