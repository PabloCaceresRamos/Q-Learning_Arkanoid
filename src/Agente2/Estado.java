package Agente2;

import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

public class Estado {

    private static int contBloqueos = 0;
    public Vector2d Avatar;
    public Vector2d Bola;
    private int[] DivisionesTablaDerecha = {29, 58, 72};//el centro de las divisiones de la tabla completa
    private int[] DivisionesTablaIzquierda = {15, 29, 58};
    private int EspacioLibre;//orientacion del espacio libre mas cercano al bloque 1 izquierda 2 derecha 0 arriba y 3 si no importa
    private Vector2d bloqueXY;
    public int numBloques;
    private double pendiente=90; 
    private double x1;
    private double y1;
    private int zonaMapaPelota;//Divide el mapa en 3 y nos dice si esta a la izquierda 1, derecha 2 o centro 0 o -1 no hay bola
    private int zonaMapaHueco=-1;//Divide el mapa en 3 y nos dice si esta a la izquierda 1, derecha 2, centro 0 o -1 todos


    //Variables dependientes del estado
    public int patinIzquierda;
    public int patinCentro;
    public int patinDerecha;
    public int bloque=-1;//orientacion del bloque 1 izquierda 2 derecha 0 arriba
    public int dirVBola=1;//Direccion vertical de la bola 1 subiendo 0 bajando o quieta
    public int bloqueado=0;// 0 si no esta bloqueado, 1 si esta bloqueado
    
    
    public String toString() {
        return "" + patinIzquierda + patinCentro + patinDerecha + bloque +dirVBola+bloqueado;
    }

    public Estado(StateObservation so, StateObservation SOFuturoX1, StateObservation SOFuturoX2, ArrayList<Integer> EspaciosLibre) {
       
        StateObservation soAux = SOFuturoX2;
        if (SOFuturoX1.getAvatarHealthPoints() > SOFuturoX2.getAvatarHealthPoints()) {
            soAux = SOFuturoX1;//Como miramos al futuro quedandonos quietos, la bola puede desaparecer, por lo que no queremos mirar tanto al futuro
        }
        Avatar = soAux.getAvatarPosition();
        Avatar.x -= 7;//pongo la x en el inicio del patin


        ArrayList<Observation>[] lista = soAux.getMovablePositions();
        ArrayList<Observation>[] lista2 = soAux.getFromAvatarSpritesPositions();
        ArrayList<Observation>[] lista3 = soAux.getMovablePositions();//Es para buscar el bloque mas cercano al avatar
        numBloques=lista3.length;

        if (lista2 != null && lista2[0].size() > 0) {
            Bola = lista2[0].get(0).position;
        } else {
            Bola = buscarBola(lista);
        }

        if (bloqueado(so, SOFuturoX1, SOFuturoX2)) {
            contBloqueos++;
        } else {
            contBloqueos = 0;
        }
        if (contBloqueos > 30) {
            //Si estamos en un bloqueo, ponemos el bloque a la derecha o a la izquierda, segun la posicion del patin
            bloqueado=1;
            

        } else {//Si no hay bloqueo elijo un bloque.
            Vector2d bloqueV = buscarBloque(lista3);

            this.EspacioLibre = orientacionHuecoLibre(EspaciosLibre, bloqueV);

            //Si hay hueco, le decimos la orientacion del hueco, si no, le decimos la orientacion del bloque mas cercano
            if (this.EspacioLibre != 3) {
                bloque = this.EspacioLibre;
            } else {
                bloque = orientacionBloque(bloqueV);
                bloqueXY=bloqueV;
            }
                
        }
        
        //Colocamos las orientaciones de las partes del patinete
        patinIzquierda = orientacionBola(DivisionesTablaIzquierda[0], DivisionesTablaDerecha[0]);
        patinCentro = orientacionBola(DivisionesTablaIzquierda[1], DivisionesTablaDerecha[1]);
        patinDerecha = orientacionBola(DivisionesTablaIzquierda[2], DivisionesTablaDerecha[2]);
        
        if(this.Bola==null){
            this.zonaMapaPelota=-1;
        }else{
            this.zonaMapaPelota=getZonaMapa(Bola);
        }
        
        PendienteYDireccionBola(so,SOFuturoX1);

        
        

    }

