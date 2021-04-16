package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("Hello world");

        String selection = "R";
        Scanner input = new Scanner(System.in);

        /***************************************************/
        while(!selection.equals("Q")){
            System.out.println("Veuillez faire votre choix");
            System.out.println("-------------------------\n");
            System.out.println("(R)echercher");
            System.out.println("(I)nsérer");
            System.out.println("(M)ettre à jour");
            System.out.println("(E)ffacer");
            System.out.println("(Q)uitter");

            selection = input.next();

            System.out.println("Votre choix est : " + selection);
        }
    }
}
