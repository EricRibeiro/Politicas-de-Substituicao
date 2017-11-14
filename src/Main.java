import arquivo.Arquivo;
import politicas.*;
import politicas.fifo.*;
import politicas.mfu.MFU;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Arquivo a = new Arquivo();
        a.lerEntrada();
        PoliticaDeSubstituicao f = new FIFO(a.getEntrada(), a.getSaida());
        PoliticaDeSubstituicao s = new MFU(a.getEntrada(), a.getSaida());
        f.executarPolitica();
        s.executarPolitica();
    }
}
