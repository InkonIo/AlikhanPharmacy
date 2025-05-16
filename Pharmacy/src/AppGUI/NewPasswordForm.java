package AppGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.formdev.flatlaf.FlatLightLaf;
import Database.DatabaseHelper;

public class NewPasswordForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;

    public NewPasswordForm(String text) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Ввод нового пароля");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(138, 209, 206));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel emailLabel = new JLabel("Почта:");
        emailLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        JLabel passwordLabel1 = new JLabel("Новый пароль:");
        passwordLabel1.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel1, gbc);

        passwordField1 = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField1, gbc);

        JLabel passwordLabel2 = new JLabel("Подтверждение пароля:");
        passwordLabel2.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel2, gbc);

        passwordField2 = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField2, gbc);

        JButton confirmButton = new JButton("Подтвердить");
        confirmButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 123, 167));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText();
            String password1 = new String(passwordField1.getPassword());
            String password2 = new String(passwordField2.getPassword());

            if (DatabaseHelper.emailExists(email)) {
                if (password1.equals(password2)) {
                    if (DatabaseHelper.updatePassword(email, password1)) {
                        JOptionPane.showMessageDialog(this, "Пароль успешно изменён!");
                        new ContactForm();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Ошибка при обновлении пароля.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Пароли не совпадают. Попробуйте снова.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Почта не найдена. Перенаправляем на форму регистрации.");
                new RegisterConForm();
                dispose();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}