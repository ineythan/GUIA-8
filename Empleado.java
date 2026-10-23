package test;

public class Empleado {

    private int id;
    private String nombre;
    private String cargo;
    private double sueldo;

    public Empleado(int id, String nombre, String cargo, double sueldo) {
        this.id = id;
        this.nombre = nombre;
        this.cargo = cargo;
        this.sueldo = sueldo;
    }

    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getCargo() {
        return this.cargo;
    }

    public double getSueldo() {
        return this.sueldo;
    }
}