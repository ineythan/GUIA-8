package test;
import java.sql.*;

public class Actividad1 {

    public static void mostrar(Connection con) throws Exception {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from trabajador order by id");
        while (rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
        }
        rs.close();
        stmt.close();
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:laboratorio.db");
            Statement stmt = con.createStatement();
            stmt.execute("create table if not exists trabajador (id integer primary key, nombre text, edad integer)");
            stmt.execute("delete from trabajador");

            stmt.executeUpdate("insert into trabajador values (1, 'harrison diaz luque', 25)");
            stmt.executeUpdate("insert into trabajador values (2, 'yoseph flores durand', 30)");
            stmt.executeUpdate("update trabajador set edad = 26 where id = 1");
            stmt.executeUpdate("delete from trabajador where id = 2");
            System.out.println("resultado con statement");
            mostrar(con);

            String[] nomWorker = {"jaccob", "randalph"};
            int[] edadVal = {22, 27};
            PreparedStatement psIns = con.prepareStatement("insert into trabajador values (?, ?, ?)");
            for (int i = 0; i < nomWorker.length; i++) {
                psIns.setInt(1, i + 3);
                psIns.setString(2, nomWorker[i]);
                psIns.setInt(3, edadVal[i]);
                psIns.executeUpdate();
            }
            PreparedStatement psAct = con.prepareStatement("update trabajador set edad = ? where id = ?");
            psAct.setInt(1, 28);
            psAct.setInt(2, 4);
            psAct.executeUpdate();
            PreparedStatement psBor = con.prepareStatement("delete from trabajador where id = ?");
            psBor.setInt(1, 3);
            psBor.executeUpdate();
            System.out.println("resultado con preparedstatement");
            mostrar(con);

            con.setAutoCommit(false);
            stmt.executeUpdate("insert into trabajador values (5, 'neythan', 20)");
            stmt.executeUpdate("insert into trabajador values (6, 'ethan', 19)");
            con.commit();
            stmt.executeUpdate("insert into trabajador values (7, 'nyevm', 24)");
            con.rollback();
            System.out.println("resultado con transacciones");
            mostrar(con);

            con.close();
        } catch (Exception e) {
            System.out.println("ocurrio un error en la operacion");
        }
    }
}