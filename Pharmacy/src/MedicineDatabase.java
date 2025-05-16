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
    }

    public static double getPrice(String medicine) {
        return medicinePrices.getOrDefault(medicine, 0.0);
    }

    public static void setPrice(String medicine, double price) {
        medicinePrices.put(medicine, price);
    }
}
