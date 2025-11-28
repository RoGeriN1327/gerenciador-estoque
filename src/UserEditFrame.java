import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Map;

public class UserEditFrame extends JFrame {

    private JTextField nomeField;
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> statusCombo;

    private UserRecord record;
    private UserDAO userDAO;

    public UserEditFrame(UserRecord rec) {
        this.record = rec;
        this.userDAO = new UserDAO("users.csv");

        setTitle("Editar Usuário: " + rec.user);
        setSize(430, 310);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        Font inputFont = new Font("SansSerif", Font.PLAIN, 16);
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // =============================
        // CAMPO USUÁRIO (NÃO EDITA)
        // =============================
        JLabel uL = new JLabel("Usuário:");
        uL.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0;
        add(uL, gbc);

        userField = new JTextField(rec.user);
        userField.setEditable(false);
        userField.setFont(inputFont);
        userField.setPreferredSize(new Dimension(250, 32));
        gbc.gridx = 1;
        add(userField, gbc);

        // =============================
        // CAMPO NOME
        // =============================
        JLabel nL = new JLabel("Nome:");
        nL.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1;
        add(nL, gbc);

        nomeField = new JTextField(rec.nome);
        nomeField.setFont(inputFont);
        nomeField.setPreferredSize(new Dimension(250, 32));
        gbc.gridx = 1;
        add(nomeField, gbc);

        // =============================
        // CAMPO SENHA
        // =============================
        JLabel pL = new JLabel("Nova senha (opcional):");
        pL.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2;
        add(pL, gbc);

        passField = new JPasswordField();
        passField.setFont(inputFont);
        passField.setPreferredSize(new Dimension(250, 32));
        gbc.gridx = 1;
        add(passField, gbc);

        // =============================
        // CAMPO STATUS 
        // =============================
        JLabel sL = new JLabel("Status:");
        sL.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 3;
        add(sL, gbc);

        statusCombo = new JComboBox<>(new String[]{
                "ATIVO",
                "BLOQUEADO",
                "CANCELADO"
        });
        statusCombo.setFont(inputFont);
        statusCombo.setPreferredSize(new Dimension(250, 32));
        gbc.gridx = 1;
        add(statusCombo, gbc);

        // Selecionar status atual
        switch (rec.status) {
            case "A" -> statusCombo.setSelectedItem("ATIVO");
            case "B" -> statusCombo.setSelectedItem("BLOQUEADO");
            case "C" -> statusCombo.setSelectedItem("CANCELADO");
        }

        // =============================
        // BOTÃO SALVAR
        // =============================
        JButton salvar = new JButton("Salvar alterações");
        salvar.setFont(new Font("SansSerif", Font.BOLD, 14));
        salvar.setPreferredSize(new Dimension(160, 36));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(salvar, gbc);

        salvar.addActionListener(e -> saveChanges());

        setVisible(true);
    }

    private void saveChanges() {
        try {
            Map<String, UserRecord> list = userDAO.loadUsers();
            UserRecord r = list.get(record.user);

            if (r == null) {
                JOptionPane.showMessageDialog(this, "Erro: usuário não encontrado.");
                return;
            }

            r.nome = nomeField.getText().trim();

            String novaSenha = new String(passField.getPassword()).trim();
            if (!novaSenha.isEmpty()) {
                r.hash = Utils.sha256(novaSenha);
            }

            // Atualiza status
            String selected = (String) statusCombo.getSelectedItem();
            r.status = selected.substring(0, 1);

            // Regrava CSV
            try (PrintWriter pw = new PrintWriter(new FileWriter("users.csv", false))) {
                pw.println("usuario;hash;nome;status");
                for (UserRecord x : list.values()) {
                    pw.println(x.user + ";" + x.hash + ";" + x.nome + ";" + x.status);
                }
            }

            JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }
}
