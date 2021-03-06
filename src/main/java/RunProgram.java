import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RunProgram {
  public static void main(String[] args) {
    //Setting these according to results from TestGeneticAlgorithmParameters
    int numStates;
    double crossover = 0.6;
    double mutation = 0.15;
    double elitism = 0.1;
    int numGenerations = 1000000;
    int populationSize = 100;

    //Get number of states from command line
    if(args.length != 1){
      System.out.println("Please specify number of states");
      return;
    }
    else{
      try{
        numStates = Integer.parseInt(args[0]);
        if(numStates <= 0){
          System.out.println("Please enter a positive number of states");
          return;
        }
      }
      catch(NumberFormatException nfe){
        System.out.println("Please specify number of states");
        return;
      }
    }

    //Create and run a genetic algorithm (with multithreading enabled)
    try{
      long maxMemory = Runtime.getRuntime().maxMemory();
      System.out.println("Max memory: " + maxMemory + "Bytes (" +  (maxMemory/(1024*1024)) + "MB)");

      //Create GA w above parameters and increaseMutation on
      GeneticAlgorithm ga = new GeneticAlgorithm(populationSize, numStates, numGenerations, crossover, mutation, elitism);
      ga.increaseMutationRate(true);
      ga.enableMultithreading(true);

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

  }
}
