package es.alvarogrlp.marvelsimu.backend.model;

/**
 * Modelo para representar ataques de personajes
 */
public class AtaqueModel {
    private int id;
    private String codigo;  // Nuevo campo
    private int personajeId;
    private int tipoAtaqueId;
    private String tipoAtaqueClave;
    private String nombre;
    private int danoBase;
    private int usosMaximos;
    private int usosRestantes;
    private int cooldownTurnos;
    private int cooldownActual;
    private String tipo; // Nuevo campo

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
     * Reiniciar el estado del ataque para un nuevo combate
     */
    public void resetearEstadoCombate() {
        usosRestantes = usosMaximos;
        cooldownActual = 0;
    }

    /**
     * Verifica si el ataque está disponible para usarse
     * @return true si está disponible
     */
    public boolean estaDisponible() {
        // Si ambos son 0, siempre está disponible (infinito)
        if (usosMaximos == 0 && cooldownTurnos == 0) {
            return true;
        }

        // Verificar usos si el ataque tiene límite de usos
        if (usosMaximos > 0 && usosRestantes <= 0) {
            return false;
        }

        // Verificar cooldown si tiene cooldown
        if (cooldownTurnos > 0 && cooldownActual > 0) {
            return false;
        }

        // Si pasó todas las verificaciones, está disponible
        return true;
    }

    /**
     * Consumir un uso del ataque y activar cooldown
     */
    public void consumirUso() {
        // Si tiene límite de usos, reducir contador
        if (usosMaximos > 0) {
            usosRestantes--;
        }

        // Si tiene cooldown, activarlo
        if (cooldownTurnos > 0) {
            cooldownActual = cooldownTurnos;
        }

        System.out.println("Ataque " + nombre + " usado. Usos restantes: " + usosRestantes + ", Cooldown actual: " + cooldownActual);
    }

    /**
     * Finalizar un turno, reduciendo el cooldown actual si es necesario
     */
    public void finalizarTurno() {
        if (cooldownActual > 0) {
            cooldownActual--;
            System.out.println("Cooldown de " + nombre + " reducido a " + cooldownActual);
        }
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de ataque (ACC, AAD, habilidad_mas_poderosa, etc.)
     * @param tipo El tipo de ataque
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return nombre + " (Daño: " + danoBase + ", Usos: " + usosRestantes + "/" + usosMaximos + ")";
    }
}