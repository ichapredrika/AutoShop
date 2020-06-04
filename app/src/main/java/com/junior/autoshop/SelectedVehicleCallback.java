package com.junior.autoshop;

import com.junior.autoshop.models.Autoshop;
import com.junior.autoshop.models.Vehicle;
import com.junior.autoshop.models.VehicleCustomer;

public interface SelectedVehicleCallback {
    void selectVehicle(VehicleCustomer vehicle);
    void deleteVehicle(VehicleCustomer vehicle);
}
