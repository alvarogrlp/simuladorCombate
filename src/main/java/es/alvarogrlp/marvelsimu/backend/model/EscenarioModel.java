package es.alvarogrlp.marvelsimu.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EscenarioModel {
    private int id;
    private String nombre;
    private String descripcion;
    private List<ModificadorEscenarioModel> modificadores;
    private List<PersonajeModel> personajesDisponibles;
    
    public EscenarioModel() {
        modificadores = new ArrayList<>();
        personajesDisponibles = new ArrayList<>();
    }
    
    public EscenarioModel(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        modificadores = new ArrayList<>();
        personajesDisponibles = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<ModificadorEscenarioModel> getModificadores() {
        return modificadores;
    }
    
    public void setModificadores(List<ModificadorEscenarioModel> modificadores) {
        this.modificadores = modificadores;
    }
    
    public void addModificador(ModificadorEscenarioModel modificador) {
        this.modificadores.add(modificador);
    }
    
    public List<PersonajeModel> getPersonajesDisponibles() {
        return personajesDisponibles;
    }
    
    public void setPersonajesDisponibles(List<PersonajeModel> personajesDisponibles) {
        this.personajesDisponibles = personajesDisponibles;
    }
    
    public void addPersonajeDisponible(PersonajeModel personaje) {
        this.personajesDisponibles.add(personaje);
    }
    
    /**
     * Aplica los modificadores de este escenario a un personaje específico
     * @param personaje Personaje al que aplicar los modificadores
     * @return Copia del personaje con los modificadores aplicados
     */
    public PersonajeModel aplicarModificadores(PersonajeModel personaje) {
        PersonajeModel personajeModificado = personaje.clonar();
        
        // Verificar si el personaje es compatible con este escenario
        boolean esCompatible = false;
        for (PersonajeModel p : personajesDisponibles) {
            if (p.getNombreCodigo().equals(personaje.getNombreCodigo())) {
                esCompatible = true;
                break;
            }
        }
        
        // Si es compatible, aplicar los modificadores
        if (esCompatible) {
            for (ModificadorEscenarioModel mod : modificadores) {
                switch (mod.getAtributo()) {
                    case "vida":
                        if (mod.getModificadorTipo().equals("multiplier")) {
                            personajeModificado.setVida(personajeModificado.getVida() * mod.getValor());
                        } else if (mod.getModificadorTipo().equals("add_pct")) {
                            personajeModificado.setVida(personajeModificado.getVida() + (personajeModificado.getVida() * mod.getValor() / 100));
                        }
                        break;
                    case "fuerza":
                        if (mod.getModificadorTipo().equals("multiplier")) {
                            personajeModificado.setFuerza(personajeModificado.getFuerza() * mod.getValor());
                        } else if (mod.getModificadorTipo().equals("add_pct")) {
                            personajeModificado.setFuerza(personajeModificado.getFuerza() + (personajeModificado.getFuerza() * mod.getValor() / 100));
                        }
                        break;
                    case "velocidad":
                        if (mod.getModificadorTipo().equals("multiplier")) {
                            personajeModificado.setVelocidad(personajeModificado.getVelocidad() * mod.getValor());
                        } else if (mod.getModificadorTipo().equals("add_pct")) {
                            personajeModificado.setVelocidad(personajeModificado.getVelocidad() + (personajeModificado.getVelocidad() * mod.getValor() / 100));
                        }
                        break;
                    case "poder":
                        if (mod.getModificadorTipo().equals("multiplier")) {
                            personajeModificado.setPoder(personajeModificado.getPoder() * mod.getValor());
                        } else if (mod.getModificadorTipo().equals("add_pct")) {
                            personajeModificado.setPoder(personajeModificado.getPoder() + (personajeModificado.getPoder() * mod.getValor() / 100));
                        }
                        break;
                    // Más casos para otros atributos...
                }
            }
        }
        
        // Inicializar la vida actual con la nueva vida máxima
        personajeModificado.inicializarVida();
        
        return personajeModificado;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscenarioModel that = (EscenarioModel) o;
        return id == that.id && Objects.equals(nombre, that.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}