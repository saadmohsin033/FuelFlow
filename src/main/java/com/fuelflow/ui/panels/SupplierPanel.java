package com.fuelflow.ui.panels;
public class SupplierPanel extends CrudPanel {
    public SupplierPanel() {
        super("Supplier Management","Supplier","supplier_id",
            new String[]{"supplier_name","phone","address"},
            new String[]{"Supplier name","Phone","Address"});
    }
}
