package es.alvarogrlp.marvelsimu.backend.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigManager {

    public static class ConfigProperties {

        static String path;

        private static final Properties properties = new Properties();

        /**
         * Obtiene el valor de una propiedad por su clave
         * @param key String con la clave de la propiedad
         * @return String con el valor de la propiedad
         */
        public static String getProperty(String key) {
            return properties.getProperty(key);
        }

        /**
         * Obtiene el valor de una propiedad por su clave
         * @param key String con la clave de la propiedad
         * @param defaultValue String con el valor predeterminado si la clave no existe
         * @return String con el valor de la propiedad o el valor predeterminado
         */
        public static String getProperty(String key, String defaultValue) {
            return properties.getProperty(key, defaultValue);
        }

        /**
         * Establece una propiedad con una clave y un valor
         * @param key String con la clave de la propiedad
         * @param value String con el valor de la propiedad
         */
        public static void setProperty(String key, String value) {
            properties.setProperty(key, value);
        }

        /**
         * Establece la ruta del archivo de propiedades y carga sus valores
         * @param rutaPath String con la ruta del archivo de propiedades
         */
        public static void setPath(String rutaPath) {
            System.out.println("Dentro del setPath");
            File file = new File(rutaPath);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Path:" + file.getAbsolutePath());
            }
            path = rutaPath;
            try {
                System.out.println("Dentro del ConfigProperties");

                FileInputStream input = new FileInputStream(path);
                InputStreamReader isr = new InputStreamReader(input, "UTF-8");
                properties.load(isr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
