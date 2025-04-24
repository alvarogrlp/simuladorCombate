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
    
    // Mapa para transformaciones (sin usar StatModifier que causa el error)
    private Map<PersonajeModel, PersonajeModel> originalFormMap = new HashMap<>();
    private Map<PersonajeModel, Integer> originalHealthMap = new HashMap<>();
    
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

            // Transformarse en Darkchild durante 2 turnos:
            // Crear directamente un nuevo personaje con las estadísticas de Darkchild
            PersonajeModel darkchild = new PersonajeModel();
            darkchild.setNombre("Magik (Darkchild)");
            darkchild.setVida(4500);
            darkchild.setFuerza(3500);
            darkchild.setVelocidad(3000);
            darkchild.setPoder(18000);
            
            // Usar nuestra implementación applyTransformation
            applyTransformation(attacker, darkchild, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Darkchild Rising - ¡Magik se transforma en la reina del Limbo!", true);
        });

        // === THANOS (Sin Guantelete) ===
        // Ya implementadas correctamente

        // === THANOS (Guantelete) ===
        abilityHandlers.put("thanos_gauntlet_hab1", (attacker, defender) -> {
            String key = "thanos_gauntlet_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Chasquido del Infinito");
            consume(key);

            // Elimina instantáneamente al enemigo
            defender.setVidaActual(0);
            
            // Vuelve a su forma sin guantelete, creando un nuevo personaje con los stats de Thanos normal
            PersonajeModel thanosNormal = new PersonajeModel();
            thanosNormal.setNombre("Thanos");
            thanosNormal.setVida(8000);
            thanosNormal.setFuerza(5000);
            thanosNormal.setVelocidad(1500);
            thanosNormal.setPoder(5500);
            
            // Usar nuestra implementación applyTransformation
            applyTransformation(attacker, thanosNormal, 999); // Permanentemente
            
            // Daña a Thanos con el 70% de su vida actual
            int damageToSelf = (int)(attacker.getVidaActual() * 0.7);
            attacker.setVidaActual(attacker.getVidaActual() - damageToSelf);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Chasquido del Infinito - ¡El universo se reordena a voluntad de Thanos!", true);
        });

        abilityHandlers.put("thanos_gauntlet_hab2", (attacker, defender) -> {
            String key = "thanos_gauntlet_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Voluntad de Thanos");
            consume(key);

            // Durante 3 turnos: 50% menos daño recibido y 25% más daño causado
            combatManager.applyDamageReduction(attacker, 0.5, 3);
            combatManager.applyDamageBoost(attacker, 0.25, 3);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Voluntad de Thanos - ¡El Guantelete potencia al Titán!", true);
        });

        // === SCARLET WITCH ===
        abilityHandlers.put("scarlet_witch_hab1", (attacker, defender) -> {
            String key = "scarlet_witch_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Caos Absoluto");
            consume(key);

            // 5000 daño directo y reduce 50% del Poder enemigo durante 2 turnos
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 5000));
            combatManager.applyStatModifier(defender, Stat.PODER, -0.5, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Caos Absoluto - ¡La realidad se distorsiona!", true);
        });

        abilityHandlers.put("scarlet_witch_hab2", (attacker, defender) -> {
            String key = "scarlet_witch_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Realidad Distorsionada");
            consume(key);

            // Invierte todas las curaciones del enemigo en daño durante 2 turnos
            combatManager.applyHealingInversion(defender, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Realidad Distorsionada - ¡La curación se convierte en dolor!", true);
        });

        // === LEGION ===
        abilityHandlers.put("legion_hab1", (attacker, defender) -> {
            String key = "legion_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Personalidades Desatadas");
            consume(key);

            // Duplica el Poder durante 3 turnos y 4000 daño inmediato
            combatManager.applyStatModifier(attacker, Stat.PODER, 1.0, 3); // +100% = duplicar
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 4000));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Personalidades Desatadas - ¡Las múltiples identidades de Legion lo potencian!", true);
        });

        abilityHandlers.put("legion_hab2", (attacker, defender) -> {
            String key = "legion_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Plano Mental");
            consume(key);

            // Legion: +50% Poder, +30% Velocidad, +20% Fuerza
            combatManager.applyStatModifier(attacker, Stat.PODER, 0.5, 2);
            combatManager.applyStatModifier(attacker, Stat.VELOCIDAD, 0.3, 2);
            combatManager.applyStatModifier(attacker, Stat.FUERZA, 0.2, 2);
            
            // Enemigo: -25% Poder y solo ataques básicos
            combatManager.applyStatModifier(defender, Stat.PODER, -0.25, 2);
            combatManager.restrictToBasicAttacks(defender, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Plano Mental - ¡Legion arrastra a todos a su mente!", true);
        });

        // === DOCTOR STRANGE ===
        abilityHandlers.put("doctor_strange_hab1", (attacker, defender) -> {
            String key = "doctor_strange_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Dimensión Espejo");
            consume(key);

            // Refleja el 30% del daño recibido durante 2 turnos
            combatManager.applyDamageReflection(attacker, 0.3, 2);
            
            // +50% daño en su próxima habilidad ofensiva
            combatManager.applyDamageBoost(attacker, 0.5, 1);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Dimensión Espejo - ¡Strange despliega una barrera reflectante!", true);
        });

        abilityHandlers.put("doctor_strange_hab2", (attacker, defender) -> {
            String key = "doctor_strange_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ojo de Agamotto");
            consume(key);

            // Omite 2 turnos del enemigo (Strange ataca 2 veces seguidas)
            combatManager.skipEnemyTurns(2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ojo de Agamotto - ¡El tiempo se detiene para todos excepto Strange!", true);
        });

        // === SILVER SURFER ===
        abilityHandlers.put("silver_surfer_hab1", (attacker, defender) -> {
            String key = "silver_surfer_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Nova Cósmica");
            consume(key);

            // 4500 daño y reduce 10% de la vida máxima del enemigo permanentemente
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 4500));
            defender.setVida(Math.max(1, defender.getVida() - (defender.getVida() / 10)));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Nova Cósmica - ¡El poder cósmico debilita la esencia del enemigo!", true);
        });

        abilityHandlers.put("silver_surfer_hab2", (attacker, defender) -> {
            String key = "silver_surfer_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Velocidad Estelar");
            consume(key);

            // +50% Velocidad durante 3 turnos
            combatManager.applyStatModifier(attacker, Stat.VELOCIDAD, 0.5, 3);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Velocidad Estelar - ¡Surfer se mueve a velocidades inconcebibles!", true);
        });

        // === ARISHEM ===
        abilityHandlers.put("arishem_hab1", (attacker, defender) -> {
            String key = "arishem_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Juicio Celestial");
            consume(key);

            // 100% de la vida actual del enemigo en daño, 50% probabilidad
            if (Math.random() < 0.5) {
                int damage = defender.getVidaActual();
                defender.setVidaActual(0);
                return CombatMessage.createAbilityMessage(attacker.getNombre(),
                    "Juicio Celestial - ¡El Celestial emite su veredicto final! (" + damage + " daño)", true);
            } else {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Juicio Celestial - El enemigo escapa al juicio");
            }
        });

        abilityHandlers.put("arishem_hab2", (attacker, defender) -> {
            String key = "arishem_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Presencia Imponente");
            consume(key);

            // Reduce 100% del daño recibido durante 1 turno
            combatManager.applyDamageReduction(attacker, 1.0, 1);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Presencia Imponente - ¡Arishem se vuelve completamente invulnerable!", true);
        });

        // === KNULL ===
        abilityHandlers.put("knull_hab1", (attacker, defender) -> {
            String key = "knull_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Oscuridad Primordial");
            consume(key);

            // 6000 daño y reduce 25% del Poder del enemigo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 6000));
            combatManager.applyStatModifier(defender, Stat.PODER, -0.25, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Oscuridad Primordial - ¡El vacío cósmico consume la energía vital!", true);
        });

        abilityHandlers.put("knull_hab2", (attacker, defender) -> {
            String key = "knull_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Rey de Simbiontes");
            consume(key);

            // Cura 30% de vida máxima
            int healAmount = (int)(attacker.getVida() * 0.3);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Rey de Simbiontes - ¡Knull absorbe la fuerza de sus creaciones!", true);
        });

        // === HULK (Máximo) ===
        abilityHandlers.put("hulk_hab1", (attacker, defender) -> {
            String key = "hulk_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Furia Gamma");
            consume(key);

            // +100% Fuerza durante 2 turnos
            combatManager.applyStatModifier(attacker, Stat.FUERZA, 1.0, 2);
            
            // Daño: 200% de su Fuerza actual
            int damage = attacker.getFuerza() * 2;
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Furia Gamma - ¡HULK APLASTA! (" + damage + " daño)", true);
        });

        abilityHandlers.put("hulk_hab2", (attacker, defender) -> {
            String key = "hulk_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Cuanto más enfadado...");
            
            // Esta habilidad se puede usar infinitas veces, no decrementar usos
            // Pero sí registrar uso (para evitar spam en el mismo turno)
            cdRemaining.put(key, 1);
            
            // Cura 10% de vida perdida
            int vidaPerdida = attacker.getVida() - attacker.getVidaActual();
            int healAmount = (int)(vidaPerdida * 0.1);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));
            
            // +10% Fuerza permanente (modificación directa del atributo)
            int fuerzaActual = attacker.getFuerza();
            attacker.setFuerza((int)(fuerzaActual * 1.1));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Cuanto más enfadado... - ¡HULK MÁS FUERTE!", true);
        });

        // === DOCTOR DOOM ===
        abilityHandlers.put("doctor_doom_hab1", (attacker, defender) -> {
            String key = "doctor_doom_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Conjuro de Muerte");
            consume(key);

            // 4000 daño
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 4000));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Conjuro de Muerte - ¡Doom desata un hechizo devastador!", true);
        });

        abilityHandlers.put("doctor_doom_hab2", (attacker, defender) -> {
            String key = "doctor_doom_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Escudo de Latveria");
            consume(key);

            // Reduce 70% del daño recibido durante 2 turnos
            combatManager.applyDamageReduction(attacker, 0.7, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Escudo de Latveria - ¡La tecnología y la magia de Doom lo protegen!", true);
        });

        // === IRON MAN ===
        abilityHandlers.put("iron_man_hab1", (attacker, defender) -> {
            String key = "iron_man_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Unibeam Máximo");
            consume(key);

            // 2000 daño y reduce 25% del Poder enemigo durante 2 turnos
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 2000));
            combatManager.applyStatModifier(defender, Stat.PODER, -0.25, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Unibeam Máximo - ¡La armadura canaliza toda su energía!", true);
        });

        abilityHandlers.put("iron_man_hab2", (attacker, defender) -> {
            String key = "iron_man_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Nanotecnología Adaptativa");
            consume(key);

            // Cura 25% de vida máxima
            int healAmount = (int)(attacker.getVida() * 0.25);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Nanotecnología Adaptativa - ¡La armadura se autorrepara!", true);
        });

        // === WOLVERINE ===
        abilityHandlers.put("wolverine_hab1", (attacker, defender) -> {
            String key = "wolverine_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Furia Berserker");
            consume(key);

            // +100% Fuerza y Velocidad durante 2 turnos
            combatManager.applyStatModifier(attacker, Stat.FUERZA, 1.0, 2);
            combatManager.applyStatModifier(attacker, Stat.VELOCIDAD, 1.0, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Furia Berserker - ¡Logan pierde el control!", true);
        });

        abilityHandlers.put("wolverine_hab2", (attacker, defender) -> {
            String key = "wolverine_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Regeneración Extrema");
            consume(key);

            // Cura 40% de vida máxima perdida
            int vidaPerdida = attacker.getVida() - attacker.getVidaActual();
            int healAmount = (int)(vidaPerdida * 0.4);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Regeneración Extrema - ¡El factor curativo de Wolverine en acción!", true);
        });

        // === SEBASTIAN SHAW ===
        abilityHandlers.put("sebastian_shaw_hab1", (attacker, defender) -> {
            String key = "sebastian_shaw_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Absorción Cinética Total");
            consume(key);

            // Inmune a daño durante 2 turnos, luego devuelve el total del daño recibido
            combatManager.applyDamageReduction(attacker, 1.0, 2); // 100% reducción = inmune
            
            // El efecto de devolver daño se implementa en el método processAttackResult 
            // de CombatManager, no lo implementamos directamente aquí

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Absorción Cinética Total - ¡Shaw se prepara para absorber todo el daño!", true);
        });

        abilityHandlers.put("sebastian_shaw_hab2", (attacker, defender) -> {
            String key = "sebastian_shaw_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Contraataque Cinético");
            consume(key);

            // Devuelve 20% del daño recibido el próximo turno
            combatManager.applyDamageReflection(attacker, 0.2, 1);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Contraataque Cinético - ¡Shaw prepara su cuerpo para devolver energía!", true);
        });

        // === SPIDER-MAN ===
        abilityHandlers.put("spider_man_hab1", (attacker, defender) -> {
            String key = "spider_man_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque de Máxima Agilidad");
            consume(key);

            // Daño: 150% de su Velocidad actual
            int damage = (int)(attacker.getVelocidad() * 1.5);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Ataque de Máxima Agilidad - ¡Spider-Man se mueve más rápido que el ojo! (" + damage + " daño)", true);
        });

        abilityHandlers.put("spider_man_hab2", (attacker, defender) -> {
            String key = "spider_man_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Telaraña Inmovilizante");
            consume(key);

            // Reduce 50% de Velocidad del enemigo durante 2 turnos
            combatManager.applyStatModifier(defender, Stat.VELOCIDAD, -0.5, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Telaraña Inmovilizante - ¡El enemigo queda atrapado en las redes de Spider-Man!", true);
        });

        // === BLACK PANTHER ===
        abilityHandlers.put("black_panther_hab1", (attacker, defender) -> {
            String key = "black_panther_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Liberación de Energía Cinética");
            consume(key);

            // Devuelve 200% del daño recibido en el turno anterior
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

            // 500 daño directo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 500));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Garras de Vibranium - ¡Las garras de vibranium penetran cualquier defensa!", true);
        });

        // === CAPTAIN AMERICA ===
        abilityHandlers.put("captain_america_hab1", (attacker, defender) -> {
            String key = "captain_america_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Justicia Imparable");
            consume(key);

            // 400 daño directo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 400));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Justicia Imparable - ¡Capitán América golpea con todas sus fuerzas!", true);
        });

        abilityHandlers.put("captain_america_hab2", (attacker, defender) -> {
            String key = "captain_america_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Escudo del Capitán");
            consume(key);

            // Reduce 50% del daño recibido durante 2 turnos
            combatManager.applyDamageReduction(attacker, 0.5, 2);

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Escudo del Capitán - ¡El escudo de vibranium protege a Steve!", true);
        });

        // === DEADPOOL ===
        abilityHandlers.put("deadpool_hab1", (attacker, defender) -> {
            String key = "deadpool_hab1";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Factor Curativo Extremo");
            consume(key);

            // Cura 70% de vida perdida
            int vidaPerdida = attacker.getVida() - attacker.getVidaActual();
            int healAmount = (int)(vidaPerdida * 0.7);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));

            return CombatMessage.createAbilityMessage(attacker.getNombre(),
                "Factor Curativo Extremo - ¡Deadpool se regenera mientras rompe la cuarta pared!", true);
        });

        abilityHandlers.put("deadpool_hab2", (attacker, defender) -> {
            String key = "deadpool_hab2";
            if (!canUse(key)) return CombatMessage.createFailedMessage(attacker.getNombre(), "Ataque Impredecible");
            consume(key);

            // 200 daño directo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 200));
            
            // 50% de reducir Velocidad enemiga un 50% durante 2 turnos
            if (Math.random() < 0.5) {
                combatManager.applyStatModifier(defender, Stat.VELOCIDAD, -0.5, 2);
                return CombatMessage.createAbilityMessage(attacker.getNombre(),
                    "Ataque Impredecible - ¡Deadpool confunde a su enemigo y reduce su velocidad!", true);
            } else {
                return CombatMessage.createAbilityMessage(attacker.getNombre(),
                    "Ataque Impredecible - ¡Deadpool ataca de forma caótica!", true);
            }
        });
    }

    /** 
     * Guarda forma original, aplica transformación y programa la vuelta.
     * Implementación corregida sin usar copyFrom
     */
    private void applyTransformation(PersonajeModel original, PersonajeModel transformed, int durationTurns) {
        // Crear una copia manual del personaje original
        PersonajeModel originalCopy = new PersonajeModel();
        originalCopy.setId(original.getId());
        originalCopy.setNombre(original.getNombre());
        originalCopy.setVida(original.getVida());
        originalCopy.setFuerza(original.getFuerza());
        originalCopy.setVelocidad(original.getVelocidad());
        originalCopy.setPoder(original.getPoder());
        originalCopy.setAtaques(original.getAtaques());
        
        // Guardar referencia a la forma original
        originalFormMap.put(original, originalCopy);
        originalHealthMap.put(original, original.getVidaActual());
        
        // Aplicar los atributos de la transformación
        original.setVida(transformed.getVida());
        original.setFuerza(transformed.getFuerza());
        original.setVelocidad(transformed.getVelocidad());
        original.setPoder(transformed.getPoder());
        original.setNombre(transformed.getNombre());
        
        // Programar la reversión después de ciertos turnos
        combatManager.scheduleEffect(() -> revertTransformation(original), durationTurns);
    }

    /**
     * Restaura forma y vida original.
     * Implementación corregida sin usar copyFrom
     */
    public void revertTransformation(PersonajeModel transformed) {
        PersonajeModel original = originalFormMap.remove(transformed);
        Integer originalHealth = originalHealthMap.remove(transformed);
        
        if (original != null && originalHealth != null) {
            // Restaurar manualmente los atributos originales
            transformed.setVida(original.getVida());
            transformed.setFuerza(original.getFuerza());
            transformed.setVelocidad(original.getVelocidad());
            transformed.setPoder(original.getPoder());
            transformed.setNombre(original.getNombre());
            transformed.setVidaActual(originalHealth);
        }
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
        // decrementar usos
        usesRemaining.computeIfPresent(key, (k,v) -> v>0 ? v-1 : 0);
        // reset cooldown
        cdRemaining.put(key, usesRemaining.get(key)==0 && cdRemaining.get(key)>0 ? 999 : cdRemaining.get(key));
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