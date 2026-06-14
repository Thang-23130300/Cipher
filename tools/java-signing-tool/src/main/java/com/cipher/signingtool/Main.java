package com.cipher.signingtool;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use Swing default look and feel when the system one is unavailable.
            }

            SigningToolFrame frame = new SigningToolFrame();
            frame.setVisible(true);
        });
    }
}
