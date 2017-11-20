package politicas.my;

import politicas.Processo;

public class ProcessoMY extends Processo {

    private Boolean foiUsado;

    public ProcessoMY(String processo, Integer pagina) {
        super(processo, pagina);
        foiUsado = false;
    }

    public ProcessoMY(Processo p) {
        super(p.getProcesso(), p.getPagina());
        foiUsado = false;
    }

    public Boolean getFoiUsado() {
        return foiUsado;
    }

    public void setFoiUsado(Boolean foiUsado) {
        this.foiUsado = foiUsado;
    }
}
