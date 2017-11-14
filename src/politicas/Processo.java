package politicas;

public class Processo {

    private String processo;
    private String localNaMemoria;
    private Integer pagina;

    public Processo(String processo, Integer pagina) {
        this.processo = processo;
        this.localNaMemoria = "";
        this.pagina = pagina;
    }

    @Override
    public String toString() {
        return "politicas.Processo{" +
                "processo='" + processo + '\'' +
                ", pagina=" + pagina +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Processo processo1 = (Processo) o;

        if (!processo.equals(processo1.processo)) return false;
        return pagina.equals(processo1.pagina);
    }

    @Override
    public int hashCode() {
        int result = processo.hashCode();
        result = 31 * result + pagina.hashCode();
        return result;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getLocalNaMemoria() {
        return localNaMemoria;
    }

    public void setLocalNaMemoria(String localNaMemoria) {
        this.localNaMemoria = localNaMemoria;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }
}
