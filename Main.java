package com.company;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
	// write your code here
        System.out.println("Hello world");

        String selection = "R";
        Scanner input = new Scanner(System.in);

        /***************************************************/
        /*while(!selection.equals("Q")){
            System.out.println("Veuillez faire votre choix");
            System.out.println("-------------------------\n");
            System.out.println("(R)echercher");
            System.out.println("(I)nsérer");
            System.out.println("(M)ettre à jour");
            System.out.println("(E)ffacer");
            System.out.println("(Q)uitter");

            selection = input.next();

            System.out.println("Votre choix est : " + selection);
        }*/
        Class.forName("com.mysql.jdbc.Driver");

        String DBurl = "jdbc:mysql://localhost:3306/restaurant";
        String username = "root" ;
        String password = "" ;

        Connection connection = DriverManager.getConnection(DBurl, username, password) ;


        String sql = "INSERT INTO `contient` (`numcom`, `numplat`, `quantite`) VALUES ('10', '11', '12');" ;
        Statement statement = connection.createStatement();

        int rows = statement.executeUpdate(sql) ;
    }
}
