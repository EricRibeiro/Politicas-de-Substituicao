package politicas.lru;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class LRU extends PoliticaDeSubstituicao {

    private Map<String, LinkedList> memoria;

    public LRU(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new TreeMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<Integer> quadros = new LinkedList<>();
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
            LinkedList<Processo> quadros = new LinkedList<>();
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

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Integer> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);
                filaDePaginas.remove(processo.getPagina());
                filaDePaginas.add(processo.getPagina());

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                filaDePaginas.remove();
                filaDePaginas.add(processo.getPagina());

            } else {
                filaDePaginas.add(processo.getPagina());
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocIgualSubsGlobal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        LinkedList<Processo> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (historicoDeEntradas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int index = historicoDeEntradas.indexOf(processo);
                Processo procRemovido = historicoDeEntradas.remove(index);
                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getTamTotalPorAlocIgual()) {
                Processo procRemovido = historicoDeEntradas.remove();
                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();
                    if (m.getValue().size() < quadrosPorProcesso) {
                        processo.setLocalNaMemoria(m.getKey());
                        historicoDeEntradas.add(processo);
                        m.getValue().add(processo);
                        adicionado = true;
                    }
                }
            }

        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsLocal() {
        criarTabelaAlocProporcional();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Integer> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);
                filaDePaginas.remove(processo.getPagina());
                filaDePaginas.add(processo.getPagina());

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(processo.getProcesso())) {
                filaDePaginas.remove();
                filaDePaginas.add(processo.getPagina());

            } else {
                filaDePaginas.add(processo.getPagina());
            }

        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();
        LinkedList<Processo> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {

            LinkedList<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (historicoDeEntradas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

                int index = historicoDeEntradas.indexOf(processo);
                Processo procRemovido = historicoDeEntradas.remove(index);
                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(processo.getProcesso()) - 1) {
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                Processo procRemovido = historicoDeEntradas.remove();
                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();
                    if (m.getValue().size() < getQtdDeQuadrosProporcional().get(m.getKey())) {
                        processo.setLocalNaMemoria(m.getKey());
                        historicoDeEntradas.add(processo);
                        m.getValue().add(processo);
                        adicionado = true;
                    }
                }
            }

        }
        setTaxaDeErrosNoArquivo();
    }
}
