package politicas.mfu;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;
import politicas.fifo.ProcessoFIFO;

import java.util.*;

public class MFU extends PoliticaDeSubstituicao {

    private Map<String, LinkedList> memoria;

    public MFU(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new HashMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<ProcessoMFU> quadros = new LinkedList<>();
            String processo = m.getKey();
            memoria.put(processo, quadros);
        }

        if (getEntrada().getTipoDeAlocacao().equals("Igual"))
            alocIgualSubsLocal();
        else
            alocProporcionalSubsLocal();
    }

    @Override
    public void inicializarMemoriaGlobal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<ProcessoMFU> quadros = new LinkedList<>();
            String processo = m.getKey();
            memoria.put(processo, quadros);
        }

        if (getEntrada().getTipoDeAlocacao().equals("Igual"))
            alocIgualSubsGlobal();
        else
            alocProporcionalSubsGlobal();
    }

    @Override
    public void alocIgualSubsLocal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        Integer tempoDeEntrada = 1;

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoMFU procMFU = new ProcessoMFU(p);
            LinkedList<ProcessoMFU> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(procMFU)) {
                setQtdDeHits(getQtdDeHits() + 1);
                int indexProc = filaDePaginas.indexOf(procMFU);
                procMFU = filaDePaginas.remove(indexProc);
                procMFU.setQtdDeAcessos(procMFU.getQtdDeAcessos() + 1);
                filaDePaginas.add(procMFU);

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                ProcessoMFU proc = Collections.max(filaDePaginas);
                filaDePaginas.remove(proc);

                procMFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procMFU);

            } else {
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procMFU);

            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocIgualSubsGlobal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        Integer tempoDeEntrada = 1;
        LinkedList<ProcessoMFU> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoMFU procMFU = new ProcessoMFU(p);
            LinkedList<ProcessoMFU> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(procMFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = historicoDeEntradas.indexOf(procMFU);
                procMFU = historicoDeEntradas.get(indexProc);
                procMFU.setQtdDeAcessos(procMFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                procMFU.setLocalNaMemoria(p.getProcesso());
                filaDePaginas.add(procMFU);
                historicoDeEntradas.add(procMFU);

            } else if (historicoDeEntradas.size() == getEntrada().getQtdDeQuadros()) {
                ProcessoMFU proc = Collections.max(historicoDeEntradas);
                LinkedList<ProcessoMFU> listaDoProcessoRemovido = memoria.get(proc.getLocalNaMemoria());
                historicoDeEntradas.remove(proc);
                listaDoProcessoRemovido.remove(proc);

                procMFU.setLocalNaMemoria(proc.getLocalNaMemoria());
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                historicoDeEntradas.add(procMFU);
                listaDoProcessoRemovido.add(procMFU);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();

                    if (m.getValue().size() < quadrosPorProcesso) {
                        procMFU.setLocalNaMemoria(m.getKey());
                        procMFU.setTempoDeEntrada(tempoDeEntrada);
                        historicoDeEntradas.add(procMFU);
                        m.getValue().add(procMFU);
                        adicionado = true;
                    }
                }
            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsLocal() {
        criarTabelaAlocProporcional();
        Integer tempoDeEntrada = 1;

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoMFU procMFU = new ProcessoMFU(p);
            LinkedList<ProcessoMFU> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(procMFU)) {
                setQtdDeHits(getQtdDeHits() + 1);
                int indexProc = filaDePaginas.indexOf(procMFU);
                procMFU = filaDePaginas.remove(indexProc);
                procMFU.setQtdDeAcessos(procMFU.getQtdDeAcessos() + 1);
                filaDePaginas.add(procMFU);

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(p.getProcesso())) {
                ProcessoMFU proc = Collections.max(filaDePaginas);
                filaDePaginas.remove(proc);

                procMFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procMFU);

            } else {
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procMFU);

            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();
        Integer tempoDeEntrada = 1;
        LinkedList<ProcessoMFU> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoMFU procMFU = new ProcessoMFU(p);
            LinkedList<ProcessoMFU> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(procMFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = historicoDeEntradas.indexOf(procMFU);
                procMFU = historicoDeEntradas.get(indexProc);
                procMFU.setQtdDeAcessos(procMFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(p.getProcesso()) - 1) {
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                procMFU.setLocalNaMemoria(p.getProcesso());
                filaDePaginas.add(procMFU);
                historicoDeEntradas.add(procMFU);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                ProcessoMFU proc = Collections.max(historicoDeEntradas);
                LinkedList<ProcessoMFU> listaDoProcessoRemovido = memoria.get(proc.getLocalNaMemoria());
                historicoDeEntradas.remove(proc);
                listaDoProcessoRemovido.remove(proc);

                procMFU.setLocalNaMemoria(proc.getLocalNaMemoria());
                procMFU.setTempoDeEntrada(tempoDeEntrada);
                historicoDeEntradas.add(procMFU);
                listaDoProcessoRemovido.add(procMFU);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();

                    if (m.getValue().size() < getQtdDeQuadrosProporcional().get(p.getProcesso())) {
                        procMFU.setLocalNaMemoria(m.getKey());
                        procMFU.setTempoDeEntrada(tempoDeEntrada);
                        historicoDeEntradas.add(procMFU);
                        m.getValue().add(procMFU);
                        adicionado = true;
                    }
                }
            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void setTaxaDeErrosNoArquivo() {
        Integer qtdDeRequisicoes = getSaida().getQtdDeRequisicoes();
        Integer qtdDeMisses = qtdDeRequisicoes - getQtdDeHits();
        Double txDeErros = (double) qtdDeMisses / (double) qtdDeRequisicoes;
        String txDeErrosFormatado = String.format("%.2f", txDeErros);
        getSaida().getTxDeErros().put("MFU", new Double(txDeErrosFormatado));
    }
}
