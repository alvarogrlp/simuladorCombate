package es.alvarogrlp.marvelsimu.backend.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.model.abtrastas.Conexion;
import es.alvarogrlp.marvelsimu.backend.util.DatabaseUtil;

public class PersonajeServiceModel extends Conexion {
    
    private static final Map<String, PersonajeModel> cachePersonajes = new HashMap<>();
    
    // Constructor usando la ruta del archivo DB
    public PersonajeServiceModel(String rutaArchivoBD) throws SQLException {
        super(rutaArchivoBD);
    }
    
    // Constructor por defecto
    public PersonajeServiceModel() {
        super();
    }
    
    /**
     * Obtiene todos los personajes de la base de datos
     */
    public List<PersonajeModel> obtenerTodosPersonajes() throws SQLException {
        String sql = "SELECT * FROM personajes";
        return obtenerPersonajesPorConsulta(sql);
    }
    
    /**
     * Obtiene un personaje específico por su código
     */
    public PersonajeModel obtenerPersonajePorCodigo(String codigo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM personajes WHERE nombre_codigo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigo);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Crear y devolver el personaje...
                PersonajeModel personaje = new PersonajeModel();
                // Asignar propiedades desde el ResultSet...
                personaje.setId(rs.getInt("id"));
                personaje.setNombre(rs.getString("nombre"));
                personaje.setNombreCodigo(rs.getString("nombre_codigo"));
                personaje.setDescripcion(rs.getString("descripcion"));
                
                // Estadísticas básicas
                personaje.setVida(rs.getInt("vida"));
                personaje.setFuerza(rs.getInt("fuerza"));
                personaje.setVelocidad(rs.getInt("velocidad"));
                personaje.setResistencia(rs.getInt("resistencia"));
                personaje.setPoderMagico(rs.getInt("poder_magico"));
                
                // ... (asignar el resto de propiedades)
                
                return personaje;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Cerrar los recursos en orden inverso
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            // Devolver la conexión al pool
            if (conn != null) {
                DatabaseUtil.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Obtiene una lista de personajes basada en una consulta SQL
     */
    private List<PersonajeModel> obtenerPersonajesPorConsulta(String sql) throws SQLException {
        List<PersonajeModel> personajes = new ArrayList<>();
        
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PersonajeModel personaje = mapearPersonajeDesdeResultSet(rs);
                personajes.add(personaje);
                // Actualizar cache
                cachePersonajes.put(personaje.getNombreCodigo(), personaje);
            }
        }
        
