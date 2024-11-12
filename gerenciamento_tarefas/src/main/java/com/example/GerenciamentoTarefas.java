package com.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class GerenciamentoTarefas extends JFrame {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JButton editarButton, atualizarButton, cadastrarUsuarioButton, cadastrarTarefaButton;  // Botão para cadastrar tarefa

    public GerenciamentoTarefas() {
        setTitle("Gerenciamento de Tarefas");

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Descrição");
        modelo.addColumn("Setor");
        modelo.addColumn("Prioridade");
        modelo.addColumn("Status");
        modelo.addColumn("Usuário");

        tabela = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabela);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);  // Adicionando o JScrollPane no centro

        carregarTarefas();

        // Botão para editar a tarefa selecionada
        editarButton = new JButton("Editar Tarefa");
        editarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarTarefa();
            }
        });

        // Botão de atualizar
        atualizarButton = new JButton("Atualizar");
        atualizarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                carregarTarefas();
            }
        });

        // Botão para cadastrar novo usuário
        cadastrarUsuarioButton = new JButton("Cadastrar Usuário");
        cadastrarUsuarioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });

        // Botão para cadastrar nova tarefa
        cadastrarTarefaButton = new JButton("Cadastrar Tarefa");
        cadastrarTarefaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarTarefa();
            }
        });

        // Painel de botões (Cadastrar Tarefa, Atualizar, Cadastrar Usuário)
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());
        painelBotoes.add(editarButton);
        painelBotoes.add(atualizarButton);
        painelBotoes.add(cadastrarUsuarioButton);
        painelBotoes.add(cadastrarTarefaButton);  // Adicionando o botão de cadastrar tarefa

        add(painelBotoes, BorderLayout.SOUTH);

        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void carregarTarefas() {
        modelo.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
             Statement stmt = conn.createStatement()) {

            String query = "SELECT t.id, t.descricao, t.setor, t.prioridade, t.status, u.nome " +
                           "FROM tarefas t " +
                           "JOIN usuarios u ON t.usuario_id = u.id " +
                           "ORDER BY CASE t.status " +
                           "             WHEN 'a fazer' THEN 1 " +
                           "             WHEN 'em andamento' THEN 2 " +
                           "             WHEN 'concluída' THEN 3 " +
                           "             ELSE 4 " +
                           "           END";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Vector<String> linha = new Vector<>();
                linha.add(rs.getString("id"));
                linha.add(rs.getString("descricao"));
                linha.add(rs.getString("setor"));
                linha.add(rs.getString("prioridade"));
                linha.add(rs.getString("status"));
                linha.add(rs.getString("nome"));
                modelo.addRow(linha);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editarTarefa() {
        int linhaSelecionada = tabela.getSelectedRow();
    
        if (linhaSelecionada != -1) {
            try {
                int tarefaId = Integer.parseInt(modelo.getValueAt(linhaSelecionada, 0).toString());
                new JanelaAlteracaoTarefa(tarefaId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar editar a tarefa. ID inválido.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar.");
        }
    }

    private void cadastrarUsuario() {
        new JanelaCadastroUsuario();
    }

    private void cadastrarTarefa() {
        new JanelaCadastroTarefa();  // Chama a janela para cadastrar nova tarefa
    }

    // Classe interna para a janela de alteração de tarefa
    class JanelaAlteracaoTarefa extends JFrame {
        private JTextField descricaoField, setorField;
        private JComboBox<String> prioridadeCombo, statusCombo;
        private JButton salvarButton;
        private int tarefaId;

        public JanelaAlteracaoTarefa(int tarefaId) {
            this.tarefaId = tarefaId;
            setTitle("Alterar Tarefa");

            descricaoField = new JTextField(20);
            setorField = new JTextField(20);
            prioridadeCombo = new JComboBox<>(new String[]{"baixa", "média", "alta"});
            statusCombo = new JComboBox<>(new String[]{"a fazer", "em andamento", "concluída"});

            salvarButton = new JButton("Salvar Alterações");
            salvarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    salvarAlteracoes();
                }
            });

            setLayout(new GridLayout(5, 2));
            add(new JLabel("Descrição:"));
            add(descricaoField);
            add(new JLabel("Setor:"));
            add(setorField);
            add(new JLabel("Prioridade:"));
            add(prioridadeCombo);
            add(new JLabel("Status:"));
            add(statusCombo);
            add(new JLabel());
            add(salvarButton);

            carregarDadosTarefa();

            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }

        private void carregarDadosTarefa() {
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 PreparedStatement stmt = conn.prepareStatement("SELECT descricao, setor, prioridade, status FROM tarefas WHERE id = ?")) {

                stmt.setInt(1, tarefaId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    descricaoField.setText(rs.getString("descricao"));
                    setorField.setText(rs.getString("setor"));
                    prioridadeCombo.setSelectedItem(rs.getString("prioridade"));
                    statusCombo.setSelectedItem(rs.getString("status"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void salvarAlteracoes() {
            String descricao = descricaoField.getText();
            String setor = setorField.getText();
            String prioridade = (String) prioridadeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE tarefas SET descricao = ?, setor = ?, prioridade = ?, status = ? WHERE id = ?")) {

                stmt.setString(1, descricao);
                stmt.setString(2, setor);
                stmt.setString(3, prioridade);
                stmt.setString(4, status);
                stmt.setInt(5, tarefaId);
                stmt.executeUpdate();

                carregarTarefas();
                dispose();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe para a janela de cadastro de usuário
    class JanelaCadastroUsuario extends JFrame {
        private JTextField nomeField, emailField;
        private JButton salvarButton;

        public JanelaCadastroUsuario() {
            setTitle("Cadastrar Novo Usuário");

            nomeField = new JTextField(20);
            emailField = new JTextField(20);
            salvarButton = new JButton("Salvar");

            salvarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cadastrarNovoUsuario();
                }
            });

            setLayout(new GridLayout(3, 2));
            add(new JLabel("Nome:"));
            add(nomeField);
            add(new JLabel("Email:"));
            add(emailField);
            add(salvarButton);

            setSize(300, 150);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }

        private void cadastrarNovoUsuario() {
            String nome = nomeField.getText();
            String email = emailField.getText();

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios (nome, email) VALUES (?, ?)")) {

                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso.");
                dispose();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe para a janela de cadastro de tarefa
    class JanelaCadastroTarefa extends JFrame {
        private JTextField descricaoField, setorField;
        private JComboBox<String> prioridadeCombo, statusCombo;
        private JComboBox<String> usuarioCombo;
        private JButton salvarButton;

        public JanelaCadastroTarefa() {
            setTitle("Cadastrar Nova Tarefa");

            descricaoField = new JTextField(20);
            setorField = new JTextField(20);
            prioridadeCombo = new JComboBox<>(new String[]{"baixa", "média", "alta"});
            statusCombo = new JComboBox<>(new String[]{"a fazer", "em andamento", "concluída"});
            usuarioCombo = new JComboBox<>();

            carregarUsuarios();  // Carregar lista de usuários no combo

            salvarButton = new JButton("Salvar Tarefa");
            salvarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    salvarTarefa();
                }
            });

            setLayout(new GridLayout(6, 2));
            add(new JLabel("Descrição:"));
            add(descricaoField);
            add(new JLabel("Setor:"));
            add(setorField);
            add(new JLabel("Prioridade:"));
            add(prioridadeCombo);
            add(new JLabel("Status:"));
            add(statusCombo);
            add(new JLabel("Usuário:"));
            add(usuarioCombo);
            add(new JLabel());
            add(salvarButton);

            setSize(300, 250);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }

        private void carregarUsuarios() {
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 Statement stmt = conn.createStatement()) {

                ResultSet rs = stmt.executeQuery("SELECT id, nome FROM usuarios");

                while (rs.next()) {
                    usuarioCombo.addItem(rs.getString("nome"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void salvarTarefa() {
            String descricao = descricaoField.getText();
            String setor = setorField.getText();
            String prioridade = (String) prioridadeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String usuarioNome = (String) usuarioCombo.getSelectedItem();

            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO tarefas (descricao, setor, prioridade, status, usuario_id) VALUES (?, ?, ?, ?, ?)")) {

                stmt.setString(1, descricao);
                stmt.setString(2, setor);
                stmt.setString(3, prioridade);
                stmt.setString(4, status);
                stmt.setInt(5, getUsuarioId(usuarioNome));  // Pega o ID do usuário selecionado
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Tarefa cadastrada com sucesso.");
                dispose();
                carregarTarefas();  // Atualiza a tabela de tarefas
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private int getUsuarioId(String usuarioNome) {
            int usuarioId = 0;
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
                 PreparedStatement stmt = conn.prepareStatement("SELECT id FROM usuarios WHERE nome = ?")) {

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
    }

    public static void main(String[] args) {
        new GerenciamentoTarefas();
    }
}
