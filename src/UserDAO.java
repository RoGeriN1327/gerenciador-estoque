import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserDAO {
    private File file;

    public UserDAO(String path) {
        this.file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println("usuario;hash;nome;status");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Map<String, UserRecord> loadUsers() throws IOException {
        Map<String, UserRecord> users = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";", -1);
                if (p.length < 4) continue;

                users.put(p[0], new UserRecord(p[0], p[1], p[2], p[3]));
            }
        }
        return users;
    }

    public synchronized boolean authenticate(String user, String hash) throws IOException {
        Map<String, UserRecord> list = loadUsers();
        UserRecord r = list.get(user);

        if (r == null) return false;
        if (!r.hash.equals(hash)) return false;

        return r.status.equals("A"); // só loga se for Ativo
    }

    public synchronized String getStatus(String user) throws IOException {
        Map<String, UserRecord> list = loadUsers();
        UserRecord r = list.get(user);
        if (r == null) return null;
        return r.status;
    }

    public synchronized void addUser(String user, String nome, String hash, String status) throws IOException {
        Map<String, UserRecord> list = loadUsers();
        if (list.containsKey(user))
            throw new IOException("Usuário já existe.");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(user + ";" + hash + ";" + nome + ";" + status);
        }
    }
}

class UserRecord {
    String user;
    String hash;
    String nome;
    String status;

    UserRecord(String user, String hash, String nome, String status) {
        this.user = user;
        this.hash = hash;
        this.nome = nome;
        this.status = status;
    }
}
