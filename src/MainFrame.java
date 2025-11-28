import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {
    private ProductDAO dao;
    private JTable table;
    private DefaultTableModel tableModel;
    private String currentUser;
    private UserDAO userDAO = new UserDAO("users.csv");

    public MainFrame(String user) {
        this.currentUser = user;
        setTitle("Gerenciador de Estoque - Usuário: " + user);
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            dao = new ProductDAO("products.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir arquivo de produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initUI();
        loadTable();
    }

    private void initUI() {
        JMenuBar menuBar = new JMenuBar();

        // MENU SISTEMA
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem fechar = new JMenuItem("Fechar");
        fechar.addActionListener(e -> System.exit(0));
        menuSistema.add(fechar);

        JMenuItem deslogar = new JMenuItem("Deslogar");
        deslogar.addActionListener(e -> {
            this.dispose(); 
            new LoginFrame().setVisible(true);
        });
        menuSistema.add(deslogar);

        // MENU GERENCIAR
        JMenu manage = new JMenu("Gerenciar");

        JMenuItem addUser = new JMenuItem("Adicionar Usuário");
        addUser.addActionListener(e -> showAddUserDialog());
        manage.add(addUser);

        JMenuItem listUsers = new JMenuItem("Listar Usuários");
        listUsers.addActionListener(e -> new UserListFrame(this).setVisible(true));
        manage.add(listUsers);


        menuBar.add(menuSistema);
        menuBar.add(manage);

        setJMenuBar(menuBar);

        tableModel = new DefaultTableModel(new Object[]{"ID","Nome","Quantidade","Preço","Descrição"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Adicionar Produto");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Excluir");
        JButton btnRefresh = new JButton("Atualizar");
        toolBar.add(btnAdd); toolBar.add(btnEdit); toolBar.add(btnDelete); toolBar.add(btnRefresh);

        btnAdd.addActionListener(e -> showProductDialog(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadTable());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadTable() {
        try {
            List<Product> list = dao.loadAll();
            tableModel.setRowCount(0);

            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

            for (Product p : list) {
                tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getQuantity(),
                        nf.format(p.getPrice()),
                        p.getDescription()
                });
            }

            centralizarColunas();
            ajustarLarguraColunas(table);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void centralizarColunas() {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    private void ajustarLarguraColunas(JTable table) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            int largura = 50;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(renderer, row, col);
                largura = Math.max(comp.getPreferredSize().width + 20, largura);
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(largura);
        }
    }

    // ---------------------- ADICIONAR USUÁRIO ------------------------
    private void showAddUserDialog() {
        JDialog dlg = new JDialog(this, true);
        dlg.setTitle("Adicionar Novo Usuário");
        dlg.setSize(420, 260);
        dlg.setLayout(new GridBagLayout());
        dlg.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LABEL NOME
        JLabel nameL = new JLabel("Nome:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dlg.add(nameL, gbc);

        JTextField nameF = new JTextField();
        nameF.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        dlg.add(nameF, gbc);

        // LABEL USUÁRIO
        JLabel userL = new JLabel("Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dlg.add(userL, gbc);

        JTextField userF = new JTextField();
        userF.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        dlg.add(userF, gbc);

        // LABEL SENHA
        JLabel passL = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        dlg.add(passL, gbc);

        JPasswordField passF = new JPasswordField();
        passF.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1;
        dlg.add(passF, gbc);

        // BOTÃO SALVAR
        JButton save = new JButton("Salvar");
        save.setPreferredSize(new Dimension(140, 34));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dlg.add(save, gbc);

        save.addActionListener(e -> {
            String nome = nameF.getText().trim();
            String usuario = userF.getText().trim();
            String senha = new String(passF.getPassword()).trim();

            if (nome.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Preencha todos os campos.");
                return;
            }

            try {
                String hash = Utils.sha256(senha);
                userDAO.addUser(usuario, nome, hash, "A"); // status fixo A
                JOptionPane.showMessageDialog(dlg, "Usuário adicionado com sucesso!");
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Erro: " + ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }

    // ---------------------- PRODUTOS ------------------------

    private void showProductDialog(Product existing) {
        JDialog dlg = new JDialog(this, true);
        dlg.setTitle(existing == null ? "Adicionar Produto" : "Editar Produto");
        dlg.setSize(400, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameL = new JLabel("Nome:");
        gbc.gridx = 0; gbc.gridy = 0; dlg.add(nameL, gbc);
        JTextField nameF = new JTextField(); gbc.gridx = 1; dlg.add(nameF, gbc);

        JLabel qtyL = new JLabel("Quantidade:"); gbc.gridx = 0; gbc.gridy = 1; dlg.add(qtyL, gbc);
        JTextField qtyF = new JTextField(); gbc.gridx = 1; dlg.add(qtyF, gbc);

        JLabel priceL = new JLabel("Preço:"); gbc.gridx = 0; gbc.gridy = 2; dlg.add(priceL, gbc);
        JTextField priceF = new JTextField(); gbc.gridx = 1; dlg.add(priceF, gbc);

        JLabel descL = new JLabel("Descrição:"); gbc.gridx = 0; gbc.gridy = 3; dlg.add(descL, gbc);
        JTextArea descA = new JTextArea(4,20);
        JScrollPane sp = new JScrollPane(descA);
        gbc.gridx = 1; dlg.add(sp, gbc);

        if (existing != null) {
            nameF.setText(existing.getName());
            qtyF.setText(String.valueOf(existing.getQuantity()));
            priceF.setText(String.valueOf(existing.getPrice()));
            descA.setText(existing.getDescription());
        }

        JButton saveBtn = new JButton("Salvar");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dlg.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            String qtys = qtyF.getText().trim();
            String prices = priceF.getText().trim();
            String desc = descA.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nome é obrigatório.");
                return;
            }

            int qty;
            double price;

            try { qty = Integer.parseInt(qtys); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dlg, "Quantidade inválida."); return; }

            try { price = Double.parseDouble(prices.replace(",", ".")); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(dlg, "Preço inválido."); return; }

            try {
                if (existing == null) {
                    dao.add(new Product(0, name, qty, price, desc));
                } else {
                    existing.setName(name);
                    existing.setQuantity(qty);
                    existing.setPrice(price);
                    existing.setDescription(desc);
                    dao.update(existing);
                }
                dlg.dispose();
                loadTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Product found = null;
            for (Product p : dao.loadAll()) if (p.getId() == id) { found = p; break; }
            if (found == null) throw new Exception("Produto não encontrado.");
            showProductDialog(found);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma exclusão do produto ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(id);
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
