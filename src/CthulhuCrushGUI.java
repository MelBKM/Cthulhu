import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;



public class CthulhuCrushGUI extends JPanel {

    static final int GROESSE = 8;
    static final int TILE_SIZE = 64;
    static String[] monsterNamen = {"cthulhu", "nyarlathotep", "shoggoth", "dagon"};
    static ImageIcon[][] monsterFeld = new ImageIcon[GROESSE][GROESSE];
    static Random rand = new Random();
    private Point ersteAuswahl = null;
    private Clip hintergrundMusik;
    
    
    // Konstruktor – lädt zufällig Monsterbilder ins Feld
    public CthulhuCrushGUI() {
        for (int i = 0; i < GROESSE; i++) {
            for (int j = 0; j < GROESSE; j++) {
                int index = rand.nextInt(monsterNamen.length);
                String pfad = "images/" + monsterNamen[index] + ".png";
                monsterFeld[i][j] = new ImageIcon(pfad); 
            }
        }
        
        
        setPreferredSize(new Dimension(GROESSE * TILE_SIZE, GROESSE * TILE_SIZE));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;

                if (ersteAuswahl == null) {
                    ersteAuswahl = new Point(x, y);
                } else {
                    Point zweiteAuswahl = new Point(x, y);
                    versucheTausch(ersteAuswahl, zweiteAuswahl);
                    ersteAuswahl = null;
                }
            }
        });
    }
 
    
    private void loescheVerbindungen() {
        System.out.println("loescheVerbindungen() wurde aufgerufen");
        boolean[][] zuLöschen = new boolean[GROESSE][GROESSE];

        // Horizontale Dreierreihen markieren
        for (int i = 0; i < GROESSE; i++) {
            for (int j = 0; j < GROESSE - 2; j++) {
                System.out.println("Überprüfe Position (" + i + "," + j + ")");
                ImageIcon m1 = monsterFeld[i][j];
                ImageIcon m2 = monsterFeld[i][j + 1];
                ImageIcon m3 = monsterFeld[i][j + 2];
                if (m1 != null && m1.equals(m2) && m1.equals(m3)) {
                    zuLöschen[i][j] = true;
                    zuLöschen[i][j + 1] = true;
                    zuLöschen[i][j + 2] = true;
                    System.out.println("Horizontale Dreierreihe bei (" + i + "," + j + ")");
                }
            }
        }

        // Vertikale Dreierreihen markieren
        for (int j = 0; j < GROESSE; j++) {
            for (int i = 0; i < GROESSE - 2; i++) {
                System.out.println("Überprüfe Position (" + i + "," + j + ")");
                ImageIcon m1 = monsterFeld[i][j];
                ImageIcon m2 = monsterFeld[i + 1][j];
                ImageIcon m3 = monsterFeld[i + 2][j];
                if (m1 != null && m1.equals(m2) && m1.equals(m3)) {
                    zuLöschen[i][j] = true;
                    zuLöschen[i + 1][j] = true;
                    zuLöschen[i + 2][j] = true;
                }
                System.out.println("Vertikale Dreierreihe bei (" + i + "," + j + ")");
            }
        }

        // Monster entfernen und durch neue ersetzen
        for (int i = 0; i < GROESSE; i++) {
            for (int j = 0; j < GROESSE; j++) {
                if (zuLöschen[i][j]) {
                    int index = rand.nextInt(monsterNamen.length);
                    String pfad = "images/" + monsterNamen[index] + ".png";
                    monsterFeld[i][j] = new ImageIcon(pfad);
                }
                System.out.println("Monster bei (" + i + "," + j + ") gelöscht und erneuert");
            }
        }

        repaint(); // Neues Bild zeichnen
    }


    // Tausch Methode
    private void versucheTausch(Point p1, Point p2) {
        if (sindNachbarn(p1, p2)) {
            // Monster tauschen
            ImageIcon temp = monsterFeld[p1.y][p1.x];
            monsterFeld[p1.y][p1.x] = monsterFeld[p2.y][p2.x];
            monsterFeld[p2.y][p2.x] = temp;

            // Verbindungen löschen und erneuern
            loescheVerbindungen();
            
            // Spielfeld neu zeichnen
            repaint();
        }
    }

    private boolean sindNachbarn(Point p1, Point p2) {
        int dx = Math.abs(p1.x - p2.x);
        int dy = Math.abs(p1.y - p2.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    // Zeichnet die Monster auf dem Spielfeld
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < GROESSE; i++) {
            for (int j = 0; j < GROESSE; j++) {
                if (monsterFeld[i][j] != null) {
                    g.drawImage(monsterFeld[i][j].getImage(), j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }
    }
    
    private void starteHintergrundmusik(String pfad) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(pfad));
            hintergrundMusik = AudioSystem.getClip();
            hintergrundMusik.open(audioIn);
            hintergrundMusik.loop(Clip.LOOP_CONTINUOUSLY); // Dauerschleife
        } catch (Exception e) {
            e.printStackTrace();
        }
     //   System.out.println(new File("sounds/halloween.wav").exists());
    }
    
    // sounds einfügen
    private void spieleSound(String dateipfad) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(dateipfad).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Einstiegspunkt – erzeugt das Fenster und zeigt das Panel
    public static void main(String[] args) {
    	System.out.println("Hallo, Welt!");
        JFrame frame = new JFrame("Cthulhu Crush 🐙");
        CthulhuCrushGUI spielPanel = new CthulhuCrushGUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new CthulhuCrushGUI());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        //starte Hintergrundmusik
        spielPanel.starteHintergrundmusik("sounds/monster.wav");
    }
}
