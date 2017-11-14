package politicas.fifo;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class FIFO extends PoliticaDeSubstituicao {

    private Map<String, Queue> memoria;

    public FIFO(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new TreeMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            Queue<Integer> quadros = new LinkedList<>();
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
            Queue<Processo> quadros = new LinkedList<>();
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
            Queue<Integer> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);

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
        Queue<Processo> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            Queue<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (historicoDeEntradas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getEntrada().getQtdDeQuadros()) {
                Processo procRemovido = historicoDeEntradas.remove();
                Queue<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, Queue>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, Queue> m = mapIterator.next();
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
            Queue<Integer> filaDePaginas = memoria.get(processo.getProcesso());

            if (filaDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);

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
        Queue<Processo> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {

            Queue<Processo> filaDePaginas = memoria.get(processo.getProcesso());

            if (historicoDeEntradas.contains(processo)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(processo.getProcesso()) - 1) {
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                Processo procRemovido = historicoDeEntradas.remove();
                Queue<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                processo.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(processo);
                filaDoProcessoRemovido.add(processo);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, Queue>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, Queue> m = mapIterator.next();
                    if (m.getValue().size() < getQtdDeQuadrosProporcional().get(processo.getProcesso())) {
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
    public void setTaxaDeErrosNoArquivo() {
        Integer qtdDeRequisicoes = getSaida().getQtdDeRequisicoes();
        Integer qtdDeMisses = qtdDeRequisicoes - getQtdDeHits();
        Double txDeErros = (double) qtdDeMisses / (double) qtdDeRequisicoes;
        String txDeErrosFormatado = String.format("%.2f", txDeErros);
        getSaida().getTxDeErros().put("FIFO", new Double(txDeErrosFormatado));
    }
}
