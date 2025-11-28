import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class UserListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private UserDAO userDAO;

    public UserListFrame() {
        super("Usuários Cadastrados");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userDAO = new UserDAO("users.csv");

        model = new DefaultTableModel(new Object[]{"Usuário", "Nome", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        loadUsers();

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton editBtn = new JButton("Editar Selecionado");
        bottom.add(editBtn);
        add(bottom, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> openEditForm());
    }

    private void loadUsers() {
        model.setRowCount(0);

        try {
            Map<String, UserRecord> list = userDAO.loadUsers();

            for (UserRecord u : list.values()) {
                
                String statusText = switch (u.status) {
                    case "A" -> "ATIVO";
                    case "B" -> "BLOQUEADO";
                    case "C" -> "CANCELADO";
                    default -> "DESCONHECIDO";
                };

                model.addRow(new Object[]{
                        u.user,
                        u.nome,
                        statusText
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar usuários:\n" + ex.getMessage());
        }
    }

    private void openEditForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um usuário para editar.");
            return;
        }

        String usuario = (String) model.getValueAt(row, 0);

        try {
            Map<String, UserRecord> list = userDAO.loadUsers();
            UserRecord r = list.get(usuario);

            if (r == null) {
                JOptionPane.showMessageDialog(this,
                        "Usuário não encontrado.");
                return;
            }

            UserEditFrame form = new UserEditFrame(r);
            form.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao abrir edição:\n" + ex.getMessage());
        }
    }
}
