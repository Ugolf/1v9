package com.company;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matik on 2014-12-15.
 */
public class Gui extends JFrame{
    private JPanel rootPanel;
    private JLabel questionNrLabel;
    private JButton randButton;
    private JButton sendButton;
    private JLabel answerLabel;
    private JLabel goodLabel;
    private JLabel wrongLabel;
    private JLabel countGood;
    private JLabel countWrong;
    private JLabel allLabel;
    private JLabel countAll;
    private JButton increaseButton;
    private JButton decreaseButton;
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel correctLabel;
    private JTextField correctField;
    private JButton fileChoose;
    private JLabel fileNameLabel;
    private JLabel readStatusLabel;
    private JTextArea questionArea;
    private JComboBox toleranceBox;
    private JLabel toleranceLabel;
    private JLabel headerLabel;

    int  n = 0, m = 0, good = 0, wrong = 0, all = 0, tolerance = 0;
    Random rand = new Random();
    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    BufferedImage header;
    String soundsGood[] = { "sounds/good.wav", "sounds/good2.wav", "sounds/good3.wav", "sounds/good4.wav" };
    String soundsWrong[] = { "sounds/wrong.wav", "sounds/wrong2.wav", "sounds/wrong3.wav", "sounds/wrong4.wav" };
   /*
    Media goodSound = new Media("good.mp3");
    Media badSound = new Media("bad.mp3");
    MediaPlayer goodPlayer = new MediaPlayer(goodSound);
    MediaPlayer badPlayer = new MediaPlayer(badSound);
    */

    public Gui(){
        super("jeden z dziewięciu v.0.12");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,500);
        //questionArea.setLineWrap(true);

        //domyslnie
        readFile("baza.txt");

        JMenuBar menubar = new JMenuBar();
        ImageIcon icon = new ImageIcon("exit.png");

