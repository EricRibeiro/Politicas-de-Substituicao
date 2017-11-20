import arquivo.Arquivo;
import politicas.*;
import politicas.OPT.OPT;
import politicas.fifo.*;
import politicas.lfu.LFU;
import politicas.lru.LRU;
import politicas.mfu.MFU;
import politicas.my.MY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.lerEntrada();
        PoliticaDeSubstituicao fifo = new FIFO(arquivo.getEntrada(), arquivo.getSaida());
        PoliticaDeSubstituicao opt = new OPT(arquivo.getEntrada(), arquivo.getSaida());
        PoliticaDeSubstituicao lru = new LRU(arquivo.getEntrada(), arquivo.getSaida());
        PoliticaDeSubstituicao lfu = new LFU(arquivo.getEntrada(), arquivo.getSaida());
        PoliticaDeSubstituicao mfu = new MFU(arquivo.getEntrada(), arquivo.getSaida());
        PoliticaDeSubstituicao my = new MY(arquivo.getEntrada(), arquivo.getSaida());
        ArrayList<PoliticaDeSubstituicao> politicas = addAllPoliticas(fifo, opt, lru, lfu, mfu, my);
        executar(politicas);
        arquivo.escreverSaida();
        System.out.println(arquivo.getSaida().toString());
    }

    private static ArrayList<PoliticaDeSubstituicao> addAllPoliticas(PoliticaDeSubstituicao... politica) {
        ArrayList<PoliticaDeSubstituicao> algoritmos = new ArrayList<PoliticaDeSubstituicao>();
        algoritmos.addAll(Arrays.asList(politica));
        return algoritmos;
    }

    private static void executar(ArrayList<PoliticaDeSubstituicao> politicas) {
        politicas.forEach(politica -> {
            politica.executarPolitica();
        });
    }
}
