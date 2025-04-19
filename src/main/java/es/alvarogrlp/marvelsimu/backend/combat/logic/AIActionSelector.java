package es.alvarogrlp.marvelsimu.backend.combat.logic;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase encargada de seleccionar las acciones de la IA en combate
 */
public class AIActionSelector {
    
    private Random random = new Random();
    
    /**
     * Selecciona el mejor ataque para la IA en base a la situación actual
     * 
     * @param aiCharacter Personaje de la IA
     * @param playerCharacter Personaje del jugador
     * @return String con el tipo de ataque elegido: "melee", "lejano", "habilidad1", "habilidad2"
     */
    public String selectBestAttack(PersonajeModel aiCharacter, PersonajeModel playerCharacter) {
        // Lista de ataques disponibles con su puntuación de prioridad
        List<AttackOption> options = new ArrayList<>();
        
        // Añadir ataque melee
        options.add(new AttackOption("melee", evaluateMeleeAttack(aiCharacter, playerCharacter)));
        
        // Añadir ataque a distancia
        options.add(new AttackOption("lejano", evaluateRangedAttack(aiCharacter, playerCharacter)));
        
        // Añadir habilidad 1 si tiene usos disponibles
        if (aiCharacter.tieneUsosDisponibles("habilidad1")) {
            options.add(new AttackOption("habilidad1", evaluateAbility1(aiCharacter, playerCharacter)));
        }
        
        // Añadir habilidad 2 si tiene usos disponibles
        if (aiCharacter.tieneUsosDisponibles("habilidad2")) {
            options.add(new AttackOption("habilidad2", evaluateAbility2(aiCharacter, playerCharacter)));
        }
        
        // Añadir algo de aleatoriedad para que no sea predecible
        options.forEach(o -> o.score += random.nextInt(20));
        
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
        
        // Si el ataque melee es fuerte, favorecer
        if (ai.getAtaqueMelee() > ai.getAtaqueLejano()) {
            score += 15;
        }
        
        // Si el enemigo tiene baja resistencia física y el ataque es físico
        if ("fisico".equals(ai.getAtaqueMeleeTipo()) && player.getResistenciaFisica() < 30) {
            score += 20;
        }
        
        // Si el enemigo tiene baja resistencia mágica y el ataque es mágico
        if ("magico".equals(ai.getAtaqueMeleeTipo()) && player.getResistenciaMagica() < 30) {
            score += 20;
        }
        
        // Si la salud del personaje de la IA es baja, favorecer ataques más seguros (a distancia)
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 25;
        }
        
        // Si la habilidad de evasión del jugador es alta, penalizar el ataque melee
        if (player.getEvasion() > 20) {
            score -= player.getEvasion() / 2;
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia del ataque a distancia en la situación actual
     */
    private int evaluateRangedAttack(PersonajeModel ai, PersonajeModel player) {
        int score = 55; // Puntuación base ligeramente inferior al melee
        
        // Si el ataque a distancia es fuerte, favorecer
        if (ai.getAtaqueLejano() > ai.getAtaqueMelee()) {
            score += 15;
        }
        
        // Si el enemigo tiene baja resistencia física y el ataque es físico
        if ("fisico".equals(ai.getAtaqueLejanoTipo()) && player.getResistenciaFisica() < 30) {
            score += 20;
        }
        
        // Si el enemigo tiene baja resistencia mágica y el ataque es mágico
        if ("magico".equals(ai.getAtaqueLejanoTipo()) && player.getResistenciaMagica() < 30) {
            score += 20;
        }
        
        // Si la salud del personaje de la IA es baja, favorecer ataques a distancia
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score += 20;
        }
        
        // Si la habilidad de evasión del jugador es alta, el ataque a distancia podría ser mejor
        if (player.getEvasion() > 20) {
            score += 10;
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia de la habilidad 1 en la situación actual
     */
    private int evaluateAbility1(PersonajeModel ai, PersonajeModel player) {
        int score = 70; // Puntuación base mayor por ser habilidad especial
        
        // Si el poder de la habilidad es alto, favorecer
        score += (ai.getHabilidad1Poder() - ai.getAtaqueMelee()) / 2;
        
        // Si la habilidad es daño verdadero, favorecer mucho
        if ("daño_verdadero".equals(ai.getHabilidad1Tipo())) {
            score += 40;
        } 
        // Si es daño físico penetrante, también favorecer
        else if ("fisico_penetrante".equals(ai.getHabilidad1Tipo())) {
            score += 25;
        }
        // Favor basado en resistencias del jugador
        else if ("fisico".equals(ai.getHabilidad1Tipo()) && player.getResistenciaFisica() < 30) {
            score += 20;
        } else if ("magico".equals(ai.getHabilidad1Tipo()) && player.getResistenciaMagica() < 30) {
            score += 20;
        }
        
        // Si la vida del jugador es baja, favorecer el uso de habilidades para rematar
        if (player.getVidaActual() < player.getVida() * 0.3) {
            score += 30;
        }
        
        // Si la vida de la IA es baja, ser más conservador con las habilidades
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 15;
        }
        
        // Considerar la cantidad de usos restantes
        int usosRestantes = ai.getUsosRestantes("habilidad1");
        if (usosRestantes == 1) {
            score -= 15; // Ser más conservador con el último uso
        }
        
        return score;
    }
    
    /**
     * Evalúa la eficacia de la habilidad 2 en la situación actual
     */
    private int evaluateAbility2(PersonajeModel ai, PersonajeModel player) {
        int score = 75; // Puntuación base mayor por ser habilidad especial más poderosa
        
        // Si el poder de la habilidad es alto, favorecer
        score += (ai.getHabilidad2Poder() - ai.getAtaqueMelee()) / 2;
        
        // Si la habilidad es daño verdadero, favorecer mucho
        if ("daño_verdadero".equals(ai.getHabilidad2Tipo())) {
            score += 40;
        } 
        // Si es daño físico penetrante, también favorecer
        else if ("fisico_penetrante".equals(ai.getHabilidad2Tipo())) {
            score += 25;
        }
        // Favor basado en resistencias del jugador
        else if ("fisico".equals(ai.getHabilidad2Tipo()) && player.getResistenciaFisica() < 30) {
            score += 20;
        } else if ("magico".equals(ai.getHabilidad2Tipo()) && player.getResistenciaMagica() < 30) {
            score += 20;
        }
        
        // Si la vida del jugador es baja, favorecer el uso de habilidades para rematar
        if (player.getVidaActual() < player.getVida() * 0.3) {
            score += 35;
        }
        
        // Si la vida de la IA es baja, ser más conservador con las habilidades
        if (ai.getVidaActual() < ai.getVida() * 0.3) {
            score -= 15;
        }
        
        // Considerar la cantidad de usos restantes
        int usosRestantes = ai.getUsosRestantes("habilidad2");
        if (usosRestantes == 1) {
            score -= 20; // Ser más conservador con el último uso
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