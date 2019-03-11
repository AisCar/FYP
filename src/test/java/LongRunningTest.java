import org.junit.Test;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LongRunningTest {

  @Test
  public void testHowWellThisThingWorks(){
    //Setting these according to results from TestGeneticAlgorithmParameters
    int numStates = 5; //Should this be command line arg?
    double crossover = 0.6;
    double mutation = 0.15;
    double elitism = 0.1;
    int numGenerations = 1000000;
    int populationSize = 150;


    try{
      //Create GA w above parameters and increaseMutation on
      GeneticAlgorithm ga = new GeneticAlgorithm(populationSize, numStates, numGenerations, crossover, mutation, elitism);
      ga.increaseMutationRate(true);

      //Write initial population to file
      try{
        String filename = "/home/aisling/fyp/results/initialPop.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write("Initial population for " + numStates + " Turing machine genetic alorithm\n");
        for(TuringMachine tm : ga.getPopulation()){
          writer.write(tm.toString() + "\n"); //Dont have fitness or score yet
        }
        writer.flush();
        writer.close();
      }
      catch(IOException ioe) {
        System.out.println(ioe.getMessage());
      }

      //run the genetic algorithm
      ga.run();

      //write final population to another file
      try{
        //String filename = "C:/Users/Aisling/Desktop/temp/test.txt";
        String filename = "/home/aisling/fyp/results/finalPop.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(numGenerations + " generations \t highest score = " + ga.getHighestScore() + "\t highest score in final generation = " + ga.getHighestScoringTM()); //Should be the same w elitism enabled though
        writer.write("Final population for " + numStates + " Turing machine genetic alorithm\n");
        for(TuringMachine tm : ga.getPopulation()){
          writer.write(tm.toString() + "Fitness: " + tm.getFitness() + "\tScore: " + tm.getScore() + "\tShifts: " + tm.getShifts() + "\n");
        }
        writer.flush();
        writer.close();
      }
      catch(IOException ioe) {
        System.out.println(ioe.getMessage());
        //Don't want to lose everything so print some stuff to console
        System.out.println("\n" + numGenerations + " generations \t highest score = " + ga.getHighestScore() + "\t highest score in final generation = " + ga.getHighestScoringTM()); //Should be the same w elitism enabled though
        System.out.println("Final population for " + numStates + " Turing machine genetic alorithm\n");
        for(int i = 0; i < 10; i++){
          TuringMachine tm = ga.getPopulation().get(i);
          System.out.println(tm.toString() + "Fitness: " + tm.getFitness() + "\tScore: " + tm.getScore() + "\tShifts: " + tm.getShifts() + "\n");

        }
      }
    }
    catch(GeneticAlgorithmException gae){
      //TODO this exception is more trouble than its worth smh remove it
      System.out.println(gae.getMessage() + gae.getStackTrace());
    }


    //
  }
}
