package politicas.my;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class MY extends PoliticaDeSubstituicao {
    private Map<String, LinkedList> memoria;

    public MY(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new HashMap<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<ProcessoMY> quadros = new LinkedList<>();
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
            LinkedList<ProcessoMY> quadros = new LinkedList<>();
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
            LinkedList<ProcessoMY> filaDePaginas = memoria.get(processo.getProcesso());
            ProcessoMY procMY = new ProcessoMY(processo);

            if (filaDePaginas.contains(procMY)) {
                setQtdDeHits(getQtdDeHits() + 1);
                procMY.setFoiUsado(true);

            } else if (filaDePaginas.size() == quadrosPorProcesso) {
                removerElemento(filaDePaginas);
                filaDePaginas.add(procMY);

            } else {
                filaDePaginas.add(procMY);
            }
        }

        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocIgualSubsGlobal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();
        LinkedList<ProcessoMY> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<ProcessoMY> filaDePaginas = memoria.get(processo.getProcesso());
            ProcessoMY procMY = new ProcessoMY(processo);

            if (historicoDeEntradas.contains(procMY)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() <= quadrosPorProcesso - 1) {
                procMY.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(procMY);
                filaDePaginas.add(procMY);

            } else if (historicoDeEntradas.size() == getTamTotalPorAlocIgual()) {
                ProcessoMY p = removerElemento(filaDePaginas);
                int index = historicoDeEntradas.indexOf(p);

                ProcessoMY procRemovido = historicoDeEntradas.remove(index);
                LinkedList<ProcessoMY> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                procMY.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(procMY);
                filaDoProcessoRemovido.add(procMY);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();
                    if (m.getValue().size() < quadrosPorProcesso) {
                        procMY.setLocalNaMemoria(m.getKey());
                        historicoDeEntradas.add(procMY);
                        m.getValue().add(procMY);
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
            LinkedList<ProcessoMY> filaDePaginas = memoria.get(processo.getProcesso());
            ProcessoMY procMY = new ProcessoMY(processo);

            if (filaDePaginas.contains(procMY)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() == getQtdDeQuadrosProporcional().get(procMY.getProcesso())) {
                removerElemento(filaDePaginas);
                filaDePaginas.add(procMY);

            } else {
                filaDePaginas.add(procMY);
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsGlobal() {
        criarTabelaAlocProporcional();

        Integer quadrosPorProcesso = getTamPorAlocIgual();
        LinkedList<ProcessoMY> historicoDeEntradas = new LinkedList<>();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<ProcessoMY> filaDePaginas = memoria.get(processo.getProcesso());
            ProcessoMY procMY = new ProcessoMY(processo);

            if (historicoDeEntradas.contains(procMY)) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(procMY.getProcesso()) - 1) {
                procMY.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(procMY);
                filaDePaginas.add(procMY);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                ProcessoMY p = removerElemento(filaDePaginas);
                int index = historicoDeEntradas.indexOf(p);

                ProcessoMY procRemovido = historicoDeEntradas.remove(index);
                LinkedList<Processo> filaDoProcessoRemovido = memoria.get(procRemovido.getLocalNaMemoria());
                filaDoProcessoRemovido.remove();

                procMY.setLocalNaMemoria(procRemovido.getLocalNaMemoria());
                historicoDeEntradas.add(procMY);
                filaDoProcessoRemovido.add(procMY);

            } else {
                Boolean adicionado = false;
                Iterator<Map.Entry<String, LinkedList>> mapIterator = memoria.entrySet().iterator();

                while (mapIterator.hasNext() && !adicionado) {
                    Map.Entry<String, LinkedList> m = mapIterator.next();
                    if (m.getValue().size() < getQtdDeQuadrosProporcional().get(m.getKey())) {
                        procMY.setLocalNaMemoria(m.getKey());
                        historicoDeEntradas.add(procMY);
                        m.getValue().add(procMY);
                        adicionado = true;
                    }
                }
            }
        }
        setTaxaDeErrosNoArquivo();
    }

    private ProcessoMY removerElemento(LinkedList<ProcessoMY> filaDePaginas) {
        ProcessoMY processo = null;
        Boolean encontrado = false;

        while (!encontrado) {
            processo = filaDePaginas.getFirst();

            if (processo.getFoiUsado()) {
                processo.setFoiUsado(false);
                filaDePaginas.remove();
                filaDePaginas.add(processo);
            } else {
                processo = filaDePaginas.remove();
                encontrado = true;
            }

        }

        return processo;
    }
}
