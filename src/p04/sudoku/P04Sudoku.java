/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p04.sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import model.Historicals;
import model.Sudokus;
import model.Sudokus.Sudoku;
import model.Users;

/**
 *
 * @author Borja S
 */
public class P04Sudoku {

    //Rutas
    static String rutaSudokusTXT = System.getProperty("user.dir") +  File.separator + "sudokus.txt";
    static String rutaSudokusXSD = System.getProperty("user.dir") +  File.separator + "sudokus.xsd";
    static String rutaSudokusXML = System.getProperty("user.dir") +  File.separator + "sudokus.xml";
    static String rutaUsuariosXSD = System.getProperty("user.dir") +  File.separator + "users.xsd";
    static String rutaUsuariosXML = System.getProperty("user.dir") +  File.separator + "usuarios.xml";
    static String rutaHistorialesXML = System.getProperty("user.dir") +  File.separator + "historicals.xml";
    static String rutaHistorialesXSD = System.getProperty("user.dir") +  File.separator + "historicals.xsd";
    
    //HashMaps
    static HashMap<Integer, Sudokus.Sudoku> sudokusHM;
    static HashMap<Integer, Historicals.Historical> historialesHM;
    static HashMap<Integer, Users.User> usuariosHM;

    public static void main(String[] args) {
        // TODO code application logic here
    }

    //Función que carga los sudokus del txt al xml
    public static void cargarTxtSudokus() {
        File sudokusXML = new File(rutaSudokusXML);
        
        Sudoku s = new Sudoku();
        //Si el archivo no existe
        if(!sudokusXML.exists()){
            FileReader fr = null;
            try {
                //Cargamos el archivo txt
                File rstxt = new File(rutaSudokusTXT);
                fr = new FileReader(rstxt);
                BufferedReader br = new BufferedReader(fr);
                //Crea el archivo xml
                String line;
                int contador = 0;
                
                while((line=br.readLine()) != null){
                    contador++;
                    switch(contador){
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
                            break;      
                    }               
                }
                
                fr.close();
                //carga los sudokus del txt al xml
                //Si el archivo existe
            } catch (FileNotFoundException ex) {
                Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(P04Sudoku.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
        }
            //marshallea
            //unmarshallea
    }
    
    //Función que marshallea los sudokus
    
    
    //funcion que unmarshallea los sudokus
    
}
