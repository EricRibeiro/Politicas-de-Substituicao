package politicas.my;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MY extends PoliticaDeSubstituicao {
    private Map<String, LinkedList> memoria;

    public MY(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new HashMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<Processo> quadros = new LinkedList<>();
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
            LinkedList<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                int index = getIndexAleatorio(filaDePaginas.size() - 1);
                filaDePaginas.remove(index);
                filaDePaginas.add(processo);

            } else {
                filaDePaginas.add(processo);
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

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getEntrada().getQtdDeQuadros()) {
                int index = getIndexAleatorio(historicoDeEntradas.size() - 1);
                Processo procRemovido = historicoDeEntradas.remove(index);
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
            LinkedList<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(processo.getProcesso())) {
                int index = getIndexAleatorio(filaDePaginas.size() - 1);
                filaDePaginas.remove(index);
                filaDePaginas.add(processo);

            } else {
                filaDePaginas.add(processo);
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();

        Integer quadrosPorProcesso = getTamPorAlocIgual();
        LinkedList<Processo> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (historicoDeEntradas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(processo.getProcesso()) - 1) {
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                int index = getIndexAleatorio(historicoDeEntradas.size() - 1);
                Processo procRemovido = historicoDeEntradas.remove(index);
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

    private int getIndexAleatorio(int limite) {
        Integer min = 0;
        return ThreadLocalRandom.current().nextInt(min, limite + 1);
    }
}
