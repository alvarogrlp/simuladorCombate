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

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeServiceModel;

public class PersonajeServiceModelTest {
    
    private PersonajeServiceModel personajeService;
    
    @Before
    public void setUp() throws SQLException {
        try {
            // Inicializar servicio con la BD real, asegurarse de que la ruta es correcta
            System.out.println("Inicializando PersonajeServiceModel...");
            personajeService = new PersonajeServiceModel();
            System.out.println("PersonajeServiceModel inicializado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar PersonajeServiceModel: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @After
    public void tearDown() {
        // Limpieza: liberar recursos
        try {
            if (personajeService != null) {
                personajeService.cerrar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Limpiar caché
        PersonajeServiceModel.limpiarCache();
    }
    
    @Test
    public void testObtenerTodosPersonajes() throws SQLException {
        List<PersonajeModel> personajes = personajeService.obtenerTodosPersonajes();
        
        // Verificar que se obtuvieron personajes
        assertNotNull("La lista de personajes no debería ser null", personajes);
        assertTrue("Debería haber al menos un personaje en la base de datos", personajes.size() > 0);
    }
    
    @Test
    public void testObtenerPersonajePorCodigo() throws SQLException {
        // Obtener todos los personajes primero para encontrar un código válido
        List<PersonajeModel> personajes = personajeService.obtenerTodosPersonajes();
        
        if (!personajes.isEmpty()) {
            String codigoExistente = personajes.get(0).getNombreCodigo();
            
            // Obtener personaje existente
            PersonajeModel personaje = personajeService.obtenerPersonajePorCodigo(codigoExistente);
            
            assertNotNull("Debería encontrar el personaje con código " + codigoExistente, personaje);
            assertEquals("El código del personaje debería coincidir", codigoExistente, personaje.getNombreCodigo());
        } else {
            fail("No hay personajes en la base de datos para probar");
        }
        
        // Obtener personaje no existente
        PersonajeModel noExiste = personajeService.obtenerPersonajePorCodigo("codigo_que_no_existe_seguro");
        assertNull("No debería encontrar un personaje con código inexistente", noExiste);
    }
    
    @Test
    public void testCachePersonajes() throws SQLException {
        // Obtener todos los personajes primero para encontrar un código válido
        List<PersonajeModel> personajes = personajeService.obtenerTodosPersonajes();
        
        if (!personajes.isEmpty()) {
            String codigoExistente = personajes.get(0).getNombreCodigo();
            
            // Primer acceso
            PersonajeModel personaje1 = personajeService.obtenerPersonajePorCodigo(codigoExistente);
            assertNotNull(personaje1);
            
            // Segundo acceso (debería venir de caché)
            PersonajeModel personaje2 = personajeService.obtenerPersonajePorCodigo(codigoExistente);
            assertNotNull(personaje2);
            
            // Deben ser el mismo objeto (referencia)
            assertTrue("El segundo acceso debería devolver el objeto en caché", 
                    personaje1 == personaje2);
            
            // Limpiar caché
            PersonajeServiceModel.limpiarCache();
            
            // Tercer acceso (debería venir de BD)
            PersonajeModel personaje3 = personajeService.obtenerPersonajePorCodigo(codigoExistente);
            assertNotNull(personaje3);
            
            // No deben ser el mismo objeto
            assertTrue("Después de limpiar caché, debería ser un nuevo objeto", 
                    personaje1 != personaje3);
        } else {
            fail("No hay personajes en la base de datos para probar");
        }
    }
}