    private Vector2d buscarBola(ArrayList<Observation>[] lista) {

        for (int i = 0; i < lista.length; i++) {
            for (int j = 0; j < lista[i].size(); j++) {
                if (lista[i].get(j).itype == 5) {
                    return lista[i].get(j).position;
                }

            }
        }
        return null;
    }

    private Vector2d buscarBloque(ArrayList<Observation>[] lista) {

        for (int i = 0; i < lista.length; i++) {
            for (int j = 0; j < lista[i].size(); j++) {
                if (lista[i].get(j).itype == 8 || lista[i].get(j).itype == 9) {
                    return lista[i].get(j).position;
                }

            }
        }
        return null;
    }

    public int orientacionBola(int limIzq, int limDer) {
        if (Bola == null) {
            return -1;
        }
        if (Bola.x < Avatar.x + limIzq) {
            return 1;
        }
        if (Bola.x > Avatar.x + limDer) {
            return 2;
        }
        //87=x+4bloques*25tama�o-25tama�o/2
        return 0;

    }

    public int orientacionBloque(Vector2d bloque) {
        if (bloque == null) {
            return 0;
        }
        if (bloque.x <= Avatar.x) {
            return 1;
        }
        if (bloque.x > Avatar.x + 87) {
            return 2;
        }
        return 0;

    }

    public int orientacionHuecoLibre(ArrayList<Integer> EspaciosLibre, Vector2d bloqueV) {
        /*
		 * Miramos cada celda, calculando la distancia al bloque buscado, nos quedamos con el que haya menos distancia
         */
        if (bloqueV == null) {
            return 0;
        }
        if (EspaciosLibre.size() <= 1) {
            return 3; //Si no hay hueco, devuelve 3
        }
        int distancia = 1000;
        double coordenadaX = 0;
        for (int i = 1; i < EspaciosLibre.size(); i++) {
            int disAux = Math.abs(EspaciosLibre.get(i) * 25 - (int) bloqueV.x);
            if (disAux < distancia) {
                distancia = disAux;
                coordenadaX = (double) (EspaciosLibre.get(i)) * 25;
            }

        }
        bloqueXY=new Vector2d(coordenadaX,EspaciosLibre.get(0)*25);
        //colocamos la zona del mapa en la que se encuentra el hueco
        this.zonaMapaHueco=getZonaMapa(new Vector2d(coordenadaX, 1.1d));//la y nos da igual, por lo que le he pueto 1.1 por poner un valor cualquieta
        //Devolvemos la orientacion de el avatar a el hueco libre
        //El avatar es mas grande por la desviacion de la bola al dar al centro
        if (coordenadaX <= Avatar.x) {
            return 1;
        }
        if (coordenadaX > Avatar.x + 87) {
            return 2;
        }
        return 0;
    }

    

    int getAvatarHealthPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getZonaMapa(Vector2d v) {
        //Devuelve en que parse se encuentra el elemento v
        if (v.x <= 150) {//zona izquierda
            return 1;
        }
        if (v.x + 87 >= 575-150) {//zona derecha
            return 2;
        }
        return 0;//zona medio
    }

    public Double getXBola() {
        return Bola.x;
    }

