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

        for (Processo p : getEntrada().getLsDeSequencias()) {
            LinkedList<Processo> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(p)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                int index = getIndexAleatorio(filaDePaginas.size() - 1);
                filaDePaginas.remove(index);
                filaDePaginas.add(p);

            } else {
                filaDePaginas.add(p);
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocIgualSubsGlobal() {
//        Integer quadrosPorProcesso = getTamPorAlocIgual();
//        LinkedList<ProcessoMY> historicoDeEntradas = new LinkedList<>();
//
//        for (Processo p : getEntrada().getLsDeSequencias()) {
//            LinkedList<ProcessoMY> filaDePaginas = memoria.get(p.getProcesso());
//            p = new ProcessoMY(p);
//
//            if (historicoDeEntradas.contains(p)) {
//                setQtdDeHits(getQtdDeHits() + 1);
//
//            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
//                historicoDeEntradas.add(p);
//                filaDePaginas.add(p);
//
//            } else if (historicoDeEntradas.size() == getEntrada().getQtdDeQuadros()) {
//                int index = getIndexAleatorio(historicoDeEntradas.size() - 1);
//                Processo procRemovido = historicoDeEntradas.remove(index);
//                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
//                filaDoProcessoRemovido.remove();
//
//                Processo procAdicionado = new Processo(p, procRemovido.getLocalNaMemoria());
//                historicoDeEntradas.add(procAdicionado);
//                filaDoProcessoRemovido.add(procAdicionado);
//
//            } else {
//                Boolean adicionado = false;
//                Iterator<Map.Entry<String, Queue>> mapIterator = memoria.entrySet().iterator();
//
//                while (mapIterator.hasNext() && !adicionado) {
//                    Map.Entry<String, Queue> m = mapIterator.next();
//                    if (m.getValue().size() < quadrosPorProcesso) {
//                        Processo procAdicionado = new Processo(p, m.getKey());
//                        historicoDeEntradas.add(procAdicionado);
//                        m.getValue().add(procAdicionado);
//                        adicionado = true;
//                    }
//                }
//            }
//
//        }
    }

    @Override
    public void alocProporcionalSubsLocal() {
        criarTabelaAlocProporcional();

        for (Processo p : getEntrada().getLsDeSequencias()) {
            LinkedList<Processo> filaDePaginas = memoria.get(p.getProcesso());

            if (filaDePaginas.contains(p)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(p.getProcesso())) {
                int index = getIndexAleatorio(filaDePaginas.size() - 1);
                filaDePaginas.remove(index);
                filaDePaginas.add(p);

            } else {
                filaDePaginas.add(p);
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {

    }

    @Override
    public void setTaxaDeErrosNoArquivo() {
        Integer qtdDeRequisicoes = getSaida().getQtdDeRequisicoes();
        Integer qtdDeMisses = qtdDeRequisicoes - getQtdDeHits();
        Double txDeErros = (double) qtdDeMisses / (double) qtdDeRequisicoes;
        String txDeErrosFormatado = String.format("%.2f", txDeErros);
        getSaida().getTxDeErros().put("MY", new Double(txDeErrosFormatado));
    }

    private int getIndexAleatorio(int limite) {
        Integer min = 0;
        return ThreadLocalRandom.current().nextInt(min, limite + 1);
    }

}
