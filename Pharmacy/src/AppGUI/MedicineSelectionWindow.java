package AppGUI;

import Diabet.DiabetWindow;
import MommyMed.MomsWindow;
import Prostuda.SimpleWindow;
import Skin.KozhaWindow;
import Vitamin.VitaminsWindow;
import Database.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MedicineSelectionWindow extends JFrame {
    private String userEmail;
    private int userId;
    private ArrayList<String> selectedMedicines;
    private JPanel categoryPanel;

    public MedicineSelectionWindow(int userId, ArrayList<String> selectedMedicines) {
        this.selectedMedicines = selectedMedicines;
        this.userId = userId;
        this.userEmail = SessionManager.getUserEmail();
        System.out.println("Создано окно app.MedicineSelectionWindow. UserID: " + this.userId);
        DatabaseHelper.printAllUsers();

        // Настройка окна
        setTitle("Выбор лекарств");
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Основной контейнер
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(138, 209, 206));

        // Панель с категориями
        categoryPanel = new JPanel();
        categoryPanel.setLayout(new GridLayout(0, 2, 10, 10));
        categoryPanel.setBackground(new Color(138, 209, 206));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Добавление кнопок категорий с Unicode-эмодзи
        addCategoryButton("\uD83E\uDDEA Диабет", DiabetWindow.class);          // 🩺
        addCategoryButton("\uD83D\uDC76 Малыши и мамы", MomsWindow.class);   // 👶
        addCategoryButton("\uD83E\uDDF4 Для кожи", KozhaWindow.class);       // 🧴
        addCategoryButton("\uD83E\uDD27 Простуда", SimpleWindow.class);      // 🤧
        addCategoryButton("\uD83D\uDC8A Витамины", VitaminsWindow.class);    // 💊

        JScrollPane categoryScrollPane = new JScrollPane(categoryPanel);
        categoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        categoryScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Панель с кнопками управления
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));

        JButton backButton = createStyledButton("Назад", new Color(0, 123, 167), Color.WHITE);
        backButton.addActionListener(e -> {
            SessionManager.clearUser(); // очистка текущей сессии
            dispose();
            new ContactForm(); // возврат к логину
        });

        JButton basketButton = createStyledButton("Корзина", new Color(0, 123, 167), Color.WHITE);
        basketButton.addActionListener(e -> {
            dispose();
            new Basket(selectedMedicines);
        });

        buttonPanel.add(backButton);
        buttonPanel.add(basketButton);
        container.add(categoryScrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Метод для добавления кнопки категории
    private void addCategoryButton(String categoryName, Class<?> categoryWindowClass) {
        JButton categoryButton = new JButton(categoryName);

        // Универсальный шрифт для всех платформ
        categoryButton.setFont(new Font("Dialog", Font.PLAIN, 14));

        categoryButton.setForeground(Color.WHITE);
        categoryButton.setBackground(new Color(0, 123, 167));
        categoryButton.setFocusPainted(false);
        categoryButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        categoryButton.setPreferredSize(new Dimension(140, 40));
        categoryButton.addActionListener(e -> openCategoryWindow(categoryWindowClass));

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(200, 230, 229));
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 90, 140), 2));
        cardPanel.add(categoryButton, BorderLayout.CENTER);

        categoryPanel.add(cardPanel);
    }

    // Метод для открытия окна категории
    private void openCategoryWindow(Class<?> categoryWindowClass) {
        try {
            categoryWindowClass.getDeclaredConstructor(String.class, ArrayList.class).newInstance(userEmail, selectedMedicines);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Метод для стилизованных кнопок
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
}
