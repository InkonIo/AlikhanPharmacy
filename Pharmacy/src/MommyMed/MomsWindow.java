package MommyMed;

import AppGUI.Basket;
import AppGUI.MedicineSelectionWindow;
import Database.DatabaseHelper;
import Database.MedicineDatabase;
import Vitamin.AscorbinWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MomsWindow extends JFrame {
    private String userEmail;
    private ArrayList<String> selectedMedicines;
    private JPanel medicinePanel;
    private JScrollPane medicineScrollPane;
    private JTextField searchField;
    private List<JPanel> medicineCards;

    public MomsWindow(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;
        this.medicineCards = new ArrayList<>();

        setTitle("Малыши и мамы");
        setBounds(100, 100, 950, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(138, 209, 206));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField("Поиск...");
        searchField.setForeground(Color.GRAY);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterMedicines(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterMedicines(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterMedicines(); }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Поиск...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Поиск...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);

        medicinePanel = new JPanel(new GridLayout(0, 1, 10, 10));
        medicinePanel.setBackground(new Color(138, 209, 206));
        medicinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Добавление лекарств для категории "Малыши и мамы"
        addMedicineCard("Детский Панадол", "Обезболивающее и жаропонижающее для малышей.", "exam/src/main/resources/Images/Moms/panadol.jpg", Panadol.class);
        addMedicineCard("Бепантен", "Мазь для ухода за детской кожей и при раздражении.", "exam/src/main/resources/Images/Moms/bepanten.jpg", Bepanten.class);
        addMedicineCard("Эспумизан Бэби", "Эффективное средство от коликов и газиков у младенцев.", "exam/src/main/resources/Images/Moms/espumizan.jpg", Espumizan.class);

        medicineScrollPane = new JScrollPane(medicinePanel);
        medicineScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        medicineScrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(138, 209, 206));

        JButton backButton = createStyledButton("Назад", new Color(0, 123, 167), Color.WHITE);
        backButton.addActionListener(e -> {
            int userId = DatabaseHelper.getUserIdByEmail(userEmail);
            if (userId != -1) {
                dispose();
                new MedicineSelectionWindow(userId, selectedMedicines);
            } else {
                JOptionPane.showMessageDialog(null, "Ошибка: ID пользователя не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton basketButton = createStyledButton("Корзина", new Color(0, 123, 167), Color.WHITE);
        basketButton.addActionListener(e -> {
            dispose();
            new Basket(selectedMedicines);
        });

        buttonPanel.add(backButton);
        buttonPanel.add(basketButton);

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(medicineScrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Обновление цен каждые 5 секунд
        Timer timer = new Timer(5000, e -> updatePrices());
        timer.start();
    }

    private void addMedicineCard(String name, String description, String imagePath, Class<?> medicineWindowClass) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(200, 230, 229));
        card.setBorder(BorderFactory.createLineBorder(new Color(0, 90, 140), 2));
        card.setPreferredSize(new Dimension(300, 100));

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        double price = MedicineDatabase.getPrice(name);
        JLabel priceLabel = new JLabel("Цена: " + price + " KZT");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(200, 230, 229));
        textPanel.add(nameLabel, BorderLayout.NORTH);
        textPanel.add(priceLabel, BorderLayout.SOUTH);

        JButton selectButton = createStyledButton("Добавить", new Color(0, 123, 167), Color.WHITE);
        selectButton.addActionListener(e -> {
            selectedMedicines.add(name);
            JOptionPane.showMessageDialog(this, name + " добавлен в корзину.", "Добавлено", JOptionPane.INFORMATION_MESSAGE);
        });

        card.add(imageLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(selectButton, BorderLayout.EAST);

        medicineCards.add(card);
        medicinePanel.add(card);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    private void filterMedicines() {
        String query = searchField.getText().trim().toLowerCase();
        medicinePanel.removeAll();

        for (JPanel card : medicineCards) {
            JLabel nameLabel = (JLabel) ((JPanel) card.getComponent(1)).getComponent(0);
            if (nameLabel.getText().toLowerCase().contains(query)) {
                medicinePanel.add(card);
            }
        }
        medicinePanel.revalidate();
        medicinePanel.repaint();
    }

    public void updatePrices() {
        for (JPanel card : medicineCards) {
            JPanel textPanel = (JPanel) card.getComponent(1);
            JLabel nameLabel = (JLabel) textPanel.getComponent(0);
            JLabel priceLabel = (JLabel) textPanel.getComponent(1);

            String medicineName = nameLabel.getText();
            double newPrice = MedicineDatabase.getPrice(medicineName);
            priceLabel.setText("Цена: " + newPrice + " KZT");
        }
        medicinePanel.revalidate();
        medicinePanel.repaint();
    }
}
