import java.sql.*;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    private static Connection connection;

    // Метод для подключения к базе данных
    public static Connection connect() {
        if (connection == null || isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Соединение с базой данных установлено.");
            } catch (SQLException e) {
                System.out.println("Ошибка соединения с базой данных");
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Проверка закрытого соединения
    private static boolean isClosed() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // Создание таблиц в базе данных
    public static void createTable() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL);";

        String createMedicinesTable = "CREATE TABLE IF NOT EXISTS medicines ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "price REAL NOT NULL);";

        String createUserMedicinesTable = "CREATE TABLE IF NOT EXISTS user_medicines ("
                + "user_id INTEGER, "
                + "medicine_id INTEGER, "
                + "FOREIGN KEY (user_id) REFERENCES users(id), "
                + "FOREIGN KEY (medicine_id) REFERENCES medicines(id), "
                + "PRIMARY KEY (user_id, medicine_id));";

        try (Statement stmt = connect().createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMedicinesTable);
            stmt.execute(createUserMedicinesTable);
            System.out.println("Все таблицы успешно созданы.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printAllUsers() {
        int userId = getCurrentUserId();
        if (userId == -1) {
            System.out.println("Нет активного пользователя.");
            return;
        }

        String query = "SELECT id, email FROM users WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Email: " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Добавление пользователя
    public static boolean addUser(String email, String password) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Получение ID пользователя по email
    public static int getUserIdByEmail(String email) {
        int userId = -1;
        String query = "SELECT id FROM users WHERE email = ? LIMIT 1";

        try (PreparedStatement pstmt = connect().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
                System.out.println("User ID найден: " + userId);
            } else {
                System.out.println("Пользователь не найден!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    // Получение ID лекарства по названию
    public static int getMedicineIdByName(String name) {
        String sql = "SELECT id FROM medicines WHERE name = ?";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Добавление лекарств для пользователя
    public static boolean addUserMedicines(int userId, List<Integer> selectedMedicinesIds) {
        String sql = "INSERT OR IGNORE INTO user_medicines (user_id, medicine_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            for (Integer medicineId : selectedMedicinesIds) {
                stmt.setInt(1, userId);
                stmt.setInt(2, medicineId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Проверка существования email
    public static boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Если есть результат, email существует
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке email");
            e.printStackTrace();
        }
        return false;
    }

    // Обновление пароля
    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Если обновлена хотя бы 1 строка, пароль изменен
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении пароля");
            e.printStackTrace();
        }
        return false;
    }

    // Проверка на валидность пароля
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    // Проверка пользователя по email и паролю
    public static boolean checkUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connect().prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                setCurrentUserId(rs.getInt("id")); // Сохраняем текущего пользователя
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Установка текущего пользователя
    private static int currentUserId = -1;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    private static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

}
