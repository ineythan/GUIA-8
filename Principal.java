package test;
import java.sql.*;
import java.util.*;

public class Principal {

    public static Scanner teclado = new Scanner(System.in);
    public static String claveVal = "1234";

    public static String leer(String msg) {
        System.out.print(msg + " ");
        return teclado.nextLine();
    }

    public static void mostrar(Connection con) throws Exception {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from empleado order by id");
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getDouble(4));
        }
        rs.close();
        stmt.close();
    }

    public static void ingresar(Connection con) throws Exception {
        PreparedStatement ps = con.prepareStatement("insert into empleado values (?, ?, ?, ?)");
        ps.setInt(1, Integer.parseInt(leer("id")));
        ps.setString(2, leer("nombre"));
        ps.setString(3, leer("cargo"));
        ps.setDouble(4, Double.parseDouble(leer("sueldo")));
        ps.executeUpdate();
        ps.close();
    }

    public static void actualizar(Connection con) throws Exception {
        PreparedStatement ps = con.prepareStatement("update empleado set nombre = ?, cargo = ?, sueldo = ? where id = ?");
        ps.setInt(4, Integer.parseInt(leer("id del empleado")));
        ps.setString(1, leer("nuevo nombre"));
        ps.setString(2, leer("nuevo cargo"));
        ps.setDouble(3, Double.parseDouble(leer("nuevo sueldo")));
        ps.executeUpdate();
        ps.close();
    }

    public static void borrar(Connection con) throws Exception {
        PreparedStatement ps = con.prepareStatement("delete from empleado where id = ?");
        ps.setInt(1, Integer.parseInt(leer("id del empleado")));
        ps.executeUpdate();
        ps.close();
    }

    public static void confirmar(Connection con) throws Exception {
        if (leer("clave para confirmar").equals(claveVal)) {
            con.commit();
            System.out.println("cambios confirmados");
        } else {
            con.rollback();
            System.out.println("clave incorrecta cambios revertidos");
        }
    }

    public static void consultar(Gestora gestora) {
        String entrada = leer("campos a mostrar id nombre cargo sueldo separados por espacio o todos");
        String[] campos = entrada.split(" ");
        if (entrada.equals("todos")) {
            campos = new String[] {"id", "nombre", "cargo", "sueldo"};
        }
        String campoCond = leer("campo de la condicion o enter si no hay");
        String op = "";
        String valorCond = "";
        if (!campoCond.equals("")) {
            op = leer("operador igual mayor menor mayorigual menorigual distinto o contiene");
            valorCond = leer("valor a comparar");
        }
        String campoOrden = leer("campo para ordenar o enter si no hay");
        boolean asc = true;
        if (!campoOrden.equals("")) {
            asc = leer("orden asc o desc").equals("asc");
        }
        int limite = Integer.parseInt(leer("limite de registros o 0 para todos"));
        gestora.consultar(campos, campoCond, op, valorCond, campoOrden, asc, limite);
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:empresa.db");
            Statement stmt = con.createStatement();
            stmt.execute("create table if not exists empleado (id integer primary key, nombre text, cargo text, sueldo real)");
            stmt.close();
            con.setAutoCommit(false);
            Gestora gestora = new Gestora();

            boolean continuar = true;
            while (continuar) {
                System.out.println("\n1 ingresar\n2 borrar\n3 actualizar\n4 mostrar\n5 consultar\n6 salir");
                try {
                    int opc = Integer.parseInt(leer("opcion"));
                    switch (opc) {
                        case 1:
                            ingresar(con);
                            confirmar(con);
                            break;
                        case 2:
                            borrar(con);
                            confirmar(con);
                            break;
                        case 3:
                            actualizar(con);
                            confirmar(con);
                            break;
                        case 4:
                            mostrar(con);
                            break;
                        case 5:
                            gestora.cargar(con);
                            consultar(gestora);
                            break;
                        case 6:
                            continuar = false;
                            break;
                        default:
                            System.out.println("opcion no valida");
                            break;
                    }
                } catch (Exception e) {
                    con.rollback();
                    System.out.println("ocurrio un error y se revirtieron los cambios");
                }
            }
            con.close();
        } catch (Exception e) {
            System.out.println("error de conexion con la base de datos");
        }
    }
}