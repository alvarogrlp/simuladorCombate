package backend.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.EscenarioModel;
import es.alvarogrlp.marvelsimu.backend.model.ModificadorEscenarioModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class EscenarioModelTest {
    
    private EscenarioModel escenario;
    private PersonajeModel personaje;
    
    @Before
    public void setUp() {
        // Crear un escenario de prueba
        escenario = new EscenarioModel(1, "Torre Stark", "La torre de los Vengadores");
        
        // Agregar modificadores
        ModificadorEscenarioModel modVida = new ModificadorEscenarioModel(1, 1, "vida", "multiplier", 120, 0);
        ModificadorEscenarioModel modFuerza = new ModificadorEscenarioModel(2, 1, "fuerza", "add_pct", 25, 0);
        
        escenario.addModificador(modVida);
        escenario.addModificador(modFuerza);
        
        // Crear un personaje para probar
        personaje = new PersonajeModel();
        personaje.setId(1);
        personaje.setNombre("Iron Man");
        personaje.setNombreCodigo("ironman");
        personaje.setVida(100);
        personaje.setFuerza(80);
        personaje.setVelocidad(70);
        personaje.setPoder(90);
        
        // Agregar el personaje al escenario
        escenario.addPersonajeDisponible(personaje);
    }
    
    @Test
    public void testConstructorVacio() {
        EscenarioModel escenarioVacio = new EscenarioModel();
        assertNotNull(escenarioVacio.getModificadores());
        assertNotNull(escenarioVacio.getPersonajesDisponibles());
        assertTrue(escenarioVacio.getModificadores().isEmpty());
        assertTrue(escenarioVacio.getPersonajesDisponibles().isEmpty());
    }
    
    @Test
    public void testConstructorConParametros() {
        assertEquals(1, escenario.getId());
        assertEquals("Torre Stark", escenario.getNombre());
        assertEquals("La torre de los Vengadores", escenario.getDescripcion());
        assertEquals(2, escenario.getModificadores().size());
        assertEquals(1, escenario.getPersonajesDisponibles().size());
    }
    
    @Test
    public void testAddModificador() {
        ModificadorEscenarioModel modVelocidad = new ModificadorEscenarioModel(3, 1, "velocidad", "multiplier", 110, 0);
        escenario.addModificador(modVelocidad);
        
        assertEquals(3, escenario.getModificadores().size());
        assertEquals(modVelocidad, escenario.getModificadores().get(2));
    }
    
    @Test
    public void testAddPersonajeDisponible() {
        PersonajeModel personaje2 = new PersonajeModel();
        personaje2.setId(2);
        personaje2.setNombre("Captain America");
        personaje2.setNombreCodigo("captain");
        
        escenario.addPersonajeDisponible(personaje2);
        
        assertEquals(2, escenario.getPersonajesDisponibles().size());
        assertEquals(personaje2, escenario.getPersonajesDisponibles().get(1));
    }
    
    @Test
    public void testSetModificadores() {
        List<ModificadorEscenarioModel> nuevosModificadores = new ArrayList<>();
        ModificadorEscenarioModel mod1 = new ModificadorEscenarioModel(10, 1, "poder", "multiplier", 150, 0);
        ModificadorEscenarioModel mod2 = new ModificadorEscenarioModel(11, 1, "velocidad", "add_pct", 30, 0);
        nuevosModificadores.add(mod1);
        nuevosModificadores.add(mod2);
        
        escenario.setModificadores(nuevosModificadores);
        
        assertEquals(2, escenario.getModificadores().size());
        assertEquals(mod1, escenario.getModificadores().get(0));
        assertEquals(mod2, escenario.getModificadores().get(1));
    }
    
    @Test
    public void testSetPersonajesDisponibles() {
        List<PersonajeModel> nuevosPersonajes = new ArrayList<>();
        PersonajeModel p1 = new PersonajeModel();
        p1.setId(5);
        p1.setNombre("Thor");
        p1.setNombreCodigo("thor");
        
        PersonajeModel p2 = new PersonajeModel();
        p2.setId(6);
        p2.setNombre("Hulk");
        p2.setNombreCodigo("hulk");
        
        nuevosPersonajes.add(p1);
        nuevosPersonajes.add(p2);
        
        escenario.setPersonajesDisponibles(nuevosPersonajes);
        
        assertEquals(2, escenario.getPersonajesDisponibles().size());
        assertEquals(p1, escenario.getPersonajesDisponibles().get(0));
        assertEquals(p2, escenario.getPersonajesDisponibles().get(1));
    }
    
    @Test
    public void testAplicarModificadoresPersonajeCompatible() {
        // El personaje está en la lista de disponibles
        PersonajeModel personajeModificado = escenario.aplicarModificadores(personaje);
        
        // Verificar que es una copia (no el mismo objeto)
        assertNotSame(personaje, personajeModificado);
        
        // Verificar los cambios por los modificadores
        assertEquals(120, personajeModificado.getVida()); // 100 * 1.2 = 120
        assertEquals(100, personajeModificado.getFuerza()); // 80 + (80 * 0.25) = 100
        assertEquals(70, personajeModificado.getVelocidad()); // Sin cambios
        assertEquals(90, personajeModificado.getPoder()); // Sin cambios
        
        // La vida actual debe ser inicializada al nuevo máximo
        assertEquals(120, personajeModificado.getVidaActual());
    }
    
    @Test
    public void testAplicarModificadoresPersonajeNoCompatible() {
        // Crear un personaje que no está en la lista
        PersonajeModel personajeNoCompatible = new PersonajeModel();
        personajeNoCompatible.setId(99);
        personajeNoCompatible.setNombre("Personaje No Compatible");
        personajeNoCompatible.setNombreCodigo("nocompatible");
        personajeNoCompatible.setVida(100);
        personajeNoCompatible.setFuerza(80);
        
        // Aplicar modificadores
        PersonajeModel resultado = escenario.aplicarModificadores(personajeNoCompatible);
        
        // No debe haber cambios
        assertEquals(100, resultado.getVida());
        assertEquals(80, resultado.getFuerza());
    }
    
    @Test
    public void testEquals() {
        EscenarioModel escenario1 = new EscenarioModel(1, "Torre Stark", "Descripción");
        EscenarioModel escenario2 = new EscenarioModel(1, "Torre Stark", "Otra descripción");
        EscenarioModel escenario3 = new EscenarioModel(2, "Wakanda", "Descripción");
        
        // Mismo ID y nombre
        assertTrue(escenario1.equals(escenario2));
        
        // Diferente ID
        assertFalse(escenario1.equals(escenario3));
    }
    
    @Test
    public void testToString() {
        assertEquals("Torre Stark", escenario.toString());
    }
}