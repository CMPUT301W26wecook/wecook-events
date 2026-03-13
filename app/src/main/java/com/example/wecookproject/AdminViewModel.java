package com.example.wecookproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.wecookproject.model.Event;
import com.example.wecookproject.model.User;

/**
 * AdminViewModel facilitates data sharing between different administrative fragments.
 */
public class AdminViewModel extends ViewModel {
    /** 
     * Holds the currently selected object.
     */
    private final MutableLiveData<Object> selectedItem = new MutableLiveData<>();

    /**
     * Sets the currently selected item.
     * 
     * @param item The object to be selected.
     */
    public void selectItem(Object item) {
        selectedItem.setValue(item);
    }

    /**
     * Select a User object.
     * 
     * @param user The User to select.
     */
    public void selectUser(User user) {
        selectItem(user);
    }

    /**
     * Select an Event object.
     * 
     * @param event The Event to select.
     */
    public void selectEvent(Event event) {
        selectItem(event);
    }

    /**
     * Returns the currently selected User.
     * 
     * @return LiveData containing the selected User or null if no User is selected.
     */
    public LiveData<User> getSelectedUser() {
        return Transformations.map(selectedItem, item -> {
            if (item instanceof User) {
                return (User) item;
            }
            return null;
        });
    }

    /**
     * Returns the currently selected Event.
     * 
     * @return LiveData containing the selected Event or null if no Event is selected.
     */
    public LiveData<Event> getSelectedEvent() {
        return Transformations.map(selectedItem, item -> {
            if (item instanceof Event) {
                return (Event) item;
            }
            return null;
        });
    }
}
