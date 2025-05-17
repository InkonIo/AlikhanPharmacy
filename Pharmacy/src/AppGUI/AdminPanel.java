package AppGUI;

import com.formdev.flatlaf.FlatLightLaf;
import Database.MedicineDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.event.DocumentListener;

public class AdminPanel extends JFrame {
    private JTable productTable, userTable;
    private DefaultTableModel productModel, userModel;

    public AdminPanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Admin Panel - Pharmacy");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(0, 123, 167));
        tabbedPane.setForeground(Color.WHITE);

        tabbedPane.add("Товары", createProductPanel());
        tabbedPane.add("Пользователи", createUserPanel());
        tabbedPane.add("Аналитика", createAnalyticsPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        productModel = new DefaultTableModel(new String[]{"Название", "Цена", "Категория"}, 0);
        productTable = new JTable(productModel);

        JButton addButton = createStyledButton("Добавить");
        JButton saveButton = createStyledButton("Сохранить");
        JButton backButton = createStyledButton("Назад");

        addButton.addActionListener(e -> openAddProductDialog());
        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> goBack());

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.add(addButton);
        controlPanel.add(saveButton);
        controlPanel.add(backButton);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void saveChanges() {
        for (int i = 0; i < productModel.getRowCount(); i++) {
            String medicine = (String) productModel.getValueAt(i, 0);
            String priceStr = (String) productModel.getValueAt(i, 1);
            double price = Double.parseDouble(priceStr.replace(" ₸", "").trim());

            MedicineDatabase.setPrice(medicine, price);
        }
        JOptionPane.showMessageDialog(this, "Изменения сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    private void goBack() {
        new ContactForm();
        dispose();
    }

    private void openAddProductDialog() {
        String[] categories = {"Простуда", "Диабет", "Малыши и мамы", "Для кожи", "Витамины"};
        Map<String, String[]> medicines = Map.of(
                "Простуда", new String[]{
                        "Цитрамон",
                        "Парацетамол",
                        "Ибупрофен",
                        "Аквамарис",
                        "Грипофрен",
                        "Колдрекс",
                        "Фервекс"
                },
                "Диабет", new String[]{"Глюкоза", "Метформин", "Глибенкламид"},
                "Малыши и мамы", new String[]{
                        "Бепантен",
                        "Панадол Бэби",
                        "Эспумизан"
                },
                "Для кожи", new String[]{"Бепантен", "Левомеколь"},
                "Витамины", new String[]{
                        "Аскорбинка",
                        "Ретинол",
                        "Тиамин"
                }
        );


        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JComboBox<String> medicineBox = new JComboBox<>(medicines.get(categories[0]));
        JTextField priceField = new JTextField(10);
        priceField.setToolTipText("Введите цену в тенге");

        categoryBox.addActionListener(e -> {
            String selectedCategory = (String) categoryBox.getSelectedItem();
            medicineBox.setModel(new DefaultComboBoxModel<>(medicines.get(selectedCategory)));
            medicineBox.setSelectedIndex(0);
            priceField.requestFocus();
        });

        priceField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validatePrice(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validatePrice(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validatePrice(); }

            private void validatePrice() {
                String text = priceField.getText().trim();
                if (!text.matches("\\d+")) {
                    priceField.setBackground(Color.PINK);
                } else {
                    priceField.setBackground(Color.WHITE);
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Категория:"));
        panel.add(categoryBox);
        panel.add(new JLabel("Препарат:"));
        panel.add(medicineBox);
        panel.add(new JLabel("Цена (₸):"));
        panel.add(priceField);

        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        JDialog dialog = new JDialog(this, "Добавить товар", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        addButton.addActionListener(e -> {
            String category = (String) categoryBox.getSelectedItem();
            String medicine = (String) medicineBox.getSelectedItem();
            String price = priceField.getText().trim();
            if (!price.matches("\\d+")) {
                JOptionPane.showMessageDialog(dialog, "Введите корректную цену", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            productModel.addRow(new Object[]{medicine, price + " ₸", category});
            MedicineDatabase.setPrice(medicine, Double.parseDouble(price));
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 123, 167));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        userModel = new DefaultTableModel(new String[]{"ID", "Email", "Пароль"}, 0);
        userTable = new JTable(userModel);

        loadUsers();

        JButton deleteButton = new JButton("Удалить");
        JButton addUserButton = new JButton("Добавить пользователя");

        deleteButton.addActionListener(e -> deleteUser());
        addUserButton.addActionListener(e -> addUser());

        JPanel controlPanel = new JPanel();
        controlPanel.add(deleteButton);
        controlPanel.add(addUserButton);

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addUser() {
        String email = JOptionPane.showInputDialog(this, "Введите email нового пользователя:", "Добавить пользователя", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(this, "Введите пароль нового пользователя:", "Добавить пользователя", JOptionPane.QUESTION_MESSAGE);

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email и пароль не могут быть пустыми!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                userModel.addRow(new Object[]{id, email, password});
            }
            JOptionPane.showMessageDialog(this, "Пользователь добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка добавления пользователя!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUsers() {
        userModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, email, password FROM users")) {

            while (rs.next()) {
                userModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка загрузки пользователей!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пользователя!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) userModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Удалить пользователя?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:users.db");
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            userModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Пользователь удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка удаления пользователя!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 248, 255));
        JLabel analyticsLabel = new JLabel("Аналитика в разработке");
        panel.add(analyticsLabel);
        return panel;
    }

    public static void main(String[] args) {
        new AdminPanel();
    }
}
