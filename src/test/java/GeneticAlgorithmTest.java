import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class GeneticAlgorithmTest {
  GeneticAlgorithm ga;

  @Before
  public void initialise(){
    ga = new GeneticAlgorithm();
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
    ArrayList<TuringMachine> selected = ga2.select(4);
    //TODO make test better
    assertEquals(4, selected.size());
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
    //Definitely do not run this once run allows more than 10 generations

    GeneticAlgorithm bigGA = new GeneticAlgorithm(60, 3); //30 2-state TMs
    ArrayList<TuringMachine> initialPop = bigGA.getPopulation();
    bigGA.run();
    ArrayList<TuringMachine> finalPop = bigGA.getPopulation();
    System.out.println("\ntestRun\nRan 10 generations\n");
    System.out.println("Population size: " + initialPop.size() + " -> " + finalPop.size());
    TuringMachine someTM = finalPop.get(0);

    System.out.println("Best score: " + finalPop.get(0).getScore());

  }

}
