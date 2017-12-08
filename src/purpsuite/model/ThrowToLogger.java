package purpsuite.model;

import purpsuite.controller.MainPane;

import java.io.IOException;
import java.util.Date;

public abstract class ThrowToLogger {
    public static void Throw(Exception e){
        try {
            MainPane.getInstance().AddLog(new Date(), e.toString());
            if (e instanceof NullPointerException) {
                e.printStackTrace();
            }
        } catch (IOException E){
            E.printStackTrace();
        }
    }
}
