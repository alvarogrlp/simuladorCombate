-- Actualizar la columna imagen_combate para cada personaje
UPDATE personajes SET imagen_combate = 'images/Ingame/hulk-ingame.png' WHERE nombre_codigo LIKE '%Hulk%';
UPDATE personajes SET imagen_combate = 'images/Ingame/spiderman-ingame.png' WHERE nombre_codigo LIKE '%Spider%';
UPDATE personajes SET imagen_combate = 'images/Ingame/ironman-ingame.png' WHERE nombre_codigo LIKE '%Iron%';
UPDATE personajes SET imagen_combate = 'images/Ingame/captainamerica-ingame.png' WHERE nombre_codigo LIKE '%Capit%';
UPDATE personajes SET imagen_combate = 'images/Ingame/doctorstrange-ingame.png' WHERE nombre_codigo LIKE '%Strange%';
UPDATE personajes SET imagen_combate = 'images/Ingame/magik-ingame.png' WHERE nombre_codigo LIKE '%Magik%';