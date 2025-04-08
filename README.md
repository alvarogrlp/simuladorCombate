# 🦸‍♂️ Simulador de Combates Marvel  
*¡Elige, combate y domina!*

![Versión](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Licencia](https://img.shields.io/badge/licencia-MIT-yellow.svg)

---

## 🧩 Descripción del Propósito

**Simulador de Combates Marvel** es una aplicación interactiva construida con JavaFX que permite a los usuarios revivir combates entre héroes y villanos del universo Marvel. El usuario puede elegir sus personajes favoritos, configurar el entorno del combate y ver el resultado de la batalla a través de un sistema que evalúa las estadísticas y habilidades de cada combatiente.

Está orientada tanto a fans del universo Marvel como a estudiantes de desarrollo que buscan aprender sobre estructuras de datos, interfaces gráficas y diseño orientado a objetos a través de un proyecto entretenido. Su enfoque modular y visual la hace ideal para personas que recién comienzan a programar y quieren algo más dinámico que los típicos ejercicios de consola.

Lo que hace a esta app única es su interfaz limpia y adaptable, su soporte para múltiples idiomas y la posibilidad futura de expandir la base de datos con más personajes, poderes personalizados y estadísticas avanzadas que aporten realismo y rejugabilidad.

---

## 🎨 Diseño

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="docs/images/prototipo-inicial.png" width="300px" height="600px" alt="Wireframe inicial"/>
        <br/>
        <i>Wireframe inicial en Figma</i>
      </td>
      <td align="center">
        <img src="docs/images/inicio-oscuro.png" width="300px" height="600px" alt="Diseño final modo oscuro"/>
        <br/>
        <i>Diseño final en JavaFX - Modo Oscuro</i>
      </td>
      <td align="center">
        <img src="docs/images/inicio-claro.png" width="300px" height="600px" alt="Diseño final modo claro"/>
        <br/>
        <i>Diseño final en JavaFX - Modo Claro</i>
      </td>
    </tr>
  </table>
</div>

> 📌 *Comparando el wireframe con el diseño final se pueden apreciar los cambios visuales, como la incorporación de tipografía más moderna, estructura visual más clara, compatibilidad con temas claro/oscuro y refinamiento general de los elementos gráficos.*


> **Cambios clave**: se simplificó la navegación, se reorganizó el menú para mejorar la experiencia de usuario y se adaptó la interfaz a modo oscuro/claro dinámico, cosa que no se contempló en el prototipo inicial.

---

## 🧪 Tecnologías Usadas

| Tecnología    | Descripción            |
|---------------|------------------------|
| ☕ Java 17     | Lenguaje principal     |
| 🎨 JavaFX 21  | Interfaz gráfica       |
| 🗃️ SQLite     | Base de datos local    |
| ⚙️ Maven      | Gestión de dependencias |
| 🎨🧵 CSS       | Estilos personalizados |


---

## ⚙️ Instrucciones de Instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/simulador-combates-marvel.git
   cd simulador-combates-marvel
   
2. Instala las dependencias y compila:
   ```bash
   mvn clean install
   
3. Ejecuta la aplicación:
   ```bash
   mvn javafx:run

---

## 🛤️ Roadmap (Mejoras Futuras)

- Implementación de **múltiples escenarios de combate** (ciudades, espacios cerrados, otros planetas).
- Nuevos **modos de juego**:
  - 1vs1 clásico
  - 3vs3
  - 5vs5
  - **Simulaciones masivas** (ej: 3000 vs 3)
- Posible **modo campaña narrativa** con progresión de combates.
- Mejora del sistema de IA para combates más realistas.
- Animaciones básicas para representar ataques o habilidades.
- Estadísticas post-combate y tablas comparativas.
- Exportar resultados o guardar combates favoritos.
- Editor de combates personalizados.

---

## 📜 Licencia

Este proyecto está licenciado bajo la **Licencia MIT**.

Puedes usar, modificar y distribuir el código con libertad, siempre que mantengas el aviso de copyright
original y la licencia.

Consulta el archivo [LICENSE](LICENSE) para más información.
