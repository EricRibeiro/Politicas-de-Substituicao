package arquivo;

import politicas.Processo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Entrada {

    private Map<String, Integer> lsDePaginas;
    private ArrayList<Processo> lsDeSequencias;
    private Integer qtdDeQuadros;
    private Integer qtdTotalDePaginas;
    private String tipoDeAlocacao;
    private String tipoDeSubstituicao;

    public Entrada(Map<String, Integer> lsDePaginas, ArrayList<Processo> lsDeSequencias, Integer qtdDeQuadros, String tipoDeAlocacao, String tipoDeSubstituicao) {
        this.lsDePaginas = lsDePaginas;
        this.lsDeSequencias = lsDeSequencias;
        this.qtdDeQuadros = qtdDeQuadros;
        this.tipoDeAlocacao = tipoDeAlocacao;
        this.tipoDeSubstituicao = tipoDeSubstituicao;
    }

    public Entrada() {
        lsDePaginas = new LinkedHashMap<>();
        lsDeSequencias = new ArrayList<>();
        qtdDeQuadros = new Integer("0");
        tipoDeAlocacao = "";
        tipoDeSubstituicao = "";
        qtdTotalDePaginas = 0;
    }

    public Map<String, Integer> getLsDePaginas() {
        return lsDePaginas;
    }

    public void setLsDePaginas(Map<String, Integer> lsDePaginas) {
        this.lsDePaginas = lsDePaginas;
    }

    public ArrayList<Processo> getLsDeSequencias() {
        return lsDeSequencias;
    }

    public void setLsDeSequencias(ArrayList<Processo> lsDeSequencias) {
        this.lsDeSequencias = lsDeSequencias;
    }

    public Integer getQtdDeQuadros() {
        return qtdDeQuadros;
    }

    public void setQtdDeQuadros(Integer qtdDeQuadros) {
        this.qtdDeQuadros = qtdDeQuadros;
    }

    public Integer getQtdTotalDePaginas() {
        if (qtdTotalDePaginas == 0) {
            for (Integer i : getLsDePaginas().values()) {
                qtdTotalDePaginas += i;
            }
        }
        return qtdTotalDePaginas;
    }

    public void setQtdTotalDePaginas(Integer qtdTotalDePaginas) {
        this.qtdTotalDePaginas = qtdTotalDePaginas;
    }

    public String getTipoDeAlocacao() {
        return tipoDeAlocacao;
    }

    public void setTipoDeAlocacao(String tipoDeAlocacao) {
        this.tipoDeAlocacao = tipoDeAlocacao;
    }

    public String getTipoDeSubstituicao() {
        return tipoDeSubstituicao;
    }

    public void setTipoDeSubstituicao(String tipoDeSubstituicao) {
        this.tipoDeSubstituicao = tipoDeSubstituicao;
    }
}
