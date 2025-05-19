

package org.example; // Doğru paket tanımlaması

 // ui.UserUI sınıfını buradan kullanıyoruz
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Tema ayarlanamadı: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            UserUI userUI = new UserUI();
            userUI.setVisible(true); // Kullanıcı arayüzünü görünür yap
        });
    }
}
