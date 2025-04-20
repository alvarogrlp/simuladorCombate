-- Agregar campo es_transformacion a la tabla personaje
ALTER TABLE personaje ADD COLUMN es_transformacion BOOLEAN DEFAULT 0;

-- Marcar DarkChild y Thanos con Guantelete como transformaciones
UPDATE personaje SET es_transformacion = 1 WHERE nombre_codigo = 'darkchild';
UPDATE personaje SET es_transformacion = 1 WHERE nombre_codigo = 'thanos_guantelete';

-- Otras transformaciones que puedan existir
-- UPDATE personaje SET es_transformacion = 1 WHERE nombre_codigo = 'hulk_world_breaker';
-- ...