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
import java.util.*;

public class CrudPanel extends JPanel implements Refreshable {
    private final String table, idColumn;
    private final String[] columns, labels;
    private final JTable grid = new JTable();
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTextField[] inputs;
    private int selectedId = -1;

    public CrudPanel(String title,String table,String idColumn,String[] columns,String[] labels) {
        this.table=table; this.idColumn=idColumn; this.columns=columns; this.labels=labels;
        this.inputs=new JTextField[columns.length];
        setLayout(new BorderLayout(0,14)); setBackground(Theme.BG); setBorder(new EmptyBorder(22,22,22,22));
        JLabel heading=new JLabel(title); heading.setFont(Theme.font(24,true)); heading.setForeground(Theme.TEXT); add(heading,BorderLayout.NORTH);
        JPanel center=new JPanel(new BorderLayout(0,12)); center.setOpaque(false);
        JPanel form=UiUtils.card(); form.setLayout(new GridBagLayout());
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(5,7,5,7); g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        for(int i=0;i<columns.length;i++){
            g.gridx=i%3; g.gridy=(i/3)*2; form.add(UiUtils.label(labels[i]),g);
            inputs[i]=UiUtils.field(12); g.gridy++; form.add(inputs[i],g);
        }
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4)); actions.setOpaque(false);
        JButton add=UiUtils.button("Add"); JButton update=UiUtils.button("Update selected"); JButton delete=UiUtils.button("Delete"); JButton clear=UiUtils.button("Clear");
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        g.gridx=0;g.gridy=((columns.length+2)/3)*2;g.gridwidth=3;form.add(actions,g);
        center.add(form,BorderLayout.NORTH);
        grid.setModel(model); grid.setRowHeight(27); grid.setFont(Theme.font(12,false)); grid.getTableHeader().setFont(Theme.font(12,true));
        JPanel tableCard=UiUtils.card(); tableCard.setLayout(new BorderLayout()); tableCard.add(new JScrollPane(grid),BorderLayout.CENTER);
        center.add(tableCard,BorderLayout.CENTER); add(center,BorderLayout.CENTER);
        add.addActionListener(e->insert()); update.addActionListener(e->update()); delete.addActionListener(e->delete()); clear.addActionListener(e->clear());
        grid.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()) loadSelected();});
        refresh();
    }
    private void insert(){
        String sql="INSERT INTO "+table+" ("+String.join(",",columns)+") VALUES ("+String.join(",",Collections.nCopies(columns.length,"?"))+")";
        execute(sql);
    }
    private void update(){
        if(selectedId<0){JOptionPane.showMessageDialog(this,"Select a row first.");return;}
        StringJoiner set=new StringJoiner(","); for(String c:columns)set.add(c+"=?");
        execute("UPDATE "+table+" SET "+set+" WHERE "+idColumn+"=?",true);
    }
    private void execute(String sql){execute(sql,false);}
    private void execute(String sql,boolean updating){
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            for(int i=0;i<inputs.length;i++)p.setString(i+1,inputs[i].getText().trim());
            if(updating)p.setInt(inputs.length+1,selectedId);
            p.executeUpdate(); JOptionPane.showMessageDialog(this,"Saved successfully."); clear(); refresh();
        }catch(Exception ex){UiUtils.error(this,ex);}
    }
    private void delete(){
        if(selectedId<0){JOptionPane.showMessageDialog(this,"Select a row first.");return;}
        if(JOptionPane.showConfirmDialog(this,"Delete selected record?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM "+table+" WHERE "+idColumn+"=?")){
            p.setInt(1,selectedId);p.executeUpdate();clear();refresh();
        }catch(Exception ex){UiUtils.error(this,ex);}
    }
    private void loadSelected(){
        int row=grid.getSelectedRow(); if(row<0)return;
        selectedId=Integer.parseInt(model.getValueAt(row,0).toString());
        for(int i=0;i<columns.length;i++)inputs[i].setText(Objects.toString(model.getValueAt(row,i+1),""));
    }
    private void clear(){selectedId=-1;for(JTextField f:inputs)f.setText("");grid.clearSelection();}
    public void refresh(){
        try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT "+idColumn+","+String.join(",",columns)+" FROM "+table+" ORDER BY "+idColumn+" DESC")){
            model.setRowCount(0);model.setColumnCount(0);model.addColumn(idColumn);for(String l:labels)model.addColumn(l);
            while(rs.next()){Object[] row=new Object[columns.length+1];for(int i=0;i<row.length;i++)row[i]=rs.getObject(i+1);model.addRow(row);}
        }catch(Exception e){model.setRowCount(0);model.setColumnCount(0);model.addColumn("Database error");model.addRow(new Object[]{e.getMessage()});}
    }
}
