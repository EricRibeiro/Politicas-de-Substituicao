package politicas.fifo;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class FIFO extends PoliticaDeSubstituicao {

    private Map<String, Queue> memoria;
    private Map<String, Integer> qtdDeQuadrosProporcional;

    public FIFO(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new TreeMap<>();
    }

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

    public void inicializarMemoriaGlobal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            Queue<ProcessoFIFO> quadros = new LinkedList<>();
            String processo = m.getKey();
            memoria.put(processo, quadros);
        }
        if (getEntrada().getTipoDeAlocacao().equals("Igual"))
            alocIgualSubsGlobal();
        else
            alocProporcionalSubsGlobal();
    }

    public void alocIgualSubsLocal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();

        for (Processo p : getEntrada().getLsDeSequencias()) {

            Queue<Integer> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(p.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);
            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                filaDePaginas.remove();
                filaDePaginas.add(p.getPagina());
            } else {
                filaDePaginas.add(p.getPagina());
            }

        }

        setTaxaDeErrosNoArquivo();
    }

    public void alocIgualSubsGlobal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        Queue<ProcessoFIFO> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {

            Queue<ProcessoFIFO> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(new ProcessoFIFO(p))) {
                setQtdDeHits(getQtdDeHits() + 1);
                System.out.println(p.toString());
            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {

                ProcessoFIFO procFifo = new ProcessoFIFO(p, p.getProcesso());
                historicoDeEntradas.add(procFifo);
                filaDePaginas.add(procFifo);

            } else if (historicoDeEntradas.size() == getEntrada().getQtdDeQuadros()) {

                ProcessoFIFO procRemovido = historicoDeEntradas.remove();
                Queue<ProcessoFIFO> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                ProcessoFIFO procAdicionado = new ProcessoFIFO(p, procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(procAdicionado);
                filaDoProcessoRemovido.add(procAdicionado);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, Queue>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, Queue> m = mapIterator.next();
                    if (m.getValue().size() < quadrosPorProcesso) {
                        ProcessoFIFO procAdicionado = new ProcessoFIFO(p, m.getKey());
                        historicoDeEntradas.add(procAdicionado);
                        m.getValue().add(procAdicionado);
                        adicionado = true;
                    }
                }
            }

        }
        setTaxaDeErrosNoArquivo();
    }

    public void alocProporcionalSubsLocal() {
        criarTabelaAlocProporcional();

        for (Processo p : getEntrada().getLsDeSequencias()) {

            Queue<Integer> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(p.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);
            } else if (filaDePaginas.size() == qtdDeQuadrosProporcional.get(p.getProcesso())) {
                filaDePaginas.remove();
                filaDePaginas.add(p.getPagina());
            } else {
                filaDePaginas.add(p.getPagina());
            }

        }
        setTaxaDeErrosNoArquivo();
    }

    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();
        Queue<ProcessoFIFO> historicoDeEntradas = new LinkedList<>();

        for (Processo p : getEntrada().getLsDeSequencias()) {

            Queue<ProcessoFIFO> filaDePaginas = memoria.get(p.getProcesso());

            if (historicoDeEntradas.contains(new ProcessoFIFO(p))) {
                setQtdDeHits(getQtdDeHits() + 1);
                System.out.println(p.toString());

            } else if (filaDePaginas.size() <= qtdDeQuadrosProporcional.get(p.getProcesso()) - 1) {
                ProcessoFIFO procFifo = new ProcessoFIFO(p, p.getProcesso());
                historicoDeEntradas.add(procFifo);
                filaDePaginas.add(procFifo);

            } else if (historicoDeEntradas.size() == qtdDeQuadrosProporcional.get("Total")) {
                ProcessoFIFO procRemovido = historicoDeEntradas.remove();
                Queue<ProcessoFIFO> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                ProcessoFIFO procAdicionado = new ProcessoFIFO(p, procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(procAdicionado);
                filaDoProcessoRemovido.add(procAdicionado);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, Queue>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, Queue> m = mapIterator.next();
                    if (m.getValue().size() < qtdDeQuadrosProporcional.get(p.getProcesso())) {
                        ProcessoFIFO procAdicionado = new ProcessoFIFO(p, m.getKey());
                        historicoDeEntradas.add(procAdicionado);
                        m.getValue().add(procAdicionado);
                        adicionado = true;
                    }
                }
            }

        }
        setTaxaDeErrosNoArquivo();
    }

    public void setTaxaDeErrosNoArquivo() {
        Integer qtdDeRequisicoes = getSaida().getQtdDeRequisicoes();
        Integer qtdDeMisses = qtdDeRequisicoes - getQtdDeHits();
        Double txDeErros = (double) qtdDeMisses / (double) qtdDeRequisicoes;
        String txDeErrosFormatado = String.format("%.2f", txDeErros);
        getSaida().getTxDeErros().put("FIFO", new Double(txDeErrosFormatado));
    }

    private void criarTabelaAlocProporcional() {
        qtdDeQuadrosProporcional = new HashMap<>();
        Integer total = 0;

        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            String processo = m.getKey();
            Integer qtdDePaginas = m.getValue();
            Integer tamPorAlocProporcional = getTamPorAlocProporcional(qtdDePaginas);
            total += tamPorAlocProporcional;
            qtdDeQuadrosProporcional.put(processo, tamPorAlocProporcional);
        }

        qtdDeQuadrosProporcional.put("Total", total);
    }

}