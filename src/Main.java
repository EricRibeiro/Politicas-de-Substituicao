import arquivo.Arquivo;
import politicas.*;
import politicas.OPT.OPT;
import politicas.fifo.*;
import politicas.lfu.LFU;
import politicas.lru.LRU;
import politicas.mfu.MFU;
import politicas.my.MY;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Arquivo a = new Arquivo();
        a.lerEntrada();
        PoliticaDeSubstituicao fifo = new FIFO(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao opt = new OPT(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao lru = new LRU(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao lfu = new LFU(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao mfu = new MFU(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao my = new MY(a.getEntrada(), a.getSaida());
        fifo.executarPolitica();
        opt.executarPolitica();
        lru.executarPolitica();
        lfu.executarPolitica();
        mfu.executarPolitica();
        my.executarPolitica();
        System.out.println(a.getSaida().toString());
        System.out.println();
    }
}
