package Agente2;

import java.util.ArrayList;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class AgentTest extends AbstractPlayer {
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;
    private static int contador=0;
    int cont=0;
    ArrayList<Integer> EspaciosLibre;
    private static int contCentro = 0;//Cuentas las veces seguidas que sale la accion centro


    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public AgentTest(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
        EspaciosLibre=huecosLibres(so);
        
        
        //disparar izquierda derecha 
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     * @throws InterruptedException 
     */
    
    public ArrayList<Integer> huecosLibres(StateObservation stateObs){
    	int cont=0;
    	int contmax=0;
    	int fila=0;
    	ArrayList<Observation>[][] M = stateObs.getObservationGrid();
		for (int i = 0; i < M[0].length; i++) {//miramos el mapa entero
			cont=0;
			for (int j = 0; j < M.length-1; j++) {
				 if (M[j][i].size()>0 && M[j][i].get(0).itype == 11) {
					
					 cont++;//Controlamos los bloques metalicos que encontramos en una fila
					
				}
			}
			if (cont>4 && cont>=contmax) {//si hay mas de 6 bloques y es igual al maximo de bloques en una fila encontrado, se queda con la posicion
				contmax=cont;
				fila=i;
			}
		}
		
		
		
		ArrayList<Integer> Resultado=new ArrayList<Integer>();//las columnas donde estan los bloques
                Resultado.add(fila);
    	
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
		
    	return Resultado;
    }
    

    
    
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer)  {
        

    	
    StateObservation SOFuturoX1 = stateObs.copy();
        SOFuturoX1.advance(Types.ACTIONS.ACTION_NIL);//futuro X1
        StateObservation SOFuturoX2 = stateObs.copy();//futuro X2
        SOFuturoX2.advance(Types.ACTIONS.ACTION_NIL);
        Estado s = new Estado(stateObs,SOFuturoX1, SOFuturoX2, EspaciosLibre); //Se crea e inicializa el estado s
        
    	String ident_s = s.toString();
    	if (!TablaQ.comprobarEstado(ident_s)) {
    	}
    	int accion = TablaQ.sacarMejor(ident_s);
        
    	return actions.get(accion);
    	
    }

}

