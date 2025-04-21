import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class CodeRes extends JFrame {
    private JTextField emailField;
    private JTextField phoneField;
    private JButton sendSmsButton;
    private JButton backButton;
    private String verificationCode;

    public CodeRes() {
        setTitle("Смена пароля");
        setSize(350, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Восстановление пароля", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 102));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("📧 Почта:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("📞 Телефон:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        sendSmsButton = new JButton("Отправить код");
        sendSmsButton.setBackground(new Color(0, 123, 167));
        sendSmsButton.setForeground(Color.WHITE);
        sendSmsButton.setFocusPainted(false);
        sendSmsButton.addActionListener(this::sendSmsAction);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(sendSmsButton, gbc);

        backButton = new JButton("Назад");
        backButton.setBackground(new Color(255, 102, 102));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            new ContactForm();
        });

        gbc.gridy = 4;
        panel.add(backButton, gbc);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void sendSmsAction(ActionEvent e) {
        verificationCode = generateVerificationCode();
        System.out.println("Код отправлен: " + verificationCode);
        String inputCode = JOptionPane.showInputDialog(this, "Введите полученный код:");

        if (inputCode != null && inputCode.equals(verificationCode)) {
            JOptionPane.showMessageDialog(this, "Код подтверждён! Введите новый пароль.");
            new NewPasswordForm(emailField.getText());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Неверный код. Попробуйте снова.");
        }
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
