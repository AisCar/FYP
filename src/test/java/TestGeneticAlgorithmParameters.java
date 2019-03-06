import org.junit.Test;
import org.junit.After;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestGeneticAlgorithmParameters {
  private int fittestNotHighestScoringCounter = 0;
  private int peakScoreNotFinalScoreCounter = 0; //using this to determine if elitism needed
  private int numGenerations = 1000000; //increase to 1,000,000 or more if looking for sigma(4) = 13


  /*
  Test basic Genetic Algorithm parameters
   */

  @Test
  public void testCrossoverRates(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.05;
    double[] crossoverRates = {0.2, 0.4, 0.6, 0.8};
    double elitismRate = 0.0; //Added to GA constructor after tests written
    //int numGenerations = 10000;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < crossoverRates.length; j++){
      double crossoverRate = crossoverRates[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
    }

    writeResults("F:/4BCT/FYP/testresults/crossoverRateResults.txt", results);

  }

  @Test
  public void testMutationRates(){
    int numStates = 4;
    int popSize = 200;
    double[] mutationRates = {0.0, 0.5, 0.1, 0.15, 0.2, 0.25};
    double crossoverRate = 0.7;
    double elitismRate = 0.0; //Added to GA constructor after tests written
    //int numGenerations = 10000;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < mutationRates.length; j++){
      double mutationRate = mutationRates[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
    }

    writeResults("F:/4BCT/FYP/testresults/mutationRateResults.txt", results);
  }

  @Test
  public void testPopulationSizes(){
    int numStates = 4;
    int[] popSizes = {100, 200, 300, 400, 500};
    double mutationRate = 0.05;
    double crossoverRate = 0.7; //TODO this is picked randomly, change to best from above?
    double elitismRate = 0.0; //Added to GA constructor after tests written
    //int numGenerations = 10000;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < popSizes.length; j++){
      int popSize = popSizes[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
    }

    writeResults("F:/4BCT/FYP/testresults/populationSizeResults.txt", results);
  }


  /*
  Test other options / improvements
   */

  @Test
  public void testIncreaseMutation(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.5;
    double crossoverRate = 0.7;
    double elitismRate = 0.0;
    //int numGenerations = 10000;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int i = 0; i < 3; i++){
      try {
        GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
        ga.increaseMutationRate(true);
        ga.run();
        TuringMachine fittest = ga.getPopulation().get(0);
        TuringMachine highestScoring = ga.getHighestScoringTM();
        int highestEverScore = ga.getHighestScore();
        if (fittest.getScore() != highestScoring.getScore()) {
          fittestNotHighestScoringCounter++;
        }
        if (highestEverScore > highestScoring.getScore()) {
          peakScoreNotFinalScoreCounter++;
        }
        results = results + crossoverRate + "\t" + mutationRate + "\t" + popSize + "\t" + highestEverScore
                + "\t" + highestScoring.getScore() + "\t" + highestScoring.getFitness() + "\t"
                + fittest.getFitness() + "\t" + fittest.getScore() + "\n";
      }
      catch(GeneticAlgorithmException gae){
        //TODO
      }
    }

    writeResults("F:/4BCT/FYP/testresults/increaseMutationTest.txt", results);
  }

  @Test
  public void testElitistSelection(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.05;
    double crossoverRate = 0.7;
    double[] elitismRates = {0.0, 0.2, 0.4, 0.6, 0.8};
    //int numGenerations = 10000;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < elitismRates.length; j++){
      double elitismRate = elitismRates[j];
      if(elitismRate + crossoverRate > 1.0){
        crossoverRate = 1.0 - elitismRate; //To prevent errors
      }
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
    }

    writeResults("F:/4BCT/FYP/testresults/elitistSelectionTestResults.txt", results);
  }

  @Test
  public void testReachabilityFitness(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.5;
    double crossoverRate = 0.7;
    double elitismRate = 0.0;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int i = 0; i < 3; i++) {
      try {
        GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
        ga.setReachableFitnessFeature(true);
        ga.run();
        TuringMachine fittest = ga.getPopulation().get(0);
        TuringMachine highestScoring = ga.getHighestScoringTM();
        int highestEverScore = ga.getHighestScore();
        if (fittest.getScore() != highestScoring.getScore()) {
          fittestNotHighestScoringCounter++;
        }
        if (highestEverScore > highestScoring.getScore()) {
          peakScoreNotFinalScoreCounter++;
        }
        results = results + crossoverRate + "\t" + mutationRate + "\t" + popSize + "\t" + highestEverScore
                + "\t" + highestScoring.getScore() + "\t" + highestScoring.getFitness() + "\t"
                + fittest.getFitness() + "\t" + fittest.getScore() + "\n";
      } catch (GeneticAlgorithmException gae) {
        //TODO
      }
    }
  }
  //TODO consider a test which compares time between a GA that doesnt run TMs with unreachable halt states and one that does - maybe just do this manually though

  @Test
  public void testNumHaltsFitness(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.5;
    double crossoverRate = 0.7;
    double elitismRate = 0.0;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";
    for(int i = 0; i < 3; i++) {
      try {
        GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
        ga.setNumHaltsFitnessFeature(true);
        ga.run();
        TuringMachine fittest = ga.getPopulation().get(0);
        TuringMachine highestScoring = ga.getHighestScoringTM();
        int highestEverScore = ga.getHighestScore();
        if (fittest.getScore() != highestScoring.getScore()) {
          fittestNotHighestScoringCounter++;
        }
        if (highestEverScore > highestScoring.getScore()) {
          peakScoreNotFinalScoreCounter++;
        }
        results = results + crossoverRate + "\t" + mutationRate + "\t" + popSize + "\t" + highestEverScore
                + "\t" + highestScoring.getScore() + "\t" + highestScoring.getFitness() + "\t"
                + fittest.getFitness() + "\t" + fittest.getScore() + "\n";
      } catch (GeneticAlgorithmException gae) {
        //TODO
      }
    }
  }


  @Test
  public void testStateUseFitness(){
    int numStates = 4;
    int popSize = 200;
    double mutationRate = 0.5;
    double crossoverRate = 0.7;
    double elitismRate = 0.0;
    String results = "(numGenerations: " + numGenerations + ")\ncrossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int i = 0; i < 3; i++) {
      try {
        GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
        ga.setStateUsageFitnessFeature(true);
        ga.run();
        TuringMachine fittest = ga.getPopulation().get(0);
        TuringMachine highestScoring = ga.getHighestScoringTM();
        int highestEverScore = ga.getHighestScore();
        if (fittest.getScore() != highestScoring.getScore()) {
          fittestNotHighestScoringCounter++;
        }
        if (highestEverScore > highestScoring.getScore()) {
          peakScoreNotFinalScoreCounter++;
        }
        results = results + crossoverRate + "\t" + mutationRate + "\t" + popSize + "\t" + highestEverScore
                + "\t" + highestScoring.getScore() + "\t" + highestScoring.getFitness() + "\t"
                + fittest.getFitness() + "\t" + fittest.getScore() + "\n";
      } catch (GeneticAlgorithmException gae) {
        //TODO
      }
    }
  }


  /*
  TODO: More tests
   */


  /*
  Helper Methods
   */

  //Helper method (was reusing same code in all tests)
  private String runThreeTimes(int popSize, int numStates, int numGenerations, double crossoverRate, double mutationRate, double elitismRate){
    String results = "";
    for(int i = 0; i < 3; i++){
      try {
        GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
        ga.run();
        TuringMachine fittest = ga.getPopulation().get(0);
        TuringMachine highestScoring = ga.getHighestScoringTM();
        int highestEverScore = ga.getHighestScore();
        if (fittest.getScore() != highestScoring.getScore()) {
          fittestNotHighestScoringCounter++;
        }
        if (highestEverScore > highestScoring.getScore()) {
          peakScoreNotFinalScoreCounter++;
        }
        results = results + crossoverRate + "\t" + mutationRate + "\t" + popSize + "\t" + highestEverScore
                + "\t" + highestScoring.getScore() + "\t" + highestScoring.getFitness() + "\t"
                + fittest.getFitness() + "\t" + fittest.getScore() + "\n";
      }
      catch(GeneticAlgorithmException gae){
        //TODO
      }
    }
    return results;
  }

  private void writeResults(String fileName, String results){
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
      writer.write(results);
      writer.flush();
      writer.close();
    }
    catch(IOException ioe) {
      //What do I want to do if this happens? I suppose print the results string to the commandline so its not lost forever
      System.out.println(ioe.getMessage());
      System.out.println(results);
    }
  }


  @After
  public void printTheCountVariables(){
    System.out.println("peakScoreNotFinalScoreCounter = " + peakScoreNotFinalScoreCounter +
            "\nfittestNotHighestScoringCounter = " + fittestNotHighestScoringCounter);
  }





  /*
  Old 5-state tests - it takes too long to run a 5-state Turing machine, so was only able to run these for 100
  generations at a time. Decided it was better to test on 4-state Turing machines
   */

  /*
  @Test
  public void testCrossoverRates(){
    int numStates = 5;
    int popSize = 200;
    double mutationRate = 0.05;
    double[] crossoverRates = {0.2, 0.4, 0.6, 0.8};
    int numGenerations = 100; //TODO increase? At least to 1000...
    String results = "crossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < crossoverRates.length; j++){
      double crossoverRate = crossoverRates[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate);
    }

    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter("F:/4BCT/FYP/testresults/crossoverRateResults.txt")); //FileWriter (String name, boolean append) instead?
      writer.write(results);
      writer.flush();
      writer.close();
    }
    catch(IOException ioe){
      //What do I want to do if this happens? I suppose print the results string to the commandline so its not lost forever
      System.out.println(ioe.getMessage());
      System.out.println(results);
    }
  }

  @Test
  public void testMutationRates(){
    int numStates = 5;
    int popSize = 200;
    double[] mutationRates = {0.0, 0.5, 0.1, 0.15, 0.2, 0.25};
    double crossoverRate = 0.7; //TODO this is picked randomly, change to best from above?
    int numGenerations = 100; //TODO increase? At least to 1000...
    String results = "crossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < mutationRates.length; j++){
      double mutationRate = mutationRates[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate);
    }

    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter("F:/4BCT/FYP/testresults/mutationRateResults.txt"));
      writer.write(results);
      writer.flush();
      writer.close();
    }
    catch(IOException ioe){
      //What do I want to do if this happens? I suppose print the results string to the commandline so its not lost forever
      System.out.println(ioe.getMessage());
      System.out.println(results);
    }

  }

  @Test
  public void testPopulationSizes(){  //never finished running
    int numStates = 5;
    int[] popSizes = {100, 200, 300, 400, 500};
    double mutationRate = 0.05;
    double crossoverRate = 0.7; //TODO this is picked randomly, change to best from above?
    int numGenerations = 100; //TODO increase? At least to 1000...
    String results = "crossover\tmutation\tpopulation\thigh score overall\t" +
            "high score current\t(its fitness)\thigh fitness current\t(its score)\n\n";

    for(int j = 0; j < popSizes.length; j++){
      int popSize = popSizes[j];
      results = results + runThreeTimes(popSize, numStates, numGenerations, crossoverRate, mutationRate);
    }

    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter("F:/4BCT/FYP/testresults/populationSizeResults.txt"));
      writer.write(results);
      writer.flush();
      writer.close();
    }
    catch(IOException ioe){
      //What do I want to do if this happens? I suppose print the results string to the commandline so its not lost forever
      System.out.println(ioe.getMessage());
      System.out.println(results);
    }
  }
  */

}
