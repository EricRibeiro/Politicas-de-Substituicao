package politicas.lfu;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class LFU extends PoliticaDeSubstituicao {

    private Map<String, LinkedList> memoria;

    public LFU(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new HashMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<ProcessoLFU> quadros = new LinkedList<>();
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
            LinkedList<ProcessoLFU> quadros = new LinkedList<>();
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
            ProcessoLFU procLFU = new ProcessoLFU(p);
            LinkedList<ProcessoLFU> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(procLFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = filaDePaginas.indexOf(procLFU);
                procLFU = filaDePaginas.get(indexProc);
                procLFU.setQtdDeAcessos(procLFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                ProcessoLFU proc = Collections.min(filaDePaginas);
                filaDePaginas.remove(proc);

                procLFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procLFU);

            } else {
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procLFU);

            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocIgualSubsGlobal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        Integer tempoDeEntrada = 1;
        LinkedList<ProcessoLFU> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoLFU procLFU = new ProcessoLFU(p);
            LinkedList<ProcessoLFU> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(procLFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = historicoDeEntradas.indexOf(procLFU);
                procLFU = historicoDeEntradas.get(indexProc);
                procLFU.setQtdDeAcessos(procLFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                procLFU.setLocalNaMemoria(p.getProcesso());
                filaDePaginas.add(procLFU);
                historicoDeEntradas.add(procLFU);

            } else if (historicoDeEntradas.size() == getTamTotalPorAlocIgual()) {
                ProcessoLFU proc = Collections.min(historicoDeEntradas);
                LinkedList<ProcessoLFU> listaDoProcessoRemovido = memoria.get(proc.getLocalNaMemoria());
                historicoDeEntradas.remove(proc);
                listaDoProcessoRemovido.remove(proc);

                procLFU.setLocalNaMemoria(proc.getLocalNaMemoria());
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                historicoDeEntradas.add(procLFU);
                listaDoProcessoRemovido.add(procLFU);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();

                    if (m.getValue().size() < quadrosPorProcesso) {
                        procLFU.setLocalNaMemoria(m.getKey());
                        procLFU.setTempoDeEntrada(tempoDeEntrada);
                        historicoDeEntradas.add(procLFU);
                        m.getValue().add(procLFU);
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
            ProcessoLFU procLFU = new ProcessoLFU(p);
            LinkedList<ProcessoLFU> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(procLFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = filaDePaginas.indexOf(procLFU);
                procLFU = filaDePaginas.get(indexProc);
                procLFU.setQtdDeAcessos(procLFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(p.getProcesso())) {
                ProcessoLFU proc = Collections.min(filaDePaginas);
                filaDePaginas.remove(proc);

                procLFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procLFU);

            } else {
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                filaDePaginas.add(procLFU);

            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();
        Integer tempoDeEntrada = 1;
        LinkedList<ProcessoLFU> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {
            ProcessoLFU procLFU = new ProcessoLFU(p);
            LinkedList<ProcessoLFU> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(procLFU)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int indexProc = historicoDeEntradas.indexOf(procLFU);
                procLFU = historicoDeEntradas.get(indexProc);
                procLFU.setQtdDeAcessos(procLFU.getQtdDeAcessos() + 1);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(p.getProcesso()) - 1) {
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                procLFU.setLocalNaMemoria(p.getProcesso());
                filaDePaginas.add(procLFU);
                historicoDeEntradas.add(procLFU);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                ProcessoLFU proc = Collections.min(historicoDeEntradas);
                LinkedList<ProcessoLFU> listaDoProcessoRemovido = memoria.get(proc.getLocalNaMemoria());
                historicoDeEntradas.remove(proc);
                listaDoProcessoRemovido.remove(proc);

                procLFU.setLocalNaMemoria(proc.getLocalNaMemoria());
                procLFU.setTempoDeEntrada(tempoDeEntrada);
                historicoDeEntradas.add(procLFU);
                listaDoProcessoRemovido.add(procLFU);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();

                    if (m.getValue().size() < getQtdDeQuadrosProporcional().get(m.getKey())) {
                        procLFU.setLocalNaMemoria(m.getKey());
                        procLFU.setTempoDeEntrada(tempoDeEntrada);
                        historicoDeEntradas.add(procLFU);
                        m.getValue().add(procLFU);
                        adicionado = true;
                    }
                }
            }
            tempoDeEntrada++;
        }
        setTaxaDeErrosNoArquivo();
    }
}
