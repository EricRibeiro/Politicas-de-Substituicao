package politicas.mfu;

import politicas.Processo;

public class ProcessoMFU extends Processo implements Comparable<ProcessoMFU> {

    private Integer qtdDeAcessos;
    private Integer tempoDeEntrada;
    private String localNaMemoria;

    public ProcessoMFU(String processo, Integer pagina) {
        super(processo, pagina);
        qtdDeAcessos = 0;
        tempoDeEntrada = 0;
    }

    public ProcessoMFU(Processo p) {
        super(p.getProcesso(), p.getPagina());
        qtdDeAcessos = 0;
        tempoDeEntrada = 0;
    }

    public ProcessoMFU(Processo p, Integer tempoDeEntrada) {
        super(p.getProcesso(), p.getPagina());
        qtdDeAcessos = 0;
        this.tempoDeEntrada = tempoDeEntrada;
    }

    @Override
    public int compareTo(ProcessoMFU processoMFU) {
        int resultado = -1;

        if (qtdDeAcessos.compareTo(processoMFU.getQtdDeAcessos()) == 1) {
            resultado = 1;
        } else if (qtdDeAcessos.compareTo(processoMFU.getQtdDeAcessos()) == 0 && tempoDeEntrada.compareTo(processoMFU.getTempoDeEntrada()) == -1) {
            resultado = 1;
        }

        return resultado;
    }

    public Integer getQtdDeAcessos() {
        return qtdDeAcessos;
    }

    public void setQtdDeAcessos(Integer qtdDeAcessos) {
        this.qtdDeAcessos = qtdDeAcessos;
    }

    public Integer getTempoDeEntrada() {
        return tempoDeEntrada;
    }

    public void setTempoDeEntrada(Integer tempoDeEntrada) {
        this.tempoDeEntrada = tempoDeEntrada;
    }

    public String getLocalNaMemoria() {
        return localNaMemoria;
    }

    public void setLocalNaMemoria(String localNaMemoria) {
        this.localNaMemoria = localNaMemoria;
    }
}
