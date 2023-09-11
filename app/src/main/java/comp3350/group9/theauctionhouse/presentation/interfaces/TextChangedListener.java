package comp3350.group9.theauctionhouse.presentation.interfaces;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public abstract class TextChangedListener implements TextWatcher {
    private final EditText textEdit;

    public TextChangedListener(EditText target) {
        this.textEdit = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        this.onTextChanged(textEdit, s);
    }

    public abstract void onTextChanged(EditText t, Editable s);
}
