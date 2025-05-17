package Database;

import java.sql.*;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    private static Connection connection;

    // Подключение к базе данных (синглтон)
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

    // Проверка закрытия соединения
    private static boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // Создание таблиц users, medicines, user_medicines
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

    public static void insertSampleMedicines() {
        String sql = "INSERT OR IGNORE INTO medicines (id, name, price) VALUES (?, ?, ?)";

        Object[][] medicines = {
                {1, "Цитрамон", 500},
                {2, "Парацетамол", 300},
                {3, "Ибупрофен", 400},
                {4, "Аквамарис", 800},
                {5, "Грипфорен", 550},
                {6, "Колдрекс", 900},
                {7, "Фервекс", 600}
        };

        try (Connection conn = connect();  // сохраняем и сразу используем try-with-resources
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Object[] med : medicines) {
                stmt.setInt(1, (Integer) med[0]); // id
                stmt.setString(2, (String) med[1]); // название
                stmt.setDouble(3, (Integer) med[2]); // цена
                stmt.addBatch(); // добавляем в партию
            }

            stmt.executeBatch();
            System.out.println("Лекарства добавлены (если отсутствовали).");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Вывод информации о текущем пользователе (по ID)
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

    // Получение ID лекарства по названию (без учета регистра)
    public static int getMedicineIdByName(String name) {
        String sql = "SELECT id FROM medicines WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addUserMedicines(int userId, List<Integer> selectedMedicinesIds) {
        String sql = "INSERT OR IGNORE INTO user_medicines (user_id, medicine_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);  // отключаем авто-коммит, начинаем транзакцию

            stmt = conn.prepareStatement(sql);
            for (Integer medicineId : selectedMedicinesIds) {
                stmt.setInt(1, userId);
                stmt.setInt(2, medicineId);
                stmt.addBatch();
            }
            stmt.executeBatch();

            conn.commit();  // коммитим транзакцию

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();  // при ошибке откатываем изменения
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Закрываем statement
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Восстанавливаем авто-коммит в true
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    // Проверка существования email
    public static boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке email");
            e.printStackTrace();
        }
        return false;
    }

    // Обновление пароля пользователя по email
    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении пароля");
            e.printStackTrace();
        }
        return false;
    }

    // Проверка валидности пароля (минимум 6 символов, хотя бы одна заглавная, цифра и спецсимвол)
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        String passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }

    // Проверка пользователя по email и паролю (логин)
    public static boolean checkUser(String email, String password) {
        String query = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connect().prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                setCurrentUserId(rs.getInt("id"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Текущий пользователь (статично хранится в памяти)
    private static int currentUserId = -1;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    private static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }
}
