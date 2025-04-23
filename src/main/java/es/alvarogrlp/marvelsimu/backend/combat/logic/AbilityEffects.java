package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class AbilityEffects {
    // Mapa para seguir los efectos activos por personaje
    private Map<PersonajeModel, Map<String, Integer>> activeEffects = new HashMap<>();
    private Map<PersonajeModel, Double> damageReductionMap = new HashMap<>();
    private Map<PersonajeModel, Double> damageReflectionMap = new HashMap<>();
    private Map<PersonajeModel, Double> damageBoostMap = new HashMap<>();
    private Map<PersonajeModel, Boolean> healingInversionMap = new HashMap<>();
    private Map<PersonajeModel, Boolean> restrictedToBasicAttacksMap = new HashMap<>();
    private int enemyTurnsToSkip = 0;
    private List<ScheduledEffect> scheduledEffects = new ArrayList<>();
    
    private CombatManager combatManager;
    
    public AbilityEffects(CombatManager combatManager) {
        this.combatManager = combatManager;
    }
    
    /**
     * Reduce el contador de todos los efectos activos al inicio de un turno
     * @param character El personaje cuyos efectos se actualizarán
     */
    public void tickEffects(PersonajeModel character) {
        if (!activeEffects.containsKey(character)) {
            return;
        }
        
        Map<String, Integer> effects = activeEffects.get(character);
        Map<String, Integer> updatedEffects = new HashMap<>();
        
        // Procesar cada efecto, reduciendo su duración
        for (Map.Entry<String, Integer> entry : effects.entrySet()) {
            String effectType = entry.getKey();
            int turnsRemaining = entry.getValue() - 1;
            
            // Si el efecto aún no ha terminado, mantenerlo
            if (turnsRemaining > 0) {
                updatedEffects.put(effectType, turnsRemaining);
            } else {
                // Efecto terminado, aplicar limpieza si es necesario
                handleEffectExpiration(character, effectType);
            }
        }
        
        // Actualizar el mapa de efectos activos
        if (updatedEffects.isEmpty()) {
            activeEffects.remove(character);
        } else {
            activeEffects.put(character, updatedEffects);
        }
        
        // Procesar efectos programados
        processScheduledEffects();
    }
    
    /**
     * Maneja la expiración de un efecto
     */
    private void handleEffectExpiration(PersonajeModel character, String effectType) {
        switch (effectType) {
            case "damageReduction":
                damageReductionMap.remove(character);
                break;
            case "damageReflection":
                damageReflectionMap.remove(character);
                break;
            case "damageBoost":
                damageBoostMap.remove(character);
                break;
            case "healingInversion":
                healingInversionMap.remove(character);
                break;
            case "restrictedToBasicAttacks":
                restrictedToBasicAttacksMap.remove(character);
                break;
        }
    }
    
    /**
     * Programa un efecto para que se ejecute después de cierto número de turnos
     * @param effect El efecto a ejecutar
     * @param turns El número de turnos después de los cuales ejecutar el efecto
     */
    public void scheduleEffect(Runnable effect, int turns) {
        scheduledEffects.add(new ScheduledEffect(effect, turns));
    }
    
    /**
     * Procesa los efectos programados, ejecutando los que hayan llegado a 0 turnos
     */
    public void processScheduledEffects() {
        List<ScheduledEffect> effectsToRemove = new ArrayList<>();
        
        for (ScheduledEffect effect : scheduledEffects) {
            effect.decrementTurns();
            if (effect.getTurnsRemaining() <= 0) {
                effect.execute();
                effectsToRemove.add(effect);
            }
        }
        
        scheduledEffects.removeAll(effectsToRemove);
    }
    
    /**
     * Aplica una reducción de daño a un personaje
     * @param character El personaje al que aplicar la reducción
     * @param reduction El factor de reducción (0.0 a 1.0)
     * @param duration Duración en turnos
     */
    public void applyDamageReduction(PersonajeModel character, double reduction, int duration) {
        damageReductionMap.put(character, reduction);
        addEffectToCharacter(character, "damageReduction", duration);
    }
    
    /**
     * Aplica un reflejo de daño a un personaje
     * @param character El personaje que reflejará daño
     * @param reflectionFactor El factor de reflejo (0.0 a 1.0)
     * @param duration Duración en turnos
     */
    public void applyDamageReflection(PersonajeModel character, double reflectionFactor, int duration) {
        damageReflectionMap.put(character, reflectionFactor);
        addEffectToCharacter(character, "damageReflection", duration);
    }
    
    /**
     * Aplica un aumento de daño a un personaje
     * @param character El personaje que recibirá el boost
     * @param boostFactor El factor de aumento (ej: 0.5 para +50%)
     * @param duration Duración en turnos o ataques
     */
    public void applyDamageBoost(PersonajeModel character, double boostFactor, int duration) {
        damageBoostMap.put(character, boostFactor);
        addEffectToCharacter(character, "damageBoost", duration);
    }
    
    /**
     * Invierte las curaciones en daño para un personaje
     * @param character El personaje afectado
     * @param duration Duración en turnos
     */
    public void applyHealingInversion(PersonajeModel character, int duration) {
        healingInversionMap.put(character, true);
        addEffectToCharacter(character, "healingInversion", duration);
    }
    
    /**
     * Restringe a un personaje a usar solo ataques básicos
     * @param character El personaje afectado
     * @param duration Duración en turnos
     */
    public void restrictToBasicAttacks(PersonajeModel character, int duration) {
        restrictedToBasicAttacksMap.put(character, true);
        addEffectToCharacter(character, "restrictedToBasicAttacks", duration);
    }
    
    /**
     * Configura la cantidad de turnos del enemigo que se omitirán
     * @param turns Número de turnos a omitir
     */
    public void skipEnemyTurns(int turns) {
        enemyTurnsToSkip = turns;
    }
    
    /**
     * Obtiene y consume un turno de los turnos a omitir
     * @return true si se debe omitir un turno
     */
    public boolean shouldSkipEnemyTurn() {
        if (enemyTurnsToSkip > 0) {
            enemyTurnsToSkip--;
            return true;
        }
        return false;
    }
    
    /**
     * Añade un efecto al mapa de efectos activos de un personaje
     */
    private void addEffectToCharacter(PersonajeModel character, String effectType, int duration) {
        Map<String, Integer> characterEffects = activeEffects.getOrDefault(character, new HashMap<>());
        characterEffects.put(effectType, duration);
        activeEffects.put(character, characterEffects);
    }
    
    /**
     * Verifica si un personaje tiene reducción de daño y devuelve el factor
     */
    public double getDamageReductionFactor(PersonajeModel character) {
        return damageReductionMap.getOrDefault(character, 0.0);
    }
    
    /**
     * Verifica si un personaje tiene reflejo de daño y devuelve el factor
     */
    public double getDamageReflectionFactor(PersonajeModel character) {
        return damageReflectionMap.getOrDefault(character, 0.0);
    }
    
    /**
     * Verifica si un personaje tiene aumento de daño y devuelve el factor
     */
    public double getDamageBoostFactor(PersonajeModel character) {
        return damageBoostMap.getOrDefault(character, 0.0);
    }
    
    /**
     * Verifica si un personaje tiene inversión de curación
     */
    public boolean hasHealingInversion(PersonajeModel character) {
        return healingInversionMap.getOrDefault(character, false);
    }
    
    /**
     * Verifica si un personaje está restringido a ataques básicos
     */
    public boolean isRestrictedToBasicAttacks(PersonajeModel character) {
        return restrictedToBasicAttacksMap.getOrDefault(character, false);
    }
    
    /**
     * Clase para representar un efecto programado para ejecutarse después de ciertos turnos
     */
    public class ScheduledEffect {
        private Runnable effect;
        private int turnsRemaining;
        
        public ScheduledEffect(Runnable effect, int turnsRemaining) {
            this.effect = effect;
            this.turnsRemaining = turnsRemaining;
        }
        
        public void execute() {
            effect.run();
        }
        
        public int getTurnsRemaining() {
            return turnsRemaining;
        }
        
        public void decrementTurns() {
            if (turnsRemaining > 0) {
                turnsRemaining--;
            }
        }
    }
}
