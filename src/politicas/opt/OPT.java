package politicas.opt;

import arquivo.Entrada;
import arquivo.Saida;
import politicas.PoliticaDeSubstituicao;
import politicas.Processo;

import java.util.*;

public class OPT extends PoliticaDeSubstituicao {

    private Map<String, LinkedList> memoria;
    private Map<String, LinkedList> oraculoLocal;
    private LinkedList<Processo> oraculoGlobal;

    public OPT(Entrada entrada, Saida saida) {
        super(entrada, saida);
        memoria = new TreeMap<>();
        oraculoLocal = new HashMap<>();
        oraculoGlobal = new LinkedList<>();
    }

    @Override
    public void inicializarMemoriaLocal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<Integer> quadros = new LinkedList<>();
            LinkedList<Integer> quadrosDoFuturo = new LinkedList<>();

            String processo = m.getKey();
            memoria.put(processo, quadros);
            oraculoLocal.put(processo, quadrosDoFuturo);
        }
        lerPaginasUsadasLocal();
        if (getEntrada().getTipoDeAlocacao().equals("Igual"))
            alocIgualSubsLocal();
        else
            alocProporcionalSubsLocal();
    }

    @Override
    public void inicializarMemoriaGlobal() {
        for (Map.Entry<String, Integer> m : getEntrada().getLsDePaginas().entrySet()) {
            LinkedList<Integer> quadros = new LinkedList<>();
            LinkedList<Integer> quadrosDoFuturo = new LinkedList<>();

            String processo = m.getKey();
            memoria.put(processo, quadros);
            oraculoLocal.put(processo, quadrosDoFuturo);
        }
        lerPaginasUsadasGlobal();
        if (getEntrada().getTipoDeAlocacao().equals("Igual"))
            alocIgualSubsGlobal();
        else
            alocProporcionalSubsGlobal();
    }

    @Override
    public void alocIgualSubsLocal() {
        Integer quadrosPorProcesso = getTamPorAlocIgual();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Integer> lsDePaginas = memoria.get(processo.getProcesso());
            LinkedList<Integer> lsDoFuturo = oraculoLocal.get(processo.getProcesso());

            if (lsDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (lsDePaginas.size() == quadrosPorProcesso) {
                lsDePaginas.remove(getPaginaParaRemocao(lsDePaginas, lsDoFuturo));
                lsDePaginas.add(processo.getPagina());

            } else {
                lsDePaginas.add(processo.getPagina());
            }

            lsDoFuturo.remove();
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
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getTamTotalPorAlocIgual()) {
                Processo procRemovido = getPaginaParaRemocao(historicoDeEntradas);
                historicoDeEntradas.remove(procRemovido);
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
            oraculoGlobal.remove();
        }
        setTaxaDeErrosNoArquivo();
    }

    @Override
    public void alocProporcionalSubsLocal() {
        criarTabelaAlocProporcional();

        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Integer> lsDePaginas = memoria.get(processo.getProcesso());
            LinkedList<Integer> lsDoFuturo = oraculoLocal.get(processo.getProcesso());

            if (lsDePaginas.contains(processo.getPagina())) {
                setQtdDeHits(getQtdDeHits() + 1);

            } else if (lsDePaginas.size() == getQtdDeQuadrosProporcional().get(processo.getProcesso())) {
                lsDePaginas.remove(getPaginaParaRemocao(lsDePaginas, lsDoFuturo));
                lsDePaginas.add(processo.getPagina());

            } else {
                lsDePaginas.add(processo.getPagina());
            }

            lsDoFuturo.remove();
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

            } else if (filaDePaginas.size() <= getQtdDeQuadrosProporcional().get(processo.getProcesso()) - 1) {
                processo.setLocalNaMemoria(processo.getProcesso());
                historicoDeEntradas.add(processo);
                filaDePaginas.add(processo);

            } else if (historicoDeEntradas.size() == getQtdDeQuadrosProporcional().get("Total")) {
                Processo procRemovido = getPaginaParaRemocao(historicoDeEntradas);
                historicoDeEntradas.remove(procRemovido);
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
            oraculoGlobal.remove();
        }
        setTaxaDeErrosNoArquivo();
    }

    public void lerPaginasUsadasLocal() {
        for (Processo processo : getEntrada().getLsDeSequencias()) {
            LinkedList<Integer> lsDoFuturo = oraculoLocal.get(processo.getProcesso());
            lsDoFuturo.add(processo.getPagina());
        }
    }

    public void lerPaginasUsadasGlobal() {
        for (Processo processo : getEntrada().getLsDeSequencias()) {
            oraculoGlobal.add(processo);
        }
    }

    public Integer getPaginaParaRemocao(LinkedList<Integer> lsDePaginas, LinkedList<Integer> lsDoFuturo) {
        Integer ultimoIndex = -1;
        Integer pagRemovida = -1;
        Queue<Integer> filaDeProcessosFinalizados = new LinkedList<>();

        for (Integer pagina : lsDePaginas) {
            Integer index = lsDoFuturo.indexOf(pagina);

            if (index == -1)
                filaDeProcessosFinalizados.add(pagina);

            else if (index > ultimoIndex) {
                ultimoIndex = index;
                pagRemovida = pagina;
            }
        }

        if (filaDeProcessosFinalizados.size() > 0)
           pagRemovida = filaDeProcessosFinalizados.remove();

        return pagRemovida;
    }

    public Processo getPaginaParaRemocao(LinkedList<Processo> historicoDeEntradas) {
        Integer ultimoIndex = -1;
        Processo pagRemovida = null;
        Queue<Processo> filaDeProcessosFinalizados = new LinkedList<>();

        for (Processo processo : historicoDeEntradas) {
            Integer index = oraculoGlobal.indexOf(processo);

            if (index == -1)
                filaDeProcessosFinalizados.add(processo);

            else if (index > ultimoIndex) {
                ultimoIndex = index;
                pagRemovida = processo;
            }
        }

        if (filaDeProcessosFinalizados.size() > 0)
            pagRemovida = filaDeProcessosFinalizados.remove();

        return pagRemovida;
    }
}
