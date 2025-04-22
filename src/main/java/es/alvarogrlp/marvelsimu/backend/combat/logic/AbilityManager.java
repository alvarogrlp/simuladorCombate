package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.HashMap;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

public class AbilityManager {

    // key = personaje_codigo + "_hab" + número (1=MásPoderosa,2=Característica)
    private Map<String,Integer> usesRemaining = new HashMap<>();
    private Map<String,Integer> cdRemaining   = new HashMap<>();
    private final CombatManager combatManager;

    public AbilityManager(CombatManager combatManager) {
        this.combatManager = combatManager;
        initAll();
    }

    private void initAll() {
        // Magik Base
        usesRemaining.put("magik_hab1",   1); cdRemaining.put("magik_hab1",   0);
        usesRemaining.put("magik_hab2",   1); cdRemaining.put("magik_hab2",   1);
        // Magik Darkchild
        usesRemaining.put("magik_dc_hab1",1); cdRemaining.put("magik_dc_hab1",0);
        usesRemaining.put("magik_dc_hab2",1); cdRemaining.put("magik_dc_hab2",0);
        // Thanos Sin Guantelete
        usesRemaining.put("thanos_hab1",  0); cdRemaining.put("thanos_hab1",  4);
        usesRemaining.put("thanos_hab2",  0); cdRemaining.put("thanos_hab2",  3);
        // Thanos Guantelete
        usesRemaining.put("thanos_g_hab1",1); cdRemaining.put("thanos_g_hab1",1);
        usesRemaining.put("thanos_g_hab2",5); cdRemaining.put("thanos_g_hab2",5);
        // Scarlet Witch
        usesRemaining.put("scarlet_hab1",0); cdRemaining.put("scarlet_hab1",3);
        usesRemaining.put("scarlet_hab2",0); cdRemaining.put("scarlet_hab2",3);
        // Legion
        usesRemaining.put("legion_hab1", 0); cdRemaining.put("legion_hab1", 6);
        usesRemaining.put("legion_hab2", 1); cdRemaining.put("legion_hab2", 1);
        // Doctor Strange
        usesRemaining.put("strange_hab1",1); cdRemaining.put("strange_hab1",0);
        usesRemaining.put("strange_hab2",5); cdRemaining.put("strange_hab2",5);
        // Silver Surfer
        usesRemaining.put("surfer_hab1", 0); cdRemaining.put("surfer_hab1", 4);
        usesRemaining.put("surfer_hab2", 0); cdRemaining.put("surfer_hab2", 5);
        // Arishem
        usesRemaining.put("arishem_hab1",0); cdRemaining.put("arishem_hab1",6);
        usesRemaining.put("arishem_hab2",0); cdRemaining.put("arishem_hab2",4);
        // Knull
        usesRemaining.put("knull_hab1",  0); cdRemaining.put("knull_hab1",  5);
        usesRemaining.put("knull_hab2",  0); cdRemaining.put("knull_hab2",  2);
        // Hulk
        usesRemaining.put("hulk_hab1",   0); cdRemaining.put("hulk_hab1",   5);
        usesRemaining.put("hulk_hab2",   0); cdRemaining.put("hulk_hab2",   0);
        // Doctor Doom
        usesRemaining.put("doom_hab1",   0); cdRemaining.put("doom_hab1",   2);
        usesRemaining.put("doom_hab2",   0); cdRemaining.put("doom_hab2",   4);
        // Iron Man
        usesRemaining.put("iron_hab1",   0); cdRemaining.put("iron_hab1",   3);
        usesRemaining.put("iron_hab2",   0); cdRemaining.put("iron_hab2",   4);
        // Wolverine
        usesRemaining.put("wolv_hab1",   0); cdRemaining.put("wolv_hab1",   4);
        usesRemaining.put("wolv_hab2",   0); cdRemaining.put("wolv_hab2",   5);
        // Sebastian Shaw
        usesRemaining.put("shaw_hab1",   0); cdRemaining.put("shaw_hab1",   5);
        usesRemaining.put("shaw_hab2",   0); cdRemaining.put("shaw_hab2",   3);
        // Spider-Man
        usesRemaining.put("spidey_hab1", 0); cdRemaining.put("spidey_hab1", 4);
        usesRemaining.put("spidey_hab2", 0); cdRemaining.put("spidey_hab2", 3);
        // Black Panther
        usesRemaining.put("panther_hab1",0); cdRemaining.put("panther_hab1",4);
        usesRemaining.put("panther_hab2",0); cdRemaining.put("panther_hab2",3);
        // Captain America
        usesRemaining.put("cap_hab1",    0); cdRemaining.put("cap_hab1",    4);
        usesRemaining.put("cap_hab2",    0); cdRemaining.put("cap_hab2",    4);
        // Deadpool
        usesRemaining.put("dead_hab1",   0); cdRemaining.put("dead_hab1",   3);
        usesRemaining.put("dead_hab2",   0); cdRemaining.put("dead_hab2",   2);
    }

