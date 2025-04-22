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
        if (poderDefensor <= 0) poderDefensor = 1;
        
        // 1) Calcular ratio de poderes
        double ratio = (double) poderAtacante / poderDefensor;
        
        // 2) Suavizar con raíz cuadrada para reducir volatilidad
        double multiplicador = Math.sqrt(ratio);
        
        // 3) Limitar el multiplicador a un rango razonable (0.1 a 5.0)
        multiplicador = Math.max(0.1, Math.min(multiplicador, 5.0));
        
        // 4) Calcular daño final basado en la fuerza y el multiplicador
        int danoFinal = (int) Math.max(1, Math.round(fuerzaAtacante * multiplicador));
        
        // 5) Añadir ligera aleatoriedad para que no sea siempre el mismo
        double factorAleatorio = 0.9 + (Math.random() * 0.2); // Entre 0.9 y 1.1
        danoFinal = (int) Math.max(1, Math.round(danoFinal * factorAleatorio));
        
        return danoFinal;
    }
}