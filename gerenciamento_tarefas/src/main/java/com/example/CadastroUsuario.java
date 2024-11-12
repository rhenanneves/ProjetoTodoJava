package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CadastroUsuario extends JFrame {
    private JTextField nomeField, emailField;
    private JButton cadastrarButton;

    public CadastroUsuario() {
        setTitle("Cadastro de Usuário");
        setLayout(new GridLayout(3, 2));

        JLabel nomeLabel = new JLabel("Nome:");
        nomeField = new JTextField();
        
        JLabel emailLabel = new JLabel("E-mail:");
        emailField = new JTextField();
        
        cadastrarButton = new JButton("Cadastrar");

        add(nomeLabel);
        add(nomeField);
        add(emailLabel);
        add(emailField);
        add(cadastrarButton);

        cadastrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void cadastrarUsuario() {
        String nome = nomeField.getText();
        String email = emailField.getText();

        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/gerenciamento_tarefas", "postgres", "postgres");
            String query = "INSERT INTO usuarios (nome, email) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, nome);
            pst.setString(2, email);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.");
        }
    }

    public static void main(String[] args) {
        new CadastroUsuario();
    }
}
