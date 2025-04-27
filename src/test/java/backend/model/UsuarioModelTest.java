package backend.model;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;

public class UsuarioModelTest {
    
    private UsuarioModel usuario;
    private Date fechaCreacion;
    
    @Before
    public void setUp() {
        fechaCreacion = new Date();
        usuario = new UsuarioModel(1, "Tony Stark", "tony@stark.com", "ironman123", fechaCreacion);
    }
    
    @Test
    public void testConstructorVacio() {
        UsuarioModel u = new UsuarioModel();
        assertEquals(0, u.getId());
        assertNull(u.getNombre());
        assertNull(u.getEmail());
        assertNull(u.getContrasenia());
        assertNull(u.getCreadoEn());
    }
    
    @Test
    public void testConstructorBasico() {
        UsuarioModel u = new UsuarioModel("Steve Rogers", "steve@avengers.com", "cap123");
        assertEquals("Steve Rogers", u.getNombre());
        assertEquals("steve@avengers.com", u.getEmail());
        assertEquals("cap123", u.getContrasenia());
        assertNull(u.getCreadoEn());
    }
    
    @Test
    public void testConstructorCompleto() {
        assertEquals(1, usuario.getId());
        assertEquals("Tony Stark", usuario.getNombre());
        assertEquals("tony@stark.com", usuario.getEmail());
        assertEquals("ironman123", usuario.getContrasenia());
        assertEquals(fechaCreacion, usuario.getCreadoEn());
    }
    
    @Test
    public void testSettersYGetters() {
        UsuarioModel u = new UsuarioModel();
        
        u.setId(5);
        assertEquals(5, u.getId());
        
        u.setNombre("Bruce Banner");
        assertEquals("Bruce Banner", u.getNombre());
        
        u.setEmail("bruce@avengers.com");
        assertEquals("bruce@avengers.com", u.getEmail());
        
        u.setContrasenia("hulk123");
        assertEquals("hulk123", u.getContrasenia());
        
        Date fecha = new Date();
        u.setCreadoEn(fecha);
        assertEquals(fecha, u.getCreadoEn());
    }
    
    @Test
    public void testEquals() {
        UsuarioModel u1 = new UsuarioModel("Usuario", "email@test.com", "pass123");
        UsuarioModel u2 = new UsuarioModel("Otro Usuario", "email@test.com", "otropass");
        UsuarioModel u3 = new UsuarioModel("Usuario", "otro@test.com", "pass123");
        
        // Mismo email
        assertTrue(u1.equals(u2));
        
        // Email diferente
        assertFalse(u1.equals(u3));
        
        // Reflexivo
        assertTrue(u1.equals(u1));
        
        // Null y otro tipo
        assertFalse(u1.equals(null));
        assertFalse(u1.equals("String"));
    }
    
    @Test
    public void testHashCode() {
        UsuarioModel u1 = new UsuarioModel("Usuario", "email@test.com", "pass123");
        UsuarioModel u2 = new UsuarioModel("Otro Usuario", "email@test.com", "otropass");
        
        assertEquals(u1.hashCode(), u2.hashCode());
    }
    
    @Test
    public void testToString() {
        String esperado = "Tony Stark (tony@stark.com)";
        assertEquals(esperado, usuario.toString());
    }
}