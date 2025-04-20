package es.alvarogrlp.marvelsimu.backend.combat.logic;

/**
 * Clase utilitaria para calcular el daño en combate
 */
public class DamageCalculator {
    
    /**
     * Calcula el daño que inflige un personaje atacante a un defensor
     * 
     * @param fuerzaAtacante Valor de fuerza del personaje que ataca
     * @param poderAtacante Valor de poder del personaje que ataca
     * @param poderDefensor Valor de poder del personaje que recibe el ataque
     * @return Cantidad de daño a aplicar
     */
    public static int calcularDano(int fuerzaAtacante, int poderAtacante, int poderDefensor) {
        // Validación básica
        if (fuerzaAtacante <= 0) return 1;
        
        // Factor de escala para evitar valores extremos
        double factorEscala = 100.0;
        
        // Base del daño determinada por la fuerza del atacante
        double danoBase = fuerzaAtacante * 0.8;
        
        // Modificador por poder del atacante (más poder = más daño)
        double modificadorPoderAtacante = 1.0 + (poderAtacante / factorEscala);
        
        // Reducción por poder del defensor (más poder = menos daño recibido)
        // Usamos función logarítmica para evitar que poder alto reduzca el daño a 0
        double reduccionDefensa = Math.log10(1 + (poderDefensor / factorEscala)) * 0.5;
        reduccionDefensa = Math.min(reduccionDefensa, 0.75); // Máximo 75% de reducción
        
        // Cálculo final con aleatoriedad suave (±10%)
        double factorAleatorio = 0.9 + (Math.random() * 0.2);
        
        // Daño final
        double danoFinal = danoBase * modificadorPoderAtacante * (1 - reduccionDefensa) * factorAleatorio;
        
        // Asegurar un daño mínimo de 1
        return Math.max(1, (int)Math.round(danoFinal));
    }
}