/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p04.sudoku;

import auxiliares.Auxiliares;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import historiales.Historicals;
import java.io.BufferedWriter;
import java.io.FileWriter;
import sudokus.Sudokus;
import sudokus.Sudokus.Sudoku;
import usuarios.Users;
import usuarios.Users.User;

/**
 *
 * @author Borja S
 */
public class P04Sudoku {

    //Rutas
    static String rutaSudokusTXT = System.getProperty("user.dir") + File.separator + "sudokus.txt";
    static String rutaSudokusXSD = System.getProperty("user.dir") + File.separator + "sudokus.xsd";
    static String rutaSudokusXML = System.getProperty("user.dir") + File.separator + "sudokus.xml";
    static String rutaUsuariosXSD = System.getProperty("user.dir") + File.separator + "users.xsd";
    static String rutaUsuariosXML = System.getProperty("user.dir") + File.separator + "usuarios.xml";
    static String rutaHistorialesXML = System.getProperty("user.dir") + File.separator + "historicals.xml";
    static String rutaHistorialesXSD = System.getProperty("user.dir") + File.separator + "historicals.xsd";

    //Arrays
    static Sudokus sudokus = new Sudokus();
    static Historicals historiales = new Historicals();
    static Users usuarios = new Users();

    //HashMaps - ¿NECESARIOS?
    static HashMap<Integer, Sudokus.Sudoku> sudokusHM;
    static HashMap<Integer, Historicals.Historical> historialesHM;
    static HashMap<Integer, Users.User> usuariosHM;

    //Auxiliares
    static User usuarioActual;
    static Sudoku sudokuActual;

    public static void main(String[] args) {
        // Cargamos los List con los datos de los XML
        cargarTxtSudokus();
        unmarshallearHistoriales();
        unmarshallearUsuarios();

        //DELETE - mostrar usuarios
        for (User u : usuarios.getUser()) {
            System.out.println("--- USUARIO ----");
            System.out.println("Nombre de usuario = " + u.getName());
            System.out.println("Contraseña = " + u.getPassword());
        }

        int mainOption = -1;

        while (mainOption != 0) {
            showMainMenu();
            mainOption = Auxiliares.pedirNumeroRango("OPCION : ", 0, 3);

            switch (mainOption) {
                case 1:
                    System.out.println("- REGISTRAR USUARIO -");
                    registrarUsuario();
                    break;
                case 2:
                    System.out.println("- LOGIN -");
                    login();
                    if (usuarioActual != null) {
                        int userOption = -1;

                        System.out.println("INFO - Bienvenid@ " + usuarioActual.getName());

                        while (userOption != 0) {

                            showUserMenu();
                            userOption = Auxiliares.pedirNumeroRango("OPCION USUARIO : ", 0, 4);
                            switch (userOption) {
                                case 1:
                                    System.out.println("- CAMBIAR PASSWORD -");
                                    changePassword();
                                    break;
                                case 2:
                                    System.out.println("- SUDOKU RÁNDOM NO JUGADO -");
                                    getSudokuRandom();
                                    System.out.println("INFO - Sudoku random (solved) :" + sudokuActual.getSolved());
                                    break;
                                case 3:
                                    System.out.println("- FINALIZAR SUDOKU -");
                                    finalizarSudoku();
                                    break;
                                case 4:
                                    System.out.println("- TIEMPO MEDIO -");
                                    break;
                                case 0:
                                    System.out.println("- SALIR -");
                                    System.out.println("Bye querid@ " + usuarioActual.getName());
                                    System.out.println("Más conocid@ como " + usuarioActual.getUsername());
                                    usuarioActual = null;
                                    sudokuActual = null;
                                    break;
                            }
                        }
                    }
                    break;
                case 3:
                    System.out.println("- RÁNKING -");
                    break;
                case 0:
                    System.out.println("- SALIR -");
                    marshallearHistoriales();
                    marshallearSudokus();
                    marshallearUsuarios();
                    System.out.println("Hasta pronto!");
                    break;
            }
        }

    }

