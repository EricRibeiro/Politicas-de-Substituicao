package politicas;

import arquivo.Entrada;
import arquivo.Saida;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.floor;
public abstract class PoliticaDeSubstituicao {

    private Entrada entrada;
    private Saida saida;
    private Integer qtdDeHits;
    private Map<String, Integer> qtdDeQuadrosProporcional;

    public PoliticaDeSubstituicao(Entrada entrada, Saida saida) {
        this.entrada = entrada;
        this.saida = saida;
        this.qtdDeHits = 0;
    }

    public PoliticaDeSubstituicao() {
        this.entrada = null;
        this.qtdDeHits = 0;
    }

    public void executarPolitica() {
        if (entrada.getTipoDeSubstituicao().equals("Local"))
            inicializarMemoriaLocal();
        else
            inicializarMemoriaGlobal();
    }

    public abstract void inicializarMemoriaLocal();

    public abstract void inicializarMemoriaGlobal();

    public abstract void alocIgualSubsLocal();

    public abstract void alocIgualSubsGlobal();

    public abstract void alocProporcionalSubsLocal();

    public abstract void alocProporcionalSubsGlobal();

    public abstract void setTaxaDeErrosNoArquivo();

    public Integer getTamPorAlocProporcional(Integer qtdDePaginasDoProcesso) {
        Double a = (double) qtdDePaginasDoProcesso / entrada.getQtdTotalDePaginas();
        Double b = floor(a * entrada.getQtdDeQuadros());
        return b.intValue();
    }

    public Integer getTamPorAlocIgual() {
        Double a = floor(entrada.getQtdDeQuadros() / entrada.getLsDePaginas().size());
        return a.intValue();
    }

    public void criarTabelaAlocProporcional() {
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

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public Saida getSaida() {
        return saida;
    }

    public void setSaida(Saida saida) {
        this.saida = saida;
    }

    public Integer getQtdDeHits() {
        return qtdDeHits;
    }

    public void setQtdDeHits(Integer qtdDeHits) {
        this.qtdDeHits = qtdDeHits;
    }

    public Map<String, Integer> getQtdDeQuadrosProporcional() {
        return qtdDeQuadrosProporcional;
    }

    public void setQtdDeQuadrosProporcional(Map<String, Integer> qtdDeQuadrosProporcional) {
        this.qtdDeQuadrosProporcional = qtdDeQuadrosProporcional;
    }
}
