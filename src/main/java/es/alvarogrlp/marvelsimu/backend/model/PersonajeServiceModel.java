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

public class PersonajeServiceModel extends Conexion {
    
    private static final String DATABASE_PATH = "src/main/resources/marvelSimu.db";
    private static final Map<String, PersonajeModel> cachePersonajes = new HashMap<>();
    
    // Constructor usando la ruta del archivo DB
    public PersonajeServiceModel(String rutaArchivoBD) throws SQLException {
        super(rutaArchivoBD);
    }
    
    // Constructor por defecto
    public PersonajeServiceModel() throws SQLException {
        super(DATABASE_PATH);
    }
    
    /**
     * Obtiene todos los personajes de la base de datos
     */
    public List<PersonajeModel> obtenerTodosPersonajes() {
        String sql = "SELECT * FROM personaje";
        return obtenerPersonajesPorConsulta(sql);
    }
    
    /**
     * Obtiene un personaje específico por su código
     */
    public PersonajeModel obtenerPersonajePorCodigo(String codigo) {
        // Primero verificar si está en caché
        if (cachePersonajes.containsKey(codigo)) {
            return cachePersonajes.get(codigo);
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM personaje WHERE nombre_codigo = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigo);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                PersonajeModel personaje = construirPersonajeDesdeResultSet(rs);
                cargarAtaques(conn, personaje);
                cargarPasivas(conn, personaje);
                
                // Agregar a caché y devolver
                cachePersonajes.put(codigo, personaje);
                return personaje;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Obtiene una lista de personajes basada en una consulta SQL
     */
    private List<PersonajeModel> obtenerPersonajesPorConsulta(String sql) {
        List<PersonajeModel> personajes = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PersonajeModel personaje = construirPersonajeDesdeResultSet(rs);
                cargarAtaques(conn, personaje);
                cargarPasivas(conn, personaje);
                
                personajes.add(personaje);
                // Actualizar cache
                cachePersonajes.put(personaje.getNombreCodigo(), personaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return personajes;
    }
    
    /**
     * Construye un objeto PersonajeModel a partir de un ResultSet
     */
    public PersonajeModel construirPersonajeDesdeResultSet(ResultSet rs) throws SQLException {
        PersonajeModel personaje = new PersonajeModel();
        
        // Datos básicos
        personaje.setId(rs.getInt("id"));
        personaje.setNombre(rs.getString("nombre"));
        personaje.setNombreCodigo(rs.getString("nombre_codigo"));
        personaje.setDescripcion(rs.getString("descripcion"));
        
        // Campo de transformación (nuevo)
        personaje.setEsTransformacion(rs.getBoolean("es_transformacion"));
        
        // Rutas de imágenes
        personaje.setImagenMiniatura(rs.getString("imagen_miniatura"));
        personaje.setImagenCombate(rs.getString("imagen_combate"));
        
        // Transformación
        int personajeBaseId = rs.getInt("personaje_base_id");
        if (!rs.wasNull()) {
            personaje.setPersonajeBaseId(personajeBaseId);
        }
        personaje.setDuracionTurnos(rs.getInt("duracion_turnos"));
        
        // Estadísticas
        personaje.setVida(rs.getInt("vida"));
        personaje.setFuerza(rs.getInt("fuerza"));
        personaje.setVelocidad(rs.getInt("velocidad"));
        personaje.setPoder(rs.getInt("poder"));
        
        return personaje;
    }
    
    /**
     * Carga los ataques para un personaje específico
     */
    private void cargarAtaques(Connection conn, PersonajeModel personaje) throws SQLException {
        String sql = "SELECT a.*, ta.clave as tipo_clave FROM ataque a " +
                     "JOIN tipo_ataque ta ON a.tipo_ataque_id = ta.id " +
                     "WHERE a.personaje_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaje.getId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AtaqueModel ataque = new AtaqueModel();
                    
                    // Cargar todos los datos del ataque
                    ataque.setId(rs.getInt("id"));
                    ataque.setPersonajeId(rs.getInt("personaje_id"));
                    ataque.setTipoAtaqueId(rs.getInt("tipo_ataque_id"));
                    
                    // Obtener y establecer el código directamente de la BD
                    String codigoAtaque = rs.getString("codigo");
                    ataque.setCodigo(codigoAtaque);
                    
                    // Obtener y establecer el nombre directamente de la BD
                    ataque.setNombre(rs.getString("nombre"));
                    ataque.setDanoBase(rs.getInt("dano_base"));
                    ataque.setUsosMaximos(rs.getInt("usos_maximos"));
                    ataque.setCooldownTurnos(rs.getInt("cooldown_turnos"));
                    
                    // IMPORTANTE: Establecer AMBOS campos necesarios para el tipo
                    String tipoClave = rs.getString("tipo_clave");
                    ataque.setTipoAtaqueClave(tipoClave);
                    ataque.setTipo(tipoClave);
                    
                    // Debug para verificar que se cargan correctamente
                    System.out.println("Ataque cargado: " + ataque.getNombre() + 
                                      " | Código: " + ataque.getCodigo() + 
                                      " | Tipo: " + ataque.getTipo() + 
                                      " | TipoClave: " + ataque.getTipoAtaqueClave());
                    
                    // Inicializar recursos de combate
                    ataque.resetearEstadoCombate();
                    
                    // Añadir el ataque al personaje
                    personaje.addAtaque(ataque);
                }
            }
        }
    }
    
    /**
     * Carga las pasivas para un personaje específico
     */
    private void cargarPasivas(Connection conn, PersonajeModel personaje) throws SQLException {
        String sql = "SELECT * FROM pasiva WHERE personaje_id = ?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, personaje.getId());
        ResultSet rs = stmt.executeQuery();
        
        List<PasivaModel> pasivas = new ArrayList<>();
        
        while (rs.next()) {
            PasivaModel pasiva = new PasivaModel();
            pasiva.setId(rs.getInt("id"));
            pasiva.setPersonajeId(rs.getInt("personaje_id"));
            pasiva.setNombre(rs.getString("nombre"));
            pasiva.setDescripcion(rs.getString("descripcion"));
            pasiva.setTriggerTipo(rs.getString("trigger_tipo"));
            pasiva.setEfectoTipo(rs.getString("efecto_tipo"));
            pasiva.setEfectoValor(rs.getInt("efecto_valor"));
            pasiva.setUsosMaximos(rs.getInt("usos_maximos"));
            pasiva.setCooldownTurnos(rs.getInt("cooldown_turnos"));
            
            pasivas.add(pasiva);
        }
        
        personaje.setPasivas(pasivas);
    }
    
    /**
     * Limpia la caché de personajes
     */
    public static void limpiarCache() {
        cachePersonajes.clear();
    }
}