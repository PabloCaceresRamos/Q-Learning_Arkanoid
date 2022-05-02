package Agente2;

import core.game.Observation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA. User: ssamot Date: 14/11/13 Time: 21:45 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class AgentTrain extends AbstractPlayer {

    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;

    public TablaQ tablaQ;

    public static int n = 0;

    public static double alpha = 0.2;
    public static final float gamma =0.15f;//0.65f;
    public static final double k = 5;

    private static Random rand = new Random(100);

    private ArrayList<Integer> EspaciosLibre;
    private static int contCentro = 0;//Cuentas las veces seguidas que sale la accion centro

    /**
     * Public constructor with state observation and time due.
     *
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public AgentTrain(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
        EspaciosLibre = huecosLibres(so);

    }

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     * @throws InterruptedException
     */
    public ArrayList<Integer> huecosLibres(StateObservation stateObs) {

        int cont = 0;
        int contmax = 0;
        int fila = -1;
        ArrayList<Observation>[][] M = stateObs.getObservationGrid();
        for (int i = 0; i < M[0].length; i++) {//miramos el mapa entero
            cont = 0;
            for (int j = 0; j < M.length - 1; j++) {
                if (M[j][i].size() > 0 && M[j][i].get(0).itype == 11) {

                    cont++;//Controlamos los bloques metalicos que encontramos en una fila

                }
            }
            if (cont > 4 && cont >= contmax) {//si hay mas de 6 bloques y es igual al maximo de bloques en una fila encontrado, se queda con la posicion
                contmax = cont;
                fila = i;
            }
        }

        ArrayList<Integer> Resultado = new ArrayList<Integer>();//las columnas donde estan los bloques
        if(fila>0){
        Resultado.add(fila);//la primera posicion es la de la fila en la que se encuentra el hueco

        for (int j = 1; j < M.length-1; j++) {//volvemos a la fila con mas bloques metalicos
                    boolean b1=false;
                    boolean b2=false;
                    boolean b3=false;
				 
                             if(M[j][fila].size() == 0) b1=true;
                             else if(M[j][fila].get(0).itype!=11) b1=true;
                             
                              if(M[j-1][fila].size() == 0) b2=true;
                             else if(M[j-1][fila].get(0).itype!=11) b2=true;
                              
                               if(M[j+1][fila].size() == 0) b3=true;
                             else if(M[j+1][fila].get(0).itype!=11) b3=true;
                             
                             if(b1 && b2 && b3)Resultado.add(j);
			 
		}
        }

        return Resultado;
    }
    


    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        int accion;


        StateObservation SOFuturoX1 = stateObs.copy();
        SOFuturoX1.advance(Types.ACTIONS.ACTION_NIL);//futuro X1
        StateObservation SOFuturoX2 = stateObs.copy();//futuro X2
        SOFuturoX2.advance(Types.ACTIONS.ACTION_NIL);
        Estado s = new Estado(stateObs,SOFuturoX1, SOFuturoX2, EspaciosLibre); //Se crea e inicializa el estado s

        String ident_s = s.toString(); // identificador de s
        if (!TablaQ.comprobarEstado(ident_s)) { // Si no esta el estado, se mete.
            TablaQ.meterRandom(ident_s, rand); // Se mete con valores random
        }

        // ESCOGER LA ACCI�N SEGUN LA POLITICA DEL AGENTE
        double epsilon = k / (k + n); // n es el tiempo o iteraciones
        double aleatorio = rand.nextDouble();
        if (aleatorio < epsilon) { // si el numero aleatorio es menor que epsilon, se ejecuta una accion aleatoria
            System.out.println("aleatorio");
            boolean repetir = false;

            repetir = false;
            accion = (int) Math.floor(Math.random() * 3);

            // Devolver accion aleatoria. Se puede generar un entero entre 0 y 2 y al final
            // del metodo act devolverlo
        } else { // Se elige la acci�n de mayor valor Q en la tablaQ
            accion = TablaQ.sacarMejor(ident_s);
        }


        // VER ESTADO SIGUIENTE Y RECOMPENSA
        
        StateObservation Futuro = stateObs.copy();
        Futuro.advance(actions.get(accion));//futuro
        StateObservation SOFuturoX1F = Futuro.copy();//futuro X1F
        SOFuturoX1F.advance(Types.ACTIONS.ACTION_NIL);
        StateObservation SOFuturoX2F = SOFuturoX1F.copy();//futuro X2F
        SOFuturoX2F.advance(Types.ACTIONS.ACTION_NIL);
        Estado sPrima = new Estado(Futuro,SOFuturoX1F, SOFuturoX2F, EspaciosLibre); //Se crea e inicializa el estado s

        String ident_sPrima = sPrima.toString(); // identificador de s
        

        if (!TablaQ.comprobarEstado(ident_sPrima)) { // Si no est� el estado, se mete.
            if (Futuro.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
                TablaQ.meter(ident_sPrima);

            } else {
                TablaQ.meterRandom(ident_sPrima, rand); // Se mete con valores random
            }
        }

        
        // SISTEMA RECOMPENSAS
        float recompensa = 0;
        if (s.patinCentro == -1 && sPrima.patinCentro != -1) {//si pasamos de un estado sin pelota a un estado con pelota
            recompensa += 1;
        }
        if(s.patinCentro == -1 && sPrima.patinCentro == -1) recompensa-=1;//Si no hay pelota y en el siguiente estado tampoco hay pelota
        
        
        if(stateObs.getAvatarHealthPoints()>Futuro.getAvatarHealthPoints()) recompensa-=0.75;// si pasamos a un estado con menos vida
        
        if(sPrima.patinCentro==0 || sPrima.patinDerecha==0 || sPrima.patinIzquierda==0 ) recompensa +=0.75;//si en el estado la bola esta arriba del patin
        
        
        if(s.distanciaBloqueBolaX()> -1 && sPrima.distanciaBloqueBolaX()>-1){
            if(s.dirVBola==0 && sPrima.dirVBola==1 && sPrima.Bola.y>300){
                //Estado donde la bola acaba de chochar calculamos la trayectoria y vemos si va cerca del bloque deseado
                if(sPrima.distanciaFurutoRespectoBloque()<30){ recompensa+=0.5; }
                else recompensa-=0.5;
            }
        }
        //Estado bloqueado y pasamos a otro estado bloqueado
        if(s.bloqueado==1 && s.Bola.x== sPrima.Bola.x && s.dirVBola==0 && sPrima.dirVBola==1) recompensa=-0.2f;


        // CALCULAR VALORES Q
        double Qactual = TablaQ.recogerQvalor(ident_s, accion);
        double Qfuturo = TablaQ.recogerQvalor(ident_sPrima, TablaQ.sacarMejor(ident_sPrima));
        double v = Qactual + alpha * (recompensa + gamma * Qfuturo - Qactual);

        TablaQ.actualizar(ident_s, accion, v);
        StateObservation faux= stateObs.copy();
        faux.advance(actions.get(accion));
        
        if(faux.isGameOver()){//Guardamos los datos
            Types.WINNER a=stateObs.getGameWinner();
            Estadistica.meterdatos((int)faux.getGameScore(), a.key(),faux.getAvatarHealthPoints(),faux.getGameTick());
        }
        return actions.get(accion); // ACCION NO ALEATORIA
    }

}
