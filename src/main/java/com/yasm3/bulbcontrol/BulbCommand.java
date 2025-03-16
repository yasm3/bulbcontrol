package com.yasm3.bulbcontrol;

public class BulbCommand {
    public int id;
    public String method;
    public Object[] params;

    BulbCommand(int id, String method, Object[] params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
}
