package es.alvarogrlp.marvelsimu.backend.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

/**
 * Utilidad para manejar conexiones a la base de datos con un pool simple
 */
public class DatabaseUtil {
    
    private static final String DB_PATH = "src/main/resources/marvelSimu.db";
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
                File dbFile = new File(DB_PATH);
                if (!dbFile.exists()) {
                    // Intentar con ruta alternativa en resources
                    String altPath = "marvelSimu.db";
                    File altFile = new File(altPath);
                    
                    if(!altFile.exists()) {
                        System.err.println("ERROR CRÍTICO: Base de datos no encontrada en " + dbFile.getAbsolutePath());
                        System.err.println("Tampoco encontrada en ruta alternativa: " + altFile.getAbsolutePath());
                        System.err.println("Ruta de trabajo actual: " + new File(".").getAbsolutePath());
                    } else {
                        System.out.println("Base de datos encontrada en ruta alternativa: " + altFile.getAbsolutePath());
                    }
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
            
            // Verificar si existe la tabla personaje
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='personaje'";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.err.println("ERROR: La tabla 'personaje' no existe en la base de datos");
                return;
            }
            
            // Verificar estructura de la tabla
            rs.close();
            stmt.close();
            
            sql = "PRAGMA table_info(personaje)";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            System.out.println("Estructura de la tabla 'personaje':");
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
        try {
            // Intentar con la ruta principal
            return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        } catch (SQLException e) {
            System.out.println("No se pudo conectar usando la ruta principal, intentando ruta alternativa");
            // Intentar con ruta alternativa
            return DriverManager.getConnection("jdbc:sqlite:marvelSimu.db");
        }
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
            
