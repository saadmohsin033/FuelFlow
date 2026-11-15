package com.fuelflow.ui;

import com.fuelflow.config.Database;
import com.fuelflow.ui.panels.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, JPanel> panels = new LinkedHashMap<>();
    private final JLabel connection = new JLabel("Checking database...");

    public MainFrame() {
        setTitle("FuelFlow | FuelFlow Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1320, 820);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        build();
        refreshConnection();
    }

    private void build() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Theme.BG);
        shell.add(sidebar(), BorderLayout.WEST);
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Theme.BG);
        right.add(topbar(), BorderLayout.NORTH);
        content.setBackground(Theme.BG);
        addPanel("Dashboard", new DashboardPanel());
        addPanel("Fuel Management", new FuelPanel());
        addPanel("Fuel Sales", new SalesPanel());
        addPanel("Customers", new CustomerPanel());
        addPanel("Employees", new EmployeePanel());
        addPanel("Suppliers", new SupplierPanel());
        addPanel("Fuel Purchases", new PurchasePanel());
        addPanel("Reports", new ReportsPanel());
        right.add(content, BorderLayout.CENTER);
        shell.add(right, BorderLayout.CENTER);
        setContentPane(shell);
        showPanel("Dashboard");
    }

    private JPanel sidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(225, 0));
        side.setBackground(Theme.NAVY);
        side.setBorder(new EmptyBorder(22, 14, 18, 14));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("  FUELFLOW");
        brand.setForeground(Color.WHITE); brand.setFont(Theme.font(21, true));
        side.add(brand); side.add(Box.createVerticalStrut(8));
        JLabel sub = new JLabel("  MANAGEMENT SYSTEM");
        sub.setForeground(Theme.MUTED); sub.setFont(Theme.font(9,true));
        side.add(sub); side.add(Box.createVerticalStrut(35));
        String[] names = {"Dashboard","Fuel Management","Fuel Sales","Customers","Employees","Suppliers","Fuel Purchases","Reports"};
        
for (String name : names) {

    JButton b = new JButton(name);

    // Button size and alignment
    b.setHorizontalAlignment(SwingConstants.LEFT);
    b.setMaximumSize(new Dimension(200, 43));
    b.setPreferredSize(new Dimension(200, 43));
    b.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Button colours
    b.setForeground(Theme.TEXT);
    b.setBackground(Theme.BUTTON);
    b.setFont(Theme.font(13, true));

    // Remove default Swing button appearance
    b.setOpaque(true);
    b.setContentAreaFilled(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);

    // Button spacing
    b.setBorder(new EmptyBorder(11, 13, 11, 8));

    // Mouse cursor
    b.setCursor(
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    );

    // Hover effect
    b.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            b.setBackground(Theme.BORDER);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            b.setBackground(Theme.BUTTON);
        }
    });

    // Existing navigation functionality
    b.addActionListener(e -> showPanel(name));

    side.add(b);
    side.add(Box.createVerticalStrut(7));
}

        side.add(Box.createVerticalGlue());
        JLabel foot = new JLabel("  Java Swing  •  MySQL");
        foot.setForeground(Theme.MUTED); foot.setFont(Theme.font(10,false)); side.add(foot);
        return side;
    }

    private JPanel topbar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(15,22,15,22));
        JLabel title = new JLabel("FuelFlow Operations");
        title.setFont(Theme.font(17,true)); title.setForeground(Theme.TEXT);
        connection.setFont(Theme.font(11,true)); connection.setForeground(Theme.MUTED);
        p.add(title, BorderLayout.WEST); p.add(connection, BorderLayout.EAST);
        return p;
    }

    private void addPanel(String name, JPanel panel) {
        panels.put(name, panel); content.add(panel, name);
    }
    private void showPanel(String name) {
        cards.show(content, name);
        JPanel p = panels.get(name);
        if (p instanceof Refreshable r) r.refresh();
    }
    private void refreshConnection() {
        boolean ok = Database.testConnection();
        connection.setText(ok ? "●  Database connected" : "●  Database not connected");
        connection.setForeground(ok ? new Color(28,145,91) : new Color(190,65,65));
    }
    public interface Refreshable { void refresh(); }
}
