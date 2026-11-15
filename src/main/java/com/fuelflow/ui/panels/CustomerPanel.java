package com.fuelflow.ui.panels;
public class CustomerPanel extends CrudPanel {
    public CustomerPanel() {
        super("Customer Management","Customer","customer_id",
            new String[]{"customer_name","phone","vehicle_number"},
            new String[]{"Customer name","Phone","Vehicle number"});
    }
}
