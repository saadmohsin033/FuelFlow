package com.fuelflow.ui.panels;

import com.fuelflow.config.Database;
import com.fuelflow.ui.MainFrame.Refreshable;
import com.fuelflow.ui.Theme;
import com.fuelflow.util.UiUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel implements Refreshable {
    private final JLabel fuels = new JLabel("—"), staff = new JLabel("—"), sales = new JLabel("—"), revenue = new JLabel("—");
    private final JTextArea recent = new JTextArea();
    public DashboardPanel() {
        setLayout(new BorderLayout(0,18)); setBackground(Theme.BG); setBorder(new EmptyBorder(24,24,24,24));
        JLabel heading = new JLabel("Dashboard"); heading.setFont(Theme.font(25,true)); heading.setForeground(Theme.TEXT);
        JLabel hint = new JLabel("Live overview from your Aiven MySQL database"); hint.setForeground(Theme.MUTED);
        JPanel head = new JPanel(new GridLayout(2,1,0,4)); head.setOpaque(false); head.add(heading); head.add(hint);
        add(head,BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0,18)); body.setOpaque(false);
        JPanel grid = new JPanel(new GridLayout(1,4,14,0)); grid.setOpaque(false);
        grid.add(stat("Fuel types",fuels)); grid.add(stat("Employees",staff)); grid.add(stat("Today's sales",sales)); grid.add(stat("Today's revenue",revenue));
        body.add(grid,BorderLayout.NORTH);
        JPanel recentCard = UiUtils.card(); recentCard.setLayout(new BorderLayout(0,10));
        JLabel t = UiUtils.label("Recent sales"); recentCard.add(t,BorderLayout.NORTH);
        recent.setEditable(false); recent.setFont(Theme.font(12,false)); recent.setForeground(Theme.TEXT);
        recentCard.add(new JScrollPane(recent),BorderLayout.CENTER); body.add(recentCard,BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
        refresh();
    }
    private JPanel stat(String title, JLabel value) {
        JPanel p=UiUtils.card(); p.setLayout(new BorderLayout(0,14));
        JLabel l=new JLabel(title); l.setForeground(Theme.MUTED); l.setFont(Theme.font(12,true));
        value.setFont(Theme.font(25,true)); value.setForeground(Theme.NAVY);
        p.add(l,BorderLayout.NORTH); p.add(value,BorderLayout.CENTER); return p;
    }
    public void refresh() {
        try(Connection c=Database.getConnection()) {
            fuels.setText(scalar(c,"SELECT COUNT(*) FROM Fuel"));
            staff.setText(scalar(c,"SELECT COUNT(*) FROM Employee"));
            sales.setText(scalar(c,"SELECT COUNT(*) FROM Fuel_Sale WHERE DATE(sale_datetime)=CURDATE()"));
            revenue.setText(UiUtils.money(scalar(c,"SELECT COALESCE(SUM(total_amount),0) FROM Fuel_Sale WHERE DATE(sale_datetime)=CURDATE()")));
            StringBuilder s=new StringBuilder();
            try(Statement st=c.createStatement(); ResultSet rs=st.executeQuery("SELECT sale_id,fuel_name,quantity,total_amount,sale_datetime FROM Sales_Details ORDER BY sale_datetime DESC LIMIT 10")) {
                while(rs.next()) s.append("#").append(rs.getInt(1)).append("   ").append(rs.getString(2))
                    .append("   ").append(rs.getBigDecimal(3)).append(" L   ")
                    .append(UiUtils.money(rs.getBigDecimal(4))).append("   ").append(rs.getTimestamp(5)).append("\n");
            }
            recent.setText(s.length()==0?"No sales recorded yet.":s.toString());
        } catch(Exception e) {
            fuels.setText("—"); staff.setText("—"); sales.setText("—"); revenue.setText("—");
            recent.setText("Database unavailable. Check src/main/resources/db.properties and Aiven network access.\n"+e.getMessage());
        }
    }
    private String scalar(Connection c,String sql) throws SQLException {
        try(Statement st=c.createStatement();ResultSet rs=st.executeQuery(sql)){rs.next();return rs.getString(1);}
    }
}
