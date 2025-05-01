package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.combat.model.Stat;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class AbilityManager {
    
    private final CombatManager combatManager;
    private final Map<String, BiFunction<PersonajeModel, PersonajeModel, CombatMessage>> abilityHandlers;
    private final Map<String, Integer> cdRemaining;
    private final Map<String, Integer> maxUses;
    private final Map<String, Integer> usesRemaining;
    
    public AbilityManager(CombatManager combatManager) {
        this.combatManager = combatManager;
        this.abilityHandlers = new HashMap<>();
        this.cdRemaining = new HashMap<>();
        this.maxUses = new HashMap<>();
        this.usesRemaining = new HashMap<>();
        
        registerAbilityHandlers();
        initializeAbilityCooldowns();
    }
    
    private void initializeAbilityCooldowns() {
        // Habilidades existentes
        usesRemaining.put("magik_hab1", 1); cdRemaining.put("magik_hab1", 0);
        usesRemaining.put("magik_hab2", 1); cdRemaining.put("magik_hab2", 0);
        usesRemaining.put("iron_man_hab1", 1); cdRemaining.put("iron_man_hab1", 0);
        usesRemaining.put("iron_man_hab2", 1); cdRemaining.put("iron_man_hab2", 0);
        usesRemaining.put("doctor_doom_hab1", 1); cdRemaining.put("doctor_doom_hab1", 0);
        usesRemaining.put("doctor_doom_hab2", 1); cdRemaining.put("doctor_doom_hab2", 0);
        
        // Registrar todas las habilidades adicionales
        // Spider-Man
        usesRemaining.put("spider_man_hab1", 1); cdRemaining.put("spider_man_hab1", 0);
        usesRemaining.put("spider_man_hab2", 1); cdRemaining.put("spider_man_hab2", 0);
        
        // Silver Surfer
        usesRemaining.put("silver_surfer_hab1", 1); cdRemaining.put("silver_surfer_hab1", 0);
        usesRemaining.put("silver_surfer_hab2", 1); cdRemaining.put("silver_surfer_hab2", 0);
        
        // Doctor Strange
        usesRemaining.put("doctor_strange_hab1", 1); cdRemaining.put("doctor_strange_hab1", 0);
        usesRemaining.put("doctor_strange_hab2", 1); cdRemaining.put("doctor_strange_hab2", 0);
        
        // Thanos
        usesRemaining.put("thanos_hab1", 1); cdRemaining.put("thanos_hab1", 0); 
        usesRemaining.put("thanos_hab2", 1); cdRemaining.put("thanos_hab2", 0);
        usesRemaining.put("thanos_gauntlet_hab1", 1); cdRemaining.put("thanos_gauntlet_hab1", 0);
        usesRemaining.put("thanos_gauntlet_hab2", 1); cdRemaining.put("thanos_gauntlet_hab2", 0);
        
        // Wolverine
        usesRemaining.put("wolverine_hab1", 1); cdRemaining.put("wolverine_hab1", 0);
        usesRemaining.put("wolverine_hab2", 1); cdRemaining.put("wolverine_hab2", 0);
        
        // Captain America
        usesRemaining.put("captain_america_hab1", 1); cdRemaining.put("captain_america_hab1", 0);
        usesRemaining.put("captain_america_hab2", 1); cdRemaining.put("captain_america_hab2", 0);
        
        // Hulk
        usesRemaining.put("hulk_hab1", 1); cdRemaining.put("hulk_hab1", 0);
        usesRemaining.put("hulk_hab2", 1); cdRemaining.put("hulk_hab2", 0);
        
        // Thor
        usesRemaining.put("thor_hab1", 1); cdRemaining.put("thor_hab1", 0);
        usesRemaining.put("thor_hab2", 1); cdRemaining.put("thor_hab2", 0);
        
        // Black Widow
        usesRemaining.put("black_widow_hab1", 1); cdRemaining.put("black_widow_hab1", 0);
        usesRemaining.put("black_widow_hab2", 1); cdRemaining.put("black_widow_hab2", 0);
        
        // Captain Marvel
        usesRemaining.put("captain_marvel_hab1", 1); cdRemaining.put("captain_marvel_hab1", 0);
        usesRemaining.put("captain_marvel_hab2", 1); cdRemaining.put("captain_marvel_hab2", 0);
        
        // Black Panther
        usesRemaining.put("black_panther_hab1", 1); cdRemaining.put("black_panther_hab1", 0);
        usesRemaining.put("black_panther_hab2", 1); cdRemaining.put("black_panther_hab2", 0);
        
        // Scarlet Witch
        usesRemaining.put("scarlet_witch_hab1", 1); cdRemaining.put("scarlet_witch_hab1", 0);
        usesRemaining.put("scarlet_witch_hab2", 1); cdRemaining.put("scarlet_witch_hab2", 0);
        
        // Deadpool
        usesRemaining.put("deadpool_hab1", 1); cdRemaining.put("deadpool_hab1", 0);
        usesRemaining.put("deadpool_hab2", 1); cdRemaining.put("deadpool_hab2", 0);
        
        // Phoenix
        usesRemaining.put("phoenix_hab1", 1); cdRemaining.put("phoenix_hab1", 0);
        usesRemaining.put("phoenix_hab2", 1); cdRemaining.put("phoenix_hab2", 0);
        
        // Ghost Rider
        usesRemaining.put("ghost_rider_hab1", 1); cdRemaining.put("ghost_rider_hab1", 0);
        usesRemaining.put("ghost_rider_hab2", 1); cdRemaining.put("ghost_rider_hab2", 0);
    }
    
    private void registerAbilityHandlers() {
        abilityHandlers.clear();

        // === MAGIK (Forma Base) ===
        abilityHandlers.put("magik_hab1", (attacker, defender) -> {
            String key = "magik_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Darkchild Rising");
            consume(key);

            // Daño simple: 3000
            int damage = 3000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Darkchild Rising - ¡Magik desata su poder! (" + damage + " daño)", true);
        });

        abilityHandlers.put("magik_hab2", (attacker, defender) -> {
            String key = "magik_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Espada Alma");
            consume(key);

            // Daño: 80% de su Poder
            int damage = (int)(attacker.getPoder() * 0.8);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Espada Alma - ¡La espada absorbe la esencia vital! (" + damage + " daño)", true);
        });

        // === THANOS (Sin Guantelete) ===
        abilityHandlers.put("thanos_hab1", (attacker, defender) -> {
            String key = "thanos_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Titán Eterno");
            consume(key);

            // Daño: 4000 fijo
            int damage = 4000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Titán Eterno - ¡Thanos demuestra su poder! (" + damage + " daño)", true);
        });

        abilityHandlers.put("thanos_hab2", (attacker, defender) -> {
            String key = "thanos_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Conquista");
            consume(key);

            // Daño: 2500 fijo
            int damage = 2500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Conquista - ¡Thanos avanza implacable! (" + damage + " daño)", true);
        });

        // === THANOS (Guantelete) ===
        abilityHandlers.put("thanos_gauntlet_hab1", (attacker, defender) -> {
            String key = "thanos_gauntlet_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Chasquido del Infinito");
            consume(key);

            // Elimina instantáneamente al enemigo (mantenemos esta habilidad específica)
            defender.setVidaActual(0);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Chasquido del Infinito - ¡El universo se reordena a voluntad de Thanos!", true);
        });

        abilityHandlers.put("thanos_gauntlet_hab2", (attacker, defender) -> {
            String key = "thanos_gauntlet_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Voluntad de Thanos");
            consume(key);

            // Daño: 5000 fijo
            int damage = 5000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Voluntad de Thanos - ¡El Guantelete desata su poder! (" + damage + " daño)", true);
        });

        // === SCARLET WITCH ===
        abilityHandlers.put("scarlet_witch_hab1", (attacker, defender) -> {
            String key = "scarlet_witch_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Caos Absoluto");
            consume(key);

            // Daño: 5000 fijo (mantenemos el daño directo)
            int damage = 5000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Caos Absoluto - ¡La realidad se distorsiona! (" + damage + " daño)", true);
        });

        abilityHandlers.put("scarlet_witch_hab2", (attacker, defender) -> {
            String key = "scarlet_witch_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Realidad Distorsionada");
            consume(key);

            // Daño: 100% de su Poder
            int damage = attacker.getPoder();
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Realidad Distorsionada - ¡La realidad golpea al enemigo! (" + damage + " daño)", true);
        });

        // === LEGION ===
        abilityHandlers.put("legion_hab1", (attacker, defender) -> {
            String key = "legion_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Personalidades Desatadas");
            consume(key);

            // Daño: 4000 fijo
            int damage = 4000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Personalidades Desatadas - ¡Las múltiples identidades atacan! (" + damage + " daño)", true);
        });

        abilityHandlers.put("legion_hab2", (attacker, defender) -> {
            String key = "legion_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Plano Mental");
            consume(key);

            // Daño: 90% de su Poder
            int damage = (int)(attacker.getPoder() * 0.9);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Plano Mental - ¡Legion ataca la mente del enemigo! (" + damage + " daño)", true);
        });

        // === DOCTOR STRANGE ===
        abilityHandlers.put("doctor_strange_hab1", (attacker, defender) -> {
            String key = "doctor_strange_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Dimensión Espejo");
            consume(key);

            // Daño: 3500 fijo
            int damage = 3500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Dimensión Espejo - ¡Strange ataca desde otra dimensión! (" + damage + " daño)", true);
        });

        abilityHandlers.put("doctor_strange_hab2", (attacker, defender) -> {
            String key = "doctor_strange_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ojo de Agamotto");
            consume(key);

            // Daño: 4500 fijo
            int damage = 4500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ojo de Agamotto - ¡El tiempo se distorsiona! (" + damage + " daño)", true);
        });

        // === SILVER SURFER ===
        abilityHandlers.put("silver_surfer_hab1", (attacker, defender) -> {
            String key = "silver_surfer_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Nova Cósmica");
            consume(key);

            // Daño: 4500 fijo (mantenemos el daño directo)
            int damage = 4500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Nova Cósmica - ¡El poder cósmico estalla! (" + damage + " daño)", true);
        });

        abilityHandlers.put("silver_surfer_hab2", (attacker, defender) -> {
            String key = "silver_surfer_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Velocidad Estelar");
            consume(key);

            // Daño: 120% de su Velocidad
            int damage = (int)(attacker.getVelocidad() * 1.2);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Velocidad Estelar - ¡Surfer golpea a velocidades inconcebibles! (" + damage + " daño)", true);
        });

        // === ARISHEM ===
        abilityHandlers.put("arishem_hab1", (attacker, defender) -> {
            String key = "arishem_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Juicio Celestial");
            consume(key);

            // 100% de la vida actual del enemigo en daño, 50% probabilidad (mantenemos esta habilidad específica)
            if (Math.random() < 0.5) {
                int damage = defender.getVidaActual();
                defender.setVidaActual(0);
                return CombatMessage.createAbilityMessage(attacker.getNombre(),
                    "Juicio Celestial - ¡El Celestial emite su veredicto final! (" + damage + " daño)", true);
            } else {
                // Si falla, hace un daño base
                int damage = 2000;
                defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
                return CombatMessage.createAbilityMessage(attacker.getNombre(),
                    "Juicio Celestial - El enemigo escapa al juicio completo (" + damage + " daño)", true);
            }
        });

        abilityHandlers.put("arishem_hab2", (attacker, defender) -> {
            String key = "arishem_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Presencia Imponente");
            consume(key);

            // Daño: 3500 fijo
            int damage = 3500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Presencia Imponente - ¡Arishem aplasta a su enemigo! (" + damage + " daño)", true);
        });

        // === KNULL ===
        abilityHandlers.put("knull_hab1", (attacker, defender) -> {
            String key = "knull_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Oscuridad Primordial");
            consume(key);

            // Daño: 6000 fijo (mantenemos el daño directo)
            int damage = 6000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Oscuridad Primordial - ¡El vacío cósmico consume la energía vital! (" + damage + " daño)", true);
        });

        abilityHandlers.put("knull_hab2", (attacker, defender) -> {
            String key = "knull_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Rey de Simbiontes");
            consume(key);

            // Daño: 4500 fijo
            int damage = 4500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Rey de Simbiontes - ¡Knull desata la furia de sus creaciones! (" + damage + " daño)", true);
        });

        // === HULK (Máximo) ===
        abilityHandlers.put("hulk_hab1", (attacker, defender) -> {
            String key = "hulk_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Furia Gamma");
            consume(key);
            
            // Daño: 200% de su Fuerza actual (mantenemos esta habilidad específica)
            int damage = attacker.getFuerza() * 2;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Furia Gamma - ¡HULK APLASTA! (" + damage + " daño)", true);
        });

        abilityHandlers.put("hulk_hab2", (attacker, defender) -> {
            String key = "hulk_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Cuanto más enfadado...");
            consume(key);
            
            // Daño: 150% de su Fuerza
            int damage = (int)(attacker.getFuerza() * 1.5);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Cuanto más enfadado... - ¡HULK MÁS FUERTE! (" + damage + " daño)", true);
        });

        // === DOCTOR DOOM ===
        abilityHandlers.put("doctor_doom_hab1", (attacker, defender) -> {
            String key = "doctor_doom_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Conjuro de Muerte");
            consume(key);

            // Daño: 4000 fijo (mantenemos este daño directo)
            int damage = 4000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Conjuro de Muerte - ¡Doom desata un hechizo devastador! (" + damage + " daño)", true);
        });

        abilityHandlers.put("doctor_doom_hab2", (attacker, defender) -> {
            String key = "doctor_doom_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Escudo de Latveria");
            consume(key);

            // Daño: 2500 fijo
            int damage = 2500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Escudo de Latveria - ¡La tecnología de Doom destruye a su enemigo! (" + damage + " daño)", true);
        });

        // === IRON MAN ===
        abilityHandlers.put("iron_man_hab1", (attacker, defender) -> {
            String key = "iron_man_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Unibeam Máximo");
            consume(key);

            // Daño: 2000 fijo (mantenemos el daño directo)
            int damage = 2000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Unibeam Máximo - ¡La armadura canaliza toda su energía! (" + damage + " daño)", true);
        });

        abilityHandlers.put("iron_man_hab2", (attacker, defender) -> {
            String key = "iron_man_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Nanotecnología Adaptativa");
            consume(key);

            // Daño: 80% de su Poder
            int damage = (int)(attacker.getPoder() * 0.8);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Nanotecnología Adaptativa - ¡Armas adaptativas atacan al enemigo! (" + damage + " daño)", true);
        });

        // === WOLVERINE ===
        abilityHandlers.put("wolverine_hab1", (attacker, defender) -> {
            String key = "wolverine_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Furia Berserker");
            consume(key);

            // Daño: 180% de su Fuerza
            int damage = (int)(attacker.getFuerza() * 1.8);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Furia Berserker - ¡Logan pierde el control! (" + damage + " daño)", true);
        });

        abilityHandlers.put("wolverine_hab2", (attacker, defender) -> {
            String key = "wolverine_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque Adamantium");
            consume(key);

            // Daño: 3500 fijo
            int damage = 3500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ataque Adamantium - ¡Las garras de Adamantium desgarran al enemigo! (" + damage + " daño)", true);
        });

        // === SEBASTIAN SHAW ===
        abilityHandlers.put("sebastian_shaw_hab1", (attacker, defender) -> {
            String key = "sebastian_shaw_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Absorción Cinética Total");
            consume(key);

            // Daño: 4000 fijo
            int damage = 4000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Absorción Cinética Total - ¡Shaw libera la energía almacenada! (" + damage + " daño)", true);
        });

        abilityHandlers.put("sebastian_shaw_hab2", (attacker, defender) -> {
            String key = "sebastian_shaw_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Contraataque Cinético");
            consume(key);

            // Daño: 3000 fijo
            int damage = 3000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Contraataque Cinético - ¡Shaw devuelve la energía! (" + damage + " daño)", true);
        });

        // === SPIDER-MAN ===
        abilityHandlers.put("spider_man_hab1", (attacker, defender) -> {
            String key = "spider_man_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque de Máxima Agilidad");
            consume(key);

            // Daño: 150% de su Velocidad actual (mantenemos esta habilidad específica)
            int damage = (int)(attacker.getVelocidad() * 1.5);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ataque de Máxima Agilidad - ¡Spider-Man se mueve más rápido que el ojo! (" + damage + " daño)", true);
        });

        abilityHandlers.put("spider_man_hab2", (attacker, defender) -> {
            String key = "spider_man_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Telaraña Inmovilizante");
            consume(key);

            // Daño: 2000 fijo
            int damage = 2000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Telaraña Inmovilizante - ¡El enemigo queda atrapado en las redes de Spider-Man! (" + damage + " daño)", true);
        });

        // === BLACK PANTHER ===
        abilityHandlers.put("black_panther_hab1", (attacker, defender) -> {
            String key = "black_panther_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Liberación de Energía Cinética");
            consume(key);

            // Mantenemos la lógica de devolver el daño recibido ya que es una mecánica específica
            int damageToReturn = combatManager.getLastDamageTaken(attacker) * 2;
            
            // Si no ha recibido daño, hacer un daño mínimo base
            if (damageToReturn <= 0) {
                damageToReturn = 500; // Daño mínimo base
            }
            
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damageToReturn));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Liberación de Energía Cinética - ¡El traje devuelve el daño con intereses! (" + damageToReturn + " daño)", true);
        });

        abilityHandlers.put("black_panther_hab2", (attacker, defender) -> {
            String key = "black_panther_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Garras de Vibranium");
            consume(key);

            // Daño: 2500 fijo
            int damage = 2500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Garras de Vibranium - ¡Las garras de vibranium penetran cualquier defensa! (" + damage + " daño)", true);
        });

        // === CAPTAIN AMERICA ===
        abilityHandlers.put("captain_america_hab1", (attacker, defender) -> {
            String key = "captain_america_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Justicia Imparable");
            consume(key);

            // Daño: 90% de su Fuerza + 50% de su Velocidad
            int damage = (int)(attacker.getFuerza() * 0.9 + attacker.getVelocidad() * 0.5);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Justicia Imparable - ¡Capitán América golpea con todas sus fuerzas! (" + damage + " daño)", true);
        });

        abilityHandlers.put("captain_america_hab2", (attacker, defender) -> {
            String key = "captain_america_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Lanzamiento de Escudo");
            consume(key);

            // Daño: 2500 fijo
            int damage = 2500;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Lanzamiento de Escudo - ¡El escudo de vibranium golpea con fuerza! (" + damage + " daño)", true);
        });

        // === DEADPOOL ===
        abilityHandlers.put("deadpool_hab1", (attacker, defender) -> {
            String key = "deadpool_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque Múltiple");
            consume(key);

            // Daño: 3000 fijo
            int damage = 3000;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ataque Múltiple - ¡Deadpool ataca desde todos los ángulos! (" + damage + " daño)", true);
        });

        abilityHandlers.put("deadpool_hab2", (attacker, defender) -> {
            String key = "deadpool_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque Impredecible");
            consume(key);

            // Daño aleatorio entre 1000 y 5000
            int damage = 1000 + (int)(Math.random() * 4000);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ataque Impredecible - ¡Deadpool ataca de forma caótica! (" + damage + " daño)", true);
        });
    }

    /** 
     * Ejecuta una habilidad según su código
     * @param abilityCode Código de la habilidad a ejecutar
     * @param attacker Personaje que usa la habilidad
     * @param defender Personaje objetivo
     * @return Mensaje de combate con el resultado
     */
    public CombatMessage executeAbility(String abilityCode, PersonajeModel attacker, PersonajeModel defender) {
        BiFunction<PersonajeModel, PersonajeModel, CombatMessage> handler = abilityHandlers.get(abilityCode);
        
        if (handler != null) {
            return handler.apply(attacker, defender);
        }
        
        // Si no se encuentra la habilidad, devolver un mensaje de error
        System.err.println("No se encontró manejador para la habilidad: " + abilityCode);
        return CombatMessage.createFailedMessage(attacker.getNombre(), "Habilidad desconocida");
    }

    /** llamar al inicio de cada turno para reducir cooldowns */
    public void tickAllCooldowns() {
        cdRemaining.replaceAll((k,v) -> v>0 ? v-1 : 0);
    }

    public boolean canUse(String key) {
        Integer cd = cdRemaining.getOrDefault(key, 0);
        if (cd > 0) return false;
        Integer uses = usesRemaining.getOrDefault(key, 0);
        if (uses != 0 && uses < 1) return false;
        return true;
    }

    private void consume(String key) {
        // Decrementar usos si existe
        Integer currentUses = usesRemaining.get(key);
        if (currentUses != null) {
            usesRemaining.put(key, currentUses > 0 ? currentUses - 1 : 0);
        } else {
            // Si no existe, inicializarlo con 0
            usesRemaining.put(key, 0);
        }

        // Reset cooldown con comprobación de nulos
        Integer currentCd = cdRemaining.get(key);
        Integer remainingUses = usesRemaining.get(key);
        
        if (remainingUses != null && currentCd != null) {
            if (remainingUses == 0 && currentCd > 0) {
                cdRemaining.put(key, 999); // Si no quedan usos, poner CD muy alto
            }
        } else {
            // Inicializar valores si son nulos
            if (remainingUses == null) {
                usesRemaining.put(key, 0);
            }
            if (currentCd == null) {
                cdRemaining.put(key, 0);
            }
        }
    }
    
    // Mantener métodos específicos para compatibilidad, pero delegando al nuevo sistema
    public CombatMessage habUnoMagik(PersonajeModel attacker, PersonajeModel defender) {
        return executeAbility("magik_hab1", attacker, defender);
    }
    
    public CombatMessage habDosMagik(PersonajeModel attacker, PersonajeModel defender) {
        return executeAbility("magik_hab2", attacker, defender);
    }
    
    public CombatMessage habTresMagik(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("magik_hab3", atk, def);
    }
    public CombatMessage habCuatroMagik(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("magik_hab4", atk, def);
    }

    public CombatMessage habUnoThanos(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("thanos_hab1", atk, def);
    }
    public CombatMessage habDosThanos(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("thanos_hab2", atk, def);
    }

    public CombatMessage habUnoScarletWitch(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("scarlet_witch_hab1", atk, def);
    }
    public CombatMessage habDosScarletWitch(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("scarlet_witch_hab2", atk, def);
    }

    public CombatMessage habUnoLegion(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("legion_hab1", atk, def);
    }
    public CombatMessage habDosLegion(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("legion_hab2", atk, def);
    }

    public CombatMessage habUnoDoctorStrange(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("doctor_strange_hab1", atk, def);
    }
    public CombatMessage habDosDoctorStrange(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("doctor_strange_hab2", atk, def);
    }

    public CombatMessage habUnoSilverSurfer(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("silver_surfer_hab1", atk, def);
    }
    public CombatMessage habDosSilverSurfer(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("silver_surfer_hab2", atk, def);
    }

    public CombatMessage habUnoArishem(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("arishem_hab1", atk, def);
    }
    public CombatMessage habDosArishem(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("arishem_hab2", atk, def);
    }

    public CombatMessage habUnoKnull(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("knull_hab1", atk, def);
    }
    public CombatMessage habDosKnull(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("knull_hab2", atk, def);
    }

    public CombatMessage habUnoHulk(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("hulk_hab1", atk, def);
    }
    public CombatMessage habDosHulk(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("hulk_hab2", atk, def);
    }

    public CombatMessage habUnoDoctorDoom(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("doctor_doom_hab1", atk, def);
    }
    public CombatMessage habDosDoctorDoom(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("doctor_doom_hab2", atk, def);
    }

    public CombatMessage habUnoIronMan(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("iron_man_hab1", atk, def);
    }
    public CombatMessage habDosIronMan(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("iron_man_hab2", atk, def);
    }

    public CombatMessage habUnoWolverine(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("wolverine_hab1", atk, def);
    }
    public CombatMessage habDosWolverine(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("wolverine_hab2", atk, def);
    }

    public CombatMessage habUnoSebastianShaw(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("sebastian_shaw_hab1", atk, def);
    }
    public CombatMessage habDosSebastianShaw(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("sebastian_shaw_hab2", atk, def);
    }

    public CombatMessage habUnoSpiderMan(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("spider_man_hab1", atk, def);
    }
    public CombatMessage habDosSpiderMan(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("spider_man_hab2", atk, def);
    }

    public CombatMessage habUnoBlackPanther(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("black_panther_hab1", atk, def);
    }
    public CombatMessage habDosBlackPanther(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("black_panther_hab2", atk, def);
    }

    public CombatMessage habUnoCaptainAmerica(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("captain_america_hab1", atk, def);
    }
    public CombatMessage habDosCaptainAmerica(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("captain_america_hab2", atk, def);
    }

    public CombatMessage habUnoDeadpool(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("deadpool_hab1", atk, def);
    }
    public CombatMessage habDosDeadpool(PersonajeModel atk, PersonajeModel def) {
        return executeAbility("deadpool_hab2", atk, def);
    }
}