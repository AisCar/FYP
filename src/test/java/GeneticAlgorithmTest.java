import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class GeneticAlgorithmTest {
  GeneticAlgorithm ga;

  @Before
  public void initialise(){
    ga = new GeneticAlgorithm(1,1);
  }

  @Test
  public void mutationTest(){
    boolean[] chromosome = {true, false, true, false, true, false};
    //GeneticAlgorithm ga = new GeneticAlgorithm();
    boolean[] mutatedChromosome = this.ga.mutateSingle(chromosome);
    //assertNotEquals(chromosome, mutatedChromosome);//This doesn't work, these are pointers
    boolean chromosomeHasChanged = false;
    for(int i = 0; i < chromosome.length; i++){
      if(chromosome[i] != mutatedChromosome[i]){
        chromosomeHasChanged = true;
      }
    }
    assertTrue(chromosomeHasChanged);
  }

  @Test
  public void crossoverTest(){
    System.out.println("\ncrossoverTest");
    boolean[] parent1 = {true, true, true, true, true, true};
    boolean[] parent2 = {false, false, false, false, false, false};
    boolean[][] children = this.ga.crossoverSingle(parent1, parent2);
    String p1 = "";
    String p2 = "";
    String c1 = "";
    String c2 = "";
    int crossoverPoint = -1;
    for(int i = 0; i < parent1.length; i++){
      p1 = p1 + (parent1[i]? "1" : "0");
    }
    for(int i = 0; i < parent2.length; i++){
      p2 = p2 + (parent2[i]? "1" : "0");
    }

    boolean crossedOver = false;
    for(int i = 0; i < parent1.length; i++){
      c1 = c1 + (children[0][i]? "1" : "0");
      if(!crossedOver && (children[0][i] != parent1[i])){
        crossedOver = true;
        crossoverPoint = i;
      }
    }

    crossedOver = false;
    for(int i = 0; i < parent1.length; i++){
      c2 = c2 + (children[1][i]? "1" : "0");
      if(!crossedOver && (children[1][i] != parent2[i])){
        crossedOver = true;
        assertEquals(i, crossoverPoint);
      }
    }

    System.out.println("Parent Chromosomes: " + p1 + " " + p2);
    System.out.println("Child Chromosomes: " + c1 + " " + c2);
    assertTrue(crossedOver);

  }

  @Test
  public void testSelection(){
    //generate 10 1-state TuringMachines
    GeneticAlgorithm ga2 = new GeneticAlgorithm(10, 1);
    //select 4 of them
    ArrayList<TuringMachine> selected = ga2.select();
    //TODO make test better
    assertEquals(10, selected.size()); //now redundant - testing old functionality - had to be changed - remove?
    //assertTrue(!selected.get(0).equals(selected.get(1))); //could coincidentally be same
  }


  @Test
  public void testNextGeneration(){
    System.out.println("\ntestNextGeneration");
    GeneticAlgorithm ga2 = new GeneticAlgorithm(10, 1);
    ArrayList<TuringMachine> initialPop = ga2.getPopulation();
    for(TuringMachine tm : initialPop){
      System.out.println(tm);
    }
    System.out.println("-----");
    ga2.nextGeneration(); //note TuringMachines arent run so sorting is pointless
    ArrayList<TuringMachine> nextPop = ga2.getPopulation();
    for(TuringMachine tm : nextPop){
      System.out.println(tm);
    }
    //This isn't really a proper test but I'm satisifed just for now
    //TODO more exhaustive tests
  }

  @Test
  public void testRun(){
    /*
    Temporary test: See if GA successfully evolves 2, 3, 4 state machines with highest state Busy Beaver Score
    Known high scores:
    2-state:
    3-state: 6 (success!)
    4-state: 13 (success!! eventually)
     */

    //ok run 4 overnight, see how goes.
    /*
    ArrayList<TuringMachine> best = new ArrayList<TuringMachine>();
    for(int i = 0; i < 4; i++){
      GeneticAlgorithm bigGA = new GeneticAlgorithm(100, 4);
      bigGA.run();
      best.add(bigGA.getPopulation().get(0));
      System.out.println("Test: " + bigGA.getPopulation().get(0).getScore());
    }
    //TODO print out TMs, not just their scores
    int j = 0;
    for(TuringMachine tm : best){
      System.out.println("Best score " + j + ": " + tm.getScore());
      j++;
    }
    */


/*
    GeneticAlgorithm bigGA = new GeneticAlgorithm(200, 4); //change this line as needed
    ArrayList<TuringMachine> initialPop = bigGA.getPopulation();
    bigGA.run();
    ArrayList<TuringMachine> finalPop = bigGA.getPopulation();
    System.out.println("\ntestRun\n");
    System.out.println("Population size: " + initialPop.size() + " -> " + finalPop.size());
    TuringMachine someTM = finalPop.get(0);

    System.out.println("Best score: " + finalPop.get(0).getScore());
    */

  }

  @Test
  public void testRepair(){
    System.out.println("\ntestRepair");
    GeneticAlgorithm ga2 = new GeneticAlgorithm(1, 4);
    //set next state to 5 (101) and 7 (111)
    boolean[] gene = {true, false, true, false, true, false, true, true, true, true};
    //add same gene to chromosome 4 times (4 states are identical)
    boolean[] chromosome = new boolean[(4*gene.length)];
    for(int i = 0; i < (4*gene.length); i++){
      chromosome[i] = gene[i % gene.length];
    }

    //repair the chromosome
    chromosome = ga2.repair(chromosome);

    System.out.println("Old\tNew");
    for(int i = 0; i < gene.length; i++){
      System.out.println(gene[i] + "\t" + chromosome[i]);
    }
    //TODO asserts etc
  }

  @Test
  public void testRepair2() {
    //TODO: Create a 5 state TM with nextState = 6 and translate and repair
    //7  = 111, 6 = 110
    GeneticAlgorithm ga2 = new GeneticAlgorithm(1, 4);
    //set next state to 5 (101) and 7 (111)
    boolean[] gene = {true, false, true, false, true, false, true, true, true, true};

  }

}
