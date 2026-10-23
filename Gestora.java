package test;
import java.sql.*;

public class Gestora {

    private Empleado[] arreglo;
    private int cant;

    public Gestora() {
        this.arreglo = new Empleado[0];
        this.cant = 0;
    }

    public void cargar(Connection con) throws Exception {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select count(*) from empleado");
        rs.next();
        this.cant = rs.getInt(1);
        rs.close();
        this.arreglo = new Empleado[this.cant];
        rs = stmt.executeQuery("select * from empleado order by id");
        int i = 0;
        while (rs.next()) {
            this.arreglo[i] = new Empleado(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
            i++;
        }
        rs.close();
        stmt.close();
    }

    private String texto(Empleado e, String campo) {
        String res = "";
        switch (campo) {
            case "id":
                res = "" + e.getId();
                break;
            case "nombre":
                res = e.getNombre();
                break;
            case "cargo":
                res = e.getCargo();
                break;
            case "sueldo":
                res = "" + e.getSueldo();
                break;
            default:
                break;
        }
        return res;
    }

    private boolean esNum(String campo) {
        return campo.equals("id") || campo.equals("sueldo");
    }

    private int comparar(String x, String y, boolean numerico) {
        int r = 0;
        if (numerico) {
            r = Double.compare(Double.parseDouble(x), Double.parseDouble(y));
        } else {
            r = x.compareTo(y);
        }
        return r;
    }

    private boolean cumple(Empleado e, String campo, String op, String valor) {
        boolean ok = false;
        int r = 0;
        if (!op.equals("contiene")) {
            r = comparar(texto(e, campo), valor, esNum(campo));
        }
        switch (op) {
            case "igual":
                ok = r == 0;
                break;
            case "mayor":
                ok = r > 0;
                break;
            case "menor":
                ok = r < 0;
                break;
            case "mayorigual":
                ok = r >= 0;
                break;
            case "menorigual":
                ok = r <= 0;
                break;
            case "distinto":
                ok = r != 0;
                break;
            case "contiene":
                ok = texto(e, campo).indexOf(valor) >= 0;
                break;
            default:
                break;
        }
        return ok;
    }

    public void consultar(String[] campos, String campoCond, String op, String valorCond, String campoOrden, boolean asc, int limite) {
        Empleado[] res = new Empleado[this.cant];
        int n = 0;
        for (int i = 0; i < this.cant; i++) {
            if (campoCond.equals("") || cumple(this.arreglo[i], campoCond, op, valorCond)) {
                res[n] = this.arreglo[i];
                n++;
            }
        }
        if (!campoOrden.equals("")) {
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - 1 - i; j++) {
                    int r = comparar(texto(res[j], campoOrden), texto(res[j + 1], campoOrden), esNum(campoOrden));
                    if (!asc) {
                        r = -r;
                    }
                    if (r > 0) {
                        Empleado aux = res[j];
                        res[j] = res[j + 1];
                        res[j + 1] = aux;
                    }
                }
            }
        }
        int tope = n;
        if (limite > 0 && limite < n) {
            tope = limite;
        }
        for (int i = 0; i < tope; i++) {
            String linea = "";
            for (int k = 0; k < campos.length; k++) {
                linea = linea + texto(res[i], campos[k]) + " ";
            }
            System.out.println(linea);
        }
    }
}