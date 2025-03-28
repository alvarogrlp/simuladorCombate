package es.alvarogrlp.marvelsimu.backend.model;

import java.util.Objects;

public class CombateModel {
    private int id;
    private int tipoDeComabate;
    private int resultado;

    public CombateModel() {
    }

    public CombateModel(int id, int tipoDeComabate, int resultado) {
        this.id = id;
        this.tipoDeComabate = tipoDeComabate;
        this.resultado = resultado;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipoDeComabate() {
        return this.tipoDeComabate;
    }

    public void setTipoDeComabate(int tipoDeComabate) {
        this.tipoDeComabate = tipoDeComabate;
    }

    public int getResultado() {
        return this.resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public CombateModel id(int id) {
        setId(id);
        return this;
    }

    public CombateModel tipoDeComabate(int tipoDeComabate) {
        setTipoDeComabate(tipoDeComabate);
        return this;
    }

    public CombateModel resultado(int resultado) {
        setResultado(resultado);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CombateModel)) {
            return false;
        }
        CombateModel combateModel = (CombateModel) o;
        return id == combateModel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
