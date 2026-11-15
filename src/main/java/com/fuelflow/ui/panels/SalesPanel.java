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

public class SalesPanel extends JPanel implements Refreshable {
    private final JComboBox<Item> fuel=new JComboBox<>(), employee=new JComboBox<>(), customer=new JComboBox<>();
    private final JTextField qty=UiUtils.field(10), price=UiUtils.field(10);
    private final JComboBox<String> payment=new JComboBox<>(new String[]{"Cash","UPI","Card"});
    private final JLabel total=new JLabel("₹0.00");
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"Sale ID","Fuel","Employee","Quantity","Total","Date"},0);
    public SalesPanel(){
        setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,22,22,22));
        JLabel h=new JLabel("Fuel Sales & Billing");h.setFont(Theme.font(24,true));h.setForeground(Theme.TEXT);add(h,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false);
        JPanel form=UiUtils.card();form.setLayout(new GridLayout(0,4,10,9));
        addField(form,"Fuel",fuel);addField(form,"Employee",employee);addField(form,"Customer (optional)",customer);
        addField(form,"Quantity (litres)",qty);addField(form,"Price per litre",price);addField(form,"Payment method",payment);
        total.setFont(Theme.font(20,true));total.setForeground(Theme.NAVY);addField(form,"Bill total",total);
        JButton calculate=UiUtils.button("Calculate");JButton save=UiUtils.button("Complete sale");
        form.add(calculate);form.add(save);body.add(form,BorderLayout.NORTH);
        JTable table=new JTable(model);table.setRowHeight(26);
        JPanel card=UiUtils.card();card.setLayout(new BorderLayout());card.add(new JScrollPane(table));body.add(card,BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
        calculate.addActionListener(e->calculate());save.addActionListener(e->sell());qty.addActionListener(e->calculate());price.addActionListener(e->calculate());
        refresh();
    }
    private void addField(JPanel p,String label,JComponent c){JPanel x=new JPanel(new BorderLayout(0,5));x.setOpaque(false);x.add(UiUtils.label(label),BorderLayout.NORTH);x.add(c);p.add(x);}
    private void calculate(){try{total.setText(UiUtils.money(Double.parseDouble(qty.getText())*Double.parseDouble(price.getText())));}catch(Exception e){total.setText("₹0.00");}}
    private void loadCombo(JComboBox<Item> box,String sql) throws SQLException{
        box.removeAllItems();
        try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
            while(r.next())box.addItem(new Item(r.getInt(1),r.getString(2)));
        }
    }
    private void sell(){
        Item f=(Item)fuel.getSelectedItem(),emp=(Item)employee.getSelectedItem(),cus=(Item)customer.getSelectedItem();
        if(f==null||emp==null){JOptionPane.showMessageDialog(this,"Add fuel and employee records first.");return;}
        try{
            double q=Double.parseDouble(qty.getText()),p=Double.parseDouble(price.getText());
            if(q<=0||p<0)throw new IllegalArgumentException("Quantity must be positive and price cannot be negative.");
            try(Connection c=Database.getConnection()){
                c.setAutoCommit(false);
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO Fuel_Sale(fuel_id,employee_id,customer_id,quantity,price_per_litre,total_amount) VALUES(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
                    ps.setInt(1,f.id);ps.setInt(2,emp.id);if(cus==null||cus.id==0)ps.setNull(3,Types.INTEGER);else ps.setInt(3,cus.id);
                    ps.setDouble(4,q);ps.setDouble(5,p);ps.setBigDecimal(6,java.math.BigDecimal.valueOf(q*p));ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()){keys.next();int saleId=keys.getInt(1);
                        try(PreparedStatement pay=c.prepareStatement("INSERT INTO Payment(sale_id,payment_method,amount_paid,payment_status) VALUES(?,?,?,'Paid')")){
                            pay.setInt(1,saleId);pay.setString(2,(String)payment.getSelectedItem());pay.setBigDecimal(3,java.math.BigDecimal.valueOf(q*p));pay.executeUpdate();
                        }
                    }
                    c.commit();
                }catch(Exception ex){c.rollback();throw ex;}
            }
            JOptionPane.showMessageDialog(this,"Sale saved and payment recorded. Stock is updated by your MySQL trigger.");
            qty.setText("");calculate();refresh();
        }catch(Exception ex){UiUtils.error(this,ex);}
    }
    public void refresh(){
        try{
            loadCombo(fuel,"SELECT fuel_id,fuel_name FROM Fuel ORDER BY fuel_name");
            loadCombo(employee,"SELECT employee_id,employee_name FROM Employee ORDER BY employee_name");
            customer.removeAllItems();customer.addItem(new Item(0,"Walk-in customer"));
            try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT customer_id,customer_name FROM Customer ORDER BY customer_name")){while(r.next())customer.addItem(new Item(r.getInt(1),r.getString(2)));}
            model.setRowCount(0);
            try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT sale_id,fuel_name,employee_name,quantity,total_amount,sale_datetime FROM Sales_Details ORDER BY sale_datetime DESC LIMIT 200")){
                while(r.next())model.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getBigDecimal(4),UiUtils.money(r.getBigDecimal(5)),r.getTimestamp(6)});
            }
        }catch(Exception e){model.setRowCount(0);model.addRow(new Object[]{"Database error",e.getMessage()});}
    }
    private record Item(int id,String name){public String toString(){return name;}}
}
