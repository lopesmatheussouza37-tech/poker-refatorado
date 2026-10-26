package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AvaliadorMao {

    public ResultadoMao avaliar(List<Carta> cartas) {
        List<Integer> valores = new ArrayList<>();
        Map<Naipe, List<Integer>> porNaipe = new HashMap<>();
        for (Carta c : cartas) {
            int v = c.valorAlto();
            valores.add(v);
            porNaipe.computeIfAbsent(c.naipe(), k -> new ArrayList<>()).add(v);
        }

        List<Integer> flushValores = null;
        for (List<Integer> lista : porNaipe.values()) {
            if (lista.size() >= 5) {
                flushValores = lista;
            }
        }

        if (flushValores != null) {
            List<Integer> sequenciaFlush = buscarSequencia(flushValores);
            if (sequenciaFlush != null) {
                if (sequenciaFlush.get(0) == 14) {
                    return new ResultadoMao(RankingMao.ROYAL_FLUSH, sequenciaFlush, "Royal Flush");
                }
                return new ResultadoMao(RankingMao.STRAIGHT_FLUSH, sequenciaFlush, "Straight Flush");
            }
        }

        Map<Integer, Integer> contagem = new HashMap<>();
        for (int v : valores) {
            contagem.merge(v, 1, Integer::sum);
        }

        List<Integer> quadras = chavesComContagem(contagem, 4);
        List<Integer> trincas = chavesComContagem(contagem, 3);
        List<Integer> pares = chavesComContagem(contagem, 2);

        if (!quadras.isEmpty()) {
            int quadra = quadras.get(0);
            int kicker = maiorExceto(valores, quadra);
            return new ResultadoMao(RankingMao.QUADRA, Arrays.asList(quadra, kicker), "Quadra");
        }

        if (!trincas.isEmpty() && (trincas.size() > 1 || !pares.isEmpty())) {
            int trinca = trincas.get(0);
            int par = trincas.size() > 1 ? trincas.get(1) : pares.get(0);
            return new ResultadoMao(RankingMao.FULL_HOUSE, Arrays.asList(trinca, par), "Full House");
        }

        if (flushValores != null) {
            List<Integer> top5 = flushValores.stream().distinct()
                    .sorted(Collections.reverseOrder()).limit(5).collect(Collectors.toList());
            return new ResultadoMao(RankingMao.FLUSH, top5, "Flush");
        }

        List<Integer> sequencia = buscarSequencia(valores);
        if (sequencia != null) {
            return new ResultadoMao(RankingMao.SEQUENCIA, sequencia, "Sequência");
        }

        if (!trincas.isEmpty()) {
            int trinca = trincas.get(0);
            List<Integer> kickers = kickers(valores, Arrays.asList(trinca), 2);
            List<Integer> desempate = new ArrayList<>();
            desempate.add(trinca);
            desempate.addAll(kickers);
            return new ResultadoMao(RankingMao.TRINCA, desempate, "Trinca");
        }

        if (pares.size() >= 2) {
            List<Integer> doisPares = pares.subList(0, 2);
            int kicker = maiorExceto(valores, doisPares.get(0), doisPares.get(1));
            List<Integer> desempate = new ArrayList<>(doisPares);
            desempate.add(kicker);
            return new ResultadoMao(RankingMao.DOIS_PARES, desempate, "Dois Pares");
        }

        if (pares.size() == 1) {
            int par = pares.get(0);
            List<Integer> kickers = kickers(valores, Arrays.asList(par), 3);
            List<Integer> desempate = new ArrayList<>();
            desempate.add(par);
            desempate.addAll(kickers);
            return new ResultadoMao(RankingMao.PAR, desempate, "Par");
        }

        List<Integer> cartaAlta = valores.stream().distinct()
                .sorted(Collections.reverseOrder()).limit(5).collect(Collectors.toList());
        return new ResultadoMao(RankingMao.CARTA_ALTA, cartaAlta, "Carta Alta");
    }

    private List<Integer> buscarSequencia(List<Integer> valoresOriginais) {
        Set<Integer> unicos = new TreeSet<>(Collections.reverseOrder());
        unicos.addAll(valoresOriginais);
        if (unicos.contains(14)) {
            unicos.add(1);
        }
        List<Integer> ordenados = new ArrayList<>(unicos);
        for (int i = 0; i <= ordenados.size() - 5; i++) {
            boolean sequencial = true;
            for (int j = 0; j < 4; j++) {
                if (ordenados.get(i + j) - ordenados.get(i + j + 1) != 1) {
                    sequencial = false;
                    break;
                }
            }
            if (sequencial) {
                return ordenados.subList(i, i + 5);
            }
        }
        return null;
    }

    private List<Integer> chavesComContagem(Map<Integer, Integer> contagem, int qtd) {
        List<Integer> chaves = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : contagem.entrySet()) {
            if (e.getValue() == qtd) {
                chaves.add(e.getKey());
            }
        }
        chaves.sort(Collections.reverseOrder());
        return chaves;
    }

    private int maiorExceto(List<Integer> valores, Integer... excluir) {
        List<Integer> excluirLista = Arrays.asList(excluir);
        return valores.stream().filter(v -> !excluirLista.contains(v)).max(Integer::compareTo).orElse(0);
    }

    private List<Integer> kickers(List<Integer> valores, List<Integer> excluir, int qtd) {
        return valores.stream().distinct().filter(v -> !excluir.contains(v))
                .sorted(Collections.reverseOrder()).limit(qtd).collect(Collectors.toList());
    }
}
