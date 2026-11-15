package com.fuelflow.util;

import com.fuelflow.ui.Theme;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class UiUtils {
    private UiUtils() {}
    public static JLabel label(String text) {
        JLabel l = new JLabel(text); l.setFont(Theme.font(13, true)); l.setForeground(Theme.TEXT); return l;
    }
    public static JTextField field(int columns) {
        JTextField f = new JTextField(columns); f.setFont(Theme.font(13, false));
        f.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(8, 9, 8, 9)));
        return f;
    }
    public static JButton button(String text) {
        JButton b = new JButton(text); b.setFont(Theme.font(12, true));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 15, 9, 15)); b.setBackground(Theme.BUTTON); b.setForeground(Theme.TEXT);
        return b;
    }
    public static JPanel card() {
        JPanel p = new JPanel(); p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(Theme.BORDER), new EmptyBorder(16,16,16,16)));
        return p;
    }
    public static String money(Object value) {
        try { return NumberFormat.getCurrencyInstance(new Locale("en","IN")).format(new BigDecimal(value.toString())); }
        catch (Exception e) { return "₹0.00"; }
    }
    public static void error(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Operation failed", JOptionPane.ERROR_MESSAGE);
    }
}
