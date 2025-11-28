import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private File file;

    public ProductDAO(String path) throws IOException {
        this.file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("id;name;quantity;price;description");
            }
        }
    }

    public synchronized List<Product> loadAll() throws IOException {
        List<Product> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(";", -1);

                if (p.length < 4) {
                    System.err.println("Linha inválida em products.csv: " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(p[0]);
                    String name = p[1];
                    int qty = Integer.parseInt(p[2]);

                    // aceita vírgula OU ponto
                    double price = Double.parseDouble(p[3].replace(",", "."));

                    String desc = p.length > 4 ? p[4] : "";

                    list.add(new Product(id, name, qty, price, desc));

                } catch (Exception ex) {
                    System.err.println("Linha inválida em products.csv: " + line);
                }
            }
        }

        return list;
    }

    private synchronized int nextId() throws IOException {
        int max = 0;
        for (Product p : loadAll()) {
            if (p.getId() > max) max = p.getId();
        }
        return max + 1;
    }

    public synchronized void add(Product product) throws IOException {
        int id = nextId();
        product = new Product(id, product.getName(), product.getQuantity(), product.getPrice(), product.getDescription());

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.printf(
                "%d;%s;%d;%.2f;%s%n",
                product.getId(),
                escape(product.getName()),
                product.getQuantity(),
                product.getPrice(),
                escape(product.getDescription())
            );
        }
    }

    public synchronized void update(Product product) throws IOException {
        List<Product> list = loadAll();
        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == product.getId()) {
                list.set(i, product);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IOException("Produto com id " + product.getId() + " não encontrado.");
        }

        saveAll(list);
    }

    public synchronized void delete(int id) throws IOException {
        List<Product> list = loadAll();
        boolean removed = list.removeIf(p -> p.getId() == id);

        if (!removed) {
            throw new IOException("Produto com id " + id + " não encontrado.");
        }

        saveAll(list);
    }

    private void saveAll(List<Product> list) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            pw.println("id;name;quantity;price;description");

            for (Product p : list) {
                pw.printf(
                    "%d;%s;%d;%.2f;%s%n",
                    p.getId(),
                    escape(p.getName()),
                    p.getQuantity(),
                    p.getPrice(),
                    escape(p.getDescription())
                );
            }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(";", ",");
    }
}
