import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import static org.mockito.Mockito.*;


public class GeneticAlgorithmTest {
  GeneticAlgorithm ga;

  @Before
  public void initialise(){
    //currently used in testCrossover, testMutation, testSelection
    ArrayList<TuringMachine> pop = new ArrayList<TuringMachine>();
    TuringMachine tm1 = mock(TuringMachine.class);
    TuringMachine tm2 = mock(TuringMachine.class);
    TuringMachine tm3 = mock(TuringMachine.class);
    when(tm1.getFitness()).thenReturn(5);
    when(tm2.getFitness()).thenReturn(10);
    when(tm3.getFitness()).thenReturn(-5);
    pop.add(tm1);
    pop.add(tm2);
    pop.add(tm3);
    ga = new GeneticAlgorithm(1, pop);
  }

  @Test
  public void testMutation(){
    System.out.println("mutationTest");
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
  public void testCrossover(){
    System.out.println("\ntestCrossover");
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
    System.out.println("testSelection");
    //ArrayList<TuringMachine> original = ga.getPopulation();
    ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>(); //to suppress compilation failure
    try{
      selected = ga.select();
    }
    catch(GeneticAlgorithmException gae){
      System.out.println(gae.getMessage());
      assertTrue(false); //TODO there's a better way to fail a test... fail()?
    }
    assertEquals(3, selected.size());
    //TODO improve this test - would be nice to assert selectionProbs values...
  }


  @Test
  public void testNextGeneration(){
    System.out.println("\ntestNextGeneration");
    //needs its own ga because otherwise would overwrite mock TMs with real ones
    GeneticAlgorithm ga2 = new GeneticAlgorithm(10, 1, 1, 0.6, 0.1);
    ArrayList<TuringMachine> initialPop = ga2.getPopulation();
    int fitness = 1;
    for(TuringMachine tm : ga2.getPopulation()){
      //actually running them would take too long so just setting an arbitrary fitness value (used in select())
      tm.fitness = fitness;
      fitness++;
    }

    /*
    for(TuringMachine tm : initialPop){
      System.out.println(tm);
    }
    System.out.println("-----");
    */
    ga2.nextGeneration();
    ArrayList<TuringMachine> nextPop = ga2.getPopulation();
    /*
    for(TuringMachine tm : nextPop){
      System.out.println(tm);
    }
    */

    //This isn't really a proper test but I'm satisifed just for now
    //TODO more exhaustive tests
  }


  @Test
  public void testRepair(){
    System.out.println("\ntestRepair");
    GeneticAlgorithm ga2 = new GeneticAlgorithm(4, new ArrayList<TuringMachine>());
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


}
