package arquivo;

import java.util.Map;
import java.util.TreeMap;

public class Saida {

    private Integer qtdDeRequisicoes;
    Map<String, String> txDeErros;

    public Saida(Integer qtdDeRequisicoes, Map<String, String> txDeErros) {
        this.qtdDeRequisicoes = qtdDeRequisicoes;
        this.txDeErros = txDeErros;
    }

    public Saida() {
        this.qtdDeRequisicoes = 0;
        this.txDeErros = new TreeMap<>();
    }

    public Integer getQtdDeRequisicoes() {
        return qtdDeRequisicoes;
    }

    public void setQtdDeRequisicoes(Integer qtdDeRequisicoes) {
        this.qtdDeRequisicoes = qtdDeRequisicoes;
    }

    public Map<String, String> getTxDeErros() {
        return txDeErros;
    }

    public void setTxDeErros(Map<String, String> txDeErros) {
        this.txDeErros = txDeErros;
    }
}
