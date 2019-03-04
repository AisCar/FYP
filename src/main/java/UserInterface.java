import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
UserInterface will:
1. Take input parameters for GeneticAlgorithm (population size, crossover rate,
mutation rate etc.) as well as n (number of states)
2. Display information about current program run
3. Enable/Disable optional features
*/

//TODO comments everywhere, this is awful

public class UserInterface extends JFrame{
  //TODO: Rename other variables so that you can rename these ones w/o mixing them up
  private double crossover, mutation, elitismRate;
  private int populationSize, numGenerations, numStates;
  private JTextField crossoverField, mutationField, generationsField, populationField, numStatesField;
  private JTextArea description;
  private JCheckBox increaseMutationCheckbox, reachabilityFitnessCheckbox, stateUseFitnessCheckbox, numHaltsFitnessCheckbox;
  private JButton runGAButton, stopGAButton;
  private boolean increaseMutation, reachabilityFitnessEnabled, stateUseFitnessEnabled, numHaltsFitnessEnabled;
  private GeneticAlgorithm geneticAlgorithm;
  JPanel p4;

  public UserInterface(){
    super("Busy Beavers");

    //set some default values
    crossover = 0.7;
    mutation = 0.05;
    elitismRate = 0.0;
    numGenerations = -1;
    populationSize = -1;
    numStates = -1;

    //set up window size
    setSize(650,400);

    //Panel 1: Header area
    JPanel p1 = new JPanel();
    JLabel title = new JLabel("Optimising Turing Machine Busy Beaver scores using a Genetic Algorithm");
    title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 16));
    p1.add(title);


    /*
    Panel 2: Set crossover rate, mutation rate, number of states, population
    size and num generations
    */
    JPanel p2 = new JPanel();
    p2.setLayout(new GridLayout(16,1));
    p2.setPreferredSize(new Dimension(200, 500));

    //user inputs crossover rate and mutation rate into JTextFields
    JLabel crossoverLabel = new JLabel("crossover rate");
    JLabel mutationLabel = new JLabel("mutation rate");
    crossoverField = new JTextField(""+crossover+"");
    DoubleInputHandler rateHandler = new DoubleInputHandler();
    crossoverField.addActionListener(rateHandler);
    mutationField = new JTextField("" + mutation + "");
    mutationField.addActionListener(rateHandler);

    //checkbox enables increase mutation code path
    CheckboxListener checkboxListener = new CheckboxListener();
    increaseMutationCheckbox = new JCheckBox("increase mutation rate"); // after several generations with no improvement");
    increaseMutationCheckbox.addItemListener(checkboxListener);

    //user inputs maximum number of generations and population size into JTextFields
    JLabel popSizeLabel = new JLabel("population size");
    JLabel numGenLabel = new JLabel("max number of generations");
    JLabel numStatesLabel = new JLabel("num states in Turing machines");
    populationField = new JTextField(1);
    generationsField = new JTextField(1);
    numStatesField = new JTextField(1);
    IntInputHandler popGenHandler = new IntInputHandler();
    populationField.addActionListener(popGenHandler);
    generationsField.addActionListener(popGenHandler);
    numStatesField.addActionListener(popGenHandler);

    //add all the components to panel 2
    p2.add(numStatesLabel);
    p2.add(numStatesField);
    p2.add(crossoverLabel);
    p2.add(crossoverField);
    p2.add(mutationLabel);
    p2.add(mutationField);
    //p2.add(increaseMutationCheckbox);
    p2.add(popSizeLabel);
    p2.add(populationField);
    p2.add(numGenLabel);
    p2.add(generationsField);


    //Panel 3: A large text area containing information about the genetic algorithm
    JPanel p3 = new JPanel();
    description = new JTextArea("Input genetic algorithm parameters on the left", 10, 10);
    description.setPreferredSize(new Dimension(400, 500));
    description.setEditable(false);
    //JScrollPane scrollPane = new JScrollPane(description); //TODO
    p3.add(description);


    //Button to run the program, and button to cancel running the program once it has started
    runGAButton = new JButton("Run Genetic Algorithm");
    stopGAButton = new JButton("Stop"); //TODO popup are you sure
    ButtonHandler buttonHandler = new ButtonHandler();
    runGAButton.addActionListener(buttonHandler);
    stopGAButton.addActionListener(buttonHandler);


    //Checkboxes for feature toggles
    JLabel p4Label = new JLabel("Optional Features");
    reachabilityFitnessCheckbox = new JCheckBox("include state reachability in fitness calculations"); // after several generations with no improvement");
    reachabilityFitnessCheckbox.addItemListener(checkboxListener);
    stateUseFitnessCheckbox = new JCheckBox("punish Turing machines which only use a subset of their states over many iterations fitness calculations"); //TODO rephrase
    stateUseFitnessCheckbox.addItemListener(checkboxListener);
    numHaltsFitnessCheckbox = new JCheckBox("encourage Turing machines with exactly one halt condition in fitness calculations");
    numHaltsFitnessCheckbox.addItemListener(checkboxListener);

    p4 = new JPanel();
    p4.add(runGAButton);

    p2.add(p4Label);
    p2.add(increaseMutationCheckbox);
    p2.add(reachabilityFitnessCheckbox);
    p2.add(stateUseFitnessCheckbox);
    p2.add(numHaltsFitnessCheckbox);


    //Add all panels to the frame
    getContentPane().add(BorderLayout.NORTH, p1);
    getContentPane().add(BorderLayout.WEST, p2);
    getContentPane().add(BorderLayout.EAST, p3);
    getContentPane().add(BorderLayout.SOUTH, p4);
    setVisible(true);
  }

  /*
  Main method
  */

  public static void main(String[] args) {
    //Run the user interface
    UserInterface ui = new UserInterface();
    ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }


  /*
  Event handlers
  */

  private class ButtonHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event){
      if(event.getSource() == runGAButton){
        //Provide feedback in large text area
        description.setText("Now running genetic algorithm...");
        description.setVisible(true);

        //Make all fields not editable
        crossoverField.setEnabled(false);
        mutationField.setEnabled(false);
        generationsField.setEnabled(false);
        populationField.setEnabled(false);
        numStatesField.setEnabled(false);
        increaseMutationCheckbox.setEnabled(false);
        numHaltsFitnessCheckbox.setEnabled(false);
        reachabilityFitnessCheckbox.setEnabled(false);
        stateUseFitnessCheckbox.setEnabled(false);

        //Replace runGAButton with stopGAButton
        p4.remove(runGAButton);
        p4.add(stopGAButton);
        p4.setVisible(true);
        p4.updateUI();

        //Call method that creates a GeneticAlgorithm object and calls its run method
        runGeneticAlgorithm();

      }
      else if(event.getSource() == stopGAButton){
        if(geneticAlgorithm != null){
          //Ask if user really wants to stop the genetic algorithm
          int input = JOptionPane.showConfirmDialog(stopGAButton, "Are you sure you want to stop the genetic algorithm?", "Cancel?",  JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);//JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
          if(input == 0){
            //Provide feedback in large text area
            description.setText("Now stopping genetic algorithm...");
            description.setVisible(true);

            //Make all fields editable again
            crossoverField.setEnabled(true);
            mutationField.setEnabled(true);
            generationsField.setEnabled(true);
            populationField.setEnabled(true);
            numStatesField.setEnabled(true);
            increaseMutationCheckbox.setEnabled(true);
            numHaltsFitnessCheckbox.setEnabled(true);
            reachabilityFitnessCheckbox.setEnabled(true);
            stateUseFitnessCheckbox.setEnabled(true);

            //replace stopGAButton with runGAButton
            p4.remove(stopGAButton);
            p4.add(runGAButton);
            p4.setVisible(true);
            p4.updateUI();

            //Stop GeneticAlgorithm's run method (after current generation)
            geneticAlgorithm.setStopRunning(true);
            //TODO - summarise?

          } //else user has said cancel so leave genetic algorithm running
        }
        else{ //geneticAlgorithm is null
          //TODO throw an error - even though should be unreachable - button shouldn't appear until after ga created
        }
      }
    }

  }

  //ActionListener for crossover rate and mutation rate parameters
  private class DoubleInputHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event){
      String currentDescription = description.getText();
      //User enters a value into the crossover rate field
      if(event.getSource() == crossoverField){
        try{
          Double d = Double.parseDouble(crossoverField.getText());
          if(d >= 0.0 && d <= 1.0){
            crossover = (double) d;
            currentDescription = currentDescription + "\nCrossover rate: " + crossover;
          }
          else{
            currentDescription = currentDescription + "\nInvalid crossover rate. Please input a double between 0 and 1.";
          }
        }
        catch(NumberFormatException nfe){
          currentDescription = currentDescription + "\nInvalid crossover rate. Please input a double between 0 and 1.";
        }
      }
      //User enters a value into the mutation rate field
      else if (event.getSource() == mutationField){
        try{
          Double d = Double.parseDouble(mutationField.getText());
          if(d >= 0.0 && d <= 1.0){
            mutation = (double) d;
            currentDescription = currentDescription + "\nMutation rate: " + mutation;
          }
          else{
            currentDescription = currentDescription + "\nInvalid mutation rate. Please input a double between 0 and 1.";
          }
        }
        catch(NumberFormatException nfe){
          currentDescription = currentDescription + "\nInvalid mutation rate. Please input a double between 0 and 1.";
        }
      }
      //Provide feedback that a change has been made (or an error has ocurred)
      description.setText(currentDescription);
      description.setVisible(true);
    }
  }

  //ActionListener for number of generations and population size parameters
  private class IntInputHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event){
      String currentDescription = description.getText();
      //User enters a value into the population size field
      if(event.getSource() == populationField){
        try{
          Integer pop = Integer.parseInt(populationField.getText());
          if(pop < 0){
            currentDescription = currentDescription + "\nPopulation size cannot be negative. Please enter a positive integer.";
          }
          else{
            populationSize = (int) pop;
            currentDescription = currentDescription + "\nPopulation size: " + populationSize;
          }
        }
        catch(NumberFormatException nfe){
          currentDescription = currentDescription + "\nInvalid population size. Please input an integer.";
        }
      }
      //User enters a value into the number of generations field
      else if (event.getSource() == generationsField){
        try{
          Integer numGen = Integer.parseInt(generationsField.getText());
          if(numGen < 0){
            currentDescription = currentDescription + "\nNumber of generations cannot be negative. Please enter a positive integer.";
          }
          else{
            numGenerations = (int) numGen;
            currentDescription = currentDescription + "\nGenetic algorithm will run for " + numGenerations + " generations.";
          }
        }
        catch(NumberFormatException nfe){
          currentDescription = currentDescription + "\nInvalid number of generations. Please input an integer.";
        }
      }
      //User enters a value into the number of states field
      else if (event.getSource() == numStatesField){
        try{
          Integer numStatesInt = Integer.parseInt(numStatesField.getText());
          if(numStatesInt < 0){
            currentDescription = currentDescription + "\nNumber of states in each Turing machine cannot be negative. Please enter a positive integer.";
          }
          else{
            numStates = (int) numStatesInt;
            currentDescription = currentDescription + "\nGenetic algorithm will run " + numStates + "-state Turing machines.";
          }
        }
        catch(NumberFormatException nfe){
          currentDescription = currentDescription + "\nInvalid number of states. Please input an integer.";
        }
      }
      //Provide user with feedback (either that value has changed or error has occurred)
      description.setText(currentDescription);
      description.setVisible(true);
    }
  }


  private class CheckboxListener implements ItemListener { //only have one checkbox atm but could make more
    @Override
    public void itemStateChanged(ItemEvent event) {
      String currentDescription = description.getText();
      if(event.getStateChange() == ItemEvent.SELECTED){
        if(event.getSource() == increaseMutationCheckbox){
          increaseMutation = true;
          currentDescription = currentDescription + "\nEnabled: Increase mutation rate if score does not increase after 10000 generations.";
        }
        else if(event.getSource() == reachabilityFitnessCheckbox){
          reachabilityFitnessEnabled = true;
          currentDescription = currentDescription + "\nEnabled: TODO";
        }
        else if(event.getSource() == stateUseFitnessCheckbox){
          stateUseFitnessEnabled = true;
          currentDescription = currentDescription + "\nEnabled: TODO";
        }
        else if(event.getSource() == numHaltsFitnessCheckbox){
          numHaltsFitnessEnabled = true;
          currentDescription = currentDescription + "\nEnabled: TODO";
        }

          //reachabilityFitnessCheckbox, stateUseFitnessCheckbox, numHaltsFitnessCheckbox
      }
      else if(event.getStateChange() == ItemEvent.DESELECTED){
        if(event.getSource() == increaseMutationCheckbox){
          increaseMutation = false;
          currentDescription = currentDescription + "\nDisabled: Increase mutation rate if score does not increase after __ generations.";
        }
        else if(event.getSource() == reachabilityFitnessCheckbox){
          reachabilityFitnessEnabled = false;
          currentDescription = currentDescription + "\nDisabled: TODO";
        }
        else if(event.getSource() == stateUseFitnessCheckbox){
          stateUseFitnessEnabled = false;
          currentDescription = currentDescription + "\nDisabled: TODO";
        }
        else if(event.getSource() == numHaltsFitnessCheckbox){
          numHaltsFitnessEnabled = false;
          currentDescription = currentDescription + "\nDisabled: TODO";
        }
      }
      description.setText(currentDescription);
      description.setVisible(true);

    }
  }

  /*
  Method that creates the genetic algorithm and displays information about it
  (called by button event handler in UserInterface constructor)
  */

  private void runGeneticAlgorithm(){
    //If user doesn't specify population size or number of generations, set it to 100
    int pop = (populationSize > 0? populationSize : 100);
    int gen = (numGenerations > 0? numGenerations : 10000);
    int states = (numStates > 0? numStates : 5);//maybe just dont run instead.. that would make more sense

    /*
    crossoverField.setEnabled(false);
    mutationField.setEnabled(false);
    generationsField.setEnabled(false);
    populationField.setEnabled(false);
    numStatesField.setEnabled(false);
    increaseMutationCheckbox.setEnabled(false);
    */

    //create a GeneticAlgorithm object
    try{
      geneticAlgorithm = new GeneticAlgorithm(pop, states, numGenerations, crossover, mutation, elitismRate);
    }
    catch(GeneticAlgorithmException gae){
      //TODO handle
    }

    //set optional features
    geneticAlgorithm.increaseMutationRate(increaseMutation);
    geneticAlgorithm.setNumHaltsFitnessFeature(numHaltsFitnessEnabled);
    geneticAlgorithm.setReachableFitnessFeature(reachabilityFitnessEnabled);
    geneticAlgorithm.setStateUsageFitnessFeature(stateUseFitnessEnabled);

    //run and monitor the genetic algorithm
    geneticAlgorithm.run();
    //TODO: update description JTextArea in real time
    description.setVisible(true);



  }

}
