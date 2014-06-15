import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

/**
 * Die Klasse ImageViewer ist angelehnt an
 * http://openbook.galileodesign.de/javainsel5 
 * Sie bietet eine
 * Benutzeroberflaeche zum Laden und Skalieren von Bildern im .jpg und .gif
 * Format.
 *
 * @author braeckle
 *
 */

/**
 * ViewComponent verwaltet die Anzeigeflaeche fuer das Bild
 */
class ViewComponent extends JComponent {
    private static final long serialVersionUID = 1L;

    /**
     * Das angezeigte Bild
     */
    private Picture pic;

    /**
     * Das Bild vor der letzten Operation für die Funktion undo()
     */
    private Picture oldpic;

    public ViewComponent() {
        super();
        // File file = new File("bilder/test.gif");
        // this.setImage(file);
    }

    /**
     * Laedt ein Bild aus der Datei file
     */
    public void setImage(File file) {
        if (pic != null)
            oldpic = new Picture(pic.getImage());
        if (file == null || !file.isFile())
            return;
        try {
            pic = new Picture(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!pic.isNull())
            repaint();
    }

    /**
     * Speichert das Bild in die Datei file
     */
    public void saveImage(File file) {
        if (pic == null || pic.isNull())
            return;
        try {
            pic.save(file.getAbsolutePath(), "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Veraendert die Maße des Bildes zu neWidth/newHight mit der
     * Interpolationsmethode mode
     */
    public void scaleImage(int newWidth, int newHeight, int mode) {
        if (newHeight < 1 || newWidth < 1) {
            System.out.println("Bild wird zu klein");
            return;
        }
        oldpic = new Picture(pic.getImage());
        if (pic != null && !pic.isNull()) {
            pic.scale(newWidth, newHeight, mode);
            repaint();
        }
    }

    /**
     * Veraendert die Grösse des Bildes um einen Factor scale mit der
     * Interpolationsmethode mode
     */
    public void scaleImage(double scale, int mode) {
        if (pic != null && !pic.isNull()) {
            int newWidth = (int) Math.round(pic.getWidth() * scale);
            int newHeight = (int) Math.round(pic.getHeight() * scale);
            this.scaleImage(newWidth, newHeight, mode);
        }
    }

    /**
     * Macht die letzte Bild-Aktion rueckgaengig = Laedt oldpic
     */
    public void undo() {
        if (oldpic != null && !oldpic.isNull()) {
            pic = new Picture(oldpic.getImage());
            oldpic = null;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (pic != null && !pic.isNull())
            g.drawImage(pic.getImage(), 0, 0, this);
    }
}

/***********************************************************/

/**
 * Die Benutzeroberflaeche
 */
public class ImageViewer extends JFrame implements ActionListener,
        MouseWheelListener {
    private static final long serialVersionUID = 1L;
    JMenuBar mbar = new JMenuBar();
    /**
     * Datei Menue
     */
    JMenu filemenu = new JMenu("Datei");
    JMenuItem openitem = new JMenuItem("Öffnen");
    JMenuItem saveitem = new JMenuItem("Speichern");
    JMenuItem undoitem = new JMenuItem("Undo");
    /**
     * Combobox fuer die Interpolationsmethoden
     */
    String[] modeStrings = {"Nearest", "Linear", "Poly", "Cubic"};
    JComboBox interpolationmodeBox = new JComboBox(modeStrings);
    /**
     * Textfeld fuer die Eingabe des Skalierungsfaktors
     */
    JTextField scaleFactorField = new JTextField(1 + "", 4);
    /**
     * Der Skalieren-Button
     */
    JButton scaleButton = new JButton("Skalieren!");
    private ViewComponent viewComponent = new ViewComponent();

    /**
     * Der Konstruktor fuegt alle Komponenten zusammen
     */
    public ImageViewer() {
        super("Bildbetrachter");

        openitem.addActionListener(this);
        //saveitem.addActionListener(this);
        undoitem.addActionListener(this);
        filemenu.add(openitem);
        //filemenu.add(saveitem);
        filemenu.add(undoitem);

        interpolationmodeBox.setSelectedIndex(0);
        scaleButton.addActionListener(this);

        mbar.add(filemenu);
        mbar.add(interpolationmodeBox);
        mbar.add(scaleFactorField);
        mbar.add(scaleButton);

        scaleFactorField.addActionListener(this);

        setJMenuBar(mbar);
        add(viewComponent);

		/* Mouseradlistener */
        this.addMouseWheelListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
    }

    /**
     * Starten der Benutzeroberflaeche
     */
    public static void main(String[] args) {
        new ImageViewer().setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        JComponent source = (JComponent) (e.getSource());

		/* Oeffnen eines neuen Bildes */
        if (source == (JComponent) openitem) {
            JFileChooser d = new JFileChooser();
            d.setCurrentDirectory(new File("./"));

            d.setFileFilter(new PictureFilter());
            int returnVal = d.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = d.getSelectedFile();
                viewComponent.setImage(file);
                System.out.println("Opening: " + file.getName() + ".\n");
            } else {
                System.out.println("Open command cancelled by user.\n");
            }
        }
        /* Speichern des Bildes */
        else if (source == (JComponent) saveitem) {
            JFileChooser d = new JFileChooser();
            d.setCurrentDirectory(new File("./"));

            d.setFileFilter(new PictureFilter());
            int returnVal = d.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = d.getSelectedFile();
                viewComponent.saveImage(file);
                System.out.println("Saving: " + file.getName() + ".\n");
            } else {
                System.out.println("Open command cancelled by user.\n");
            }

			/* Rueckgaengig machen der letzten Aktion */
        } else if (source == (JComponent) undoitem) {
            viewComponent.undo();

			/* Das Bild skalieren */
        } else if (source == (JComponent) scaleButton
                || source == (JComponent) scaleFactorField) {
            int mode = interpolationmodeBox.getSelectedIndex();
            try {
                double scalefactor = Double.parseDouble(scaleFactorField
                        .getText());
                if (scalefactor >= 0)
                    viewComponent.scaleImage(scalefactor, mode);
                else
                    System.out.println("Bitte positive Zahl eingeben!");
            } catch (NumberFormatException exept) {
                System.out.println("Bitte eine Zahl eingeben!");
            }
        }

    }

    @Override
    /**
     * Mit dem Mausrad laesst sich das Bild um einen 10% Faktor skalieren
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mode = interpolationmodeBox.getSelectedIndex();

        int notches = e.getWheelRotation();
        if (notches < 0) {
            viewComponent.scaleImage(Math.pow(0.8, -notches), mode);
        } else {
            viewComponent.scaleImage(Math.pow(1.2, notches), mode);
        }
    }

    /**
     * *****************************************************
     */

    class PictureFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory()
                    || f.getName().toLowerCase().endsWith(".jpg")
                    || f.getName().toLowerCase().endsWith(".gif")
                    || f.getName().toLowerCase().endsWith(".bmp")
                    || f.getName().toLowerCase().endsWith(".png");
        }

        @Override
        public String getDescription() {
            return "*.jpg;*.gif;*.bmp;*.png";
        }

    }

}