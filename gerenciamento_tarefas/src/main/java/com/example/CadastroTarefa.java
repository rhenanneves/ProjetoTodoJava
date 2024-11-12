package com.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CadastroTarefa extends JFrame {
    private JTextField descricaoField;
    private JTextField setorField;
    private JComboBox<String> prioridadeCombo;
    private JComboBox<String> usuarioCombo;
    private JButton cadastrarButton;

    public CadastroTarefa() {
        setTitle("Cadastro de Tarefa");

        // Criar os campos de entrada
        descricaoField = new JTextField(20);
        descricaoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Fonte maior e legível
        descricaoField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        setorField = new JTextField(20);
        setorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setorField.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        prioridadeCombo = new JComboBox<>(new String[]{"Baixa", "Média", "Alta"});
        prioridadeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        prioridadeCombo.setBackground(new Color(245, 245, 245));  // Fundo claro

        usuarioCombo = new JComboBox<>();
        usuarioCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usuarioCombo.setBackground(new Color(245, 245, 245));

        // Preencher comboBox de usuários
        carregarUsuarios();

        // Botão de cadastro com estilo moderno
        cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cadastrarButton.setBackground(new Color(76, 175, 80));  // Cor de fundo verde
        cadastrarButton.setForeground(Color.WHITE);  // Texto branco
        cadastrarButton.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80)));
        cadastrarButton.setPreferredSize(new Dimension(100, 40));  // Botão com tamanho personalizado
        cadastrarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarTarefa();
            }
        });

        // Layout com espaçamento e alinhamento
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espaçamento entre os componentes
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        add(descricaoField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Setor:"), gbc);

        gbc.gridx = 1;
        add(setorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Prioridade:"), gbc);

        gbc.gridx = 1;
        add(prioridadeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        add(usuarioCombo, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        add(cadastrarButton, gbc);

        // Configuração da janela
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // Centraliza a janela
        setVisible(true);
    }

    // Carregar usuários para o ComboBox
    private void carregarUsuarios() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
            String query = "SELECT nome FROM usuarios";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                usuarioCombo.addItem(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para cadastrar uma tarefa
    private void cadastrarTarefa() {
        if (descricaoField.getText().isEmpty() || setorField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descricao = descricaoField.getText();
        String setor = setorField.getText();
        String prioridade = (String) prioridadeCombo.getSelectedItem();
        String usuarioNome = (String) usuarioCombo.getSelectedItem();

        // Recupera o ID do usuário selecionado
        int usuarioId = getUsuarioId(usuarioNome);

        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
            String query = "INSERT INTO tarefas (descricao, setor, prioridade, status, usuario_id) VALUES (?, ?, ?, 'a fazer', ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, descricao);
            stmt.setString(2, setor);
            stmt.setString(3, prioridade);
            stmt.setInt(4, usuarioId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cadastro concluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar tarefa.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Recuperar ID do usuário com base no nome
    private int getUsuarioId(String usuarioNome) {
        int usuarioId = -1;
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
            String query = "SELECT id FROM usuarios WHERE nome = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usuarioNome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuarioId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarioId;
    }

    // Limpar campos após cadastro
    private void limparCampos() {
        descricaoField.setText("");
        setorField.setText("");
        prioridadeCombo.setSelectedIndex(0);
        usuarioCombo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new CadastroTarefa();
    }
}
