import AppGUI.ContactForm;
import Database.DatabaseHelper;

public static void main(String[] args) {
    DatabaseHelper.connect();
    DatabaseHelper.createTable();
    DatabaseHelper.insertSampleMedicines();
    new ContactForm();
}
