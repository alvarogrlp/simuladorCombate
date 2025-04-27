package backend.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;

public class PasivaModelTest {
    
    private PasivaModel pasiva;
    
    @Before
    public void setUp() {
        pasiva = new PasivaModel(1, 100, "Regeneración", "Recupera vida cada turno", 
                                "on_turn_start", "heal_pct", 10, 3, 1);
    }
    
    @Test
    public void testConstructorVacio() {
        PasivaModel pasivaVacia = new PasivaModel();
        assertEquals(0, pasivaVacia.getId());
        assertEquals(0, pasivaVacia.getPersonajeId());
        assertNull(pasivaVacia.getNombre());
        assertNull(pasivaVacia.getDescripcion());
        assertNull(pasivaVacia.getTriggerTipo());
        assertNull(pasivaVacia.getEfectoTipo());
        assertEquals(0, pasivaVacia.getEfectoValor());
        assertEquals(0, pasivaVacia.getUsosMaximos());
        assertEquals(0, pasivaVacia.getCooldownTurnos());
    }
    
    @Test
    public void testConstructorCompleto() {
        assertEquals(1, pasiva.getId());
        assertEquals(100, pasiva.getPersonajeId());
        assertEquals("Regeneración", pasiva.getNombre());
        assertEquals("Recupera vida cada turno", pasiva.getDescripcion());
        assertEquals("on_turn_start", pasiva.getTriggerTipo());
        assertEquals("heal_pct", pasiva.getEfectoTipo());
        assertEquals(10, pasiva.getEfectoValor());
        assertEquals(3, pasiva.getUsosMaximos());
        assertEquals(1, pasiva.getCooldownTurnos());
        
        // Valores inicializados por el constructor
        assertTrue(pasiva.isActiva());
        assertEquals(3, pasiva.getUsosDisponibles());
        assertEquals(0, pasiva.getCooldownRestante());
    }
    
    @Test
    public void testResetearEstadoCombate() {
        // Consumir un uso
        pasiva.activar();
        assertEquals(2, pasiva.getUsosDisponibles());
        assertEquals(1, pasiva.getCooldownRestante());
        
        // Resetear
        pasiva.resetearEstadoCombate();
        assertEquals(3, pasiva.getUsosDisponibles());
        assertEquals(0, pasiva.getCooldownRestante());
        assertTrue(pasiva.isActiva());
    }
    
    @Test
    public void testEstaDisponible() {
        // Inicialmente disponible
        assertTrue(pasiva.estaDisponible());
        
        // Tras activar, con cooldown
        pasiva.activar();
        assertFalse(pasiva.estaDisponible());
        
        // Reducir cooldown
        pasiva.reducirCooldown();
        assertTrue(pasiva.estaDisponible());
        
        // Agotar usos
        pasiva.activar();
        pasiva.reducirCooldown();
        pasiva.activar();
        pasiva.reducirCooldown();
        pasiva.activar();
        
        // Sin usos no debe estar disponible
        assertFalse(pasiva.estaDisponible());
    }
    
    @Test
    public void testActivar() {
        assertEquals(3, pasiva.getUsosDisponibles());
        assertEquals(0, pasiva.getCooldownRestante());
        
        pasiva.activar();
        assertEquals(2, pasiva.getUsosDisponibles());
        assertEquals(1, pasiva.getCooldownRestante());
        
        // Con usos ilimitados
        PasivaModel pasivaInfinita = new PasivaModel(2, 100, "Infinita", "Sin límite", 
                                                  "passive", "other", 0, 0, 0);
        
        assertEquals(Integer.MAX_VALUE, pasivaInfinita.getUsosDisponibles());
        
        // Activar no reduce los usos pero activa cooldown
        pasivaInfinita.activar();
        assertEquals(Integer.MAX_VALUE, pasivaInfinita.getUsosDisponibles());
        assertEquals(0, pasivaInfinita.getCooldownRestante());
    }
    
    @Test
    public void testReducirCooldown() {
        pasiva.activar();
        assertEquals(1, pasiva.getCooldownRestante());
        
        pasiva.reducirCooldown();
        assertEquals(0, pasiva.getCooldownRestante());
        
        // No debería ir a negativo
        pasiva.reducirCooldown();
        assertEquals(0, pasiva.getCooldownRestante());
    }
    
    @Test
    public void testSettersYGetters() {
        PasivaModel p = new PasivaModel();
        
        p.setId(5);
        assertEquals(5, p.getId());
        
        p.setPersonajeId(200);
        assertEquals(200, p.getPersonajeId());
        
        p.setNombre("Nueva Pasiva");
        assertEquals("Nueva Pasiva", p.getNombre());
        
        p.setDescripcion("Nueva descripción");
        assertEquals("Nueva descripción", p.getDescripcion());
        
        p.setTriggerTipo("on_damage_taken");
        assertEquals("on_damage_taken", p.getTriggerTipo());
        
        p.setEfectoTipo("reduce_damage_pct");
        assertEquals("reduce_damage_pct", p.getEfectoTipo());
        
        p.setEfectoValor(25);
        assertEquals(25, p.getEfectoValor());
        
        p.setUsosMaximos(5);
        assertEquals(5, p.getUsosMaximos());
        
        p.setCooldownTurnos(2);
        assertEquals(2, p.getCooldownTurnos());
        
        p.setUsosDisponibles(4);
        assertEquals(4, p.getUsosDisponibles());
        
        p.setCooldownRestante(1);
        assertEquals(1, p.getCooldownRestante());
        
        p.setActiva(false);
        assertFalse(p.isActiva());
    }
    
    @Test
    public void testEquals() {
        PasivaModel p1 = new PasivaModel(1, 100, "Pasiva", "Desc", "trigger", "efecto", 10, 3, 1);
        PasivaModel p2 = new PasivaModel(1, 100, "Otro nombre", "Otra desc", "trigger", "efecto", 20, 5, 2);
        PasivaModel p3 = new PasivaModel(2, 100, "Pasiva", "Desc", "trigger", "efecto", 10, 3, 1);
        
        // Mismo id y personajeId
        assertTrue(p1.equals(p2));
        
        // Diferente id
        assertFalse(p1.equals(p3));
        
        // Null y otro tipo
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("String"));
    }
    
    @Test
    public void testHashCode() {
        PasivaModel p1 = new PasivaModel(1, 100, "Pasiva", "Desc", "trigger", "efecto", 10, 3, 1);
        PasivaModel p2 = new PasivaModel(1, 100, "Otro", "Otra", "otro", "otro", 20, 5, 2);
        
        assertEquals(p1.hashCode(), p2.hashCode());
    }
    
    @Test
    public void testToString() {
        assertEquals("Regeneración", pasiva.toString());
    }
}