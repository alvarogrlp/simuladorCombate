package backend.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.util.DatabaseUtil;

/**
 * Tests para la clase DatabaseUtil
 */
public class DatabaseUtilTest {
    
    @Before
    public void setUp() {
        // Inicializar el pool de conexiones antes de cada test
        DatabaseUtil.initializePool();
    }
    
    @After
    public void tearDown() {
        // Cerrar todas las conexiones después de cada test
        DatabaseUtil.closeAllConnections();
    }
    
    /**
     * Test para verificar que se puede obtener una conexión del pool
     */
    @Test
    public void testGetConnection() {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            assertNotNull("La conexión no debería ser null", conn);
            assertFalse("La conexión debería estar abierta", conn.isClosed());
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        } finally {
            if (conn != null) {
                DatabaseUtil.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Test para verificar que se puede devolver una conexión al pool
     */
    @Test
    public void testReleaseConnection() {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            assertNotNull(conn);
            
            // Liberar la conexión
            DatabaseUtil.releaseConnection(conn);
            
            // Este test pasa si no hay excepciones
            assertTrue(true);
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
    
    /**
     * Test para verificar si el cargado de personajes funciona.
     * Este test puede fallar si no hay acceso a la base de datos real,
     * por lo que principalmente comprobamos que el método no lanza excepciones.
     */
    @Test(timeout = 15000) // 15 segundos de timeout
    public void testLoadAllCharacters() {
        try {
            // Agregar captura detallada de errores
            System.out.println("Iniciando testLoadAllCharacters()...");
            System.out.println("DatabaseUtil.initializePool() ya fue llamado en setUp()");
            Map<String, PersonajeModel> personajes = null;
            
            try {
                // Intentar cargar personajes
                System.out.println("Llamando a loadAllCharacters()...");
                personajes = DatabaseUtil.loadAllCharacters();
                System.out.println("Llamada a loadAllCharacters() completada con éxito.");
            } catch (Exception e) {
                System.err.println("ERROR CAPTURADO en loadAllCharacters(): " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-lanzar para que el test falle apropiadamente
            }
            
            // Verifica que el mapa no es null (aunque podría estar vacío si no hay personajes)
            assertNotNull("El mapa de personajes no debería ser null", personajes);
            System.out.println("Mapa de personajes verificado (no es null). Contiene " + personajes.size() + " personajes.");
            
            // Verificar algunas propiedades de los personajes si hay alguno
            if (!personajes.isEmpty()) {
                System.out.println("Se encontraron " + personajes.size() + " personajes. Verificando propiedades básicas...");
                
                // Limitar la verificación a máximo 3 personajes para evitar bloqueos
                int count = 0;
                for (PersonajeModel personaje : personajes.values()) {
                    if (count++ >= 3) {
                        System.out.println("Limitando verificación a 3 personajes para evitar bloqueos...");
                        break;
                    }
                    
                    try {
                        assertNotNull("El nombre del personaje no debería ser null", personaje.getNombre());
                        assertTrue("El ID del personaje debería ser positivo", personaje.getId() > 0);
                        System.out.println("Personaje verificado: " + personaje.getNombre() + " (ID: " + personaje.getId() + ")");
                        System.out.println("  - Ataques: " + (personaje.getAtaques() != null ? personaje.getAtaques().size() : "null"));
                        System.out.println("  - Pasivas: " + (personaje.getPasivas() != null ? personaje.getPasivas().size() : "null"));
                    } catch (AssertionError ae) {
                        System.err.println("Error en verificación de personaje: " + ae.getMessage());
                        throw ae;
                    }
                }
            } else {
                System.out.println("No se encontraron personajes en la base de datos (mapa vacío).");
            }
            
            System.out.println("Test completado exitosamente.");
            
        } catch (Exception e) {
            System.err.println("Test falló con excepción: " + e.getMessage());
            e.printStackTrace();
            fail("Excepción no esperada en test: " + e.getMessage());
        }
    }
    
    /**
     * Test para verificar que el método closeAllConnections no lanza excepciones
     */
    @Test
    public void testCloseAllConnections() {
        try {
            // Obtener algunas conexiones primero
            Connection conn1 = DatabaseUtil.getConnection();
            Connection conn2 = DatabaseUtil.getConnection();
            
            // Liberarlas
            DatabaseUtil.releaseConnection(conn1);
            DatabaseUtil.releaseConnection(conn2);
            
            // Cerrar todas
            DatabaseUtil.closeAllConnections();
            
            // Reinicializar el pool para los siguientes tests
            DatabaseUtil.initializePool();
            
            // Si llegamos aquí, el test pasa
            assertTrue(true);
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
    
    /**
     * Test para verificar que las conexiones del pool están realmente funcionando
     */
    @Test
    public void testConnectionsWorkingProperly() {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            
            // Verificar que la conexión es válida
            assertTrue("La conexión debería ser válida", conn.isValid(1));
            
            // Verificar que no está en modo autocommit (comportamiento por defecto de SQLite)
            assertTrue("SQLite debería estar en modo autocommit por defecto", conn.getAutoCommit());
            
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        } finally {
            if (conn != null) {
                DatabaseUtil.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Test de estrés para el pool de conexiones (obtener y liberar varias veces)
     */
    @Test
    public void testConnectionPoolStress() {
        final int ITERATIONS = 10;
        
        try {
            // Array para almacenar las conexiones
            Connection[] connections = new Connection[ITERATIONS];
            
            // Obtener varias conexiones
            for (int i = 0; i < ITERATIONS; i++) {
                connections[i] = DatabaseUtil.getConnection();
                assertNotNull("La conexión " + i + " no debería ser null", connections[i]);
            }
            
            // Liberar todas las conexiones
            for (int i = 0; i < ITERATIONS; i++) {
                DatabaseUtil.releaseConnection(connections[i]);
            }
            
            // El test pasa si llegamos hasta aquí sin excepciones
            assertTrue(true);
        } catch (SQLException e) {
            fail("Error en prueba de estrés del pool: " + e.getMessage());
        }
    }
}
