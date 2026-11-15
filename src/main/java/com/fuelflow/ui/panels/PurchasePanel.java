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

public class PurchasePanel extends JPanel implements Refreshable {
    private final JComboBox<Item> supplier=new JComboBox<>(),fuel=new JComboBox<>(),employee=new JComboBox<>();
    private final JTextField quantity=UiUtils.field(10),cost=UiUtils.field(10);
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Supplier","Fuel","Quantity","Cost/L","Date"},0);
    public PurchasePanel(){
        setLayout(new BorderLayout(0,14));setBackground(Theme.BG);setBorder(new EmptyBorder(22,22,22,22));
        JLabel h=new JLabel("Fuel Purchase Management");h.setFont(Theme.font(24,true));h.setForeground(Theme.TEXT);add(h,BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0,12));body.setOpaque(false);
        JPanel form=UiUtils.card();form.setLayout(new GridLayout(0,3,10,9));
        addField(form,"Supplier",supplier);addField(form,"Fuel",fuel);addField(form,"Recorded by employee",employee);
        addField(form,"Quantity (litres)",quantity);addField(form,"Cost per litre",cost);
        JButton save=UiUtils.button("Record purchase");form.add(save);body.add(form,BorderLayout.NORTH);
        JTable t=new JTable(model);t.setRowHeight(26);JPanel card=UiUtils.card();card.setLayout(new BorderLayout());card.add(new JScrollPane(t));body.add(card);add(body);
        save.addActionListener(e->save());
        refresh();
    }
    private void addField(JPanel p,String l,JComponent c){JPanel x=new JPanel(new BorderLayout(0,5));x.setOpaque(false);x.add(UiUtils.label(l),BorderLayout.NORTH);x.add(c);p.add(x);}
    private void load(JComboBox<Item>b,String sql)throws SQLException{b.removeAllItems();try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){while(r.next())b.addItem(new Item(r.getInt(1),r.getString(2)));}}
    private void save(){
        Item sup=(Item)supplier.getSelectedItem(),f=(Item)fuel.getSelectedItem(),emp=(Item)employee.getSelectedItem();
        if(sup==null||f==null||emp==null){JOptionPane.showMessageDialog(this,"Add supplier, fuel and employee records first.");return;}
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO Fuel_Purchase(supplier_id,fuel_id,employee_id,quantity,cost_per_litre) VALUES(?,?,?,?,?)")){
            double q=Double.parseDouble(quantity.getText()),v=Double.parseDouble(cost.getText());if(q<=0||v<0)throw new IllegalArgumentException("Enter a positive quantity and non-negative cost.");
            p.setInt(1,sup.id);p.setInt(2,f.id);p.setInt(3,emp.id);p.setDouble(4,q);p.setDouble(5,v);p.executeUpdate();
            JOptionPane.showMessageDialog(this,"Purchase recorded. Your MySQL trigger updates fuel stock.");
            quantity.setText("");cost.setText("");refresh();
        }catch(Exception e){UiUtils.error(this,e);}
    }
    public void refresh(){
        try{load(supplier,"SELECT supplier_id,supplier_name FROM Supplier");load(fuel,"SELECT fuel_id,fuel_name FROM Fuel");load(employee,"SELECT employee_id,employee_name FROM Employee");}
        catch(Exception ignored){}
        model.setRowCount(0);
        try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT p.purchase_id,s.supplier_name,f.fuel_name,p.quantity,p.cost_per_litre,p.purchase_date FROM Fuel_Purchase p JOIN Supplier s ON p.supplier_id=s.supplier_id JOIN Fuel f ON p.fuel_id=f.fuel_id ORDER BY p.purchase_date DESC")){
            while(r.next())model.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getBigDecimal(4),r.getBigDecimal(5),r.getTimestamp(6)});
        }catch(Exception e){model.addRow(new Object[]{"DB error",e.getMessage()});}
    }
    private record Item(int id,String name){public String toString(){return name;}}
}