        return personajes;
    }
    
    /**
     * Mapea un personaje desde el ResultSet de la base de datos
     */
    private PersonajeModel mapearPersonajeDesdeResultSet(ResultSet rs) throws SQLException {
        PersonajeModel personaje = new PersonajeModel();
        
        // Mapear propiedades básicas
        personaje.setId(rs.getInt("id"));
        personaje.setNombre(rs.getString("nombre"));
        personaje.setNombreCodigo(rs.getString("nombre_codigo"));
        personaje.setDescripcion(rs.getString("descripcion"));
        personaje.setVida(rs.getInt("vida"));
        personaje.setFuerza(rs.getInt("fuerza"));
        personaje.setVelocidad(rs.getInt("velocidad"));
        personaje.setResistencia(rs.getInt("resistencia"));
        personaje.setPoderMagico(rs.getInt("poder_magico"));
        personaje.setAtaqueMelee(rs.getInt("ataque_melee"));
        personaje.setAtaqueLejano(rs.getInt("ataque_lejano"));
        personaje.setHabilidad1Poder(rs.getInt("habilidad1_poder"));
        personaje.setHabilidad2Poder(rs.getInt("habilidad2_poder"));
        personaje.setAtaqueMeleeNombre(rs.getString("ataque_melee_nombre"));
        personaje.setAtaqueLejanoNombre(rs.getString("ataque_lejano_nombre"));
        personaje.setHabilidad1Nombre(rs.getString("habilidad1_nombre"));
        personaje.setHabilidad2Nombre(rs.getString("habilidad2_nombre"));
        
        // AÑADIR: Mapeo de resistencias, críticos y otras stats que faltan
        personaje.setResistenciaFisica(rs.getInt("resistencia_fisica"));
        personaje.setResistenciaMagica(rs.getInt("resistencia_magica"));
        personaje.setEvasion(rs.getInt("evasion"));
        personaje.setProbabilidadCritico(rs.getInt("probabilidad_critico"));
        personaje.setMultiplicadorCritico(rs.getDouble("multiplicador_critico"));
        
        // AÑADIR: Mapeo de la pasiva
        personaje.setPasivaNombre(rs.getString("pasiva_nombre"));
        personaje.setPasivaDescripcion(rs.getString("pasiva_descripcion"));
        personaje.setPasivaTipo(rs.getString("pasiva_tipo"));
        personaje.setPasivaValor(rs.getInt("pasiva_valor"));
        
        // AÑADIR: Mapeo de los tipos de ataque
        personaje.setAtaqueMeleeTipo(rs.getString("ataque_melee_tipo"));
        personaje.setAtaqueLejanoTipo(rs.getString("ataque_lejano_tipo"));
        personaje.setHabilidad1Tipo(rs.getString("habilidad1_tipo"));
        personaje.setHabilidad2Tipo(rs.getString("habilidad2_tipo"));
        
        // AÑADIR: Mapeo de los usos de las habilidades
        personaje.setHabilidad1Usos(rs.getInt("usos_habilidad1"));
        personaje.setHabilidad2Usos(rs.getInt("usos_habilidad2"));
        
        // IMPORTANTE: Asignar correctamente las rutas de imágenes
        String imagenCombate = rs.getString("imagen_combate");
        String imagenMiniatura = rs.getString("imagen_miniatura");
        
        System.out.println("Cargando de BD personaje: " + personaje.getNombre());
        System.out.println("  - imagen_combate de BD: " + imagenCombate);
        System.out.println("  - imagen_miniatura de BD: " + imagenMiniatura);
        
        // Si no hay rutas en la BD, generar basadas en códigos de personaje
        if (imagenCombate == null || imagenCombate.isEmpty()) {
            String nombreCodigo = personaje.getNombreCodigo().toLowerCase().replaceAll("\\s+", "");
            
            // Mapeo de nombres de personajes a códigos de archivo
            String characterCode = null;
            if (nombreCodigo.contains("hulk")) {
                characterCode = "hulk";
            } else if (nombreCodigo.contains("spider")) {
                characterCode = "spiderman";
            } else if (nombreCodigo.contains("iron")) {
                characterCode = "ironman";
            } else if (nombreCodigo.contains("cap") || nombreCodigo.contains("amer")) {
                characterCode = "captainamerica";
            } else if (nombreCodigo.contains("strange") || nombreCodigo.contains("doctor")) {
                characterCode = "doctorstrange";
            } else if (nombreCodigo.contains("magik")) {
                characterCode = "magik";
            }
            
            if (characterCode != null) {
                imagenCombate = "images/Ingame/" + characterCode + "-ingame.png";
                System.out.println("  - Generando imagen_combate: " + imagenCombate);
            }
        }
        
        if (imagenMiniatura == null || imagenMiniatura.isEmpty()) {
            imagenMiniatura = "images/Personajes/" + personaje.getNombreCodigo().toLowerCase().replaceAll("\\s+", "") + ".png";
            System.out.println("  - Generando imagen_miniatura: " + imagenMiniatura);
        }
        
        personaje.setImagenCombate(imagenCombate);
        personaje.setImagenMiniatura(imagenMiniatura);
        
        return personaje;
    }
    
    /**
     * Limpia la caché de personajes
     */
    public static void limpiarCache() {
        cachePersonajes.clear();
    }
    
    /**
     * Obtiene todos los personajes utilizando la conexión heredada
     */
    public List<PersonajeModel> getPersonajes() {
        List<PersonajeModel> personajes = new ArrayList<>();
        
        try (Connection conn = this.getConnection()) {
            String query = "SELECT * FROM personajes";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PersonajeModel personaje = mapearPersonajeDesdeResultSet(rs);
                personajes.add(personaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return personajes;
    }
}