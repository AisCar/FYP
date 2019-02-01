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
*/

//TODO comments everywhere, this is awful

public class UserInterface extends JFrame{
  //TODO: Rename other variables so that you can rename these ones w/o mixing them up
  private double crossover, mutation;
  private int populationSize, numGenerations;
  private boolean increaseMutation;
  private JTextField crossoverField, mutationField, generationsField, populationField;
  private JTextArea description;
  private JCheckBox increaseMutationCheckbox;

  public UserInterface(){
    super("Busy Beavers");

    //set some default values
    crossover = 0.7;
    mutation = 0.05;
    numGenerations = -1;
    populationSize = -1;

    //set up window details (move this line down?)
    setSize(600,400);

    //Panel 1: Header area
    JPanel p1 = new JPanel();
    JLabel title = new JLabel("Optimising Turing Machine Busy Beaver scores using a Genetic Algorithm");
    title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 16));
    p1.add(title);


    //Panel 2: Set crossover rate, mutation rate, population size and num generations
    JPanel p2 = new JPanel();
    p2.setLayout(new GridLayout(10,1));
    p2.setPreferredSize(new Dimension(175, 500));

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
    populationField = new JTextField(1);
    generationsField = new JTextField(1);
    IntInputHandler popGenHandler = new IntInputHandler();
    populationField.addActionListener(popGenHandler);
    generationsField.addActionListener(popGenHandler);

    //add all the components to panel 2
    p2.add(crossoverLabel);
    p2.add(crossoverField);
    p2.add(mutationLabel);
    p2.add(mutationField);
    p2.add(increaseMutationCheckbox);
    p2.add(popSizeLabel);
    p2.add(populationField);
    p2.add(numGenLabel);
    p2.add(generationsField);


    //Panel 3: A large text area containing information ...
    JPanel p3 = new JPanel();
    //JTextArea
    description = new JTextArea("Input genetic algorithm parameters on the left", 10, 10);
    description.setPreferredSize(new Dimension(375, 500));
    description.setEditable(false);
    //going for a console look?
    /*
    description.setForeground(Color.WHITE);
    description.setBackground(Color.BLACK);
    description.setFont(Font.getFont(Font.DIALOG_INPUT)); //not noticing a difference... uh oh.
    */
    p3.add(description);


    //Button to run the program
    JButton runGAButton = new JButton("Run Genetic Algorithm");
    runGAButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent event){
        description.setText("Now running genetic algorithm...");
        description.setVisible(true);
        //TODO set all in panel 2 to not editable - maybe replace em w a new panel?
        runGeneticAlgorithm();
      }
    });


    //Add all panels to the frame
    getContentPane().add(BorderLayout.NORTH, p1);
    getContentPane().add(BorderLayout.WEST, p2); //p2a and p2b?
    getContentPane().add(BorderLayout.EAST, p3);
    getContentPane().add(BorderLayout.SOUTH, runGAButton);
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

  //ActionListener for crossover rate and mutation rate parameters
  private class DoubleInputHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event){
      String currentDescription = description.getText();
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
      description.setText(currentDescription);
      description.setVisible(true);
    }
  }

  //ActionListener for number of generations and population size parameters
  private class IntInputHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event){
      String currentDescription = description.getText();
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
      description.setText(currentDescription);
      description.setVisible(true);
    }
  }


  private class CheckboxListener implements ItemListener { //only have one checkbox atm but could make more
    @Override
    public void itemStateChanged(ItemEvent event) {
      if(event.getStateChange() == ItemEvent.SELECTED){
        increaseMutation = true; //will need another line or two if adding more checkboxes
      }
      else if(event.getStateChange() == ItemEvent.DESELECTED){
        increaseMutation = false;
      }
    }
  }

  /*
  Method that creates the genetic algorithm and displays information about it
  (called by button event handler in constructor)
  */

  private void runGeneticAlgorithm(){
    //TODO: on hitting run GA button, initialise a GeneticAlgorithm object
    //periodically get description from this object
    //display the info on the description jtextarea
    

  }

}
