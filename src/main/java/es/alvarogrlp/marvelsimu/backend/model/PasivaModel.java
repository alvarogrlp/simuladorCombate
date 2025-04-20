package es.alvarogrlp.marvelsimu.backend.model;

import java.util.Objects;

public class PasivaModel {
    private int id;
    private int personajeId;
    private String nombre;
    private String descripcion;
    private String triggerTipo;
    private String efectoTipo;
    private int efectoValor;
    private int usosMaximos;
    private int cooldownTurnos;
    
    // Para combate
    private int usosDisponibles;
    private int cooldownRestante;
    private boolean activa;
    
    public PasivaModel() {
    }
    
    public PasivaModel(int id, int personajeId, String nombre, String descripcion, 
                     String triggerTipo, String efectoTipo, int efectoValor, 
                     int usosMaximos, int cooldownTurnos) {
        this.id = id;
        this.personajeId = personajeId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.triggerTipo = triggerTipo;
        this.efectoTipo = efectoTipo;
        this.efectoValor = efectoValor;
        this.usosMaximos = usosMaximos;
        this.cooldownTurnos = cooldownTurnos;
        
        // Inicializar para combate
        resetearEstadoCombate();
    }
    
    /**
     * Resetea los valores de contadores para iniciar o reiniciar un combate
     */
    public void resetearEstadoCombate() {
        this.usosDisponibles = (usosMaximos > 0) ? usosMaximos : Integer.MAX_VALUE;
        this.cooldownRestante = 0;
        this.activa = true;
    }
    
    /**
     * Verifica si la pasiva está disponible para activarse
     */
    public boolean estaDisponible() {
        return activa && usosDisponibles > 0 && cooldownRestante == 0;
    }
    
    /**
     * Reduce el uso disponible y aplica cooldown si corresponde
     */
    public void activar() {
        if (usosDisponibles > 0) {
            if (usosMaximos > 0) {
                usosDisponibles--;
            }
            
            if (cooldownTurnos > 0) {
                cooldownRestante = cooldownTurnos;
            }
        }
    }
    
    /**
     * Reduce el cooldown en un turno
     */
    public void reducirCooldown() {
        if (cooldownRestante > 0) {
            cooldownRestante--;
        }
    }
    
    // Getters y setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPersonajeId() {
        return personajeId;
    }
    
    public void setPersonajeId(int personajeId) {
        this.personajeId = personajeId;
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
    
    public String getTriggerTipo() {
        return triggerTipo;
    }
    
    public void setTriggerTipo(String triggerTipo) {
        this.triggerTipo = triggerTipo;
    }
    
    public String getEfectoTipo() {
        return efectoTipo;
    }
    
    public void setEfectoTipo(String efectoTipo) {
        this.efectoTipo = efectoTipo;
    }
    
    public int getEfectoValor() {
        return efectoValor;
    }
    
    public void setEfectoValor(int efectoValor) {
        this.efectoValor = efectoValor;
    }
    
    public int getUsosMaximos() {
        return usosMaximos;
    }
    
    public void setUsosMaximos(int usosMaximos) {
        this.usosMaximos = usosMaximos;
    }
    
    public int getCooldownTurnos() {
        return cooldownTurnos;
    }
    
    public void setCooldownTurnos(int cooldownTurnos) {
        this.cooldownTurnos = cooldownTurnos;
    }
    
    public int getUsosDisponibles() {
        return usosDisponibles;
    }
    
    public void setUsosDisponibles(int usosDisponibles) {
        this.usosDisponibles = usosDisponibles;
    }
    
    public int getCooldownRestante() {
        return cooldownRestante;
    }
    
    public void setCooldownRestante(int cooldownRestante) {
        this.cooldownRestante = cooldownRestante;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasivaModel that = (PasivaModel) o;
        return id == that.id && personajeId == that.personajeId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, personajeId);
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}