package es.ies.puerto.service;

import es.ies.puerto.model.Usuario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {
    private String path = "/home/dam/Escritorio/Pro/App/simuladorCombate/src/main/java/resources/es/alvarogrlp/marvelsimu/usuarios.json";
    private File file;
    private List<Usuario> listUsuarios;

    public UsuarioService() {
        file = new File(path);
        loadFile(file);
    }

    private void loadFile(File file) {
        try {
            if (file.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(path)));
                listUsuarios = parseUsuarios(content);
            } else {
                listUsuarios = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(File file, List<Usuario> usuarios) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(usuariosToJson(usuarios));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        int posicion = listUsuarios.indexOf(usuario);
        if (posicion >= 0) {
            return false;
        }
        boolean insertar = listUsuarios.add(usuario);
        if (insertar) {
            saveFile(file, listUsuarios);
        }
        return insertar;
    }

    public List<Usuario> getUsuarios() {
        return listUsuarios;
    }

    private List<Usuario> parseUsuarios(String json) {
        List<Usuario> usuarios = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return usuarios;
        }
        json = json.substring(1, json.length() - 1);
        String[] entries = json.split("},\\{");
        for (String entry : entries) {
            entry = entry.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
            String[] fields = entry.split(",");
            if (fields.length < 4) {
                continue; // Skip invalid entries
            }
            String nombreUsuario = fields[0].split(":")[1].replace("\"", "");
            String password = fields[1].split(":")[1].replace("\"", "");
            String nombre = fields[2].split(":")[1].replace("\"", "");
            String email = fields[3].split(":")[1].replace("\"", "");
            usuarios.add(new Usuario(nombreUsuario, password, nombre, email));
        }
        return usuarios;
    }

    private String usuariosToJson(List<Usuario> usuarios) {
        StringBuilder json = new StringBuilder("[");
        for (Usuario usuario : usuarios) {
            json.append("{\"nombreUsuario\":\"").append(usuario.getNombreUsuario()).append("\",")
                .append("\"password\":\"").append(usuario.getPassword()).append("\",")
                .append("\"nombre\":\"").append(usuario.getNombre()).append("\",")
                .append("\"email\":\"").append(usuario.getEmail()).append("\"},");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 1); 
        }
        json.append("]");
        return json.toString();
    }
}