PRAGMA foreign_keys = OFF;
BEGIN TRANSACTION;

-- Eliminar tablas si ya existen
DROP VIEW   IF EXISTS partida_resumen;
DROP TABLE  IF EXISTS partida_eliminacion;
DROP TABLE  IF EXISTS partida_personaje;
DROP TABLE  IF EXISTS partida;
DROP TABLE  IF EXISTS escenario_personaje;
DROP TABLE  IF EXISTS escenario_modificador;
DROP TABLE  IF EXISTS escenario;
DROP TABLE  IF EXISTS pasiva;
DROP TABLE  IF EXISTS ataque;
DROP TABLE  IF EXISTS tipo_ataque;
DROP TABLE  IF EXISTS personaje;
DROP TABLE  IF EXISTS usuario;

-- 1) Usuarios
CREATE TABLE usuario (
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre         TEXT    NOT NULL,
  email          TEXT    NOT NULL UNIQUE,
  password_hash  TEXT    NOT NULL
);

-- 2) Personajes (incluye formas/transformaciones)
CREATE TABLE personaje (
  id                INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre            TEXT    NOT NULL,
  nombre_codigo     TEXT    NOT NULL UNIQUE,
  descripcion       TEXT,
  imagen_miniatura  TEXT,
  imagen_combate    TEXT,
  personaje_base_id INTEGER REFERENCES personaje(id) ON DELETE SET NULL,
  duracion_turnos   INTEGER NOT NULL DEFAULT 0,   -- 0 = permanente
  vida              INTEGER NOT NULL,
  fuerza            INTEGER NOT NULL,
  velocidad         INTEGER NOT NULL,
  poder             INTEGER NOT NULL
);
CREATE INDEX idx_personaje_base ON personaje(personaje_base_id);

-- 3) Tipos de ataque
CREATE TABLE tipo_ataque (
  id    INTEGER PRIMARY KEY AUTOINCREMENT,
  clave TEXT    NOT NULL UNIQUE   -- 'ACC','AAD','habilidad_mas_poderosa','habilidad_caracteristica'
);

-- 4) Ataques y habilidades
CREATE TABLE ataque (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  personaje_id     INTEGER NOT NULL REFERENCES personaje(id) ON DELETE CASCADE,
  tipo_ataque_id   INTEGER NOT NULL REFERENCES tipo_ataque(id),
  nombre           TEXT    NOT NULL,
  dano_base        INTEGER NOT NULL,
  usos_maximos     INTEGER NOT NULL DEFAULT 0,   -- 0 = ilimitado
  cooldown_turnos  INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX idx_ataque_personaje ON ataque(personaje_id);

-- 5) Pasivas (habilidades automáticas)
CREATE TABLE pasiva (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  personaje_id     INTEGER NOT NULL REFERENCES personaje(id) ON DELETE CASCADE,
  nombre           TEXT    NOT NULL,
  descripcion      TEXT    NOT NULL,
  trigger_tipo     TEXT    NOT NULL,  -- e.g. 'on_damage_taken','before_enemy_action', etc.
  efecto_tipo      TEXT    NOT NULL,  -- e.g. 'reduce_damage_pct','force_random_attack', etc.
  efecto_valor     INTEGER NOT NULL,  -- valor numérico (% o puntos)
  usos_maximos     INTEGER NOT NULL DEFAULT 0,
  cooldown_turnos  INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX idx_pasiva_personaje ON pasiva(personaje_id);

-- 6) Escenarios
CREATE TABLE escenario (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre        TEXT    NOT NULL UNIQUE,
  descripcion   TEXT
);

-- 7) Modificadores de escenario
CREATE TABLE escenario_modificador (
  id               INTEGER PRIMARY KEY AUTOINCREMENT,
  escenario_id     INTEGER NOT NULL REFERENCES escenario(id) ON DELETE CASCADE,
  atributo         TEXT    NOT NULL,         -- 'poder','vida','disable_heal',...
  modificador_tipo TEXT    NOT NULL,         -- 'add_pct','multiplier','flag'
  valor            INTEGER NOT NULL,
  duracion_turnos  INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX idx_escmod_escenario ON escenario_modificador(escenario_id);

-- 8) Personajes disponibles por escenario
CREATE TABLE escenario_personaje (
  escenario_id  INTEGER NOT NULL REFERENCES escenario(id) ON DELETE CASCADE,
  personaje_id  INTEGER NOT NULL REFERENCES personaje(id) ON DELETE CASCADE,
  PRIMARY KEY (escenario_id, personaje_id)
);

-- 9) Historial de partidas
CREATE TABLE partida (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id      INTEGER NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  escenario_id    INTEGER NOT NULL REFERENCES escenario(id),
  fecha_creacion  DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  ganador_equipo  INTEGER NOT NULL CHECK(ganador_equipo IN (1,2))
);

-- 10) Personajes en cada partida
CREATE TABLE partida_personaje (
  partida_id      INTEGER NOT NULL REFERENCES partida(id) ON DELETE CASCADE,
  equipo          INTEGER NOT NULL CHECK(equipo IN (1,2)),
  personaje_id    INTEGER NOT NULL REFERENCES personaje(id),
  poder_inicial   INTEGER NOT NULL,
  PRIMARY KEY(partida_id, personaje_id)
);
CREATE INDEX idx_pp_partida ON partida_personaje(partida_id);

-- 11) Registro de eliminaciones
CREATE TABLE partida_eliminacion (
  partida_id           INTEGER NOT NULL REFERENCES partida(id) ON DELETE CASCADE,
  turno                INTEGER NOT NULL,
  personaje_atacante   INTEGER NOT NULL REFERENCES personaje(id),
  personaje_eliminado  INTEGER NOT NULL REFERENCES personaje(id),
  PRIMARY KEY(partida_id, turno, personaje_eliminado)
);

-- 12) Vista resumen de partida
CREATE VIEW partida_resumen AS
SELECT
  p.id             AS partida,
  p.fecha_creacion,
  pp.equipo,
  SUM(pp.poder_inicial) AS poder_total,
  CASE WHEN p.ganador_equipo = pp.equipo THEN 1 ELSE 0 END AS gano
FROM
  partida p
  JOIN partida_personaje pp ON p.id = pp.partida_id
GROUP BY
  p.id, pp.equipo;

COMMIT;
PRAGMA foreign_keys = ON;
