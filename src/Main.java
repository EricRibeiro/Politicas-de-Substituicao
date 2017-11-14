import arquivo.Arquivo;
import politicas.*;
import politicas.fifo.*;
import politicas.lfu.LFU;
import politicas.mfu.MFU;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Arquivo a = new Arquivo();
        a.lerEntrada();
        PoliticaDeSubstituicao f = new FIFO(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao m = new MFU(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao l = new LFU(a.getEntrada(), a.getSaida());
        f.executarPolitica();
        m.executarPolitica();
        l.executarPolitica();
        System.out.println();
    }
}
