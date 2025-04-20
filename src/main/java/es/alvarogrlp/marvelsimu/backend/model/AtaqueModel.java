package es.alvarogrlp.marvelsimu.backend.model;

/**
 * Modelo para representar ataques de personajes
 */
public class AtaqueModel {
    private int id;
    private int personajeId;
    private int tipoAtaqueId;
    private String tipoAtaqueClave;
    private String nombre;
    private int danoBase;
    private int usosMaximos;
    private int usosRestantes;
    private int cooldownTurnos;
    private int cooldownActual;
    
    // Constructor por defecto
    public AtaqueModel() {
        this.usosRestantes = 0;
        this.cooldownActual = 0;
    }
    
    // Constructor completo
    public AtaqueModel(int id, int personajeId, int tipoAtaqueId, String tipoAtaqueClave, 
                      String nombre, int danoBase, int usosMaximos, int cooldownTurnos) {
        this.id = id;
        this.personajeId = personajeId;
        this.tipoAtaqueId = tipoAtaqueId;
        this.tipoAtaqueClave = tipoAtaqueClave;
        this.nombre = nombre;
        this.danoBase = danoBase;
        this.usosMaximos = usosMaximos;
        this.usosRestantes = usosMaximos;
        this.cooldownTurnos = cooldownTurnos;
        this.cooldownActual = 0;
    }
    
    /**
     * Reinicia el estado de combate del ataque
     */
    public void resetearEstadoCombate() {
        this.usosRestantes = this.usosMaximos;
        this.cooldownActual = 0;
    }
    
    /**
     * Verifica si el ataque está disponible para usar
     */
    public boolean estaDisponible() {
        // Si es un ataque sin límite de usos
        if (usosMaximos == 0) {
            return cooldownActual == 0;
        }
        
        // Si es un ataque con límite de usos
        return usosRestantes > 0 && cooldownActual == 0;
    }
    
    /**
     * Consume un uso del ataque
     */
    public void consumirUso() {
        if (usosRestantes > 0) {
            usosRestantes--;
        }
        
        if (cooldownTurnos > 0) {
            cooldownActual = cooldownTurnos;
        }
    }
    
    /**
     * Actualiza el cooldown al finalizar un turno
     */
    public void finalizarTurno() {
        if (cooldownActual > 0) {
            cooldownActual--;
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
    
    public int getTipoAtaqueId() {
        return tipoAtaqueId;
    }
    
    public void setTipoAtaqueId(int tipoAtaqueId) {
        this.tipoAtaqueId = tipoAtaqueId;
    }
    
    public String getTipoAtaqueClave() {
        return tipoAtaqueClave;
    }
    
    public void setTipoAtaqueClave(String tipoAtaqueClave) {
        this.tipoAtaqueClave = tipoAtaqueClave;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getDanoBase() {
        return danoBase;
    }
    
    public void setDanoBase(int danoBase) {
        this.danoBase = danoBase;
    }
    
    public int getUsosMaximos() {
        return usosMaximos;
    }
    
    public void setUsosMaximos(int usosMaximos) {
        this.usosMaximos = usosMaximos;
        // Actualizar usos restantes si se cambia el máximo
        if (this.usosRestantes > this.usosMaximos) {
            this.usosRestantes = this.usosMaximos;
        }
    }
    
    public int getUsosRestantes() {
        return usosRestantes;
    }
    
    public void setUsosRestantes(int usosRestantes) {
        this.usosRestantes = usosRestantes;
    }
    
    public int getCooldownTurnos() {
        return cooldownTurnos;
    }
    
    public void setCooldownTurnos(int cooldownTurnos) {
        this.cooldownTurnos = cooldownTurnos;
    }
    
    public int getCooldownActual() {
        return cooldownActual;
    }
    
    public void setCooldownActual(int cooldownActual) {
        this.cooldownActual = cooldownActual;
    }
    
    @Override
    public String toString() {
        return nombre + " (Daño: " + danoBase + ", Usos: " + usosRestantes + "/" + usosMaximos + ")";
    }
}