    public boolean bloqueado(StateObservation so, StateObservation SOFuturoX1, StateObservation SOFuturoX2) {
        //Analizaremos la x de las bolas del presente furuto y futuro x2 y si es igual, es que estamos en un bucle vertical.
        Vector2d BolaSO;
        Vector2d BolaSOF1;
        Vector2d BolaSOF2;

        ArrayList<Observation>[] lista11 = so.getMovablePositions();
        ArrayList<Observation>[] lista21 = so.getFromAvatarSpritesPositions();

        if (lista21 != null && lista21[0].size() > 0) {
            BolaSO = lista21[0].get(0).position;
        } else {
            BolaSO = buscarBola(lista11);
        }

        ArrayList<Observation>[] lista12 = SOFuturoX1.getMovablePositions();
        ArrayList<Observation>[] lista22 = SOFuturoX1.getFromAvatarSpritesPositions();

        if (lista22 != null && lista22[0].size() > 0) {
            BolaSOF1 = lista22[0].get(0).position;
        } else {
            BolaSOF1 = buscarBola(lista12);
        }

        ArrayList<Observation>[] lista13 = SOFuturoX2.getMovablePositions();
        ArrayList<Observation>[] lista23 = SOFuturoX2.getFromAvatarSpritesPositions();

        if (lista23 != null && lista23[0].size() > 0) {
            BolaSOF2 = lista23[0].get(0).position;
        } else {
            BolaSOF2 = buscarBola(lista13);
        }

        if (BolaSO != null && BolaSOF1 != null && BolaSOF2 != null) {
            if (BolaSO.x == BolaSOF1.x && BolaSO.x == BolaSOF2.x) {
                return true;
            }
        }

        return false;
    }
    
    public void PendienteYDireccionBola(StateObservation so, StateObservation SOFuturoX1){
         Vector2d BolaSO;
        Vector2d BolaSOF1;

        ArrayList<Observation>[] lista11 = so.getMovablePositions();
        ArrayList<Observation>[] lista21 = so.getFromAvatarSpritesPositions();

        if (lista21 != null && lista21[0].size() > 0) {
            BolaSO = lista21[0].get(0).position;
        } else {
            BolaSO = buscarBola(lista11);
        }

        ArrayList<Observation>[] lista12 = SOFuturoX1.getMovablePositions();
        ArrayList<Observation>[] lista22 = SOFuturoX1.getFromAvatarSpritesPositions();

        if (lista22 != null && lista22[0].size() > 0) {
            BolaSOF1 = lista22[0].get(0).position;
        } else {
            BolaSOF1 = buscarBola(lista12);
        }
        
        if(BolaSO!=null && BolaSOF1!= null){
        //calculo la pendiente respencto a estos dos puntos
        this.x1=BolaSOF1.x;
        this.y1=BolaSOF1.y;
        if((int)BolaSO.x != (int)BolaSOF1.x) {
            this.pendiente = Math.abs((BolaSO.y-BolaSOF1.y)/(BolaSO.x-BolaSOF1.x));
            
        }
        
        //calculo la orientacion bola
        if(BolaSO.y < BolaSOF1.y) this.dirVBola=0; //bajando

        
    }
    }
    
    public int distanciaBloqueBola(){
        if(Bola==null) return -1;
        if(bloqueXY==null) return -1;
        return (int)Math.sqrt(Math.pow((Bola.x-bloqueXY.x),2)-Math.pow((Bola.y-bloqueXY.y),2));
    }
    
    public int distanciaBloqueBolaX(){
        if(Bola==null) return -1;
        if(bloqueXY==null) return -1;
        return (int)Math.abs(Bola.x-bloqueXY.x);
    }
    
    public double distanciaFurutoRespectoBloque(){
        //calculamos la trayectoria recta y vemos cuanta distancia hay entre la bola y el bloque en la coordenada y
        //x=((y-y')+(x'*m))/m
        if(Bola==null) return -1.0;
        if(bloqueXY==null) return -1.0;
        if(pendiente==90){return Math.abs(x1-bloqueXY.x);}
        double xBolaFutura= ((bloqueXY.y-y1)+(x1*pendiente))/pendiente;
        return Math.abs(xBolaFutura-bloqueXY.x);
    }
    


}