            String sql = "SELECT * FROM personaje";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            // Verificar si hay resultados
            if (!rs.isBeforeFirst()) {
                System.err.println("¡ADVERTENCIA! La tabla 'personaje' está vacía.");
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
                    int personajeId = rs.getInt("id");
                    System.out.println("Cargando personaje con ID: " + id);
                    
                    PersonajeModel character = buildPersonajeFromResultSet(rs);
                    
                    // AÑADIR AQUÍ: Cargar ataques y pasivas
                    List<AtaqueModel> ataques = cargarAtaquesPersonaje(personajeId);
                    character.setAtaques(ataques);
                    
                    List<PasivaModel> pasivas = cargarPasivasPersonaje(personajeId);
                    character.setPasivas(pasivas);
                    
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
        
        try {
            // Datos básicos
            character.setId(rs.getInt("id"));
            character.setNombre(rs.getString("nombre"));
            character.setNombreCodigo(rs.getString("nombre_codigo"));
            character.setDescripcion(rs.getString("descripcion"));
            
            // Estadísticas básicas - con manejo de excepciones individual
            try {
                character.setVida(rs.getInt("vida"));
            } catch (SQLException e) {
                System.err.println("Error al obtener 'vida': " + e.getMessage());
                character.setVida(100); // Valor por defecto
            }
            
            try {
                character.setFuerza(rs.getInt("fuerza"));
            } catch (SQLException e) {
                System.err.println("Error al obtener 'fuerza': " + e.getMessage());
                character.setFuerza(10); // Valor por defecto
            }
            
            try {
                character.setVelocidad(rs.getInt("velocidad"));
            } catch (SQLException e) {
                System.err.println("Error al obtener 'velocidad': " + e.getMessage());
                character.setVelocidad(10); // Valor por defecto
            }
            
            try {
                character.setPoder(rs.getInt("poder"));
            } catch (SQLException e) {
                System.err.println("Error al obtener 'poder': " + e.getMessage());
                character.setPoder(10); // Valor por defecto
            }
            
            // Imágenes con valores por defecto seguros
            character.setImagenMiniatura(getStringOrDefault(rs, "imagen_miniatura", "defaultMiniatura.png"));
            character.setImagenCombate(getStringOrDefault(rs, "imagen_combate", "defaultCombate.png"));
            
            System.out.println("Personaje construido exitosamente: " + character.getNombre());
            
            return character;
            
        } catch (SQLException e) {
            System.err.println("Error al construir personaje: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Verifica si un recurso existe en el classpath
     */
    private static boolean resourceExists(String resourcePath) {
        try {
            return DatabaseUtil.class.getClassLoader().getResource(resourcePath) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Carga los ataques de un personaje desde la base de datos
     * @param personajeId ID del personaje
     * @return Lista de ataques
     */
    private static List<AtaqueModel> cargarAtaquesPersonaje(int personajeId) {
        List<AtaqueModel> ataques = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            
            // Verificar si la tabla existe
            String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='ataque'";
            stmt = conn.prepareStatement(checkTable);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("La tabla 'ataque' no existe en la base de datos");
                return ataques;
            }
            
            rs.close();
            stmt.close();
            
            // Cargar los ataques usando JOIN con tipo_ataque
            String sql = "SELECT a.*, ta.clave AS tipo_ataque_clave FROM ataque a " +
                         "JOIN tipo_ataque ta ON a.tipo_ataque_id = ta.id " +
                         "WHERE a.personaje_id = ?";
        
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, personajeId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                AtaqueModel ataque = new AtaqueModel();
                ataque.setId(rs.getInt("id"));
                ataque.setPersonajeId(personajeId);
                ataque.setTipoAtaqueId(rs.getInt("tipo_ataque_id"));
                ataque.setTipoAtaqueClave(rs.getString("tipo_ataque_clave"));
                
                // Añadir el código (campo obligatorio según el esquema SQL)
                ataque.setCodigo(rs.getString("codigo"));
                
                ataque.setNombre(rs.getString("nombre"));
                ataque.setDanoBase(rs.getInt("dano_base"));
                ataque.setUsosMaximos(rs.getInt("usos_maximos"));
                ataque.setCooldownTurnos(rs.getInt("cooldown_turnos"));
                ataque.resetearEstadoCombate();
                ataques.add(ataque);
            }
            
            System.out.println("Cargados " + ataques.size() + " ataques para el personaje ID: " + personajeId);
        } catch (SQLException e) {
            System.err.println("Error cargando ataques del personaje " + personajeId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return ataques;
    }
    
    /**
     * Carga las pasivas de un personaje desde la base de datos
     * @param personajeId ID del personaje
     * @return Lista de pasivas
     */
    private static List<PasivaModel> cargarPasivasPersonaje(int personajeId) {
        List<PasivaModel> pasivas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            
            // Verificar si la tabla existe
            String checkTable = "SELECT name FROM sqlite_master WHERE type='table' AND name='pasiva'";
            stmt = conn.prepareStatement(checkTable);
            rs = stmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("La tabla 'pasiva' no existe en la base de datos");
                return pasivas;
            }
            
            rs.close();
            stmt.close();
            
            // Verificar la estructura de la tabla para adaptarnos dinámicamente
            stmt = conn.prepareStatement("PRAGMA table_info(pasiva)");
            rs = stmt.executeQuery();
            
            // Obtener nombres de columnas disponibles
            List<String> columnNames = new ArrayList<>();
            while (rs.next()) {
                columnNames.add(rs.getString("name").toLowerCase());
                System.out.println("Columna en tabla pasiva: " + rs.getString("name"));
            }
            
            rs.close();
            stmt.close();
            
            // Construir la consulta SQL basada en las columnas disponibles
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM pasiva WHERE personaje_id = ?");
            
            stmt = conn.prepareStatement(sqlBuilder.toString());
            stmt.setInt(1, personajeId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PasivaModel pasiva = new PasivaModel();
                
                // Campos obligatorios según el SQL
                pasiva.setId(rs.getInt("id"));
                pasiva.setPersonajeId(personajeId);
                
                // Verificar y establecer cada campo dinámicamente
                if (columnNames.contains("nombre")) {
                    pasiva.setNombre(rs.getString("nombre"));
                } else {
                    pasiva.setNombre("Pasiva sin nombre");
                }
                
                if (columnNames.contains("descripcion")) {
                    pasiva.setDescripcion(rs.getString("descripcion"));
                } else {
                    pasiva.setDescripcion("Sin descripción");
                }
                
                if (columnNames.contains("trigger_tipo")) {
                    pasiva.setTriggerTipo(rs.getString("trigger_tipo"));
                } else {
                    pasiva.setTriggerTipo("default");
                }
                
                if (columnNames.contains("efecto_tipo")) {
                    pasiva.setEfectoTipo(rs.getString("efecto_tipo"));
                } else {
                    pasiva.setEfectoTipo("default");
                }
                
                if (columnNames.contains("efecto_valor")) {
                    pasiva.setEfectoValor(rs.getInt("efecto_valor"));
                } else {
                    pasiva.setEfectoValor(0);
                }
                
                if (columnNames.contains("usos_maximos")) {
                    pasiva.setUsosMaximos(rs.getInt("usos_maximos"));
                } else {
                    pasiva.setUsosMaximos(0);
                }
                
                if (columnNames.contains("cooldown_turnos")) {
                    pasiva.setCooldownTurnos(rs.getInt("cooldown_turnos"));
                } else {
                    pasiva.setCooldownTurnos(0);
                }
                
                // Inicializar el estado para combate
                pasiva.resetearEstadoCombate();
                pasivas.add(pasiva);
            }
            
            System.out.println("Cargadas " + pasivas.size() + " pasivas para el personaje ID: " + personajeId);
        } catch (SQLException e) {
            System.err.println("Error cargando pasivas del personaje " + personajeId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return pasivas;
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
            if (conn != null) {
                // Liberar la conexión al pool en lugar de cerrarla
                releaseConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("Error liberando recursos: " + e.getMessage());
        }
    }
}