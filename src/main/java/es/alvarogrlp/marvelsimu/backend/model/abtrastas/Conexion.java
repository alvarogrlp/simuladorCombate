package es.alvarogrlp.marvelsimu.backend.model.abtrastas;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Conexion {

    private String rutaArchivoBD;
    private Connection connection;

    public Conexion(){}


    /**
     * Constructor con path de conexion
     * 
     * @param unaRutaArchivoBD ruta de la bbdd
     * @throws SQLException error controlado
     */
    public Conexion(String unaRutaArchivoBD) throws SQLException {
        if (unaRutaArchivoBD == null || unaRutaArchivoBD.isEmpty()) {
            throw new SQLException("El fichero es nullo o vacio");
        }
        File file = new File(unaRutaArchivoBD);
        if (!file.exists()) {
            throw new SQLException("No exise la bbdd:" + unaRutaArchivoBD);
        }

        rutaArchivoBD = unaRutaArchivoBD;
    }


    public String getRutaArchivoBD() {
        return this.rutaArchivoBD;
    }

    public Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + rutaArchivoBD);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return this.connection;
    }


    /**
     * Funcion que abre la conexion a la bbdd
     * @return
     * @throws SQLException
     */
    public Connection conectar() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + rutaArchivoBD);
        }
        return connection;
    }

    /**
     * Funcion que cierra la conexion de bbdd
     * @throws SQLException
     */
    public void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
