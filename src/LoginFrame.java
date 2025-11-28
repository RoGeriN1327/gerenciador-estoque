import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private UserDAO userDAO;

    public LoginFrame() {
        setTitle("Gerenciador de Estoque - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            userDAO = new UserDAO("users.csv");
        } catch (Exception e) {
            showError("Erro ao acessar arquivo de usuários: " + e.getMessage());
        }
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Usuário:");
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(userLabel, gbc);

        userField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Senha:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(passLabel, gbc);

        passField = new JPasswordField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        panel.add(passField, gbc);

        JButton loginBtn = new JButton("Entrar");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> doLogin());

        getContentPane().add(panel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Preencha usuário e senha.");
            return;
        }

        try {
            String hash = Utils.sha256(password);

            try {
                boolean ok = userDAO.authenticate(username, hash);
                if (ok) {
                    new MainFrame(username).setVisible(true);
                    this.dispose();
                }
            } catch (IOException e) {

                if (e.getMessage().startsWith("STATUS_")) {
                    String st = e.getMessage().replace("STATUS_", "");

                    switch (st) {
                        case "B" -> showError("Usuário BLOQUEADO.");
                        case "C" -> showError("Usuário CANCELADO.");
                        default -> showError("Status inválido.");
                    }
                } else {
                    showError("Falha no login: " + e.getMessage());
                }

            }

        } catch (Exception ex) {
            showError("Erro inesperado: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
