package es.alvarogrlp.marvelsimu.backend.combat.logic;

import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;

/**
 * Interfaz que define un manejador de habilidad específica
 */
public interface AbilityHandler {
    /**
     * Ejecuta la habilidad
     * @param attacker Personaje que usa la habilidad
     * @param defender Personaje objetivo (puede ser null para habilidades sin objetivo)
     * @return Mensaje de combate con el resultado
     */
    CombatMessage execute(PersonajeModel attacker, PersonajeModel defender);
}