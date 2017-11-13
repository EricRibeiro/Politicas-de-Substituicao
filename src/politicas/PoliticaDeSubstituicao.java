package politicas;

import arquivo.Entrada;
import arquivo.Saida;

import static java.lang.Math.floor;
public abstract class PoliticaDeSubstituicao {

    private Entrada entrada;
    private Saida saida;
    private Integer qtdDeHits;

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
}
