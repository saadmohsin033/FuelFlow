
package com.fuelflow.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;

public class Theme {

    // Main colours
    public static final Color NAVY =
            new Color(45, 55, 72);

    public static final Color NAVY_LIGHT =
            new Color(160, 174, 192);

    public static final Color BLUE =
            new Color(45, 55, 72);

    public static final Color BUTTON =
            Color.WHITE;

    public static final Color BACKGROUND =
            new Color(245, 247, 250);

    public static final Color BG = BACKGROUND;

    public static final Color BORDER =
            new Color(160, 174, 192);

    public static final Color WHITE =
            Color.WHITE;

    public static final Color TEXT =
            new Color(45, 55, 72);

    public static final Color MUTED =
            new Color(160, 174, 192);

    public static final Color GREEN =
            new Color(22, 163, 74);

    public static final Color RED =
            new Color(220, 38, 38);

    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 24);

    public static final Font LABEL_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    public static Font font(int size, boolean bold) {
        return new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size);
    }

    // Sidebar navigation buttons
    public static void styleNavButton(JButton button) {
        button.setBackground(BUTTON);
        button.setForeground(TEXT);
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorder(
            BorderFactory.createEmptyBorder(12, 16, 12, 10)
        );
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(
            new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );
    }

    // Blue primary button
    public static void stylePrimaryButton(JButton button) {
        styleActionButton(button, BUTTON);
    }

    // Green update button
    public static void styleUpdateButton(JButton button) {
        styleActionButton(button, BUTTON);
    }

    // Red delete button
    public static void styleDeleteButton(JButton button) {
        styleActionButton(button, BUTTON);
    }

    private static void styleActionButton(
            JButton button, Color color) {

        button.setBackground(color);
        button.setForeground(TEXT);
        button.setFont(LABEL_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        );
        button.setCursor(
            new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );
    }

    // Text fields
    public static void styleTextField(JTextField field) {
        field.setBackground(WHITE);
        field.setForeground(TEXT);
        field.setCaretColor(BLUE);
        field.setFont(NORMAL_FONT);
        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    BORDER
                ),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );
    }

    // Combo boxes
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(WHITE);
        combo.setForeground(TEXT);
        combo.setFont(NORMAL_FONT);
    }
}
