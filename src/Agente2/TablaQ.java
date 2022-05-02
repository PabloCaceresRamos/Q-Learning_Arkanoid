package Agente2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.File;
import java.io.FileReader;

public class TablaQ {

	public static Map<String, double[]> tabla = new HashMap<String, double[]>();
        
        public static Map<String, Integer> tablaResumida = new HashMap<String, Integer>();

	
	public static void meter(String estado,double[] a) {
		tabla.put(estado, a);
	}
	
	public static void meter(String estado) {
		double vI = 0;// Valor inicial de todos los q-valores
		double[] a = { vI, vI, vI };// inicializamos los q-valores
		tabla.put(estado, a);
	}

	public static void meterRandom(String estado, Random rand) {
		double[] valores = new double[3];
		for (int i = 0; i < 3; i++) {
			valores[i] = rand.nextDouble()*0.1 ;
		}
		tabla.put(estado, valores);
	}

	public static int sacarMejor(String s) {

		double[] t = tabla.get(s);// sacamos la tabla con los valores de las acciones del estado s
		int max = 0;

		if (t == null) {//si no se ha dado ese estado hacemos una accion aleatoria
				max = (int) Math.floor(Math.random()*3);	
			return max;
			}
			
		for (int i = 1; i < t.length; i++) {// buscamos el indice con mayor valor
			if (t[i] > t[max]) {
				max = i;
			}
		}

		return max;
	}

	public static double[] sacar(String s) {
		return tabla.get(s);
	}

	public static boolean comprobarEstado(String s) {
		return tabla.containsKey(s);
	}

	public static void actualizar(String s, int i, double valor) {
		double[] t = tabla.get(s);
		t[i] = valor;
		tabla.put(s, t);
	}

	public static double recogerQvalor(String s, int i) {
		double[] t = tabla.get(s);
		return t[i];
	}

	public static  void guardarTabla(String nombre) {
		//https://geekytheory.com/como-crear-y-modificar-ficheros-con-java
		try {
			//String ruta = "./TablaQ.txt";
			File archivo = new File("./"+nombre+".txt");
			BufferedWriter bw;
			if(archivo.exists()) {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("El fichero de texto ya estaba creado.");
			} else {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("Acabo de crear el fichero de texto.");
			}
			
			for (Map.Entry<String,double[]> fila : tabla.entrySet()) {
				bw.write(fila.getKey());
				double [] QValores=fila.getValue();
				for (int i = 0; i < QValores.length; i++) {
					bw.write(";"+QValores[i]);
				}
				bw.write("\n");

			}
			
			bw.close();
		}catch(Exception ex) { 
			System.out.println("error generar fichero");
		}
	}
	
	public static void leerFichero(String nombre) throws IOException {
		
	      BufferedReader br = null;
	      
	      try {
	         br =new BufferedReader(new FileReader("./"+nombre+".txt"));
	         String line = br.readLine();//Leemos una linea
	         
	         while (null!=line) {//Mientras leamos una linea continuamos
	            String [] fields = line.split(";");//Dividimos los datos separandolos por el ;
	            
	            String estado=fields[0];
	            double  QV[]=new double[3];	
	            for (int i = 1; i < fields.length; i++) {
					QV[i-1]=Double.parseDouble(fields[i]);
				}
	            meter(estado, QV);
	            line = br.readLine();//leemos otra linea
	         }
	         
	      } catch (Exception e) {
	      } finally {
	         if (null!=br) {
	            br.close();
	         }
	      }
		
	}
        
        public static void resumirTabla(){
            tablaResumida=new HashMap<String, Integer>();
            
            for (Map.Entry<String,double[]> fila : tabla.entrySet()) {
				String estado=fila.getKey();
                                tablaResumida.put(estado, sacarMejor(estado));
			}
        }
        
        public static int sacarMejorResumen(String s) {

		Integer t = tablaResumida.get(s);// sacamos la tabla con los valores de las acciones del estado s
		int max = 0;

		if (t == null) {//si no se ha dado ese estado hacemos una accion aleatoria
				max = (int) Math.floor(Math.random()*3);	
			return max;
			}
			
		

		return t;
	}
        
        public static void leerTablaResumida(String nombre) throws IOException {
                tablaResumida=new HashMap<String, Integer>();
	      BufferedReader br = null;
	      
	      try {
	         br =new BufferedReader(new FileReader("./"+nombre+".txt"));
	         String line = br.readLine();//Leemos una linea
	         
	         while (null!=line) {//Mientras leamos una linea continuamos
	            String [] fields = line.split(";");//Dividimos los datos separandolos por el ;
	            
	            String estado=fields[0];
                    Integer accion= Integer.parseInt(fields[1]);
	            
                    tablaResumida.put(estado, accion);
	            line = br.readLine();//leemos otra linea
	         }
	         
	      } catch (Exception e) {
	      } finally {
	         if (null!=br) {
	            br.close();
	         }
	      }
		
	}
        
        public static  void guardarTablaResumida(String nombre) {
		//https://geekytheory.com/como-crear-y-modificar-ficheros-con-java
		try {
			//String ruta = "./TablaQ.txt";
			File archivo = new File("./"+nombre+".txt");
			BufferedWriter bw;
			if(archivo.exists()) {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("El fichero de texto ya estaba creado.");
			} else {
			      bw = new BufferedWriter(new FileWriter(archivo));
			      System.out.println("Acabo de crear el fichero de texto.");
			}
			
			for (Map.Entry<String,Integer> fila : tablaResumida.entrySet()) {
				bw.write(fila.getKey());
                                bw.write(";"+fila.getValue());
				bw.write("\n");

			}
			
			bw.close();
		}catch(Exception ex) { 
			System.out.println("error generar fichero");
		}
	}
}
