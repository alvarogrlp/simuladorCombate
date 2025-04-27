package backend.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class PersonajeModelTest {
    
    private PersonajeModel personaje;
    
    @Before
    public void setUp() {
        personaje = new PersonajeModel();
        personaje.setId(1);
        personaje.setNombre("Iron Man");
        personaje.setNombreCodigo("ironman");
        personaje.setDescripcion("El Vengador Blindado");
        personaje.setVida(1000);
        personaje.setFuerza(80);
        personaje.setVelocidad(70);
        personaje.setPoder(90);
        personaje.setImagenMiniatura("ironman_mini.png");
        personaje.setImagenCombate("ironman_combat.png");
        
        // Agregar ataques
        AtaqueModel ataque1 = new AtaqueModel(1, 1, 1, "ACC", "Puñetazo", 50, 0, 0);
        ataque1.setCodigo("ironman_melee");
        ataque1.setTipo("ACC");
        
        AtaqueModel ataque2 = new AtaqueModel(2, 1, 2, "AAD", "Rayo Repulsor", 70, 0, 0);
        ataque2.setCodigo("ironman_range");
        ataque2.setTipo("AAD");
        
        AtaqueModel ataque3 = new AtaqueModel(3, 1, 3, "habilidad_mas_poderosa", "Unibeam", 150, 3, 1);
        ataque3.setCodigo("ironman_hab1");
        ataque3.setTipo("habilidad_mas_poderosa");
        
        personaje.addAtaque(ataque1);
        personaje.addAtaque(ataque2);
        personaje.addAtaque(ataque3);
        
        // Agregar pasivas
        List<PasivaModel> pasivas = new ArrayList<>();
        PasivaModel pasiva = new PasivaModel(1, 1, "Armadura Adaptativa", 
                                            "Reduce daño recibido", "on_damage_taken", 
                                            "reduce_damage_pct", 20, 0, 0);
        pasivas.add(pasiva);
        personaje.setPasivas(pasivas);
        
        // Inicializar para combate
        personaje.inicializarVida();
    }
    
    @Test
    public void testConstructor() {
        PersonajeModel p = new PersonajeModel();
        assertNotNull(p.getAtaques());
        assertNotNull(p.getPasivas());
        assertNotNull(p.getBuffsActivos());
        assertNotNull(p.getDebuffsActivos());
        assertFalse(p.isDerrotado());
    }
    
    @Test
    public void testAddAtaque() {
        int tamanoInicial = personaje.getAtaques().size();
        
        AtaqueModel nuevoAtaque = new AtaqueModel(4, 1, 4, "habilidad_caracteristica", "Misiles", 100, 2, 2);
        nuevoAtaque.setCodigo("ironman_hab2");
        nuevoAtaque.setTipo("habilidad_caracteristica");
        
        personaje.addAtaque(nuevoAtaque);
        
        assertEquals(tamanoInicial + 1, personaje.getAtaques().size());
        assertEquals(nuevoAtaque, personaje.getAtaques().get(tamanoInicial));
    }
    
    @Test
    public void testGetAtaquePorTipo() {
        AtaqueModel ataqueMelee = personaje.getAtaquePorTipo("ACC");
        assertNotNull(ataqueMelee);
        assertEquals("Puñetazo", ataqueMelee.getNombre());
        
        AtaqueModel ataqueRango = personaje.getAtaquePorTipo("AAD");
        assertNotNull(ataqueRango);
        assertEquals("Rayo Repulsor", ataqueRango.getNombre());
        
        AtaqueModel habilidad1 = personaje.getAtaquePorTipo("habilidad_mas_poderosa");
        assertNotNull(habilidad1);
        assertEquals("Unibeam", habilidad1.getNombre());
        
        // Tipo inexistente
        assertNull(personaje.getAtaquePorTipo("tipo_inexistente"));
    }
    
    @Test
    public void testGetAtaquePorCodigo() {
        AtaqueModel ataque = personaje.getAtaquePorCodigo("ironman_melee");
        assertNotNull(ataque);
        assertEquals("Puñetazo", ataque.getNombre());
        
        // Código inexistente
        assertNull(personaje.getAtaquePorCodigo("codigo_inexistente"));
    }
    
    @Test
    public void testInicializarVida() {
        // Reducir vida
        personaje.setVidaActual(500);
        
        // Consumir un uso de habilidad
        AtaqueModel habilidad = personaje.getAtaquePorTipo("habilidad_mas_poderosa");
        habilidad.consumirUso();
        assertEquals(2, habilidad.getUsosRestantes());
        
        // Inicializar de nuevo
        personaje.inicializarVida();
        
        // Verificar restablecimiento
        assertEquals(1000, personaje.getVidaActual());
        assertEquals(3, habilidad.getUsosRestantes());
        assertTrue(personaje.getBuffsActivos().isEmpty());
        assertTrue(personaje.getDebuffsActivos().isEmpty());
    }
    
    @Test
    public void testRecibirDano() {
        assertEquals(1000, personaje.getVidaActual());
        
        // Recibir daño
        personaje.recibirDano(300);
        assertEquals(700, personaje.getVidaActual());
        assertFalse(personaje.isDerrotado());
        
        // Más daño
        personaje.recibirDano(600);
        assertEquals(100, personaje.getVidaActual());
        assertFalse(personaje.isDerrotado());
        
        // Daño letal
        boolean derrotado = personaje.recibirDano(200);
        assertEquals(0, personaje.getVidaActual());
        assertTrue(personaje.isDerrotado());
        assertTrue(derrotado);
        
        // Daño a personaje ya derrotado
        derrotado = personaje.recibirDano(100);
        assertEquals(0, personaje.getVidaActual());
        assertTrue(personaje.isDerrotado());
        assertTrue(derrotado);
    }
    
    @Test
    public void testRecibirDañoConPasivas() {
        // Crear un personaje con pasiva de supervivencia
        PersonajeModel personajeResistente = new PersonajeModel();
        personajeResistente.setVida(1000);
        
        List<PasivaModel> pasivas = new ArrayList<>();
        
        // Pasiva de reducción de daño
        PasivaModel pasiva1 = new PasivaModel(1, 1, "Armadura", "Reduce daño", 
                                            "on_damage_taken", "reduce_damage_pct", 50, 1, 0);
        
        // Pasiva de supervivencia
        PasivaModel pasiva2 = new PasivaModel(2, 1, "Último Aliento", "Revive con poca vida", 
                                            "on_fatal_damage", "revive_pct", 20, 1, 0);
        
        pasivas.add(pasiva1);
        pasivas.add(pasiva2);
        personajeResistente.setPasivas(pasivas);
        personajeResistente.inicializarVida();
        
        // Recibir daño con reducción
        personajeResistente.recibirDaño(200, "fisico", false);
        // 200 - 50% = 100 de daño
        assertEquals(900, personajeResistente.getVidaActual());
        
        // La pasiva está usada, siguiente daño sin reducción
        personajeResistente.recibirDaño(200, "fisico", false);
        assertEquals(700, personajeResistente.getVidaActual());
        
        // Daño letal, pero con pasiva de supervivencia
        boolean derrotado = personajeResistente.recibirDaño(800, "fisico", false);
        assertFalse(derrotado);
        assertEquals(200, personajeResistente.getVidaActual()); // 20% de 1000
        
        // Otro daño letal, esta vez sin pasiva disponible
        derrotado = personajeResistente.recibirDaño(300, "fisico", false);
        assertTrue(derrotado);
        assertEquals(0, personajeResistente.getVidaActual());
    }
    
    @Test
    public void testRegenerar() {
        // Reducir vida
        personaje.recibirDano(500);
        assertEquals(500, personaje.getVidaActual());
        
        // Regenerar parcialmente
        int vidaActual = personaje.regenerar(200);
        assertEquals(700, vidaActual);
        assertEquals(700, personaje.getVidaActual());
        
        // Regenerar excediendo el máximo
        vidaActual = personaje.regenerar(500);
        assertEquals(1000, vidaActual);
        assertEquals(1000, personaje.getVidaActual());
        
        // Regenerar con vida llena
        vidaActual = personaje.regenerar(100);
        assertEquals(1000, vidaActual);
        assertEquals(1000, personaje.getVidaActual());
    }
    
    @Test
    public void testGetPasivaPorNombre() {
        PasivaModel pasiva = personaje.getPasivaPorNombre("Armadura Adaptativa");
        assertNotNull(pasiva);
        assertEquals("reduce_damage_pct", pasiva.getEfectoTipo());
        
        // Nombre inexistente
        assertNull(personaje.getPasivaPorNombre("Pasiva Inexistente"));
    }
    
    @Test
    public void testClonar() {
        PersonajeModel clon = personaje.clonar();
        
        // Verificar que es un objeto diferente
        assertNotSame(personaje, clon);
        
        // Verificar propiedades básicas
        assertEquals(personaje.getId(), clon.getId());
        assertEquals(personaje.getNombre(), clon.getNombre());
        assertEquals(personaje.getNombreCodigo(), clon.getNombreCodigo());
        assertEquals(personaje.getVida(), clon.getVida());
        assertEquals(personaje.getFuerza(), clon.getFuerza());
        assertEquals(personaje.getVelocidad(), clon.getVelocidad());
        assertEquals(personaje.getPoder(), clon.getPoder());
        
        // Verificar que los ataques son copias, no referencias
        assertNotSame(personaje.getAtaques(), clon.getAtaques());
        assertEquals(personaje.getAtaques().size(), clon.getAtaques().size());
        
        // Verificar que las pasivas son copias, no referencias
        assertNotSame(personaje.getPasivas(), clon.getPasivas());
        assertEquals(personaje.getPasivas().size(), clon.getPasivas().size());
        
        // Vida actual inicializada
        assertEquals(personaje.getVida(), clon.getVidaActual());
    }
    
    @Test
    public void testCompatibilidadAntiguosMetodos() {
        // Probar getters de compatibilidad
        assertEquals(50, personaje.getAtaqueMelee());
        assertEquals(70, personaje.getAtaqueLejano());
        assertEquals(150, personaje.getHabilidad1Poder());
        
        assertEquals("Puñetazo", personaje.getAtaqueMeleeNombre());
        assertEquals("Rayo Repulsor", personaje.getAtaqueLejanoNombre());
        assertEquals("Unibeam", personaje.getHabilidad1Nombre());
        
        // Probar setters de compatibilidad
        personaje.setAtaqueMelee(60);
        assertEquals(60, personaje.getAtaqueMelee());
        
        personaje.setHabilidad1Nombre("Super Unibeam");
        assertEquals("Super Unibeam", personaje.getHabilidad1Nombre());
        
        // Probar con uno que no existe y debe crearse
        personaje.setHabilidad2Nombre("Misiles Inteligentes");
        assertEquals("Misiles Inteligentes", personaje.getHabilidad2Nombre());
        
        // Verificar creación del ataque
        AtaqueModel nuevoAtaque = personaje.getAtaquePorTipo("habilidad_caracteristica");
        assertNotNull(nuevoAtaque);
        assertEquals("Misiles Inteligentes", nuevoAtaque.getNombre());
    }
    
    @Test
    public void testEquals() {
        PersonajeModel p1 = new PersonajeModel();
        p1.setId(1);
        p1.setNombreCodigo("ironman");
        
        PersonajeModel p2 = new PersonajeModel();
        p2.setId(1);
        p2.setNombreCodigo("ironman");
        
        PersonajeModel p3 = new PersonajeModel();
        p3.setId(2);
        p3.setNombreCodigo("thor");
        
        // Mismo id y código
        assertTrue(p1.equals(p2));
        
        // Diferentes
        assertFalse(p1.equals(p3));
        
        // Null y otro tipo
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("String"));
    }
    
    @Test
    public void testToString() {
        String esperado = "Iron Man (ironman)";
        assertEquals(esperado, personaje.toString());
    }
}