    /** llamar al inicio de cada turno para reducir cooldowns */
    public void tickAllCooldowns(){
        cdRemaining.replaceAll((k,v)-> v>0? v-1:0);
    }

    public boolean canUse(String key){
        Integer cd = cdRemaining.getOrDefault(key,0);
        if(cd>0) return false;
        Integer uses = usesRemaining.getOrDefault(key,0);
        if(uses!=0 && uses<1) return false;
        return true;
    }

    private void consume(String key){
        // decrementar usos
        usesRemaining.computeIfPresent(key,(k,v)-> v>0? v-1:0);
        // reset cooldown
        cdRemaining.put(key, usesRemaining.get(key)==0 && cdRemaining.get(key)>0? 999: cdRemaining.get(key));
    }

    // === Magik (Forma Base) ===
    public CombatMessage habUnoMagik(PersonajeModel magik, PersonajeModel target) {
        String key = "magik_hab1";
        if (!canUse(key)) {
            return CombatMessage.createFailedMessage(magik.getNombre(), "Darkchild Rising");
        }
        consume(key);
        boolean ok = combatManager.applyTransformation(magik, "magik_darkchild");
        return CombatMessage.createAbilityMessage(
            magik.getNombre(), "Darkchild Rising",
            true, // isPlayerAction
            ok    // success
        );
    }

    public CombatMessage habDosMagik(PersonajeModel magik, PersonajeModel target) {
        // dejamos para el futuro
        return null;
    }

    // === Magik (Darkchild) ===
    public CombatMessage habUnoDarkChild(PersonajeModel magikDc, PersonajeModel target) {
        String clave = "magik_dc_hab1";
        if (!canUse(clave)) return null;
        consume(clave);
        // Trono del Caos (Habilidad Más Poderosa)
        target.setVida(target.getVida() - 4000);
        // TODO: almacenar un contador de 2 turnos para bloquear curas y buffs
        return CombatMessage.createAbilityMessage(magikDc.getNombre(), "Trono del Caos", true);
    }

    public CombatMessage habDosDarkChild(PersonajeModel magikDc) {
        String clave = "magik_dc_hab2";
        if (!canUse(clave)) return null;
        consume(clave);
        // Auge Infernal (Habilidad Característica)
        int healAmount = (int) (magikDc.getVida() * 0.7);
        magikDc.setVida(magikDc.getVida() + healAmount);
        magikDc.setFuerza((int) (magikDc.getFuerza() * 1.3));
        magikDc.setPoder((int) (magikDc.getPoder() * 1.3));
        // TODO: almacenar un contador de 1 turno para mantener el buff
        return CombatMessage.createAbilityMessage(magikDc.getNombre(), "Auge Infernal", true);
    }

    // === Thanos Sin Guantelete ===
    public CombatMessage habUnoThanos(PersonajeModel thanos, PersonajeModel target){
        String clave="thanos_hab1";
        if(!canUse(clave)) return null;
        consume(clave);
        // TODO: Aniquilación Cósmica – daño=150% fuerza+50% poder, -10% vida máx. enemiga
        return CombatMessage.createAbilityMessage(thanos.getNombre(), "Aniquilación Cósmica", false);
    }
    public CombatMessage habDosThanos(PersonajeModel thanos, PersonajeModel target){
        String clave = "thanos_hab2";
        if (!canUse(clave)) {
            // devolvemos mensaje de fallo, nunca null
            return CombatMessage.createFailedMessage(
                thanos.getNombre(),
                "Intimidación Titán"
            );
        }
        consume(clave);
        // TODO: aplicar la lógica de Intimidación Titán
        return CombatMessage.createAbilityMessage(
            thanos.getNombre(),
            "Intimidación Titán",
            false // es IA
        );
    }

    // === Thanos Guantelete ===
    public CombatMessage habUnoThanosG(PersonajeModel thanosG, PersonajeModel target){
        String clave="thanos_g_hab1";
        if(!canUse(clave)) return null;
        consume(clave);
        // TODO: Chasquido del Infinito – insta-kill enemigo, reven transform, dañar thanos 70% vida actual
        return CombatMessage.createAbilityMessage(thanosG.getNombre(), "Chasquido del Infinito", false);
    }
    public CombatMessage habDosThanosG(PersonajeModel thanosG){
        String clave="thanos_g_hab2";
        if(!canUse(clave)) return null;
        consume(clave);
        // TODO: Voluntad de Thanos – 3 turnos 50% menos daño y +25% daño en todos sus ataques
        return CombatMessage.createAbilityMessage(thanosG.getNombre(), "Voluntad de Thanos", false);
    }

    // === Scarlet Witch ===
    public CombatMessage habUnoScarlet(PersonajeModel sw, PersonajeModel target){ /*...*/ return null; }
    public CombatMessage habDosScarlet(PersonajeModel sw, PersonajeModel target){ /*...*/ return null; }

    // ... continúa de igual forma para Legion, Doctor Strange, Silver Surfer,
    //     Arishem, Knull, Hulk, Doctor Doom, Iron Man, Wolverine,
    //     Sebastian Shaw, Spider-Man, Black Panther, Captain America, Deadpool

}