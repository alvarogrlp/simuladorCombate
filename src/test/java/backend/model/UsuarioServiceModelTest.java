package backend.model;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.model.UsuarioServiceModel;

public class UsuarioServiceModelTest {
    
    private UsuarioServiceModel usuarioService;
    private UsuarioModel usuarioPrueba;
    
    @Before
    public void setUp() throws SQLException {
        // Crear una instancia con la BD real - probar rutas alternativas si falla
        try {
            // Intenta con la ruta principal
            usuarioService = new UsuarioServiceModel();
            
            // Crear un usuario de prueba único para evitar conflictos
            String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";
            usuarioPrueba = new UsuarioModel("TestUser" + System.currentTimeMillis(), 
                                            uniqueEmail, 
                                            "password123");
            
        } catch (SQLException e) {
            System.err.println("Error con ruta principal, intentando ruta alternativa: " + e.getMessage());
            
            // Intenta con ruta alternativa
            try {
                usuarioService = new UsuarioServiceModel("marvelSimu.db");
                
                String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";
                usuarioPrueba = new UsuarioModel("TestUser" + System.currentTimeMillis(), 
                                                uniqueEmail, 
                                                "password123");
            } catch (SQLException e2) {
                System.err.println("Error con ruta alternativa: " + e2.getMessage());
                throw e2; // Re-lanzar para que el test falle apropiadamente
            }
        }
    }
    
    @After
    public void tearDown() {
        try {
            // Solo cerrar si existe una conexión
            if (usuarioService != null) {
                usuarioService.cerrar();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    @Test
    public void testAgregarYEliminarUsuario() throws SQLException {
        // Primero contar usuarios existentes
        ArrayList<UsuarioModel> usuariosInicial = usuarioService.obtenerUsuarios();
        int cantidadInicial = usuariosInicial.size();
        
        // Agregar el usuario de prueba
        boolean resultado = usuarioService.agregarUsuario(usuarioPrueba);
        assertTrue("Debería agregar el usuario correctamente", resultado);
        
        // Verificar que se agregó
        ArrayList<UsuarioModel> usuariosDespues = usuarioService.obtenerUsuarios();
        assertEquals("Debería haber un usuario más", cantidadInicial + 1, usuariosDespues.size());
        
        // Verificar que podemos obtenerlo por email
        UsuarioModel recuperado = usuarioService.obtenerCredencialesUsuario(usuarioPrueba.getEmail());
        assertNotNull("Debería poder recuperar el usuario agregado", recuperado);
        assertEquals("Email debería coincidir", usuarioPrueba.getEmail(), recuperado.getEmail());
        
        // Guardar el ID asignado
        usuarioPrueba.setId(recuperado.getId());
        
        // Eliminar el usuario de prueba
        resultado = usuarioService.eliminarUsuario(usuarioPrueba);
        assertTrue("Debería eliminar el usuario correctamente", resultado);
        
        // Verificar eliminación
        ArrayList<UsuarioModel> usuariosFinal = usuarioService.obtenerUsuarios();
        assertEquals("Debería volver a la cantidad inicial", cantidadInicial, usuariosFinal.size());
        
        // Verificar que ya no existe
        recuperado = usuarioService.obtenerCredencialesUsuario(usuarioPrueba.getEmail());
        assertNull("No debería encontrar el usuario eliminado", recuperado);
    }
    
    @Test
    public void testActualizarUsuario() throws SQLException {
        // Agregar el usuario primero
        usuarioService.agregarUsuario(usuarioPrueba);
        
        // Obtener el ID asignado
        UsuarioModel recuperado = usuarioService.obtenerCredencialesUsuario(usuarioPrueba.getEmail());
        usuarioPrueba.setId(recuperado.getId());
        
        // Modificar datos
        String nuevoNombre = "NombreActualizado" + System.currentTimeMillis();
        String nuevaPassword = "passwordActualizada123";
        
        usuarioPrueba.setNombre(nuevoNombre);
        usuarioPrueba.setContrasenia(nuevaPassword);
        
        // Actualizar
        boolean resultado = usuarioService.actualizarUsuario(usuarioPrueba);
        assertTrue("Debería actualizar el usuario correctamente", resultado);
        
        // Verificar actualización
        UsuarioModel actualizado = usuarioService.obtenerCredencialesUsuario(usuarioPrueba.getEmail());
        assertNotNull("Debería encontrar el usuario", actualizado);
        assertEquals("Nombre debería estar actualizado", nuevoNombre, actualizado.getNombre());
        assertEquals("Contraseña debería estar actualizada", nuevaPassword, actualizado.getContrasenia());
    }
}