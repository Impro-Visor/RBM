/**
 * This Java Class is part of the RBM-provisor Application
 * which, in turn, is part of the Intelligent Music Software
 * project at Harvey Mudd College, under the directorship of Robert Keller.
 *
 * Copyright (C) 2009 Robert Keller and Harvey Mudd College
 *
 * RBM-provisor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * RBM-provisor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RBM-provisor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package rbm;
import javax.swing.*;
import java.io.*;
/**
 *
 * @author Peter Swire
 */

public class MainFrame extends JFrame {
  static final long serialVersionUID = 2679202602307945852L;
    private MusicBrain brain;
    private FileSelector fileSelector;
    private ParameterPanel paramsPanel;
    private GenerationPanel genPanel;
    private TrainingPanel trainingPanel;
    private EnergyDisplay energyDisplay;
    private ReceptiveFieldPanel receptiveFieldPanel;
    private boolean dirtyBit;
    private Thread currTrainer;

    public MainFrame() {
        super("RBM-provisor");
        dirtyBit = false;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(fileSelector = new FileSelector(this));
        mainPanel.add(leftPanel);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.add(paramsPanel = new ParameterPanel());
        middlePanel.add(genPanel = new GenerationPanel(this));

        mainPanel.add(middlePanel);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(trainingPanel = new TrainingPanel(this));
        rightPanel.add(receptiveFieldPanel = new ReceptiveFieldPanel(this));

        //put energyPanel in separate panel in order to make border work
        JPanel energyPanel = new JPanel();
        energyPanel.add(energyDisplay = new EnergyDisplay(430,250));
        energyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Energy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Lucida Grande", 1, 13)));
        rightPanel.add(energyPanel);

        mainPanel.add(rightPanel);

        this.add(mainPanel);

        pack();

    }

    public int[] getLayerSizes() {
        return paramsPanel.getLayerSizes();
    }

    public int getNumEpochs() {
        return paramsPanel.getNumEpochs();
    }

    public float getLearningRate() {
        return paramsPanel.getLearningRate();
    }

    public boolean transposeInputs() {
        return trainingPanel.transposeInputs();
    }

    public boolean useWindowing() {
        return trainingPanel.useWindowing();
    }

    public int getWindowLength() {
        return trainingPanel.getWindowLength();
    }

    public int getStepSize() {
        return trainingPanel.getTrainingStepSize();
    }

    public File[] getTrainingData() {
        return fileSelector.getSelectedFiles();
    }

    public int getNumGenCycles() {
        return genPanel.getNumGenCycles();
    }

    public int getNumOutputs() {
        return genPanel.getNumOutputs();
    }

    public ProgressBars getTrainingProgressBars() {
        return trainingPanel.getProgressBars();
    }

    public EnergyDisplay getEnergyDisplay() {
        return energyDisplay;
    }

    public boolean hasBrain() {
        return (brain != null);
    }


    public void trainBrain()
    {
        trainBrain(false);
    }

    public void trainBrain(boolean continuedTraining) {
        File[] inputFiles = fileSelector.getSelectedFiles();
        if (inputFiles == null || inputFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No files selected");
            System.out.println("No files selected");
        } else {
            checkDirtyBit();
            
            String extension = FileParser.getExtension(inputFiles[0]);
            for (int i = 0; i < inputFiles.length; i++) {
                if (!FileParser.getExtension(inputFiles[i]).equals(extension)) {
                    JOptionPane.showMessageDialog(this, "Input files are of different types");
                    System.out.println("File type mismatch");
                    return;
                }
            }

            trainingPanel.setAllButtonsEnabled(false);
            trainingPanel.setStopButtonEnabled(true);
            genPanel.setButtonEnabled(false);
            paramsPanel.setComponentsEnabled(false);
            receptiveFieldPanel.setComponentsEnabled(false);

            energyDisplay.reset(paramsPanel.getNumLayers(), paramsPanel.getNumEpochs());
            brain = new MusicBrain(this);
            genPanel.importSettings(brain);
            dirtyBit = true;
            if(!continuedTraining)
            {
                currTrainer = new TrainingWorker();
                currTrainer.start();
            }
            else
            {
                //instantiate continuous training worker and start
                currTrainer = new ContinuousTrainingWorker(paramsPanel.getLayerSizes(), this);
                currTrainer.start();
                
            }
            
        }
    }

