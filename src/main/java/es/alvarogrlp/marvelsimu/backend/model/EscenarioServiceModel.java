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

public class EscenarioServiceModel extends Conexion {
    
    private static final String DATABASE_PATH = "src/main/resources/marvelSimu.db";
    private static final Map<Integer, EscenarioModel> cacheEscenarios = new HashMap<>();
    
    public EscenarioServiceModel() throws SQLException {
        super(DATABASE_PATH);
    }
    
    public EscenarioServiceModel(String rutaArchivoBD) throws SQLException {
        super(rutaArchivoBD);
    }
    
    /**
     * Obtiene todos los escenarios disponibles en la base de datos
     * @return Lista de escenarios completos con sus modificadores
     */
    public List<EscenarioModel> obtenerTodosEscenarios() throws SQLException {
        List<EscenarioModel> escenarios = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM escenario";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                EscenarioModel escenario = mapearEscenarioDesdeResultSet(rs);
                
                // Cargar los modificadores para este escenario
                cargarModificadoresEscenario(conn, escenario);
                
                // Cargar los personajes disponibles para este escenario
                cargarPersonajesDisponibles(conn, escenario);
                
                escenarios.add(escenario);
                cacheEscenarios.put(escenario.getId(), escenario);
            }
            
            return escenarios;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            cerrar();
        }
    }
    
    /**
     * Obtiene un escenario específico por su ID
     * @param id ID del escenario a buscar
     * @return Escenario completo con modificadores
     */
    public EscenarioModel obtenerEscenarioPorId(int id) throws SQLException {
        // Primero ver si está en caché
        if (cacheEscenarios.containsKey(id)) {
            return cacheEscenarios.get(id);
        }
        
        Connection conn = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM escenario WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                EscenarioModel escenario = mapearEscenarioDesdeResultSet(rs);
                
                // Cargar los modificadores para este escenario
                cargarModificadoresEscenario(conn, escenario);
                
                // Cargar los personajes disponibles para este escenario
                cargarPersonajesDisponibles(conn, escenario);
                
                cacheEscenarios.put(escenario.getId(), escenario);
                return escenario;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            cerrar();
        }
    }
    
    /**
     * Obtiene un escenario específico por su nombre
     * @param nombre Nombre del escenario a buscar
     * @return Escenario completo con modificadores
     */
    public EscenarioModel obtenerEscenarioPorNombre(String nombre) throws SQLException {
        Connection conn = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM escenario WHERE nombre = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                EscenarioModel escenario = mapearEscenarioDesdeResultSet(rs);
                
                // Cargar los modificadores para este escenario
                cargarModificadoresEscenario(conn, escenario);
                
                // Cargar los personajes disponibles para este escenario
                cargarPersonajesDisponibles(conn, escenario);
                
                cacheEscenarios.put(escenario.getId(), escenario);
                return escenario;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            cerrar();
        }
    }
    
    private EscenarioModel mapearEscenarioDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        
        return new EscenarioModel(id, nombre, descripcion);
    }
    
    private void cargarModificadoresEscenario(Connection conn, EscenarioModel escenario) throws SQLException {
        String sql = "SELECT * FROM escenario_modificador WHERE escenario_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, escenario.getId());
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("id");
            int escenarioId = rs.getInt("escenario_id");
            String atributo = rs.getString("atributo");
            String modificadorTipo = rs.getString("modificador_tipo");
            int valor = rs.getInt("valor");
            int duracionTurnos = rs.getInt("duracion_turnos");
            
            ModificadorEscenarioModel modificador = new ModificadorEscenarioModel(
                id, escenarioId, atributo, modificadorTipo, valor, duracionTurnos
            );
            
            escenario.addModificador(modificador);
        }
    }
    
    private void cargarPersonajesDisponibles(Connection conn, EscenarioModel escenario) throws SQLException {
        String sql = "SELECT p.* FROM personaje p " +
                     "JOIN escenario_personaje ep ON p.id = ep.personaje_id " +
                     "WHERE ep.escenario_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, escenario.getId());
        ResultSet rs = stmt.executeQuery();
        
        PersonajeServiceModel personajeService = new PersonajeServiceModel(DATABASE_PATH);
        
        while (rs.next()) {
            PersonajeModel personaje = personajeService.construirPersonajeDesdeResultSet(rs);
            escenario.addPersonajeDisponible(personaje);
        }
    }
    
    /**
     * Limpia la caché de escenarios
     */
    public static void limpiarCache() {
        cacheEscenarios.clear();
    }
}