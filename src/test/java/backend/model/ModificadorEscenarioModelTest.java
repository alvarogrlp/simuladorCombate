package backend.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.ModificadorEscenarioModel;

/**
 * Tests para la clase ModificadorEscenarioModel
 */
public class ModificadorEscenarioModelTest {
    
    private ModificadorEscenarioModel modificador;
    
    @Before
    public void setUp() {
        // Inicializar un modificador para usar en los tests
        modificador = new ModificadorEscenarioModel(1, 5, "VIDA", "PORCENTAJE", 20, 3);
    }
    
    @Test
    public void testConstructorVacio() {
        ModificadorEscenarioModel mod = new ModificadorEscenarioModel();
        assertNotNull("El objeto no debería ser null", mod);
        assertEquals("El id debería ser 0", 0, mod.getId());
        assertEquals("El escenarioId debería ser 0", 0, mod.getEscenarioId());
        assertNull("El atributo debería ser null", mod.getAtributo());
        assertNull("El tipo de modificador debería ser null", mod.getModificadorTipo());
        assertEquals("El valor debería ser 0", 0, mod.getValor());
        assertEquals("La duración de turnos debería ser 0", 0, mod.getDuracionTurnos());
    }
    
    @Test
    public void testConstructorCompleto() {
        assertNotNull("El objeto no debería ser null", modificador);
        assertEquals("El id debería ser 1", 1, modificador.getId());
        assertEquals("El escenarioId debería ser 5", 5, modificador.getEscenarioId());
        assertEquals("El atributo debería ser VIDA", "VIDA", modificador.getAtributo());
        assertEquals("El tipo de modificador debería ser PORCENTAJE", "PORCENTAJE", modificador.getModificadorTipo());
        assertEquals("El valor debería ser 20", 20, modificador.getValor());
        assertEquals("La duración de turnos debería ser 3", 3, modificador.getDuracionTurnos());
    }
    
    @Test
    public void testSettersYGetters() {
        ModificadorEscenarioModel mod = new ModificadorEscenarioModel();
        
        mod.setId(2);
        assertEquals("El id debería ser 2", 2, mod.getId());
        
        mod.setEscenarioId(10);
        assertEquals("El escenarioId debería ser 10", 10, mod.getEscenarioId());
        
        mod.setAtributo("VELOCIDAD");
        assertEquals("El atributo debería ser VELOCIDAD", "VELOCIDAD", mod.getAtributo());
        
        mod.setModificadorTipo("ABSOLUTO");
        assertEquals("El tipo de modificador debería ser ABSOLUTO", "ABSOLUTO", mod.getModificadorTipo());
        
        mod.setValor(15);
        assertEquals("El valor debería ser 15", 15, mod.getValor());
        
        mod.setDuracionTurnos(5);
        assertEquals("La duración de turnos debería ser 5", 5, mod.getDuracionTurnos());
    }
    
    @Test
    public void testEquals() {
        ModificadorEscenarioModel mod1 = new ModificadorEscenarioModel(1, 5, "VIDA", "PORCENTAJE", 20, 3);
        ModificadorEscenarioModel mod2 = new ModificadorEscenarioModel(1, 5, "VIDA", "PORCENTAJE", 20, 3);
        ModificadorEscenarioModel mod3 = new ModificadorEscenarioModel(2, 5, "VIDA", "PORCENTAJE", 20, 3);
        
        assertTrue("Objetos iguales deberían ser equals", mod1.equals(mod2));
        assertFalse("Objetos con id diferente no deberían ser equals", mod1.equals(mod3));
        assertTrue("Un objeto debería ser igual a sí mismo", mod1.equals(mod1));
        assertFalse("Un objeto no debería ser igual a null", mod1.equals(null));
        assertFalse("Un objeto no debería ser igual a otro tipo de objeto", mod1.equals("String"));
    }
    
    @Test
    public void testHashCode() {
        ModificadorEscenarioModel mod1 = new ModificadorEscenarioModel(1, 5, "VIDA", "PORCENTAJE", 20, 3);
        ModificadorEscenarioModel mod2 = new ModificadorEscenarioModel(1, 5, "VIDA", "PORCENTAJE", 20, 3);
        
        assertEquals("Objetos iguales deberían tener el mismo hashCode", mod1.hashCode(), mod2.hashCode());
    }
    
    @Test
    public void testToString() {
        String expectedString = "VIDA: PORCENTAJE 20 (3 turnos)";
        assertEquals("El formato toString debería ser correcto", expectedString, modificador.toString());
    }
}