package backend.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;

public class AtaqueModelTest {
    
    private AtaqueModel ataque;
    
    @Before
    public void setUp() {
        // Crear un ataque de prueba
        ataque = new AtaqueModel(1, 100, 1, "ACC", "Golpe Poderoso", 50, 3, 2);
    }
    
    @Test
    public void testResetearEstadoCombate() {
        // Consumir un uso
        ataque.consumirUso();
        assertEquals(2, ataque.getUsosRestantes());
        assertEquals(2, ataque.getCooldownActual());
        
        // Resetear estado
        ataque.resetearEstadoCombate();
        assertEquals(3, ataque.getUsosRestantes());
        assertEquals(0, ataque.getCooldownActual());
    }
    
    @Test
    public void testEstaDisponible() {
        // Inicialmente disponible
        assertTrue(ataque.estaDisponible());
        
        // Consumir un uso
        ataque.consumirUso();
        assertFalse(ataque.estaDisponible());
        
        // Pasar un turno
        ataque.finalizarTurno();
        assertFalse(ataque.estaDisponible());
        
        // Pasar otro turno
        ataque.finalizarTurno();
        assertTrue(ataque.estaDisponible());
    }
    
    @Test
    public void testAtaqueSinLimites() {
        // Ataque sin límite de usos ni cooldown
        AtaqueModel ataqueInfinito = new AtaqueModel(2, 100, 1, "AAD", "Rayo Infinito", 30, 0, 0);
        
        // Debe estar siempre disponible
        assertTrue(ataqueInfinito.estaDisponible());
        
        // Consumir usos no debe cambiar su disponibilidad
        for (int i = 0; i < 10; i++) {
            ataqueInfinito.consumirUso();
            assertTrue(ataqueInfinito.estaDisponible());
        }
    }
    
    @Test
    public void testConsumirUso() {
        assertEquals(3, ataque.getUsosRestantes());
        assertEquals(0, ataque.getCooldownActual());
        
        ataque.consumirUso();
        assertEquals(2, ataque.getUsosRestantes());
        assertEquals(2, ataque.getCooldownActual());
        
        // Consumir todos los usos
        ataque.consumirUso();
        ataque.consumirUso();
        assertEquals(0, ataque.getUsosRestantes());
        assertEquals(2, ataque.getCooldownActual());
    }
    
    @Test
    public void testFinalizarTurno() {
        ataque.consumirUso();
        assertEquals(2, ataque.getCooldownActual());
        
        ataque.finalizarTurno();
        assertEquals(1, ataque.getCooldownActual());
        
        ataque.finalizarTurno();
        assertEquals(0, ataque.getCooldownActual());
        
        // No debería ir a valores negativos
        ataque.finalizarTurno();
        assertEquals(0, ataque.getCooldownActual());
    }
    
    @Test
    public void testSetUsosMaximos() {
        ataque.setUsosMaximos(5);
        assertEquals(5, ataque.getUsosMaximos());
        
        // Reducir máximo por debajo de los restantes
        ataque.setUsosMaximos(1);
        assertEquals(1, ataque.getUsosMaximos());
        assertEquals(1, ataque.getUsosRestantes());
    }
    
    @Test
    public void testSetTipoYGetTipo() {
        ataque.setTipo("fisico");
        assertEquals("fisico", ataque.getTipo());
        
        ataque.setTipo("magico");
        assertEquals("magico", ataque.getTipo());
    }
    
    @Test
    public void testToString() {
        String esperado = "Golpe Poderoso (Daño: 50, Usos: 3/3)";
        assertEquals(esperado, ataque.toString());
        
        // Consumir un uso y verificar
        ataque.consumirUso();
        esperado = "Golpe Poderoso (Daño: 50, Usos: 2/3)";
        assertEquals(esperado, ataque.toString());
    }
}