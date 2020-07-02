package com.junior.autoshop;

import com.junior.autoshop.models.Autoshop;

public interface FavoriteAutoshopCallback {
    void favoriteAutoshop(Autoshop autoshop, String favId);
    void unfavoriteAutoshop(Autoshop autoshop);
}
