package es.alvarogrlp.marvelsimu.backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PersonajeModel {
    // Propiedades básicas
    private int id;
    private String nombre;
    private String nombreCodigo;
    private String descripcion;
    private boolean esTransformacion;
    
    // Transformación
    private Integer personajeBaseId;
    private int duracionTurnos;   // 0 = permanente
    
    // Estadísticas básicas
    private int vida;
    private int fuerza;
    private int velocidad;
    private int poder;  // Antes era poderMagico
    
    // Rutas de imágenes
    private String imagenCombate;
    private String imagenMiniatura;
    
    // Propiedades para el estado durante el combate
    private int vidaActual;
    private boolean derrotado;
    
    // Ataques y pasivas (nuevo modelo)
    private List<AtaqueModel> ataques;
    private List<PasivaModel> pasivas;
    
    // Para buffs y debuffs temporales
    private Map<String, Integer> buffsActivos;
    private Map<String, Integer> debuffsActivos;
    
    // Constructor vacío
    public PersonajeModel() {
        ataques = new ArrayList<>();
        pasivas = new ArrayList<>();
        buffsActivos = new HashMap<>();
        debuffsActivos = new HashMap<>();
        derrotado = false;
    }
    
    /**
     * Inicializa la vida del personaje al máximo y resetea el estado de combate
     */
    public void inicializarVida() {
        this.vidaActual = this.vida;
        this.derrotado = false;
        
        // Reiniciar ataques y pasivas para combate
        if (ataques != null) {
            for (AtaqueModel ataque : ataques) {
                ataque.resetearEstadoCombate();
            }
        }
        
        if (pasivas != null) {
            for (PasivaModel pasiva : pasivas) {
                pasiva.resetearEstadoCombate();
            }
        }
        
        // Limpiar buffs y debuffs
        buffsActivos.clear();
        debuffsActivos.clear();
    }
    
    /**
     * Método para que el personaje reciba daño
     * @param cantidad Cantidad de daño a recibir
     * @param tipoAtaque Tipo de ataque (para resistencias)
     * @param aplicarCritico Si debe aplicarse daño crítico
     * @return Verdadero si el personaje queda derrotado
     */
    public boolean recibirDaño(int cantidad, String tipoAtaque, boolean aplicarCritico) {
        // Verificar derrotado
        if (isDerrotado()) {
            return true;
        }
        
        // Comprobar buff de inmunidad
        if (buffsActivos.containsKey("inmunidad") && buffsActivos.get("inmunidad") > 0) {
            return false;
        }
        
        // Comprobar posibles pasivas (escudo, reducción, etc)
        int dañoFinal = cantidad;
        for (PasivaModel pasiva : pasivas) {
            if (pasiva.estaDisponible() && pasiva.getTriggerTipo().equals("on_damage_taken")) {
                if (pasiva.getEfectoTipo().equals("reduce_damage_pct")) {
                    dañoFinal = dañoFinal * (100 - pasiva.getEfectoValor()) / 100;
                    pasiva.activar();
                }
            }
        }
        
        // Aplicar el daño final
        vidaActual -= dañoFinal;
        
        // Comprobar si el personaje ha sido derrotado
        if (vidaActual <= 0) {
            // Comprobar pasiva de supervivencia
            boolean revivido = false;
            for (PasivaModel pasiva : pasivas) {
                if (pasiva.estaDisponible() && pasiva.getTriggerTipo().equals("on_fatal_damage")) {
                    if (pasiva.getEfectoTipo().equals("revive_pct")) {
                        vidaActual = vida * pasiva.getEfectoValor() / 100;
                        pasiva.activar();
                        revivido = true;
                        break;
                    }
                }
            }
            
            if (!revivido) {
                vidaActual = 0;
                derrotado = true;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Método simplificado para que el personaje reciba daño
     * @param cantidad Cantidad de daño a recibir
     * @return Verdadero si el personaje queda derrotado
     */
    public boolean recibirDano(int cantidad) {
        // Verificar derrotado
        if (isDerrotado()) {
            return true;
        }
        
        // Aplicar el daño
        vidaActual -= cantidad;
        
        // Comprobar si el personaje ha sido derrotado
        if (vidaActual <= 0) {
            vidaActual = 0;
            derrotado = true;
            return true;
        }
        
        return false;
    }
    
    /**
     * Regenera una cantidad de puntos de vida sin exceder el máximo
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
     * Obtiene un ataque específico por su tipo
     */
    public AtaqueModel getAtaquePorTipo(String tipoAtaque) {
        for (AtaqueModel ataque : ataques) {
            if (ataque.getTipoAtaqueClave().equals(tipoAtaque)) {
                return ataque;
            }
        }
        return null;
    }
    
    /**
     * Obtiene una pasiva específica por su nombre
     */
    public PasivaModel getPasivaPorNombre(String nombre) {
        for (PasivaModel pasiva : pasivas) {
            if (pasiva.getNombre().equals(nombre)) {
                return pasiva;
            }
        }
        return null;
    }
    
    /**
     * Crea una copia completa de este personaje para usarla en combate
     */
    public PersonajeModel clonar() {
        PersonajeModel clon = new PersonajeModel();
        
        // Propiedades básicas
        clon.setId(this.id);
        clon.setNombre(this.nombre);
        clon.setNombreCodigo(this.nombreCodigo);
        clon.setDescripcion(this.descripcion);
        clon.setEsTransformacion(this.esTransformacion);
        
        // Transformación
        clon.setPersonajeBaseId(this.personajeBaseId);
        clon.setDuracionTurnos(this.duracionTurnos);
        
        // Estadísticas
        clon.setVida(this.vida);
        clon.setFuerza(this.fuerza);
        clon.setVelocidad(this.velocidad);
        clon.setPoder(this.poder);
        
        // Imágenes
        clon.setImagenCombate(this.imagenCombate);
        clon.setImagenMiniatura(this.imagenMiniatura);
        
        // Clonar ataques
        List<AtaqueModel> ataquesClon = new ArrayList<>();
        for (AtaqueModel ataque : this.ataques) {
            AtaqueModel ataqueClon = new AtaqueModel(
                ataque.getId(),
                ataque.getPersonajeId(),
                ataque.getTipoAtaqueId(),
                ataque.getTipoAtaqueClave(),
                ataque.getNombre(),
                ataque.getDanoBase(),
                ataque.getUsosMaximos(),
                ataque.getCooldownTurnos()
            );
            ataquesClon.add(ataqueClon);
        }
        clon.setAtaques(ataquesClon);
        
        // Clonar pasivas
        List<PasivaModel> pasivasClon = new ArrayList<>();
        for (PasivaModel pasiva : this.pasivas) {
            PasivaModel pasivaClon = new PasivaModel(
                pasiva.getId(),
                pasiva.getPersonajeId(),
                pasiva.getNombre(),
                pasiva.getDescripcion(),
                pasiva.getTriggerTipo(),
                pasiva.getEfectoTipo(),
                pasiva.getEfectoValor(),
                pasiva.getUsosMaximos(),
                pasiva.getCooldownTurnos()
            );
            pasivasClon.add(pasivaClon);
        }
        clon.setPasivas(pasivasClon);
        
        // Estado
        clon.inicializarVida();
        
        return clon;
    }
    
    // Getters y setters
    
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
    
    public String getNombreCodigo() {
        return nombreCodigo;
    }
    
    public void setNombreCodigo(String nombreCodigo) {
        this.nombreCodigo = nombreCodigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public boolean isEsTransformacion() {
        return esTransformacion;
    }
    
    public void setEsTransformacion(boolean esTransformacion) {
        this.esTransformacion = esTransformacion;
    }
    
    public Integer getPersonajeBaseId() {
        return personajeBaseId;
    }
    
    public void setPersonajeBaseId(Integer personajeBaseId) {
        this.personajeBaseId = personajeBaseId;
    }
    
    public int getDuracionTurnos() {
        return duracionTurnos;
    }
    
    public void setDuracionTurnos(int duracionTurnos) {
        this.duracionTurnos = duracionTurnos;
    }
    
    public int getVida() {
        return vida;
    }
    
    public void setVida(int vida) {
        this.vida = vida;
    }
    
    public int getFuerza() {
        return fuerza;
    }
    
    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }
    
    public int getVelocidad() {
        return velocidad;
    }
    
    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
    
    public int getPoder() {
        return poder;
    }
    
    public void setPoder(int poder) {
        this.poder = poder;
    }
    
    public String getImagenCombate() {
        return imagenCombate;
    }
    
    public void setImagenCombate(String imagenCombate) {
        this.imagenCombate = imagenCombate;
    }
    
    public String getImagenMiniatura() {
        return imagenMiniatura;
    }
    
    public void setImagenMiniatura(String imagenMiniatura) {
        this.imagenMiniatura = imagenMiniatura;
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
    
    public List<AtaqueModel> getAtaques() {
        return ataques;
    }
    
    public void setAtaques(List<AtaqueModel> ataques) {
        this.ataques = ataques;
    }
    
    public List<PasivaModel> getPasivas() {
        return pasivas;
    }
    
    public void setPasivas(List<PasivaModel> pasivas) {
        this.pasivas = pasivas;
    }
    
    public Map<String, Integer> getBuffsActivos() {
        return buffsActivos;
    }
    
    public void setBuffsActivos(Map<String, Integer> buffsActivos) {
        this.buffsActivos = buffsActivos;
    }
    
    public Map<String, Integer> getDebuffsActivos() {
        return debuffsActivos;
    }
    
    public void setDebuffsActivos(Map<String, Integer> debuffsActivos) {
        this.debuffsActivos = debuffsActivos;
    }
    
    // Métodos de compatibilidad para facilitar la transición al nuevo modelo
    // Estos métodos mapean las propiedades antiguas a las nuevas estructuras
    
    public int getAtaqueMelee() {
        AtaqueModel ataque = getAtaquePorTipo("ACC");
        return ataque != null ? ataque.getDanoBase() : 0;
    }

    public void setAtaqueMelee(int ataqueMelee) {
        AtaqueModel ataque = getAtaquePorTipo("ACC");
        if (ataque != null) {
            ataque.setDanoBase(ataqueMelee);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("ACC");
            nuevoAtaque.setNombre("Ataque Cuerpo a Cuerpo");
            nuevoAtaque.setDanoBase(ataqueMelee);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public int getAtaqueLejano() {
        AtaqueModel ataque = getAtaquePorTipo("AAD");
        return ataque != null ? ataque.getDanoBase() : 0;
    }

    public void setAtaqueLejano(int ataqueLejano) {
        AtaqueModel ataque = getAtaquePorTipo("AAD");
        if (ataque != null) {
            ataque.setDanoBase(ataqueLejano);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("AAD");
            nuevoAtaque.setNombre("Ataque a Distancia");
            nuevoAtaque.setDanoBase(ataqueLejano);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public int getHabilidad1Poder() {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_mas_poderosa");
        return ataque != null ? ataque.getDanoBase() : 0;
    }

    public void setHabilidad1Poder(int habilidad1Poder) {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_mas_poderosa");
        if (ataque != null) {
            ataque.setDanoBase(habilidad1Poder);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("habilidad_mas_poderosa");
            nuevoAtaque.setNombre("Habilidad 1");
            nuevoAtaque.setDanoBase(habilidad1Poder);
            nuevoAtaque.setUsosMaximos(3);
            nuevoAtaque.setCooldownTurnos(1);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public int getHabilidad2Poder() {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_caracteristica");
        return ataque != null ? ataque.getDanoBase() : 0;
    }

    public void setHabilidad2Poder(int habilidad2Poder) {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_caracteristica");
        if (ataque != null) {
            ataque.setDanoBase(habilidad2Poder);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("habilidad_caracteristica");
            nuevoAtaque.setNombre("Habilidad 2");
            nuevoAtaque.setDanoBase(habilidad2Poder);
            nuevoAtaque.setUsosMaximos(2);
            nuevoAtaque.setCooldownTurnos(2);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public String getAtaqueMeleeNombre() {
        AtaqueModel ataque = getAtaquePorTipo("ACC");
        return ataque != null ? ataque.getNombre() : "";
    }

    public void setAtaqueMeleeNombre(String ataqueMeleeNombre) {
        AtaqueModel ataque = getAtaquePorTipo("ACC");
        if (ataque != null) {
            ataque.setNombre(ataqueMeleeNombre);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("ACC");
            nuevoAtaque.setNombre(ataqueMeleeNombre);
            nuevoAtaque.setDanoBase(50); // Valor por defecto
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public String getAtaqueLejanoNombre() {
        AtaqueModel ataque = getAtaquePorTipo("AAD");
        return ataque != null ? ataque.getNombre() : "";
    }

    public void setAtaqueLejanoNombre(String ataqueLejanoNombre) {
        AtaqueModel ataque = getAtaquePorTipo("AAD");
        if (ataque != null) {
            ataque.setNombre(ataqueLejanoNombre);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("AAD");
            nuevoAtaque.setNombre(ataqueLejanoNombre);
            nuevoAtaque.setDanoBase(40); // Valor por defecto
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public String getHabilidad1Nombre() {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_mas_poderosa");
        return ataque != null ? ataque.getNombre() : "";
    }

    public void setHabilidad1Nombre(String habilidad1Nombre) {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_mas_poderosa");
        if (ataque != null) {
            ataque.setNombre(habilidad1Nombre);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("habilidad_mas_poderosa");
            nuevoAtaque.setNombre(habilidad1Nombre);
            nuevoAtaque.setDanoBase(100); // Valor por defecto
            nuevoAtaque.setUsosMaximos(3);
            nuevoAtaque.setCooldownTurnos(1);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    public String getHabilidad2Nombre() {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_caracteristica");
        return ataque != null ? ataque.getNombre() : "";
    }

    public void setHabilidad2Nombre(String habilidad2Nombre) {
        AtaqueModel ataque = getAtaquePorTipo("habilidad_caracteristica");
        if (ataque != null) {
            ataque.setNombre(habilidad2Nombre);
        } else {
            // Crear un nuevo ataque si no existe
            AtaqueModel nuevoAtaque = new AtaqueModel();
            nuevoAtaque.setPersonajeId(this.id);
            nuevoAtaque.setTipoAtaqueClave("habilidad_caracteristica");
            nuevoAtaque.setNombre(habilidad2Nombre);
            nuevoAtaque.setDanoBase(150); // Valor por defecto
            nuevoAtaque.setUsosMaximos(2);
            nuevoAtaque.setCooldownTurnos(2);
            nuevoAtaque.resetearEstadoCombate();
            if (this.ataques == null) {
                this.ataques = new ArrayList<>();
            }
            this.ataques.add(nuevoAtaque);
        }
    }

    // Estos métodos son menos importantes pero se mantienen por compatibilidad
    
    public String getAtaqueMeleeTipo() {
        return "fisico"; // Valor por defecto para compatibilidad
    }

    public void setAtaqueMeleeTipo(String ataqueMeleeTipo) {
        // Los tipos de ataques ahora se gestionan de otra manera
    }

    public String getAtaqueLejanoTipo() {
        return "fisico"; // Valor por defecto para compatibilidad
    }

    public void setAtaqueLejanoTipo(String ataqueLejanoTipo) {
        // Los tipos de ataques ahora se gestionan de otra manera
    }

    public String getHabilidad1Tipo() {
        return "fisico"; // Valor por defecto para compatibilidad
    }

    public void setHabilidad1Tipo(String habilidad1Tipo) {
        // Los tipos de ataques ahora se gestionan de otra manera
    }

    public String getHabilidad2Tipo() {
        return "fisico"; // Valor por defecto para compatibilidad
    }

    public void setHabilidad2Tipo(String habilidad2Tipo) {
        // Los tipos de ataques ahora se gestionan de otra manera
    }

    public String getPasivaNombre() {
        if (pasivas != null && !pasivas.isEmpty()) {
            return pasivas.get(0).getNombre();
        }
        return "";
    }

    public void setPasivaNombre(String pasivaNombre) {
        if (pasivas == null || pasivas.isEmpty()) {
            PasivaModel nuevaPasiva = new PasivaModel();
            nuevaPasiva.setPersonajeId(this.id);
            nuevaPasiva.setNombre(pasivaNombre);
            nuevaPasiva.setEfectoTipo("other");
            nuevaPasiva.setTriggerTipo("passive");
            nuevaPasiva.setEfectoValor(0);
            List<PasivaModel> nuevasPasivas = new ArrayList<>();
            nuevasPasivas.add(nuevaPasiva);
            this.pasivas = nuevasPasivas;
        } else {
            pasivas.get(0).setNombre(pasivaNombre);
        }
    }

    public String getPasivaDescripcion() {
        if (pasivas != null && !pasivas.isEmpty()) {
            return pasivas.get(0).getDescripcion();
        }
        return "";
    }

    public void setPasivaDescripcion(String pasivaDescripcion) {
        if (pasivas != null && !pasivas.isEmpty()) {
            pasivas.get(0).setDescripcion(pasivaDescripcion);
        }
    }

    public String getPasivaTipo() {
        if (pasivas != null && !pasivas.isEmpty()) {
            return pasivas.get(0).getEfectoTipo();
        }
        return "other";
    }

    public void setPasivaTipo(String pasivaTipo) {
        if (pasivas != null && !pasivas.isEmpty()) {
            // Convertir el tipo antiguo al nuevo formato
            switch (pasivaTipo) {
                case "armadura":
                    pasivas.get(0).setTriggerTipo("on_damage_taken");
                    pasivas.get(0).setEfectoTipo("reduce_damage_pct");
                    break;
                case "barrera":
                    pasivas.get(0).setTriggerTipo("on_start_combat");
                    pasivas.get(0).setEfectoTipo("shield_pct");
                    break;
                case "regeneracion":
                    pasivas.get(0).setTriggerTipo("on_turn_start");
                    pasivas.get(0).setEfectoTipo("heal_pct");
                    break;
                case "contraataque":
                    pasivas.get(0).setTriggerTipo("on_damage_taken");
                    pasivas.get(0).setEfectoTipo("counter_pct");
                    break;
                case "critico":
                    pasivas.get(0).setTriggerTipo("on_attack");
                    pasivas.get(0).setEfectoTipo("critical_chance_pct");
                    break;
                default:
                    pasivas.get(0).setTriggerTipo("passive");
                    pasivas.get(0).setEfectoTipo("other");
            }
        }
    }

    public int getPasivaValor() {
        if (pasivas != null && !pasivas.isEmpty()) {
            return pasivas.get(0).getEfectoValor();
        }
        return 0;
    }

    public void setPasivaValor(int pasivaValor) {
        if (pasivas != null && !pasivas.isEmpty()) {
            pasivas.get(0).setEfectoValor(pasivaValor);
        }
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
