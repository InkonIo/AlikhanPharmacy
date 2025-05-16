package Prostuda;

import Database.MedicineDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CitramonWindow extends JFrame {
    private String userEmail;
    private ArrayList<String> selectedMedicines;
    private int count = 0;
    private JLabel countLabel;

    public CitramonWindow(String userEmail, ArrayList<String> selectedMedicines) {
        this.userEmail = userEmail;
        this.selectedMedicines = selectedMedicines;

        setTitle("Цитрамон");
        setBounds(100, 100, 700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        double price = MedicineDatabase.getPrice("Цитрамон");

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(200, 230, 229));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon icon = new ImageIcon("exam/src/main/resources/Images/Prostuda/citramon.png");
        Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setBackground(new Color(200, 230, 229));

        JLabel nameLabel = new JLabel("Цитрамон - " + price + " тг.");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel descLabel = new JLabel("<html>Снимает головную боль.<br>Обладает обезболивающим действием.</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        textPanel.add(nameLabel);
        textPanel.add(descLabel);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        controlPanel.setBackground(new Color(200, 230, 229));

        JPanel countPanel = new JPanel();
        countPanel.setBackground(new Color(200, 230, 229));

        JButton minusButton = createStyledButton("-", new Color(167, 0, 0), Color.WHITE);
        minusButton.setPreferredSize(new Dimension(50, 40));
        minusButton.addActionListener(e -> decreaseCount());

        countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton plusButton = createStyledButton("+", new Color(0, 167, 89), Color.WHITE);
        plusButton.setPreferredSize(new Dimension(50, 40));
        plusButton.addActionListener(e -> increaseCount());

        countPanel.add(minusButton);
        countPanel.add(Box.createHorizontalStrut(10));
        countPanel.add(countLabel);
        countPanel.add(Box.createHorizontalStrut(10));
        countPanel.add(plusButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(200, 230, 229));

        JButton addToBasketButton = createStyledButton("Добавить", new Color(0, 167, 89), Color.WHITE);
        addToBasketButton.setPreferredSize(new Dimension(120, 40));
        addToBasketButton.addActionListener(e -> addToBasket());

        JButton backButton = createStyledButton("Назад", new Color(167, 0, 0), Color.WHITE);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> {
            dispose();
            new SimpleWindow(userEmail, selectedMedicines);
        });

        buttonPanel.add(addToBasketButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(backButton);

        controlPanel.add(countPanel);
        controlPanel.add(buttonPanel);

        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.EAST);

        getContentPane().add(panel);
        setVisible(true);
    }

    private void addToBasket() {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                selectedMedicines.add("Цитрамон");
            }
        }
    }

    private void decreaseCount() {
        if (count > 0) {
            count--;
            countLabel.setText(String.valueOf(count));
        }
    }

    private void increaseCount() {
        count++;
        countLabel.setText(String.valueOf(count));
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return button;
    }
}