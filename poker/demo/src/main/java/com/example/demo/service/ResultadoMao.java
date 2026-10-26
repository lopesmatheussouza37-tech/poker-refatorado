package com.example.demo.service;

import java.util.List;

public class ResultadoMao implements Comparable<ResultadoMao> {
    private RankingMao ranking;
    private List<Integer> desempate;
    private String descricao;

    public ResultadoMao(RankingMao ranking, List<Integer> desempate, String descricao) {
        this.ranking = ranking;
        this.desempate = desempate;
        this.descricao = descricao;
    }

    public RankingMao getRanking() {
        return ranking;
    }

    public List<Integer> getDesempate() {
        return desempate;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public int compareTo(ResultadoMao outro) {
        if (this.ranking.getForca() != outro.ranking.getForca()) {
            return Integer.compare(this.ranking.getForca(), outro.ranking.getForca());
        }
        int tamanho = Math.min(this.desempate.size(), outro.desempate.size());
        for (int i = 0; i < tamanho; i++) {
            int cmp = Integer.compare(this.desempate.get(i), outro.desempate.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
