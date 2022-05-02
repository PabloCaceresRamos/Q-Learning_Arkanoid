package tracks.singlePlayer;

import Agente2.Estadistica;
import Agente2.TablaQ;
import java.util.Random;

import core.logging.Logger;
import java.io.IOException;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

    public static void main(String[] args) {

        // Available tracks:
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
        String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
        String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
        String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

        String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
        String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

        //Load available games
        String spGamesCollection = "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);

        //Game settings
        boolean visuals = true;
        int seed = new Random().nextInt();

        // Game and level to play
        int gameIdx = 111;
        int levelIdx = 3; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameIdx][1];
        String game = games[gameIdx][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        System.out.println(gameName);

        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"


        
        
        
        
int trainTest=1;// 0 Entrenamos, 1 Fase Test
levelIdx = 8 ;
level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

// Fase de test
if(trainTest==1){
    //ejecutar con tabla Q
//        try {
//            TablaQ.leerFichero("tabla4750");//cargamos la tablaQ
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        ArcadeMachine.runOneGame(game, level1, visuals, "Agente2.AgentTest", recordActionsFile, seed, 0);
        
//Ejecutar con tabla Q Resumida
        try {
            TablaQ.leerTablaResumida("tablaResumen4750");//cargamos la tablaQ
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        ArcadeMachine.runOneGame(game, level1, visuals, "Agente2.AgentTest_1", recordActionsFile, seed, 0);
}
else{
    try {
            TablaQ.leerFichero("tabla4750");//cargamos la tablaQ
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    
// Fase de entrenamiento
        int M = 1;
        int X = 0;
        for (int i = 0; i < X; i++) {

            
            String level10 = new String(game).replace(gameName, gameName + "_lvl" + i%5);
            ArcadeMachine.runGames(game, new String[]{level10}, M, "Agente2.AgentTrain", null);//120
            
//            if(i%250==0){
//            TablaQ.guardarTabla("tabla"+i);
//            Estadistica.guardarDatos("Estadistica"+i);
//            }          


              //Si se quiere la tabla resumida hay que resumierla y luego crear el fichero
//                 TablaQ.resumirTabla();
//        
//                 TablaQ.guardarTablaResumida("tablaResumen");
//                 Estadistica.guardarDatos("Estadistica"+i);
            

        }
       
}
    }
}
