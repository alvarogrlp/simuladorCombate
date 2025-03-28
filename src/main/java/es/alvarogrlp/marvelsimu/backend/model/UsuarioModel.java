package es.alvarogrlp.marvelsimu.backend.model;

import java.util.Objects;

public class UsuarioModel {
    private String nombre;
    private String email;
    private String contrasenia;

    public UsuarioModel() {
    }

    public UsuarioModel(String nombre, String email, String contrasenia) {
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return this.contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public UsuarioModel nombre(String nombre) {
        setNombre(nombre);
        return this;
    }

    public UsuarioModel email(String email) {
        setEmail(email);
        return this;
    }

    public UsuarioModel contrasenia(String contrasenia) {
        setContrasenia(contrasenia);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UsuarioModel)) {
            return false;
        }
        UsuarioModel usuarioModel = (UsuarioModel) o;
        return Objects.equals(email, usuarioModel.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
