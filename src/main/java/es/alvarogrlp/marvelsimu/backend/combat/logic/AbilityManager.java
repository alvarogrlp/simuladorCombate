package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.HashMap;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class AbilityManager {

    // Mapa de disponibilidad de habilidades
    private Map<String,Integer> usesRemaining = new HashMap<>();
    private Map<String,Integer> cdRemaining = new HashMap<>();
    
    // Mapa de manejadores de habilidades
    private Map<String, AbilityHandler> abilityHandlers = new HashMap<>();
    
    private final CombatManager combatManager;

    public AbilityManager(CombatManager combatManager) {
        this.combatManager = combatManager;
        initAvailability();
        registerAbilityHandlers();
    }

    private void initAvailability() {
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
        // Habilidades existentes
        // Magik, Doctor Doom, Iron Man...
        
        // Añadir todas las habilidades faltantes
        
        // === Spider-Man ===
        abilityHandlers.put("spider_man_hab1", (attacker, defender) -> {
            String key = "spider_man_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Telaraña Inmovilizadora");
            }
            consume(key);
            
            // Habilidad: Telaraña inmovilizadora (restringe al oponente a ataques básicos por 2 turnos)
            combatManager.restrictToBasicAttacks(defender, 2);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Telaraña Inmovilizadora - ¡Enemigo atrapado por 2 turnos!", true);
        });
        
        abilityHandlers.put("spider_man_hab2", (attacker, defender) -> {
            String key = "spider_man_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Sentido Arácnido");
            }
            consume(key);
            
            // Habilidad: Sentido arácnido (esquiva el próximo ataque)
            combatManager.applyDamageReduction(attacker, 1.0, 1); // 100% reducción por 1 turno
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Sentido Arácnido - ¡Próximo ataque esquivado!", true);
        });
        
        // === Silver Surfer ===
        abilityHandlers.put("silver_surfer_hab1", (attacker, defender) -> {
            String key = "silver_surfer_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Explosión Cósmica");
            }
            consume(key);
            
            // Explosión Cósmica - Gran daño basado en poder
            int damage = Math.max(3000, attacker.getPoder() / 3);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Explosión Cósmica - ¡Devastación del poder cósmico!", true);
        });
        
        abilityHandlers.put("silver_surfer_hab2", (attacker, defender) -> {
            String key = "silver_surfer_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Viaje Espacial");
            }
            consume(key);
            
            // Saltar el siguiente turno del enemigo
            combatManager.skipEnemyTurns(1);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Viaje Espacial - ¡Demasiado rápido para ser visto!", true);
        });
        
        // === Doctor Strange ===
        abilityHandlers.put("doctor_strange_hab1", (attacker, defender) -> {
            String key = "doctor_strange_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Bandas de Cyttorak");
            }
            consume(key);
            
            // Bandas de Cyttorak - Restringir al enemigo y causar daño
            combatManager.restrictToBasicAttacks(defender, 2);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1500));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Bandas de Cyttorak - ¡El enemigo ha sido inmovilizado!", true);
        });
        
        abilityHandlers.put("doctor_strange_hab2", (attacker, defender) -> {
            String key = "doctor_strange_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Ojo de Agamotto");
            }
            consume(key);
            
            // Curación y protección
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + 2000));
            combatManager.applyDamageReduction(attacker, 0.5, 2); // 50% reducción por 2 turnos
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Ojo de Agamotto - Tiempo manipulado para curar heridas", true);
        });
        
        // === Thanos ===
        abilityHandlers.put("thanos_hab1", (attacker, defender) -> {
            String key = "thanos_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Chasquido");
            }
            consume(key);
            
            // Chasquido - 50% de la vida actual
            int currentHealth = defender.getVidaActual();
            int damage = currentHealth / 2;
            defender.setVidaActual(currentHealth - damage);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Chasquido - Perfectamente equilibrado, como todo debería ser", true);
        });
        
        abilityHandlers.put("thanos_hab2", (attacker, defender) -> {
            String key = "thanos_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Lluvia de meteoros");
            }
            consume(key);
            
            // Lluvia de meteoros - Daño masivo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 3000));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Lluvia de meteoros - ¡El cielo se desploma!", true);
        });
        
        // === Wolverine ===
        abilityHandlers.put("wolverine_hab1", (attacker, defender) -> {
            String key = "wolverine_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Furia Berserker");
            }
            consume(key);
            
            // Furia Berserker - Aumento de daño por 2 turnos
            combatManager.applyDamageBoost(attacker, 0.5, 2); // +50% daño por 2 turnos
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Furia Berserker - ¡La bestia interior desatada!", true);
        });
        
        abilityHandlers.put("wolverine_hab2", (attacker, defender) -> {
            String key = "wolverine_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Factor Curativo");
            }
            consume(key);
            
            // Factor Curativo - Regeneración masiva
            int healAmount = attacker.getVida() / 3;
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + healAmount));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Factor Curativo - ¡Heridas regeneradas!", true);
        });
        
        // === Captain America ===
        abilityHandlers.put("captain_america_hab1", (attacker, defender) -> {
            String key = "captain_america_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Escudo Indestructible");
            }
            consume(key);
            
            // Escudo Indestructible - Reduce daño y refleja parte
            combatManager.applyDamageReduction(attacker, 0.75, 2); // Muestra mensaje de reducción
            combatManager.applyDamageReflection(attacker, 0.3, 2); // Muestra mensaje de reflejo
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Escudo Indestructible - ¡Defensa impenetrable!", true);
        });
        
        abilityHandlers.put("captain_america_hab2", (attacker, defender) -> {
            String key = "captain_america_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Lanzamiento Perfecto");
            }
            consume(key);
            
            // Lanzamiento Perfecto - Daño certero
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 2000));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Lanzamiento Perfecto - ¡El escudo siempre vuelve!", true);
        });
        
        // === Hulk ===
        abilityHandlers.put("hulk_hab1", (attacker, defender) -> {
            String key = "hulk_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Aplastamiento");
            }
            consume(key);
            
            // Aplastamiento - Daño basado en fuerza
            int damage = Math.max(2500, attacker.getFuerza() * 2);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Aplastamiento - ¡HULK APLASTA!", true);
        });
        
        abilityHandlers.put("hulk_hab2", (attacker, defender) -> {
            String key = "hulk_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Ira Incontenible");
            }
            consume(key);
            
            // Ira Incontenible - Aumento masivo de stats
            attacker.setFuerza(attacker.getFuerza() + 500);
            attacker.setPoder(attacker.getPoder() + 300);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Ira Incontenible - ¡HULK SE ENFADA MÁS!", true);
        });
        
        // === Thor ===
        abilityHandlers.put("thor_hab1", (attacker, defender) -> {
            String key = "thor_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Mjolnir Golpe Divino");
            }
            consume(key);
            
            // Mjolnir Golpe Divino - Daño masivo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 3000));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Mjolnir Golpe Divino - ¡Por Asgard!", true);
        });
        
        abilityHandlers.put("thor_hab2", (attacker, defender) -> {
            String key = "thor_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Tormenta de Relámpagos");
            }
            consume(key);
            
            // Tormenta de Relámpagos - Varios impactos
            for (int i = 0; i < 3; i++) {
                defender.setVidaActual(Math.max(0, defender.getVidaActual() - 800));
            }
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Tormenta de Relámpagos - ¡El poder del trueno!", true);
        });
        
        // === Black Widow ===
        abilityHandlers.put("black_widow_hab1", (attacker, defender) -> {
            String key = "black_widow_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Mordida de la Viuda");
            }
            consume(key);
            
            // Mordida de la Viuda - Daño y debilitamiento
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1500));
            defender.setFuerza((int)(defender.getFuerza() * 0.8)); // 20% reducción de fuerza
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Mordida de la Viuda - ¡Golpe preciso al punto débil!", true);
        });
        
        abilityHandlers.put("black_widow_hab2", (attacker, defender) -> {
            String key = "black_widow_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Descarga Eléctrica");
            }
            consume(key);
            
            // Descarga Eléctrica - Aturdir al enemigo (salta el próximo turno)
            combatManager.skipEnemyTurns(1);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1000));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Descarga Eléctrica - ¡Enemigo aturdido!", true);
        });
        
        // === Captain Marvel ===
        abilityHandlers.put("captain_marvel_hab1", (attacker, defender) -> {
            String key = "captain_marvel_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Rayo Fotónico");
            }
            consume(key);
            
            // Rayo Fotónico - Daño masivo basado en poder
            int damage = Math.max(2500, attacker.getPoder() / 3 * 2);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Rayo Fotónico - ¡Energía cósmica pura!", true);
        });
        
        abilityHandlers.put("captain_marvel_hab2", (attacker, defender) -> {
            String key = "captain_marvel_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Absorción de Energía");
            }
            consume(key);
            
            // Absorción de Energía - Aumenta estadísticas
            attacker.setPoder(attacker.getPoder() + 500);
            attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + 1500));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Absorción de Energía - ¡Poder incrementado!", true);
        });
        
        // === Black Panther ===
        abilityHandlers.put("black_panther_hab1", (attacker, defender) -> {
            String key = "black_panther_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Absorción Kinética");
            }
            consume(key);
            
            // Absorción Kinética - Resistencia y contraataque
            combatManager.applyDamageReduction(attacker, 0.5, 2); // 50% reducción
            combatManager.applyDamageReflection(attacker, 0.5, 2); // 50% reflejo
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Absorción Kinética - El traje absorbe y refleja el daño", true);
        });
        
        abilityHandlers.put("black_panther_hab2", (attacker, defender) -> {
            String key = "black_panther_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Garras de Vibranium");
            }
            consume(key);
            
            // Garras de Vibranium - Daño que ignora defensas
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 2200));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Garras de Vibranium - ¡Cortan a través de cualquier defensa!", true);
        });
        
        // === Scarlet Witch ===
        abilityHandlers.put("scarlet_witch_hab1", (attacker, defender) -> {
            String key = "scarlet_witch_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Caos Hexagonal");
            }
            consume(key);
            
            // Caos Hexagonal - Invertir curaciones
            combatManager.applyHealingInversion(defender, 2);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1800));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Caos Hexagonal - ¡La realidad se deforma!", true);
        });
        
        abilityHandlers.put("scarlet_witch_hab2", (attacker, defender) -> {
            String key = "scarlet_witch_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Manipulación de la Realidad");
            }
            consume(key);
            
            // Manipulación de la Realidad - Efectos aleatorios
            int effect = (int)(Math.random() * 3);
            switch (effect) {
                case 0:
                    // Daño masivo
                    defender.setVidaActual(Math.max(0, defender.getVidaActual() - 3000));
                    return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                        "Manipulación de la Realidad - ¡Desintegración!", true);
                case 1:
                    // Curación
                    attacker.setVidaActual(Math.min(attacker.getVida(), attacker.getVidaActual() + 2500));
                    return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                        "Manipulación de la Realidad - ¡Regeneración!", true);
                case 2:
                    // Omitir turnos enemigo
                    combatManager.skipEnemyTurns(2);
                    return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                        "Manipulación de la Realidad - ¡Tiempo congelado!", true);
                default:
                    return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                        "Manipulación de la Realidad", true);
            }
        });
        
        // === Deadpool ===
        abilityHandlers.put("deadpool_hab1", (attacker, defender) -> {
            String key = "deadpool_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Factor Curativo");
            }
            consume(key);
            
            // Factor Curativo - Regeneración completa
            attacker.setVidaActual(attacker.getVida());
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Factor Curativo - ¡Completamente regenerado, nena!", true);
        });
        
        abilityHandlers.put("deadpool_hab2", (attacker, defender) -> {
            String key = "deadpool_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Romper la Cuarta Pared");
            }
            consume(key);
            
            // Romper la Cuarta Pared - Efecto único
            // Saltarse un turno del enemigo y restaurar usos de habilidades
            combatManager.skipEnemyTurns(1);
            usesRemaining.put("deadpool_hab1", 1);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Romper la Cuarta Pared - ¡Hey jugador! ¿Me extrañaste?", true);
        });
        
        // === Phoenix ===
        abilityHandlers.put("phoenix_hab1", (attacker, defender) -> {
            String key = "phoenix_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Explosión Psíquica");
            }
            consume(key);
            
            // Explosión Psíquica - Daño masivo
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - 3500));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Explosión Psíquica - ¡La mente del enemigo se quiebra!", true);
        });
        
        abilityHandlers.put("phoenix_hab2", (attacker, defender) -> {
            String key = "phoenix_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Fuerza Fénix");
            }
            consume(key);
            
            // Fuerza Fénix - Gran poder pero costo de vida
            attacker.setPoder(attacker.getPoder() * 2); // Duplicar poder
            attacker.setVidaActual(Math.max(1, attacker.getVidaActual() / 2)); // Costo de vida
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Fuerza Fénix - ¡Poder incomparable a cambio de vida!", true);
        });
        
        // === Ghost Rider ===
        abilityHandlers.put("ghost_rider_hab1", (attacker, defender) -> {
            String key = "ghost_rider_hab1";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Mirada de Penitencia");
            }
            consume(key);
            
            // Mirada de Penitencia - Daño basado en los "pecados" (daño previo)
            int damage = Math.min(5000, defender.getVida() - defender.getVidaActual() + 2000);
            defender.setVidaActual(Math.max(0, defender.getVidaActual() - damage));
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Mirada de Penitencia - ¡Siente el peso de tus pecados!", true);
        });
        
        abilityHandlers.put("ghost_rider_hab2", (attacker, defender) -> {
            String key = "ghost_rider_hab2";
            if (!canUse(key)) {
                return CombatMessage.createFailedMessage(attacker.getNombre(), "Cadenas Infernales");
            }
            consume(key);
            
            // Cadenas Infernales - Restricción y daño continuo
            combatManager.restrictToBasicAttacks(defender, 2);
            
            // Programar daño para los próximos turnos
            combatManager.scheduleEffect(() -> {
                defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1000));
            }, 1);
            
            combatManager.scheduleEffect(() -> {
                defender.setVidaActual(Math.max(0, defender.getVidaActual() - 1000));
            }, 2);
            
            return CombatMessage.createAbilityMessage(attacker.getNombre(), 
                "Cadenas Infernales - ¡Arderás por tus pecados!", true);
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
        AbilityHandler handler = abilityHandlers.get(abilityCode);
        
        if (handler != null) {
            return handler.execute(attacker, defender);
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
    
    // ... Continuar con los demás métodos de compatibilidad
}