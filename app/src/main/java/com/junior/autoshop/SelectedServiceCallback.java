package com.junior.autoshop;

import com.junior.autoshop.models.Service;

public interface SelectedServiceCallback {
    void selectService(Service service);

    void deleteService(Service service);

    void addNoteService(Service service);
}
