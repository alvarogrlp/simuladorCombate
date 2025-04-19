package es.alvarogrlp.marvelsimu.backend.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersonajeModel {
    private int id;
    private String nombre;
    private String nombreCodigo;
    private String descripcion;
    
    // Estadísticas básicas
    private int vida;
    private int fuerza;
    private int velocidad;
    private int resistencia;
    private int poderMagico;
    
    // Nuevas estadísticas
    private int resistenciaFisica;
    private int resistenciaMagica;
    private int evasion;
    private int probabilidadCritico;
    private double multiplicadorCritico;
    
    // Habilidad pasiva
    private String pasivaNombre;
    private String pasivaDescripcion;
    private String pasivaTipo;
    private int pasivaValor;
    
    // Ataques con su tipo
    private int ataqueMelee;
    private int ataqueLejano;
    private int habilidad1Poder;
    private int habilidad2Poder;
    
    private String ataqueMeleeNombre;
    private String ataqueLejanoNombre;
    private String habilidad1Nombre;
    private String habilidad2Nombre;
    
    private String ataqueMeleeTipo;
    private String ataqueLejanoTipo;
    private String habilidad1Tipo;
    private String habilidad2Tipo;
    
    // Rutas de imágenes
    private String imagenCombate;
    private String imagenMiniatura;
    private String imagenCompleta;
    
    // Propiedades para el estado durante el combate
    private int vidaActual;
    private boolean derrotado;
    
    // Usos de habilidades
    private Map<String, Integer> usosHabilidad = new HashMap<>();
    private int habilidad1Usos;
    private int habilidad2Usos;
    
    // Constructor vacío
    public PersonajeModel() {
    }
    
    // Constructor completo
    public PersonajeModel(int id, String nombre, String nombreCodigo, String descripcion, 
                         int vida, int fuerza, int velocidad, int resistencia, int poderMagico,
                         int ataqueMelee, int ataqueLejano, int habilidad1Poder, int habilidad2Poder,
                         String ataqueMeleeNombre, String ataqueLejanoNombre, String habilidad1Nombre, String habilidad2Nombre,
                         String imagenCombate, String imagenMiniatura) {
        this.id = id;
        this.nombre = nombre;
        this.nombreCodigo = nombreCodigo;
        this.descripcion = descripcion;
        this.vida = vida;
        this.fuerza = fuerza;
        this.velocidad = velocidad;
        this.resistencia = resistencia;
        this.poderMagico = poderMagico;
        this.ataqueMelee = ataqueMelee;
        this.ataqueLejano = ataqueLejano;
        this.habilidad1Poder = habilidad1Poder;
        this.habilidad2Poder = habilidad2Poder;
        this.ataqueMeleeNombre = ataqueMeleeNombre;
        this.ataqueLejanoNombre = ataqueLejanoNombre;
        this.habilidad1Nombre = habilidad1Nombre;
        this.habilidad2Nombre = habilidad2Nombre;
        this.imagenCombate = imagenCombate;
        this.imagenMiniatura = imagenMiniatura;
    }
    
    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getNombreCodigo() { return nombreCodigo; }
    public void setNombreCodigo(String nombreCodigo) { this.nombreCodigo = nombreCodigo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }
    
    public int getFuerza() { return fuerza; }
    public void setFuerza(int fuerza) { this.fuerza = fuerza; }
    
    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
    
    public int getResistencia() { return resistencia; }
    public void setResistencia(int resistencia) { this.resistencia = resistencia; }
    
    public int getPoderMagico() { return poderMagico; }
    public void setPoderMagico(int poderMagico) { this.poderMagico = poderMagico; }
    
    public int getAtaqueMelee() { return ataqueMelee; }
    public void setAtaqueMelee(int ataqueMelee) { this.ataqueMelee = ataqueMelee; }
    
    public int getAtaqueLejano() { return ataqueLejano; }
    public void setAtaqueLejano(int ataqueLejano) { this.ataqueLejano = ataqueLejano; }
    
    public int getHabilidad1Poder() { return habilidad1Poder; }
    public void setHabilidad1Poder(int habilidad1Poder) { this.habilidad1Poder = habilidad1Poder; }
    
    public int getHabilidad2Poder() { return habilidad2Poder; }
    public void setHabilidad2Poder(int habilidad2Poder) { this.habilidad2Poder = habilidad2Poder; }
    
    public String getAtaqueMeleeNombre() { return ataqueMeleeNombre; }
    public void setAtaqueMeleeNombre(String ataqueMeleeNombre) { this.ataqueMeleeNombre = ataqueMeleeNombre; }
    
    public String getAtaqueLejanoNombre() { return ataqueLejanoNombre; }
    public void setAtaqueLejanoNombre(String ataqueLejanoNombre) { this.ataqueLejanoNombre = ataqueLejanoNombre; }
    
    public String getHabilidad1Nombre() { return habilidad1Nombre; }
    public void setHabilidad1Nombre(String habilidad1Nombre) { this.habilidad1Nombre = habilidad1Nombre; }
    
    public String getHabilidad2Nombre() { return habilidad2Nombre; }
    public void setHabilidad2Nombre(String habilidad2Nombre) { this.habilidad2Nombre = habilidad2Nombre; }
    
    public String getImagenCombate() { 
        return imagenCombate; 
    }
    
    public void setImagenCombate(String imagenCombate) { this.imagenCombate = imagenCombate; }
    
    public String getImagenMiniatura() { 
        return imagenMiniatura; 
    }
    
    public void setImagenMiniatura(String imagenMiniatura) { this.imagenMiniatura = imagenMiniatura; }
    
    public String getImagenCompleta() {
        return imagenCompleta;
    }
    
    public void setImagenCompleta(String imagenCompleta) {
        this.imagenCompleta = imagenCompleta;
    }
    
    public int getVidaActual() {
        return vidaActual;
    }
    
    public void setVidaActual(int vidaActual) {
        this.vidaActual = vidaActual;
        // Actualizar estado de derrotado si la vida llega a 0
        if (vidaActual <= 0) {
            this.derrotado = true;
        }
    }
    
    public boolean isDerrotado() {
        return derrotado;
    }
    
    public void setDerrotado(boolean derrotado) {
        this.derrotado = derrotado;
    }
    
    public int getResistenciaFisica() { return resistenciaFisica; }
    public void setResistenciaFisica(int resistenciaFisica) { this.resistenciaFisica = resistenciaFisica; }
    
    public int getResistenciaMagica() { return resistenciaMagica; }
    public void setResistenciaMagica(int resistenciaMagica) { this.resistenciaMagica = resistenciaMagica; }
    
    public int getEvasion() { return evasion; }
    public void setEvasion(int evasion) { this.evasion = evasion; }
    
    public int getProbabilidadCritico() { return probabilidadCritico; }
    public void setProbabilidadCritico(int probabilidadCritico) { this.probabilidadCritico = probabilidadCritico; }
    
    public double getMultiplicadorCritico() { return multiplicadorCritico; }
    public void setMultiplicadorCritico(double multiplicadorCritico) { this.multiplicadorCritico = multiplicadorCritico; }
    
    public String getPasivaNombre() { return pasivaNombre; }
    public void setPasivaNombre(String pasivaNombre) { this.pasivaNombre = pasivaNombre; }
    
    public String getPasivaDescripcion() { return pasivaDescripcion; }
    public void setPasivaDescripcion(String pasivaDescripcion) { this.pasivaDescripcion = pasivaDescripcion; }
    
    public String getPasivaTipo() { return pasivaTipo; }
    public void setPasivaTipo(String pasivaTipo) { this.pasivaTipo = pasivaTipo; }
    
    public int getPasivaValor() { return pasivaValor; }
    public void setPasivaValor(int pasivaValor) { this.pasivaValor = pasivaValor; }
    
    public String getAtaqueMeleeTipo() { return ataqueMeleeTipo; }
    public void setAtaqueMeleeTipo(String ataqueMeleeTipo) { this.ataqueMeleeTipo = ataqueMeleeTipo; }
    
    public String getAtaqueLejanoTipo() { return ataqueLejanoTipo; }
    public void setAtaqueLejanoTipo(String ataqueLejanoTipo) { this.ataqueLejanoTipo = ataqueLejanoTipo; }
    
    public String getHabilidad1Tipo() { return habilidad1Tipo; }
    public void setHabilidad1Tipo(String habilidad1Tipo) { this.habilidad1Tipo = habilidad1Tipo; }
    
    public String getHabilidad2Tipo() { return habilidad2Tipo; }
    public void setHabilidad2Tipo(String habilidad2Tipo) { this.habilidad2Tipo = habilidad2Tipo; }
    
    public int getHabilidad1Usos() {
        return habilidad1Usos;
    }
    
    public void setHabilidad1Usos(int habilidad1Usos) {
        this.habilidad1Usos = habilidad1Usos;
    }
    
    public int getHabilidad2Usos() {
        return habilidad2Usos;
    }
    
    public void setHabilidad2Usos(int habilidad2Usos) {
        this.habilidad2Usos = habilidad2Usos;
    }
    
    /**
     * Inicializa la vida del personaje al máximo
     */
    public void inicializarVida() {
        this.vidaActual = this.vida;
        this.derrotado = false;
        
        // Inicializar usos de habilidades
        inicializarUsosHabilidades();
    }
    
    /**
     * Método para que el personaje reciba daño
     * @param cantidad Cantidad de daño a recibir
     * @return Verdadero si el personaje queda derrotado
     */
    public boolean recibirDaño(int cantidad) {
        // Aplicar resistencia al daño (ejemplo simple)
        int dañoReducido = (int)(cantidad * (1.0 - (resistencia / 1000.0)));
        dañoReducido = Math.max(1, dañoReducido); // Al menos 1 de daño
        
        vidaActual -= dañoReducido;
        
        if (vidaActual <= 0) {
            vidaActual = 0;
            derrotado = true;
            return true;
        }
        
        return false;
    }
    
    // Método actualizado para recibir daño con todas las nuevas mecánicas
    public boolean recibirDaño(int cantidad, String tipoAtaque, boolean aplicarCritico) {
        // Verificar derrotado
        if (isDerrotado()) {
            return true;
        }
        
        // CORREGIR: Verificar si tipoAtaque es null para prevenir NullPointerException
        if (tipoAtaque == null) {
            tipoAtaque = "fisico"; // Valor por defecto
        }
        
        // Variables para tracking de efectos
        boolean ataqueEvadido = false;
        boolean ataqueReducido = false;
        int dañoFinal = cantidad;
        
        // Comprobación de evasión (solo para personajes con evasión)
        if (evasion > 0 && Math.random() * 100 < evasion) {
            // Evasión exitosa
            ataqueEvadido = true;
            dañoFinal = 0;
        } else if ("daño_verdadero".equals(tipoAtaque)) {
            // El daño verdadero ignora todas las reducciones y defensas
            dañoFinal = cantidad;
        } else {
            // Calcular daño recibido según tipo de ataque
            if (tipoAtaque.equals("fisico")) {
                // Aplicar resistencia física (reducida)
                dañoFinal = (int)(cantidad * (1.0 - (resistenciaFisica / 200.0)));
            } else {
                // Aplicar resistencia mágica para ataques mágicos (reducida)
                dañoFinal = (int)(cantidad * (1.0 - (resistenciaMagica / 200.0)));
            }
            
            // Aplicar habilidad pasiva según su tipo (con menor probabilidad)
            if (!ataqueEvadido) {
                switch (pasivaTipo) {
                    case "reduccion":
                    case "barrera":
                    case "armadura":
                        // Probabilidad máxima del 30% de reducir el daño
                        int probabilidadMaxReduccion = 30;
                        if (pasivaValor > probabilidadMaxReduccion) {
                            pasivaValor = probabilidadMaxReduccion;
                        }
                        
                        // Intentar reducir el daño
                        if (Math.random() * 100 < pasivaValor) {
                            dañoFinal = (int)(dañoFinal * 0.7);  // Reduce sólo un 30% del daño
                            ataqueReducido = true;
                        }
                        break;
                }
            }
            
            // Aplicar crítico si procede (aumentado)
            if (aplicarCritico) {
                dañoFinal = (int)(dañoFinal * multiplicadorCritico);
            }
            
            // Asegurar al menos 1 de daño si no fue evadido
            if (!ataqueEvadido && dañoFinal < 1) {
                dañoFinal = 1;
            }
        }
        
        // Aplicar el daño final
        vidaActual -= dañoFinal;
        
        // Comprobar si el personaje ha sido derrotado
        if (vidaActual <= 0) {
            vidaActual = 0;
            derrotado = true;
            return true;
        }
        
        return false;
    }
    
    // Método para aplicar pasivas que se activan al inicio del turno
    public void aplicarPasivasIniciaTurno() {
        if (derrotado) return;
        
        switch (pasivaTipo) {
            case "regeneracion":
                int cantidadRegeneracion = (vida * pasivaValor) / 100;
                vidaActual += cantidadRegeneracion;
                if (vidaActual > vida) vidaActual = vida;
                break;
            // Otras pasivas de inicio de turno
        }
    }
    
    /**
     * Inicializa los usos de habilidades
     */
    public void inicializarUsosHabilidades() {
        // Inicializar usos de habilidades
        usosHabilidad.put("habilidad1", 3); // 3 usos para la habilidad 1
        usosHabilidad.put("habilidad2", 2); // 2 usos para la habilidad 2 (más poderosa)
    }
    
    /**
     * Verifica si una habilidad tiene usos disponibles
     */
    public boolean tieneUsosDisponibles(String habilidad) {
        if (!usosHabilidad.containsKey(habilidad)) {
            return true; // Los ataques básicos no tienen límite
        }
        return usosHabilidad.get(habilidad) > 0;
    }
    
    /**
     * Consume un uso de la habilidad
     */
    public void consumirUsoHabilidad(String habilidad) {
        if (usosHabilidad.containsKey(habilidad) && usosHabilidad.get(habilidad) > 0) {
            usosHabilidad.put(habilidad, usosHabilidad.get(habilidad) - 1);
        }
    }
    
    /**
     * Retorna los usos restantes de una habilidad
     */
    public int getUsosRestantes(String habilidad) {
        if (!usosHabilidad.containsKey(habilidad)) {
            return -1; // Los ataques básicos no tienen límite
        }
        return usosHabilidad.get(habilidad);
    }
    
    // Método para determinar si un ataque es crítico
    public boolean esGolpeCritico() {
        return Math.random() * 100 < probabilidadCritico;
    }
    
    // Método para obtener el poder de un tipo de ataque específico
    public int getPoderAtaque(String tipoAtaque) {
        switch (tipoAtaque) {
            case "melee": return ataqueMelee;
            case "lejano": return ataqueLejano;
            case "habilidad1": return habilidad1Poder;
            case "habilidad2": return habilidad2Poder;
            default: return 0;
        }
    }
    
    /**
     * Obtiene el nombre del ataque según su tipo
     */
    public String getNombreAtaque(String tipoAtaque) {
        switch (tipoAtaque) {
            case "melee":
                return ataqueMeleeNombre;
            case "lejano":
                return ataqueLejanoNombre;
            case "habilidad1":
                return habilidad1Nombre;
            case "habilidad2":
                return habilidad2Nombre;
            default:
                return "Ataque";
        }
    }
    
    /**
     * Obtiene el tipo de daño del ataque según su tipo
     */
    public String getTipoAtaque(String tipoAtaque) {
        switch (tipoAtaque) {
            case "melee":
                return ataqueMeleeTipo;
            case "lejano":
                return ataqueLejanoTipo;
            case "habilidad1":
                return habilidad1Tipo;
            case "habilidad2":
                return habilidad2Tipo;
            default:
                return "fisico";
        }
    }

    /**
     * Regenera una cantidad de puntos de vida sin exceder el máximo
     * 
     * @param cantidad Cantidad de vida a regenerar
     * @return Vida actual después de la regeneración
     */
    public int regenerar(int cantidad) {
        if (vidaActual < vida) {
            vidaActual += cantidad;
            
            // No exceder la vida máxima
            if (vidaActual > vida) {
                vidaActual = vida;
            }
        }
        
        return vidaActual;
    }

    /**
     * Crea una copia completa de este personaje
     * @return Una nueva instancia con los mismos datos
     */
    public PersonajeModel clonar() {
        PersonajeModel clon = new PersonajeModel();
        
        // Propiedades básicas
        clon.setId(this.id);
        clon.setNombre(this.nombre);
        clon.setNombreCodigo(this.nombreCodigo);
        clon.setDescripcion(this.descripcion);
        
        // Estadísticas
        clon.setVida(this.vida);
        clon.setFuerza(this.fuerza);
        clon.setVelocidad(this.velocidad);
        clon.setResistencia(this.resistencia);
        clon.setPoderMagico(this.poderMagico);
        
        // Ataques
        clon.setAtaqueMelee(this.ataqueMelee);
        clon.setAtaqueLejano(this.ataqueLejano);
        clon.setHabilidad1Poder(this.habilidad1Poder);
        clon.setHabilidad2Poder(this.habilidad2Poder);
        clon.setAtaqueMeleeNombre(this.ataqueMeleeNombre);
        clon.setAtaqueLejanoNombre(this.ataqueLejanoNombre);
        clon.setHabilidad1Nombre(this.habilidad1Nombre);
        clon.setHabilidad2Nombre(this.habilidad2Nombre);
        
        // Imágenes
        clon.setImagenCombate(this.imagenCombate);
        clon.setImagenMiniatura(this.imagenMiniatura);
        clon.setImagenCompleta(this.imagenCompleta);
        
        // Resistencias
        clon.setResistenciaFisica(this.resistenciaFisica);
        clon.setResistenciaMagica(this.resistenciaMagica);
        clon.setEvasion(this.evasion);
        clon.setProbabilidadCritico(this.probabilidadCritico);
        clon.setMultiplicadorCritico(this.multiplicadorCritico);
        
        // Pasiva
        clon.setPasivaNombre(this.pasivaNombre);
        clon.setPasivaDescripcion(this.pasivaDescripcion);
        clon.setPasivaTipo(this.pasivaTipo);
        clon.setPasivaValor(this.pasivaValor);
        
        // Tipos de ataque
        clon.setAtaqueMeleeTipo(this.ataqueMeleeTipo);
        clon.setAtaqueLejanoTipo(this.ataqueLejanoTipo);
        clon.setHabilidad1Tipo(this.habilidad1Tipo);
        clon.setHabilidad2Tipo(this.habilidad2Tipo);
        
        // Usos de habilidades
        clon.setHabilidad1Usos(this.habilidad1Usos);
        clon.setHabilidad2Usos(this.habilidad2Usos);
        
        // Estado
        clon.setVidaActual(this.vida);
        clon.setDerrotado(false);
        
        // Inicializar usos de habilidades
        clon.usosHabilidad = new HashMap<>(this.usosHabilidad);
        
        return clon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonajeModel)) return false;
        PersonajeModel that = (PersonajeModel) o;
        return id == that.id && 
               Objects.equals(nombreCodigo, that.nombreCodigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreCodigo);
    }
    
    @Override
    public String toString() {
        return nombre + " (" + nombreCodigo + ")";
    }
}
