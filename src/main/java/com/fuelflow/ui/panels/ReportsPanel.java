package com.fuelflow.ui.panels;

import com.fuelflow.config.Database;
import com.fuelflow.ui.MainFrame.Refreshable;
import com.fuelflow.ui.Theme;
import com.fuelflow.util.UiUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel implements Refreshable {
    private final DefaultTableModel model=new DefaultTableModel();
    private final JTable table=new JTable(model);
    public ReportsPanel(){
        setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,22,22,22));
        JLabel h=new JLabel("Reports & Analytics");h.setFont(Theme.font(24,true));h.setForeground(Theme.TEXT);add(h,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false);
        JPanel actions=UiUtils.card();actions.setLayout(new FlowLayout(FlowLayout.LEFT,10,3));
        JButton stock=UiUtils.button("Fuel stock report"),sales=UiUtils.button("Sales report"),purchases=UiUtils.button("Supplier purchase report"),daily=UiUtils.button("Daily revenue");
        actions.add(stock);actions.add(sales);actions.add(purchases);actions.add(daily);body.add(actions,BorderLayout.NORTH);
        JPanel card=UiUtils.card();card.setLayout(new BorderLayout());table.setRowHeight(27);card.add(new JScrollPane(table));body.add(card);add(body);
        stock.addActionListener(e->load("SELECT * FROM Fuel_Stock_Report"));
        sales.addActionListener(e->load("SELECT * FROM Sales_Details ORDER BY sale_datetime DESC"));
        purchases.addActionListener(e->load("SELECT * FROM Supplier_Purchase_Report ORDER BY purchase_date DESC"));
        daily.addActionListener(e->load("SELECT DATE(sale_datetime) AS sale_day, COUNT(*) AS transactions, SUM(total_amount) AS revenue FROM Fuel_Sale GROUP BY DATE(sale_datetime) ORDER BY sale_day DESC"));
        load("SELECT * FROM Fuel_Stock_Report");
    }
    private void load(String sql){
        try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
            ResultSetMetaData md=r.getMetaData();model.setRowCount(0);model.setColumnCount(0);
            for(int i=1;i<=md.getColumnCount();i++)model.addColumn(md.getColumnLabel(i));
            while(r.next()){Object[] row=new Object[md.getColumnCount()];for(int i=1;i<=row.length;i++)row[i-1]=r.getObject(i);model.addRow(row);}
        }catch(Exception e){UiUtils.error(this,e);}
    }
    public void refresh(){load("SELECT * FROM Fuel_Stock_Report");}
}
