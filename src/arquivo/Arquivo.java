package arquivo;

import politicas.Processo;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Arquivo {

    private Entrada entrada;
    private Saida saida;
    private String linha;

    public Arquivo() {
        entrada = new Entrada();
        saida = new Saida();
    }

    public void lerEntrada() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while ((linha = reader.readLine()) != null) {
            if (linha.contains("Tamanho(quadros)="))
                setQtdDeQuadros();
            else if (linha.contains("Processos(páginas):"))
                setLsDePaginas(reader);
            else if (linha.contains("Alocação="))
                setTipoDeAlocacao();
            else if (linha.contains("Substituição="))
                setTipoDeSubstituicao();
            else if (linha.contains("Sequência:"))
                setLsDeSequencias(reader);
            else {
                System.out.println("Arquivo com formato inválido!");
                linha = null;
            }
        }

        saida.setQtdDeRequisicoes(entrada.getLsDeSequencias().size());
        reader.close();
    }

    public void escreverSaida() throws IOException {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        File file = new File(path + "/saida.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fw);
        fw.write(getSaida().toString());
        fw.close();
    }

    private void setQtdDeQuadros() {
        Integer qtdDeQuadros = new Integer(linha.substring(linha.indexOf("=") + 1));
        entrada.setQtdDeQuadros(qtdDeQuadros);
    }

    private void setLsDePaginas(BufferedReader reader) throws IOException {
        Boolean ehUltimoProcesso = false;
        String processo;
        Integer pagina;

        while (!ehUltimoProcesso) {
            linha = reader.readLine();
            if (linha.contains(";")) {
                linha = linha.replace(";", "");
                ehUltimoProcesso = true;
            }
            processo = linha.substring(0, linha.indexOf("="));
            pagina = new Integer(linha.substring(linha.indexOf("=") + 1));
            entrada.getLsDePaginas().put(processo, pagina);
        }
    }

    private void setTipoDeAlocacao() {
        String tipoDeAlocacao = linha.substring(linha.indexOf("=") + 1);
        entrada.setTipoDeAlocacao(tipoDeAlocacao);
    }

    private void setTipoDeSubstituicao() {
        String tipoDeSubstituicao = linha.substring(linha.indexOf("=") + 1);
        entrada.setTipoDeSubstituicao(tipoDeSubstituicao);
    }

    private void setLsDeSequencias(BufferedReader reader) throws IOException {
        Boolean ehUltimoProcesso = false;
        String processo;
        Integer pagina;
        Processo p;

        while (!ehUltimoProcesso) {
            linha = reader.readLine();
            if (linha.contains(";")) {
                linha = linha.replace(";", "");
                ehUltimoProcesso = true;
            }
            processo = linha.substring(0, linha.indexOf(","));
            pagina = new Integer(linha.substring(linha.indexOf(",") + 1));
            p = new Processo(processo, pagina);
            entrada.getLsDeSequencias().add(p);
        }
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public Saida getSaida() {
        return saida;
    }
}
