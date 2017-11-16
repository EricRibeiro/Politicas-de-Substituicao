package politicas.OPT;

import politicas.Processo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessoOPT extends Processo implements Comparable<ProcessoOPT> {

    private Map<String, List> paginasUsadas;

    public ProcessoOPT(String processo, Integer pagina) {
        super(processo, pagina);
        paginasUsadas = new HashMap<>();
    }

    public ProcessoOPT(Processo p) {
        super(p.getProcesso(), p.getPagina());
        paginasUsadas = new HashMap<>();
    }

    @Override
    public int compareTo(ProcessoOPT processoLFU) {
        return -1;
    }
}
