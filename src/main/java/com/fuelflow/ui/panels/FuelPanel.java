package com.fuelflow.ui.panels;
public class FuelPanel extends CrudPanel {
    public FuelPanel() {
        super("Fuel Management","Fuel","fuel_id",
            new String[]{"fuel_name","unit_price","current_stock","capacity"},
            new String[]{"Fuel name","Unit price (₹/L)","Current stock (L)","Capacity (L)"});
    }
}
