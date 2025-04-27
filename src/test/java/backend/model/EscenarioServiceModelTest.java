package backend.model;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.EscenarioModel;
import es.alvarogrlp.marvelsimu.backend.model.EscenarioServiceModel;

public class EscenarioServiceModelTest {
    
    private EscenarioServiceModel escenarioService;
    
    @Before
    public void setUp() throws SQLException {
        try {
            // Usar la base de datos existente (sin parámetros usa la BD predeterminada)
            escenarioService = new EscenarioServiceModel();
        } catch (SQLException e) {
            fail("Error al inicializar los tests: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        // Limpieza: liberar recursos
        try {
            if (escenarioService != null) {
                escenarioService.cerrar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Limpiar caché
        EscenarioServiceModel.limpiarCache();
    }
    
    @Test
    public void testObtenerTodosEscenarios() throws SQLException {
        List<EscenarioModel> escenarios = escenarioService.obtenerTodosEscenarios();
        
        // Verificar que se obtuvieron escenarios
        assertNotNull("La lista de escenarios no debería ser null", escenarios);
        assertTrue("Debería haber al menos un escenario en la base de datos", escenarios.size() > 0);
    }
    
    @Test
    public void testObtenerEscenarioPorId() throws SQLException {
        // Obtener todos los escenarios primero para encontrar un id válido
        List<EscenarioModel> escenarios = escenarioService.obtenerTodosEscenarios();
        
        if (!escenarios.isEmpty()) {
            int idExistente = escenarios.get(0).getId();
            
            // Obtener escenario existente
            EscenarioModel escenario = escenarioService.obtenerEscenarioPorId(idExistente);
            
            assertNotNull("Debería encontrar el escenario con id " + idExistente, escenario);
            assertEquals("El id del escenario debería coincidir", idExistente, escenario.getId());
        } else {
            fail("No hay escenarios en la base de datos para probar");
        }
        
        // Verificar escenario inexistente (ID muy grande para que no exista)
        EscenarioModel noExiste = escenarioService.obtenerEscenarioPorId(99999);
        assertNull("Debería devolver null para ID inexistente", noExiste);
    }
    
    @Test
    public void testLimpiarCache() throws SQLException {
        List<EscenarioModel> escenarios = escenarioService.obtenerTodosEscenarios();
        
        if (!escenarios.isEmpty()) {
            int idExistente = escenarios.get(0).getId();
            
            // Cargar en caché
            EscenarioModel escenario = escenarioService.obtenerEscenarioPorId(idExistente);
            assertNotNull(escenario);
            
            // Limpiar caché
            EscenarioServiceModel.limpiarCache();
            
            // Al obtener de nuevo, debería consultar BD no caché
            EscenarioModel nuevoEscenario = escenarioService.obtenerEscenarioPorId(idExistente);
            assertNotNull("Debería cargar de nuevo de la BD", nuevoEscenario);
            assertEquals("Nombre debería coincidir", escenario.getNombre(), nuevoEscenario.getNombre());
            
            // No son la misma instancia después de limpiar caché
            // Usando assertTrue con condición negada en lugar de assertFalse
            assertTrue("Después de limpiar caché, deberían ser objetos diferentes", 
                      escenario != nuevoEscenario);
        } else {
            fail("No hay escenarios en la base de datos para probar");
        }
    }
}