package AppGUI;

import com.formdev.flatlaf.FlatLightLaf;
import Database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterConForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterConForm() {
        // Установка темы
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Настройки окна
        setTitle("Pharmacy - Регистрация");
        setBounds(400, 100, 400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Центрируем окно

        // Основной контейнер
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Панель заголовка
        JPanel headerPanel = new JPanel(new GridLayout(3, 1)); // Увеличено на 3 строки
        headerPanel.setBackground(new Color(138, 209, 206));
        JLabel titleLabel = new JLabel("Регистрация", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        JLabel subtitleLabel = new JLabel("Заполните поля для регистрации", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0, 0, 0)); // Оранжевый цвет
        JLabel reminderLabel = new JLabel("Рекомендуется запомнить пароль!", JLabel.CENTER);
        reminderLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        reminderLabel.setForeground(new Color(255, 140, 0)); // Оранжевый цвет
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        headerPanel.add(reminderLabel); // Добавление нового текста

        // Панель формы с градиентом
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(138, 209, 206), getWidth(), getHeight(), new Color(176, 224, 230));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Настройка GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы вокруг элементов
        gbc.anchor = GridBagConstraints.CENTER;

        // Email Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        // Email Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Пароль:"), gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(passwordField, gbc);

        // Confirm Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Повторите пароль:"), gbc);

        // Confirm Password Field
        gbc.gridx = 1;
        gbc.gridy = 2;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBorder(BorderFactory.createEmptyBorder());
        formPanel.add(confirmPasswordField, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));  // Вертикальное расположение кнопок

        JButton registerButton = new JButton("Зарегистрироваться");
        JButton backButton = new JButton("Назад");

        // Стилизация кнопок
        registerButton.setBackground(new Color(0, 123, 167));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        backButton.setBackground(new Color(0, 123, 167));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        // Центрируем кнопки
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавление отступов для кнопок
        registerButton.setMargin(new Insets(5, 5, 5, 5));
        backButton.setMargin(new Insets(5, 5, 5, 5));

        // Событие кнопки "Зарегистрироваться"
        registerButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Проверка на пустые поля
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка на совпадение паролей
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Пароли не совпадают", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка на существование email в базе данных
            DatabaseHelper.connect(); // Убедитесь, что соединение установлено
            if (DatabaseHelper.emailExists(email)) {
                JOptionPane.showMessageDialog(null, "Этот email уже зарегистрирован", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка пароля на соответствие требованиям
            if (!DatabaseHelper.isValidPassword(password)) {
                JOptionPane.showMessageDialog(null, "Пароль должен содержать минимум 6 символов, включая хотя бы одну цифру, одну заглавную букву и один специальный символ.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Если все проверки прошли успешно, добавляем пользователя в базу
            if (DatabaseHelper.addUser(email, password)) {
                JOptionPane.showMessageDialog(null, "Регистрация завершена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Закрыть форму регистрации
                new ContactForm(); // Открыть окно входа
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка при регистрации", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Событие кнопки "Назад"
        backButton.addActionListener((ActionEvent e) -> {
            dispose();
            new ContactForm(); // Возврат к форме входа
        });

        // Добавляем кнопки на панель
        buttonPanel.add(registerButton);    // Сначала "Зарегистрироваться"
        buttonPanel.add(Box.createVerticalStrut(10));  // Отступ между кнопками
        buttonPanel.add(backButton);

        // Сборка компонентов
        container.add(headerPanel, BorderLayout.NORTH);
        container.add(formPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
        getRootPane().setDefaultButton(registerButton);
    }

    public static void main(String[] args) {
        new RegisterConForm(); // Открытие окна регистрации
    }
}
