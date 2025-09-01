package edu.universidad.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Bus {
    private static final IntegerProperty cursosVersion = new SimpleIntegerProperty(0);
    private Bus() {}
    public static IntegerProperty cursosVersionProperty(){ return cursosVersion; }
    public static int getCursosVersion(){ return cursosVersion.get(); }
    public static void bumpCursos(){ cursosVersion.set(cursosVersion.get()+1); }
}