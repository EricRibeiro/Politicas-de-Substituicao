import arquivo.Arquivo;
import politicas.*;
import politicas.fifo.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Arquivo a = new Arquivo();
        a.lerEntrada();
        PoliticaDeSubstituicao s = new FIFO(a.getEntrada(), a.getSaida());
        s.executarPolitica();
        System.out.println();
    }
}
