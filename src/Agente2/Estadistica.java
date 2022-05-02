/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agente2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author Pablo
 */
public class Estadistica {
    
    
    private static  ArrayList<Integer[]> lista=new ArrayList<Integer[]>();
    
    public static void meterdatos(Integer puntos, Integer gana,Integer vida, Integer tick){
      Integer [] aux= {gana,puntos,vida,tick};
      lista.add(aux);
    }
    
    public static  void guardarDatos(String nombre) {
        

		try {
			//String ruta = "./Estadistica.txt";
			File archivo = new File("./"+nombre+".txt");
			BufferedWriter bw;
			if(archivo.exists()) {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("El fichero de texto ya estaba creado.");
			} else {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("Acabo de crear el fichero de texto.");
			}
			
			for (Integer [] fila : lista) {
                            if(fila[0]==1) bw.write("Victoria");
                            else bw.write("Derrota");
		
                                bw.write(";"+fila[1]);
                                bw.write(";"+fila[2]);
                                bw.write(";"+fila[3]);
                                
	
				bw.write("\n");

			}
			
			bw.close();
		}catch(Exception ex) { 
			System.out.println("error generar fichero");
		}
	}
}