        JMenu file = new JMenu("Plik");
        JMenu about = new JMenu("O programie");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem eMenuItem = new JMenuItem("Wyjscie", icon);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });


        final JMenuItem loadMenuItem = new JMenuItem("Wczytaj pytania", icon);
        loadMenuItem.setMnemonic(KeyEvent.VK_W);
        loadMenuItem.setToolTipText("Wczytuje pytania z wybranego pliku");
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setCurrentDirectory(new File("."));
                int returnVal = jFileChooser.showOpenDialog(Gui.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser.getSelectedFile();
                    fileNameLabel.setText("" + file.getName());
                    readFile(file.getName());
                } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                    fileNameLabel.setText("Brak");
                } else if (returnVal == JFileChooser.ERROR_OPTION) {
                    fileNameLabel.setText("Bląd!");
                } else {
                    fileNameLabel.setText("Nieznany...");
                }
            }
        });

        JMenuItem resetMenuItem = new JMenuItem("Resetuj liczniki", icon);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wrong = 0; good = 0; all = 0;
                countWrong.setText(""+wrong);
                countGood.setText(""+good);
                countAll.setText(""+all);
            }
        });
        JMenuItem helpMenuItem = new JMenuItem("Pomoc");
        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(rootPanel, "Plik .txt: pytanie ? odpowiedz\n" +
                        "CTRL + S - Losuj pytanie\n" +
                        "CTRL + D - Odejmij punkt\n" +
                        "CTRL + F - Dodaj punkt\n" +
                        "CTRL + W - Wczytaj plik\n", "Pomoc", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem aboutMenuItem = new JMenuItem("O autorze");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pt1 = "<html><body width='150'>";
                String pt2 =
                        "<h1>Jeden z dziewięciu!</h1>" +
                                "<p>Napisał i pisze <i>Matik</i> <br>" +
                                " Java + swing <br>" +
                                " 08.02.2015 <br>" +
                                " Wersja 0.12 <br><br>" +
                                "<p>...</p>";
                String s = pt1 + pt2;
                JOptionPane.showMessageDialog(rootPanel,
                        s,
                        "O autorze",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        JMenuItem bugsMenuItem = new JMenuItem("Bugi");
        bugsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bug = "<html><body width='250'>" +
                        " - 'zatwierdzam' <br>" +
                        " - layout <br>" +
                        " - pytania o konrketną liczbę <br>";
                JOptionPane.showMessageDialog(rootPanel,
                        bug,
                        "Błędy",
                        JOptionPane.PLAIN_MESSAGE);
            }

        });

        file.add(loadMenuItem);
        file.add(resetMenuItem);
        file.add(eMenuItem);
        about.add(helpMenuItem);
        about.add(aboutMenuItem);
        about.add(bugsMenuItem);
        menubar.add(file);
        menubar.add(about);
        setJMenuBar(menubar);
        toleranceBox.addItem("0");
        toleranceBox.addItem("1");
        toleranceBox.addItem("2");
        toleranceBox.addItem("3");
        toleranceBox.addItem("4");
        toleranceBox.addItem("5");
        toleranceBox.addItem("6");
        toleranceBox.setEditable(true);


        try {
            header = ImageIO.read(new File("img/header.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        headerLabel = new JLabel(new ImageIcon(header));
        add(headerLabel);

        /*
        Czionki
         */
        Font questionFont = new Font("Sans", Font.PLAIN, 18);
        Font answerFont = new Font("Sans", Font.PLAIN, 16);
        Font correctFont = new Font("Sans", Font.PLAIN, 16);
        questionArea.setFont(questionFont);
        answerField.setFont(answerFont);
        correctField.setFont(correctFont);

        rootPanel.setBorder(new EmptyBorder(10,10,10,10));
        setResizable(false);
/*
        fileChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == fileChoose) {
                    final JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("."));
                    int returnVal = jFileChooser.showOpenDialog(Gui.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = jFileChooser.getSelectedFile();
                        fileNameLabel.setText("" + file.getName());
                        readFile(file.getName());
                    } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                        fileNameLabel.setText("Brak");
                    } else if (returnVal == JFileChooser.ERROR_OPTION) {
                        fileNameLabel.setText("Bląd!");
                    } else {
                        fileNameLabel.setText("Nieznany...");
                    }
                }
            }
        });
  */

        /*
        Keybindings
         */
        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action_rand", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randButton.doClick();
             //   System.out.println("Rand question");
            }
        });
        actionMap.put("action_increase", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increaseButton.doClick();
               // System.out.println("Increase point");
            }
        });
        actionMap.put("action_decrease", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decreaseButton.doClick();
               //System.out.println("Decrease point");
            }
        });
        actionMap.put("load_file", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMenuItem.doClick();
                //System.out.println("Decrease point");
            }
        });

        InputMap keyMap = new ComponentInputMap(rootPanel);
        keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                java.awt.Event.CTRL_MASK), "action_increase");
        keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                java.awt.Event.CTRL_MASK), "action_rand");
        keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,
                java.awt.Event.CTRL_MASK), "action_decrease");
        keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,
                java.awt.Event.CTRL_MASK), "load_file");
        SwingUtilities.replaceUIActionMap(rootPanel, actionMap);
        SwingUtilities.replaceUIInputMap(rootPanel, JComponent.WHEN_IN_FOCUSED_WINDOW,
                keyMap);
        // *****************************************************

        countGood.setText(String.valueOf(good));
        countWrong.setText(String.valueOf(wrong));
        countAll.setText(String.valueOf(all));

        toleranceBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tolerance = toleranceBox.getSelectedIndex();
                System.out.println("Tolerancja: " + tolerance);
            }
        });

        randButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                n = rand.nextInt(questions.size());
                System.out.println(n);
                questionArea.setText(questions.get(n));
                questionNrLabel.setText("Pytanie nr: " + n);
                answerField.setText("");
                correctField.setText("");
                rootPanel.requestFocusInWindow();
                answerField.requestFocus();
            }
        });
        answerField.requestFocus();
        answerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String odp ="";
                if(answerField.getText().isEmpty() == false){
                    if(questions.get(n).contains("wiek") || questions.get(n).contains("rok") || answerField.getText().length() < tolerance){
                        odp = answerField.getText().substring(0, answerField.getText().length());

                        if((answers.get(n).toUpperCase()).contains(odp.toUpperCase())==true){
                            System.out.println("ODP: "+odp);
                            good++;
                            all++;
                            updateValue(good, wrong, all);
                            correctLabel.setText("Dobrze!");
                            playSound(1);
                        } else {
                            wrong++;
                            all++;
                            updateValue(good,wrong,all);
                            correctLabel.setText("Źle!");
                            playSound(0);
                        }


                    }else {
                        odp = answerField.getText().substring(0, answerField.getText().length() - tolerance);
                        if((answers.get(n).toUpperCase()).contains(odp.toUpperCase())==true){
                            System.out.println("ODP: "+odp);
                            good++;
                            all++;
                            updateValue(good,wrong,all);
                            correctLabel.setText("Dobrze!");
                            playSound(1);
                        } else {
                            wrong++;
                            all++;
                            updateValue(good,wrong,all);
                            correctLabel.setText("Źle!");
                            playSound(0);
                        }
                    }
                }
                if(answerField.getText().isEmpty() == true){
                    wrong++;all++;
                    updateValue(good,wrong,all);
                    playSound(0);
                }
                correctField.setText(answers.get(n));
                System.out.println(odp.toUpperCase()+" "+answers.get(n).toUpperCase());
            }
        });

        increaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                good++; wrong--;
                updateValue(good,wrong,all);
            }
        });

        decreaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                good--; wrong++;
                updateValue(good,wrong,all);
            }
        });


        randButton.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_F8){randButton.doClick();}
                if(e.getKeyCode() == KeyEvent.VK_END){randButton.doClick();}
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    void updateValue(int g, int w, int a){
        countAll.setText(""+a);
        countGood.setText(""+g+"            "+ round(((float) g / (float) a)*100,1) +" %");
        countWrong.setText(""+w+"            "+ round(((float) w / (float) a)*100,1) +" %");
    }


    void insertBase(String line){
        String[] elements = line.split("\\? ");
        questions.add(elements[0]);
        answers.add(elements[1]);
    }

    void clearBase(){
        questions.removeAll(questions);
        answers.removeAll(answers);
    }

    void readFile(String name){
        clearBase();
        try {
            File fileDir = new File(name);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));

            String str;
            while ((str = in.readLine()) != null && str.length() > 0 ) {
                insertBase(str);
            }
            in.close();
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        if(answers.size() != questions.size()){
            readStatusLabel.setText("Plik niespojny");
        }else{ readStatusLabel.setText("Wczytywanie poprawne   Pytań: "+questions.size());}
        //System.out.println(questions);
        //System.out.println(questions.size());
        //System.out.println(answers);
        //System.out.println(answers.size());
    }

    public void playSound(int x){
        m = rand.nextInt(soundsWrong.length);
        String gongFile;
        if (x==0){
            gongFile = soundsWrong[m];
        } else{
            gongFile = soundsGood[m];
        }
        InputStream in = null;
        try {
            in = new FileInputStream(gongFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        AudioStream audioStream = null;
        try {
            audioStream = new AudioStream(in);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        AudioPlayer.player.start(audioStream);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static double round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
