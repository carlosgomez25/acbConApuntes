package com.company;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException, ParseException {
        Menu menu = new Menu();
        int option;
        DBAccessor dbaccessor = new DBAccessor();
        dbaccessor.init();
        dbaccessor.conn = dbaccessor.getConnection();
        if (dbaccessor.conn != null) {
            option = menu.menuPral();
            while (option > 0 && option < 11) {
                switch (option) {
                    case 1:
                        dbaccessor.mostrarEquips();
                        break;

                    case 2:
                        dbaccessor.mostrarJugador();
                        break;

                    case 3:
                        dbaccessor.crearEquipo();
                        break;

                    case 4:
                        dbaccessor.crearJugador();
                        break;

                    case 5:
                        dbaccessor.crearPartit();
                        break;

                    case 6:
                        dbaccessor.mostrarJugadorSinEquipo();
                        break;

                    case 7:
                        dbaccessor.asignarJugador();
                        break;

                    case 8:
                        dbaccessor.desvincularJugador();
                        break;

                    case 9:
                        dbaccessor.carregaEstadistiques();
                        break;

                    case 10:
                        dbaccessor.sortir();
                        break;

                    default:
                        System.out.println("Introdueixi una de les opcions anteriors");
                        break;

                }
                option = menu.menuPral();
            }
       }
    }

}
