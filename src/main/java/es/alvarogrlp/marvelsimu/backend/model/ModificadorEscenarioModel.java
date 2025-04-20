package es.alvarogrlp.marvelsimu.backend.model;

import java.util.Objects;

public class ModificadorEscenarioModel {
    private int id;
    private int escenarioId;
    private String atributo;
    private String modificadorTipo;
    private int valor;
    private int duracionTurnos;
    
    public ModificadorEscenarioModel() {
    }
    
    public ModificadorEscenarioModel(int id, int escenarioId, String atributo, String modificadorTipo, int valor, int duracionTurnos) {
        this.id = id;
        this.escenarioId = escenarioId;
        this.atributo = atributo;
        this.modificadorTipo = modificadorTipo;
        this.valor = valor;
        this.duracionTurnos = duracionTurnos;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEscenarioId() {
        return escenarioId;
    }
    
    public void setEscenarioId(int escenarioId) {
        this.escenarioId = escenarioId;
    }
    
    public String getAtributo() {
        return atributo;
    }
    
    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }
    
    public String getModificadorTipo() {
        return modificadorTipo;
    }
    
    public void setModificadorTipo(String modificadorTipo) {
        this.modificadorTipo = modificadorTipo;
    }
    
    public int getValor() {
        return valor;
    }
    
    public void setValor(int valor) {
        this.valor = valor;
    }
    
    public int getDuracionTurnos() {
        return duracionTurnos;
    }
    
    public void setDuracionTurnos(int duracionTurnos) {
        this.duracionTurnos = duracionTurnos;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModificadorEscenarioModel that = (ModificadorEscenarioModel) o;
        return id == that.id && escenarioId == that.escenarioId && valor == that.valor && duracionTurnos == that.duracionTurnos && Objects.equals(atributo, that.atributo) && Objects.equals(modificadorTipo, that.modificadorTipo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, escenarioId, atributo, modificadorTipo, valor, duracionTurnos);
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s %d (%d turnos)", atributo, modificadorTipo, valor, duracionTurnos);
    }
}