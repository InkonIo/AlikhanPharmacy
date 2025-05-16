package Database;

import java.util.HashMap;
import java.util.Map;

public class MedicineDatabase {
    private static final Map<String, Double> medicinePrices = new HashMap<>();

    static {
        // Пример данных
        medicinePrices.put("Цитрамон", 500.0);
        medicinePrices.put("Парацетамол", 300.0);
        medicinePrices.put("Ибупрофен", 400.0);
        medicinePrices.put("Аквамарис", 800.0);
        medicinePrices.put("Грипфорен", 550.0);
        medicinePrices.put("Колдрекс", 900.0);
        medicinePrices.put("Фервекс", 600.0);
        medicinePrices.put("Ретинол", 700.0);
        medicinePrices.put("Тиамин", 900.0);
        medicinePrices.put("Аскорбинка", 300.0);
        medicinePrices.put("Эспумизан", 1100.0);
        medicinePrices.put("Бепантен", 1900.0);
        medicinePrices.put("Панадол Бэби", 900.0);
    }

    public static Map<String, Double> getAllMedicines() {
        return new HashMap<>(medicinePrices);
    }

    public static double getPrice(String medicine) {
        return medicinePrices.getOrDefault(medicine, 0.0);
    }

    public static void setPrice(String medicine, double price) {
        medicinePrices.put(medicine, price);
    }
}
