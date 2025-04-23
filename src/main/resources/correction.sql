-- Habilitar soporte para claves foráneas
PRAGMA foreign_keys = OFF;

-- Iniciar transacción
BEGIN TRANSACTION;

-- 1. Crear una tabla temporal para guardar los datos actuales
CREATE TABLE ataque_temp (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  personaje_id     INTEGER NOT NULL REFERENCES personaje(id) ON DELETE CASCADE,
  tipo_ataque_id   INTEGER NOT NULL REFERENCES tipo_ataque(id),
  codigo           TEXT    NOT NULL,  -- Nuevo campo
  nombre           TEXT    NOT NULL,
  dano_base        INTEGER NOT NULL,
  usos_maximos     INTEGER NOT NULL DEFAULT 0,
  cooldown_turnos  INTEGER NOT NULL DEFAULT 0
);

-- 2. Copiar datos a la tabla temporal y generar códigos automáticamente
INSERT INTO ataque_temp (id, personaje_id, tipo_ataque_id, codigo, nombre, dano_base, usos_maximos, cooldown_turnos)
SELECT 
    a.id, 
    a.personaje_id, 
    a.tipo_ataque_id,
    -- Generar codigo combinando nombre_codigo del personaje con tipo de ataque
    (SELECT p.nombre_codigo FROM personaje p WHERE p.id = a.personaje_id) || 
    CASE 
        WHEN t.clave = 'ACC' THEN '_melee'
        WHEN t.clave = 'AAD' THEN '_range'
        WHEN t.clave = 'habilidad_mas_poderosa' THEN '_hab1'
        WHEN t.clave = 'habilidad_caracteristica' THEN '_hab2'
        ELSE '_unknown'
    END,
    a.nombre,
    a.dano_base,
    a.usos_maximos,
    a.cooldown_turnos
FROM ataque a
JOIN tipo_ataque t ON a.tipo_ataque_id = t.id;

-- 3. Eliminar la tabla original
DROP TABLE ataque;

-- 4. Renombrar la tabla temporal a ataque
ALTER TABLE ataque_temp RENAME TO ataque;

-- 5. Crear los índices
CREATE INDEX idx_ataque_personaje ON ataque(personaje_id);
CREATE INDEX idx_ataque_codigo ON ataque(codigo);

-- Confirmar los cambios
COMMIT;

-- Reactivar las claves foráneas
PRAGMA foreign_keys = ON;