    //Función que carga los sudokus del txt al xml
    public static void cargarTxtSudokus() {
        File sudokusXML = new File(rutaSudokusXML);

        Sudoku s = new Sudoku();
        //Si el archivo no existe
        if (!sudokusXML.exists()) {
            System.out.println("INFO - XML de sudokus no existe");
            FileReader fr = null;
            try {
                //Cargamos el archivo txt
                File rstxt = new File(rutaSudokusTXT);
                fr = new FileReader(rstxt);
                BufferedReader br = new BufferedReader(fr);
                //Crea el archivo xml
                String line;
                int contador = 0;
                int nsudos = 1;
                while ((line = br.readLine()) != null) {
                    contador++;
                    switch (contador) {
                        case 1:
                            String[] datos = line.split(" ");
                            s.setLevel(Integer.parseInt(datos[1]));
                            s.setDescription(datos[2]);
                            break;
                        case 2:
                            s.setProblem(line);
                            break;
                        case 3:
                            s.setSolved(line);
                            contador = 0;
                            sudokus.getSudoku().add(s);
                            s = new Sudoku();
                            break;
                    }
                }

                fr.close();
                //carga los sudokus del txt al xml

                //marshallea
                marshallearSudokus();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Si el archivo existe
        } else {
            System.out.println("INFO - El XML de Sudokus existe");
        }

        //unmarshallea
        unmarshallearSudokus();

    }

    //Función que unmarshallea los sudokus
    public static void unmarshallearSudokus() {
        try {
            JAXBContext contexto = JAXBContext.newInstance(Sudokus.class);
            Unmarshaller u = contexto.createUnmarshaller();
            sudokus = (Sudokus) u.unmarshal(new File(rutaSudokusXML));
            System.out.println("INFO - Sudokus unmarhsalleados");
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //funcion que marshallea los sudokus
    public static void marshallearSudokus() {
        try {
            JAXBContext contexto = JAXBContext.newInstance(Sudokus.class);
            Marshaller m = contexto.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(sudokus, new FileOutputStream(rutaSudokusXML));
            System.out.println("INFO - Sudokus marhsalleados");
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Función que unmarshallea los usuarios
    public static void unmarshallearUsuarios() {
        try {
            File f = new File(rutaUsuariosXML);
            if (!f.exists()) {
                System.out.println("INFO - Usuarios XML no existe");
                //f.createNewFile();
            }
            JAXBContext contexto = JAXBContext.newInstance(Users.class);
            Unmarshaller u = contexto.createUnmarshaller();
            usuarios = (Users) u.unmarshal(f);
            System.out.println("INFO - Usuarios unmarhsalleados");
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Función que marshallea los usuarios
    public static void marshallearUsuarios() {
        try {
            JAXBContext contexto = JAXBContext.newInstance(Users.class);
            Marshaller m = contexto.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(usuarios, new File(rutaUsuariosXML));
            System.out.println("INFO - Usuarios marhsalleados");
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Funcion que unmarshallea los historiales
    public static void unmarshallearHistoriales() {
        try {
            File f = new File(rutaHistorialesXML);
            if (!f.exists()) {
                System.out.println("INFO - Historiales XML no existe");
                //historiales = new Historicals();
            } else {
                JAXBContext contexto = JAXBContext.newInstance(Historicals.class);
                Unmarshaller u = contexto.createUnmarshaller();
                historiales = (Historicals) u.unmarshal(f);
                System.out.println("INFO - Historiales unmarhsalleados");
            }
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Función que marshallea los historiales
    public static void marshallearHistoriales() {
        try {
//            File f = new File(rutaHistorialesXML);
//            if(!f.exists()){
//                System.out.println("INFO - Historiales XML no existe, lo creamos");
//                FileWriter fw = new FileWriter(f);
//                BufferedWriter bw = new BufferedWriter(fw);
//                bw.close();
//            }
            JAXBContext contexto = JAXBContext.newInstance(Historicals.class);
            Marshaller m = contexto.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(historiales, new File(rutaHistorialesXML));
            System.out.println("INFO - Historiales marhsalleados");
        } catch (JAXBException ex) {
            Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //OPCIONAL - Función que crea un HM de sudokus
    //Función que muestra el menú principal
    public static void showMainMenu() {
        System.out.println("********** MENU **********");
        System.out.println("1.- Registrar usuario");
        System.out.println("2.- Login");
        System.out.println("3.- Ranking usuarios");
        System.out.println("0.- Salir");
        System.out.println("********** **** **********");
    }

    //Función que muestra el menú del usuario registrado
    public static void showUserMenu() {
        System.out.println("******* MENU USUARIO *******");
        System.out.println("1.- Cambiar password");
        System.out.println("2.- Obtener sudoku no jugado");
        System.out.println("3.- Finalizar sudoku");
        System.out.println("4.- Tiempo medio");
        System.out.println("0.- Salir");
        System.out.println("********** **** **********");
    }

    //Función que crea un usuario y lo devuelve, en caso de ya existir, devuelve un null
    public static User registrarUsuario() {

        //Obtenemos los datos
        String nombre = Auxiliares.pedirCadena("Nombre real =  ");
        String pseudo = Auxiliares.pedirCadena("Nombre de usuario = ");
        String contra = Auxiliares.pedirCadena("Contraseña = ");

        //Creamos el usuario
        User u = new User();

        u.setName(nombre);
        u.setUsername(pseudo);
        u.setPassword(contra);

        //Comprobamos si existe
        //Si existe no se añade
        //Si no existe se añade usuario al list y se marshallea
        if (!userExists(u)) {
            usuarios.getUser().add(u);
            marshallearUsuarios();
            System.out.println("Usuario registrado!");
            return u;
        } else {
            System.out.println("ERROR - Usuario (username) ya existe");
            return null;
        }

    }

    //Función que devuelve boolean dependiendo de si usuario (nombre de usuario) existe
    public static boolean userExists(User usuario) {
        boolean exists = false;
        for (User user : usuarios.getUser()) {
            if (user.getUsername().equals(usuario.getUsername())) {
                exists = true;
            }
        }
        return exists;
    }

    //Función que simula un login mediante el nombre de usuario y la contraseña
    public static void login() {
        String username = Auxiliares.pedirCadena("Nombre de usuario = ");
        String password = Auxiliares.pedirCadena("Contraseña = ");

        boolean encontrado = false;

        for (User user : usuarios.getUser()) {
            if (user.getUsername().equals(username)) {
                if (user.getPassword().equals(password)) {
                    usuarioActual = user;
                    System.out.println("Login correcto!");
                } else {
                    System.out.println("ERROR - Contraseña (password) incorrecta");
                }
            } else {
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("ERROR - Nombre de usuario (username) no encontrado");
        }
    }

    //Función que permite cambiar la contraseña (password) al usuario registrado pidiendo contraseña actual, nueva y verificación de la nueva
    public static void changePassword() {
        if (usuarioActual != null) {
            String actualPass = Auxiliares.pedirCadena("Contraseña (password) actual = ");
            String newPass = Auxiliares.pedirCadena("Contraseña (password) nueva = ");
            String newPass2 = Auxiliares.pedirCadena("Verificación (password) nueva = ");

            if (actualPass.equals(usuarioActual.getPassword())) {
                if (newPass.equals(newPass2)) {
                    usuarioActual.setPassword(newPass);

                    for (User u : usuarios.getUser()) {
                        if (u.getUsername().equals(usuarioActual.getUsername())) {
                            u.setPassword(newPass);
                            marshallearUsuarios();
                        }
                    }
                    System.out.println("INFO - Contraseña modificada correctamente");
                } else {
                    System.out.println("ERROR - Verificacion nueva contraseña erronea");
                }
            } else {
                System.out.println("ERROR - Contraseña actual erronea");
            }
        } else {
            System.out.println("ERROR - No hay usuario registrado");
        }
    }

    //Función que establece un sudoku random no jugado al usuario
    public static void getSudokuRandom() {

        int nSudokus = sudokus.getSudoku().size();
        System.out.println("INFO - Número de sudokus : " + nSudokus);
        int nRandom = (int) Math.floor(Math.random() * nSudokus + 1);
        //int random = (int )(Math.random() * 50 + 1);
        System.out.println("INFO - Número random : " + nRandom);

        //Damos sudoku aleatorio de los sudokus
        Sudoku s = sudokus.getSudoku().get(nRandom);

        int contador = 0;
        //Mientras no lo haya jugado seguimos dando sudokus
        while (userPlayedSudoku(s) && contador < 100) {
            if (contador >= 100) {
                System.out.println("INFO - Has jugado todos los Sudokus");
            } else {
                nRandom = (int) Math.floor(Math.random() * nSudokus + 1);
                s = sudokus.getSudoku().get(nRandom);
                System.out.println("INFO - Sudoku nº" + nRandom + " jugado");
                contador++;
            }
        }
        //Establecemos el sudoku actual con el sudoku random
        sudokuActual = s;
        System.out.println("INFO - Sudoku actual actualizado");
        //System.out.println("Level : " + sudokuActual.getLevel());
        //System.out.println("Problem : " + sudokuActual.getProblem());
        //System.out.println("Solved : " + sudokuActual.getSolved());
    }

    //Función que devuelve un boolean dependiendo de si el usuario actual a registrado el sudoku en los historiales a partir de un sudoku
    public static boolean userPlayedSudoku(Sudoku sudoku) {

        boolean played = false;
        if (historiales.getHistorical().isEmpty()) {
            System.out.println("INFO - No hay ningún historial registrado");
        } else {
            for (Historicals.Historical h : historiales.getHistorical()) {
                if (h.getUsername().equals(usuarioActual.getUsername())) {
                    if (h.getSudoku().getSolved().equals(sudoku.getSolved())) {
                        if (h.getSudoku().getProblem().equals(sudoku.getProblem())) {
                            if (h.getSudoku().getLevel() == sudoku.getLevel()) {
                                if (h.getSudoku().getDescription().equals(sudoku.getDescription())) {
                                    played = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return played;
    }

    //Función que registra en el historial un sudoku acabado, solicitando el tiempo
    public static void finalizarSudoku() {

        //TODO - No registra correctamente el historial (aún)
        if (sudokuActual == null) {
            System.out.println("ERROR - Obtén un sudoku antes");
        } else {
            //Obtenemos el tiempo
            int time = Auxiliares.pedirNumero("Tiempo en completarlo (segundos) = ");
            //Creamos un historial 
            Historicals.Historical h = new Historicals.Historical();
            //Añadimos los atributos al historial (añadimos el usuario y el sudoku actual y el tiempo solicitado)
            h.setTime(time);
            h.setUsername(usuarioActual.getUsername());
            System.out.println("INFO - Description sudokuActual : " + sudokuActual.getDescription());
            Historicals.Historical.Sudoku s = new Historicals.Historical.Sudoku();
            h.setSudoku(s);
            h.getSudoku().setDescription(sudokuActual.getDescription());
            System.out.println("INFO - Level sudokuActual : " + sudokuActual.getLevel());
            h.getSudoku().setLevel(sudokuActual.getLevel());
            System.out.println("INFO - Problem sudokuActual : " + sudokuActual.getProblem());
            h.getSudoku().setProblem(sudokuActual.getProblem());
            System.out.println("INFO - Solved sudokuActual : " + sudokuActual.getSolved());
            h.getSudoku().setSolved(sudokuActual.getSolved());
            //Añadimos el historial a los historiales
            historiales.getHistorical().add(h);
            //hacemos un marshall
            marshallearHistoriales();
            System.out.println("INFO - Historial registrado");
        }

    }
}
