package main.java.backend.es.alvarogrlp.marvelsim.model;
import java.util.Objects;

public class PersonajeModel {
    private String nombre;
    private int poder;
    private String descripcion;

    public PersonajeModel() {
    }

    public PersonajeModel(String nombre, int poder, String descripcion) {
        this.nombre = nombre;
        this.poder = poder;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPoder() {
        return this.poder;
    }

    public void setPoder(int poder) {
        this.poder = poder;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PersonajeModel nombre(String nombre) {
        setNombre(nombre);
        return this;
    }

    public PersonajeModel poder(int poder) {
        setPoder(poder);
        return this;
    }

    public PersonajeModel descripcion(String descripcion) {
        setDescripcion(descripcion);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PersonajeModel)) {
            return false;
        }
        PersonajeModel personajeModel = (PersonajeModel) o;
        return Objects.equals(nombre, personajeModel.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }    
}
