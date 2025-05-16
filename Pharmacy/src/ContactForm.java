import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class ContactForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private ArrayList<String> selectedMedicines;

    public ContactForm() {
        // Инициализация списка выбранных лекарств
        selectedMedicines = new ArrayList<>();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Pharmacy - Войти");
        setBounds(400, 100, 400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(new File("exam/src/main/resources/Images/drugs.png").getAbsolutePath());
        setIconImage(icon.getImage());

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Панель заголовка
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(138, 209, 206));

        JLabel titleLabel = new JLabel("Добро пожаловать в Аптеку!", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(138, 209, 206));

        JLabel subtitleLabel = new JLabel("Введите данные для входа", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        subtitleLabel.setOpaque(true);
        subtitleLabel.setBackground(new Color(138, 209, 206));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Форма входа
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(138, 209, 206),
                        getWidth(), getHeight(), new Color(176, 224, 230)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Поле Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Поле Пароль
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton loginButton = new JButton("Войти");
        JButton registerButton = new JButton("Нет аккаунта? Зарегистрироваться");
        JButton codeResButton = new JButton("Восстановить пароль");

        loginButton.setBackground(new Color(0, 123, 167));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton.setBackground(new Color(0, 123, 167));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        codeResButton.setBackground(new Color(255, 102, 102));
        codeResButton.setForeground(Color.WHITE);
        codeResButton.setFocusPainted(false);
        codeResButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Действие при входе
        loginButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Вход для администратора
            if ("Ali".equals(email) && "01".equals(password)) {
                JOptionPane.showMessageDialog(null, "Вход успешен, Привет, Админ!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new AdminPanel(); // Открываем окно админки
                return;
            }

            // Проверка через базу данных
            DatabaseHelper.connect();
            if (DatabaseHelper.checkUser(email, password)) {
                int userId = DatabaseHelper.getUserIdByEmail(email);
                SessionManager.setUser(userId, email); // Запоминаем пользователя

                JOptionPane.showMessageDialog(null, "Вход успешен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MedicineSelectionWindow(userId, selectedMedicines);
            } else {
                JOptionPane.showMessageDialog(null, "Неверный email или пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Действие при нажатии на "Регистрация"
        registerButton.addActionListener((ActionEvent e) -> {
            dispose();
            new RegisterConForm();
        });

        // Действие при нажатии на "Восстановить пароль"
        codeResButton.addActionListener((ActionEvent e) -> {
            dispose();
            new CodeRes();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(codeResButton);

        // Добавление элементов в контейнер
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        // Добавление действия на клавишу Enter
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> loginButton.doClick());

        setVisible(true);
    }

    public static void main(String[] args) {
        new ContactForm();
    }
}
