package es.alvarogrlp.marvelsimu.backend.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

/**
 * Utilidad para manejar conexiones a la base de datos con un pool simple
 */
public class DatabaseUtil {
    
    private static final String DB_PATH = "src/main/resources/usuarios.db";
    private static BlockingQueue<Connection> connectionPool;
    
    /**
     * Inicializa el pool de conexiones
     */
    public static void initializePool() {
        if (connectionPool == null) {
            try {
                // Cargar el driver JDBC para SQLite
                Class.forName("org.sqlite.JDBC");
                
                // Verificar que la base de datos existe
                File dbFile = new File("usuarios.db");
                if (!dbFile.exists()) {
                    System.err.println("ERROR CRÍTICO: Base de datos no encontrada en " + dbFile.getAbsolutePath());
                    System.err.println("Ruta de trabajo actual: " + new File(".").getAbsolutePath());
                } else {
                    System.out.println("Base de datos encontrada en: " + dbFile.getAbsolutePath());
                }
                
                // Crear el pool con 5 conexiones iniciales
                connectionPool = new LinkedBlockingQueue<>(10);
                
                for (int i = 0; i < 5; i++) {
                    Connection conn = createConnection();
                    if (conn != null) {
                        connectionPool.add(conn);
                    }
                }
                
                System.out.println("Pool de conexiones inicializado con " + connectionPool.size() + " conexiones");
                
                // Verificar estructura de la base de datos
                checkDatabaseStructure();
                
            } catch (Exception e) {
                System.err.println("Error inicializando pool de conexiones: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica la estructura de la base de datos para diagnóstico
     */
    private static void checkDatabaseStructure() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            
            // Verificar si existe la tabla personajes
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='personajes'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.err.println("ERROR: La tabla 'personajes' no existe en la base de datos");
                return;
            }
            
            // Verificar estructura de la tabla
            rs.close();
            stmt.close();
            
            sql = "PRAGMA table_info(personajes)";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            System.out.println("Estructura de la tabla 'personajes':");
            while (rs.next()) {
                System.out.println(rs.getInt("cid") + ": " + 
                                  rs.getString("name") + " - " + 
                                  rs.getString("type"));
            }
            
        } catch (Exception e) {
            System.err.println("Error verificando estructura de BD: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Crea una nueva conexión a la base de datos
     */
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }
    
    /**
     * Obtiene una conexión del pool
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Obtener una conexión del pool
            Connection conn = connectionPool.take();
            
            // Verificar si la conexión sigue válida
            if (conn.isClosed() || !conn.isValid(1)) {
                System.out.println("Reemplazando conexión cerrada o inválida");
                conn = createConnection();
            }
            
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupción al obtener conexión", e);
        }
    }
    
    /**
     * Devuelve una conexión al pool
     */
    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                // Si la conexión está cerrada, crear una nueva
                if (conn.isClosed()) {
                    conn = createConnection();
                }
                
                // Devolver la conexión al pool usando add() o offer() en lugar de put()
                boolean added = connectionPool.offer(conn);
                if (!added) {
                    // Si no se pudo añadir (pool lleno), cerrar la conexión
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Cierra todas las conexiones del pool
     */
    public static void closeAllConnections() {
        while (!connectionPool.isEmpty()) {
            try {
                Connection conn = connectionPool.take();
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Carga todos los personajes disponibles desde la base de datos
     * @return Mapa con los personajes indexados por su identificador
     * @throws Exception Si ocurre un error al cargar los personajes
     */
    public static Map<String, PersonajeModel> loadAllCharacters() throws Exception {
        Map<String, PersonajeModel> charactersMap = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            
            // Listar todas las columnas de la tabla para debugging
            System.out.println("Intentando cargar personajes de la base de datos...");
            
            String sql = "SELECT * FROM personajes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            // Verificar si hay resultados
            if (!rs.isBeforeFirst()) {
                System.err.println("¡ADVERTENCIA! La tabla 'personajes' está vacía.");
            }
            
            // Mostrar los nombres de todas las columnas para debugging
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            System.out.println("La tabla tiene " + columnCount + " columnas:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(i + ": " + metaData.getColumnName(i));
            }
            
            // Cargar personajes
            while (rs.next()) {
                try {
                    String id = rs.getString("id");
                    System.out.println("Cargando personaje con ID: " + id);
                    
                    PersonajeModel character = buildPersonajeFromResultSet(rs);
                    charactersMap.put(id, character);
                    System.out.println("Personaje cargado: " + character.getNombre());
                } catch (Exception e) {
                    System.err.println("Error al cargar un personaje específico: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Total de personajes cargados: " + charactersMap.size());
            
            return charactersMap;
            
        } catch (Exception e) {
            System.err.println("Error cargando personajes: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Crea un objeto PersonajeModel a partir de un ResultSet
     * @param rs ResultSet con los datos del personaje
     * @return PersonajeModel construido
     * @throws SQLException Si hay error al obtener los datos
     */
    private static PersonajeModel buildPersonajeFromResultSet(ResultSet rs) throws SQLException {
        PersonajeModel character = new PersonajeModel();
        
        // Guardar el ID exactamente como está en la base de datos
        String id = rs.getString("id");
        character.setNombreCodigo(id);
        System.out.println("Construyendo personaje con ID: " + id + ", Nombre: " + rs.getString("nombre"));
        
        // Asignar datos básicos
        character.setNombre(rs.getString("nombre"));
        character.setDescripcion(rs.getString("descripcion"));
        
        // Estadísticas básicas
        character.setVida(rs.getInt("vida"));
        character.setFuerza(rs.getInt("fuerza"));
        character.setVelocidad(rs.getInt("velocidad"));
        character.setResistencia(rs.getInt("resistencia"));
        character.setPoderMagico(rs.getInt("poder_magico"));
        
        // Ataques y habilidades
        character.setAtaqueMelee(rs.getInt("ataque_melee"));
        character.setAtaqueLejano(rs.getInt("ataque_lejano"));
        character.setHabilidad1Poder(rs.getInt("habilidad1_poder"));
        character.setHabilidad2Poder(rs.getInt("habilidad2_poder"));
        
        // Nombres de ataques y habilidades
        character.setAtaqueMeleeNombre(rs.getString("ataque_melee_nombre"));
        character.setAtaqueLejanoNombre(rs.getString("ataque_lejano_nombre"));
        character.setHabilidad1Nombre(rs.getString("habilidad1_nombre"));
        character.setHabilidad2Nombre(getStringOrDefault(rs, "habilidad2_nombre"));
        
        // Tipos de ataques y habilidades
        character.setAtaqueMeleeTipo(getStringOrDefault(rs, "ataque_melee_tipo", "fisico"));
        character.setAtaqueLejanoTipo(getStringOrDefault(rs, "ataque_lejano_tipo", "fisico"));
        character.setHabilidad1Tipo(getStringOrDefault(rs, "habilidad1_tipo", "fisico"));
        character.setHabilidad2Tipo(getStringOrDefault(rs, "habilidad2_tipo", "fisico"));
        
        // Imágenes
        character.setImagenCombate(getStringOrDefault(rs, "imagen_combate", "images/personajes/default.png"));
        character.setImagenMiniatura(getStringOrDefault(rs, "imagen_miniatura", "images/personajes/default_mini.png"));
        
        // Defensas
        character.setResistenciaFisica(rs.getInt("resistencia_fisica"));
        character.setResistenciaMagica(rs.getInt("resistencia_magica"));
        character.setEvasion(rs.getInt("evasion"));
        
        // Críticos
        try {
            character.setProbabilidadCritico(rs.getInt("probabilidad_critico"));
            character.setMultiplicadorCritico(rs.getDouble("multiplicador_critico"));
        } catch (SQLException e) {
            character.setProbabilidadCritico(5);  // valor por defecto
            character.setMultiplicadorCritico(1.5);  // valor por defecto
        }
        
        // Pasiva
        character.setPasivaNombre(getStringOrDefault(rs, "pasiva_nombre", ""));
        character.setPasivaDescripcion(getStringOrDefault(rs, "pasiva_descripcion", ""));
        character.setPasivaTipo(getStringOrDefault(rs, "pasiva_tipo", "ninguna"));
        character.setPasivaValor(rs.getInt("pasiva_valor"));
        
        return character;
    }
    
    /**
     * Obtiene un valor String de un ResultSet o devuelve un valor por defecto si hay error
     * @param rs ResultSet de donde obtener el valor
     * @param columnName Nombre de la columna
     * @param defaultValue Valor por defecto
     * @return El valor de la columna o el valor por defecto si hay error
     */
    private static String getStringOrDefault(ResultSet rs, String columnName, String defaultValue) {
        try {
            String value = rs.getString(columnName);
            return (value != null) ? value : defaultValue;
        } catch (SQLException e) {
            return defaultValue;
        }
    }
    
    /**
     * Sobrecarga para usar cadena vacía como valor por defecto
     */
    private static String getStringOrDefault(ResultSet rs, String columnName) {
        return getStringOrDefault(rs, columnName, "");
    }
    
    /**
     * Cierra los recursos de base de datos
     */
    private static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error cerrando conexiones: " + e.getMessage());
        }
    }
}