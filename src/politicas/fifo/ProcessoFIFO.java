package politicas.fifo;

import politicas.Processo;

public class ProcessoFIFO extends Processo {

    protected String localNaMemoria;

    public ProcessoFIFO(String processo, Integer pagina) {
        super(processo, pagina);
        localNaMemoria = "";
    }

    public ProcessoFIFO(Processo p, String localNaMemoria) {
        super(p.getProcesso(), p.getPagina());
        this.localNaMemoria = localNaMemoria;
    }

    public ProcessoFIFO(Processo p) {
        super(p.getProcesso(), p.getPagina());
        this.localNaMemoria = "";
    }

    public String getLocalNaMemoria() {
        return localNaMemoria;
    }

    public void setLocalNaMemoria(String localNaMemoria) {
        this.localNaMemoria = localNaMemoria;
    }
}
