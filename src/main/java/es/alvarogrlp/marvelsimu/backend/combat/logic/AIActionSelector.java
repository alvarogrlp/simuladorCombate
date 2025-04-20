package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

/**
 * Clase encargada de seleccionar las acciones de la IA en combate
 */
public class AIActionSelector {
    
    private Random random = new Random();
    
    /**
     * Selecciona el mejor ataque para la IA en base a la situación actual
     */
    public String selectBestAttack(PersonajeModel aiCharacter, PersonajeModel playerCharacter) {
        // Lista de ataques disponibles con su puntuación de prioridad
        List<AttackOption> options = new ArrayList<>();
        
        // Verificar ataques disponibles
        AtaqueModel ataqueCC = aiCharacter.getAtaquePorTipo("ACC");
        AtaqueModel ataqueAD = aiCharacter.getAtaquePorTipo("AAD");
        AtaqueModel habilidad1 = aiCharacter.getAtaquePorTipo("habilidad_mas_poderosa");
        AtaqueModel habilidad2 = aiCharacter.getAtaquePorTipo("habilidad_caracteristica");
        
        // Añadir ataque melee si está disponible
        if (ataqueCC == null || ataqueCC.estaDisponible()) {
            options.add(new AttackOption("melee", evaluateMeleeAttack(aiCharacter, playerCharacter)));
        }
        
        // Añadir ataque a distancia si está disponible
        if (ataqueAD == null || ataqueAD.estaDisponible()) {
            options.add(new AttackOption("lejano", evaluateRangedAttack(aiCharacter, playerCharacter)));
        }
        
        // Añadir habilidades si están disponibles
        if (habilidad1 != null && habilidad1.estaDisponible()) {
            options.add(new AttackOption("habilidad1", evaluateAbility1(aiCharacter, playerCharacter)));
        }
        
        if (habilidad2 != null && habilidad2.estaDisponible()) {
            options.add(new AttackOption("habilidad2", evaluateAbility2(aiCharacter, playerCharacter)));
        }
        
        // Añadir algo de aleatoriedad para que no sea predecible
        options.forEach(o -> o.score += random.nextInt(20));
        
        // Si no hay opciones, usar ataque melee por defecto
        if (options.isEmpty()) {
            return "melee";
        }
        
        // Ordenar opciones por puntuación de mayor a menor
        options.sort((a, b) -> Integer.compare(b.score, a.score));
        
        // Devolver la opción con mayor puntuación
        return options.get(0).attackType;
    }
    
    /**
     * Evalúa la eficacia del ataque melee en la situación actual
     */
    private int evaluateMeleeAttack(PersonajeModel ai, PersonajeModel player) {
        int score = 60; // Puntuación base
        
        // Considerar relación de poder entre los personajes
        if (ai.getPoder() > player.getPoder()) {
            score += 15; // Favorecemos ataque melee si tenemos más poder
        }
        
        // Si la salud del personaje de la IA es baja, ser más precavido
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 25;
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia del ataque a distancia en la situación actual
     */
    private int evaluateRangedAttack(PersonajeModel ai, PersonajeModel player) {
        int score = 55; // Puntuación base
        
        // Si el poder del AI es menor que el del jugador, favorecer ataque a distancia
        if (ai.getPoder() < player.getPoder()) {
            score += 15;
        }
        
        // Si la salud de la IA es baja, favorecer ataques a distancia
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score += 20;
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia de la habilidad 1 en la situación actual
     */
    private int evaluateAbility1(PersonajeModel ai, PersonajeModel player) {
        int score = 70; // Puntuación base alta al ser habilidad especial
        
        // Obtener la habilidad
        AtaqueModel habilidad = ai.getAtaquePorTipo("habilidad_mas_poderosa");
        
        // Si la vida del enemigo es baja, intentar rematar
        if (player.getVidaActual() < player.getVida() * 0.3) {
            score += 30;
        }
        
        // Si nuestra vida es baja, ser conservador con las habilidades
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 15;
        }
        
        // Considerar usos restantes
        if (habilidad != null && habilidad.getUsosRestantes() == 1) {
            score -= 15; // Más conservador con el último uso
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia de la habilidad 2 en la situación actual
     */
    private int evaluateAbility2(PersonajeModel ai, PersonajeModel player) {
        int score = 75; // Puntuación base más alta para la habilidad definitiva
        
        // Obtener la habilidad
        AtaqueModel habilidad = ai.getAtaquePorTipo("habilidad_caracteristica");
        
        // Si la vida del enemigo es muy baja, usar la habilidad definitiva
        if (player.getVidaActual() < player.getVida() * 0.25) {
            score += 40;
        }
        
        // Si nuestra vida es baja, ser conservador con las habilidades
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 20;
        }
        
        // Considerar usos restantes
        if (habilidad != null && habilidad.getUsosRestantes() == 1) {
            score -= 25; // Mucho más conservador con el último uso
        }
        
        return score;
    }
    
    /**
     * Clase interna para representar una opción de ataque con su puntuación
     */
    private class AttackOption {
        String attackType;
        int score;
        
        public AttackOption(String attackType, int score) {
            this.attackType = attackType;
            this.score = score;
        }
    }
}