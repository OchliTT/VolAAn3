package com.example.helpmyplsnaxyu;
import android.content.DialogInterface;
import android.view.View;

public interface DialogCloseListener {
    void onClick(View v);

    public void handleDialogClose(DialogInterface dialog);
}