    public void addLayerToBrain() {
        if (brain != null) {
            int newLayerSize = 0;
            String input = (String) (JOptionPane.showInputDialog(
                    this,
                    "Set new layer size",
                    "Add layer",
                    JOptionPane.PLAIN_MESSAGE));
            if (input == null) {
                return;
            }
            try {
                newLayerSize = Integer.parseInt(input);
                if (newLayerSize <= 0) {
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Number format error", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            

            if (newLayerSize > 0) {
                trainingPanel.setAllButtonsEnabled(false);
                trainingPanel.setStopButtonEnabled(true);
                genPanel.setButtonEnabled(false);
                paramsPanel.setComponentsEnabled(false);
                receptiveFieldPanel.setComponentsEnabled(false);

                paramsPanel.addLayer(newLayerSize);

                energyDisplay.reset(paramsPanel.getNumLayers(), paramsPanel.getNumEpochs());

                dirtyBit = true;

                currTrainer = new TrainingWorker(newLayerSize);
                currTrainer.start();
            }
        }
    }


    public void saveBrain() {
        JFileChooser chooser = new JFileChooser();
        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            try {
                String filepath = chooser.getSelectedFile().getCanonicalPath();
                if (!filepath.endsWith(".brain")) filepath += ".brain";
                FileParser.writeObject(brain, filepath);
                dirtyBit = false;
            } catch (IOException ex) {
                System.err.println("Error saving to file");
            }
        }
    }

    public void loadBrain() { 
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return (f.getName().endsWith(".brain"));
            }

            public String getDescription() {
                return "MusicBrain files (.brain)";
            }
        });
        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            brain = (MusicBrain)FileParser.readObject(chooser.getSelectedFile());
            brain.setOwner(this);
            paramsPanel.importSettings(brain);
            trainingPanel.importSettings(brain);
            genPanel.importSettings(brain);
            fileSelector.importSettings(brain);
        }
    }

    public void generateOutput() {
        if (brain != null) {
            trainingPanel.setAllButtonsEnabled(false);
            genPanel.setAllComponentsEnabled(false);
            receptiveFieldPanel.setComponentsEnabled(false);
            new GenerationWorker().start();
        }
    }

    private File getSeed()
    {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Choose Seed");
        fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return (f.getName().endsWith(".lick") || f.getName().endsWith(".ls"));
            }
            public String getDescription() {
                return "Lick or Leadsheet files (.lick or .ls)";
            }
        });
        int status = fc.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }
    }

    public void stopTraining() {
        if (currTrainer != null) {
            currTrainer.interrupt();
        }
    }

    public void writeReceptiveField(boolean clustering, boolean thresholding, String filename) {
        if (brain != null) {
                trainingPanel.setAllButtonsEnabled(false);
                genPanel.setButtonEnabled(false);
                paramsPanel.setComponentsEnabled(false);
                receptiveFieldPanel.setComponentsEnabled(false);
                final boolean tempClustering = clustering;
                final boolean tempThresholding = thresholding;
                final String tempFilename = filename;
                new Thread() {
                    @Override
                    public void run() {
                        brain.writeReceptiveFieldImage(tempClustering, tempThresholding, tempFilename);
                        trainingPanel.setAllButtonsEnabled(true);
                        genPanel.setButtonEnabled(true);
                        paramsPanel.setComponentsEnabled(true);
                        receptiveFieldPanel.setComponentsEnabled(true);
                    }
                }.start();

        }
    }

    private void checkDirtyBit() {
        if (dirtyBit == true) {
            int status = JOptionPane.showOptionDialog(this, "Changes have been made to the Music Brain. \n" +
                    "Would you like to save the state of the machine?",
                    "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (status == JOptionPane.YES_OPTION) {
                saveBrain();
            }
        }
    }

    class TrainingWorker extends Thread {

        int newLayerSize; //Used for adding layers

        public TrainingWorker() {
            this.newLayerSize = 0;
        }

        public TrainingWorker(int newLayerSize) {
            this.newLayerSize = newLayerSize;
        }

        @Override
        public void run() {
            try {
                if (newLayerSize == 0) {
                    brain.train();
                } else {
                    brain.addLayerToBrain(newLayerSize);
                }
            } catch (InterruptedException e)
            {
                
            }
            trainingPanel.setAllButtonsEnabled(true);
            trainingPanel.setStopButtonEnabled(false);
            genPanel.setButtonEnabled(true);
            paramsPanel.setComponentsEnabled(true);
            receptiveFieldPanel.setComponentsEnabled(true);
            currTrainer = null;
        }
    }
    class ContinuousTrainingWorker extends Thread {

        int[] layerSizes; //Used for adding layers
        MainFrame owner;
        public ContinuousTrainingWorker(int[] layerSizes, MainFrame owner) {
            this.layerSizes = layerSizes;
            this.owner = owner;
        }

        @Override
        public void run() {
            try {
                if(layerSizes.length > 0)
                {
                    //erase brain's layers to only contain the first untrained rbm
                    brain.resetToFirstLayer();
                    //set our brain's epochs to the epoch step size; each time we train, we will train by that many epochs
                    brain.setNumEpochs(trainingPanel.getEpochStepSize());
                    //for each layer we'll train
                    for(int i = 0; i < layerSizes.length; i++)
                    {

                        for(int y = 1; y <= trainingPanel.getEpochSteps(); y++)
                        {
                            if(!(new File("Brains/ContinuousGen/" + i + "Layer" + (y*trainingPanel.getEpochStepSize()) + "Epoch.brain").isFile()))
                            {
                                //if we are past the first layer's round of training for all epochs, now we'll split and train each brain off of the saved brain for that epoch level
                                if(i > 0)
                                {
                                    brain = (MusicBrain) FileParser.readObject("Brains/ContinuousGen/" + (i-1) + "Layer" + (y*trainingPanel.getEpochStepSize()) + "Epoch.brain");
                                    //add layer to brain, which will be auto-trained
                                    brain.setNumEpochs(y*trainingPanel.getEpochStepSize());
                                    brain.setOwner(owner);
                                    brain.addLayerToBrain(layerSizes[i]);
                                }
                                else
                                {
                                //write brain for each epoch step on each layer
                                    brain.train(i);
                                }
                                
                                FileParser.writeObject(brain, "Brains/ContinuousGen/" + i + "Layer" + (y*trainingPanel.getEpochStepSize()) + "Epoch.brain");
                            }
                        }
                    }
                }
            } catch (InterruptedException e)
            {
                
            }
            trainingPanel.setAllButtonsEnabled(true);
            trainingPanel.setStopButtonEnabled(false);
            genPanel.setButtonEnabled(true);
            paramsPanel.setComponentsEnabled(true);
            receptiveFieldPanel.setComponentsEnabled(true);
            currTrainer = null;
        }
    }

    class GenerationWorker extends Thread {

        @Override
        public void run() {
            universalGenerateHelper();
            trainingPanel.setAllButtonsEnabled(true);
            genPanel.setAllComponentsEnabled(true);
            paramsPanel.setComponentsEnabled(true);
            receptiveFieldPanel.setComponentsEnabled(true);
        }
    }

    private void universalGenerateHelper() {
        File chordSeedFile = null;

        chordSeedFile = getSeed();
        if (chordSeedFile == null) {
            return;
        }
        
        DataVessel generationSeed = FileParser.parseFile(chordSeedFile);
        System.out.println(generationSeed.getLength() + " parsed length");
        generationSeed = DataGenerator.chordData(generationSeed.getMelodySize(), generationSeed.getChords());
        System.out.println(generationSeed.getLength() + "chord length");
        DataVessel output = null;

        // used as a tag at the end of a filename, so that all the outputs can be associated with each other
        long time = System.currentTimeMillis();

        for (int i = 0; i < genPanel.getNumOutputs(); i++) {
            if (!genPanel.windowedOn()) { //non-windowed case
                output = brain.generate(generationSeed, genPanel.getNumGenCycles());
            } else { //windowed case
                if (brain.getNumMelodyRows() % genPanel.getGenStepSize() != 0) {
                    JOptionPane.showMessageDialog(this, "Chosen seed and step size incompatible with trained network. \n" +
                            "Make sure the file and network have the same pitch range and that step size divides " + brain.getNumMelodyRows(),
                            "LRBM Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                output = brain.loopingWindowedGenerate(generationSeed, genPanel.getNumGenCycles(),
                        genPanel.getGenStepSize(), genPanel.getNewMelodySize());
            }
            
            String extension = genPanel.getSelectedFileExtension();
            String filename = genPanel.getOutDirectory() + File.separatorChar + genPanel.getOutFilename() + "_" + i + "_" + time +
                    extension;

            if (extension.equals(".lick")) {
                FileParser.writeDataVesselToFile(output, filename);
            } else if (extension.equals(".ls")) {
                //resolution scalar seems to affect both reading and writing of Leadsheets, maybe increasing it can improve detection of smaller features
                FileParser.writeDataVesselToLeadsheet(output, leadsheet.Constants.BEAT / leadsheet.Constants.RESOLUTION_SCALAR, filename);
            }


        }
        brain.resetVisFrame();
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            //meh
        }
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
    }


}
