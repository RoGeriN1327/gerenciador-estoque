import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UserListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private UserDAO userDAO;

    public UserListFrame() {
        setTitle("Usuários Cadastrados");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        userDAO = new UserDAO();

        model = new DefaultTableModel(new Object[]{"ID", "Nome", "Login", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        carregarUsuarios();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();

                    if (row >= 0) {
                        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                        editarUsuario(id);
                    }
                }
            }
        });
    }

    private void carregarUsuarios() {
        model.setRowCount(0);
        List<User> users = userDAO.listAll();

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getName(),
                    u.getLogin(),
                    u.getStatus()
            });
        }

        ajustarLarguraColunas();
    }

    private void ajustarLarguraColunas() {
        final int margin = 15;
        for (int col = 0; col < table.getColumnCount(); col++) {
            int largura = 75;
            for (int row = 0; row < table.getRowCount(); row++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
                largura = Math.max(comp.getPreferredSize().width + margin, largura);
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(largura);
        }
    }

    private void editarUsuario(int id) {
        User user = userDAO.findById(id);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado!");
            return;
        }

        UserForm form = new UserForm(user, true);

        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                carregarUsuarios();
            }
        });

        form.setVisible(true);
    }
}
