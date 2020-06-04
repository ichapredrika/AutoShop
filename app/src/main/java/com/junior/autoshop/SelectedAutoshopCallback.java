package com.junior.autoshop;

import com.junior.autoshop.models.Autoshop;

public interface SelectedAutoshopCallback {
    void selectAutoshop(Autoshop autoshop);
    void deleteAutoshop(Autoshop autoshop);
}
