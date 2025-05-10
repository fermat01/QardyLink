import javax.swing.*;
import java.awt.*;

public class QardylAppLink extends JFrame {
    private final ReadPanel readPanel;
    private final WritePanel writePanel;
    private final DeletePanel deletePanel;
    private final AboutPanel aboutPanel;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public QardylAppLink() {
        setTitle("QardyLink");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the window icon (make sure the image exists in /images inside your JAR)
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/logo_nfc.jpg")));

        // Define the custom background color
        Color bgColor = new Color(0xF2F8FC);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(bgColor);

        NFCManager nfcManager = new NFCManager();

        readPanel = new ReadPanel(nfcManager);
        writePanel = new WritePanel(nfcManager);
        deletePanel = new DeletePanel(nfcManager);
        aboutPanel = new AboutPanel(); 

        mainPanel.add(readPanel, "READ");
        mainPanel.add(writePanel, "WRITE");
        mainPanel.add(deletePanel, "DELETE");
        mainPanel.add(aboutPanel, "ABOUT"); 

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        switchPanel.setBackground(bgColor);

        JButton toAboutBtn = new JButton("À propos"); 
        JButton toWriteBtn = new JButton("Écrire un tag");
        JButton toReadBtn = new JButton("Lecture de la carte NFC");
        JButton toDeleteBtn = new JButton("Suppression des données d'un tag");

        toReadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toWriteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toDeleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toAboutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        toAboutBtn.addActionListener(e -> cardLayout.show(mainPanel, "ABOUT"));
        toWriteBtn.addActionListener(e -> cardLayout.show(mainPanel, "WRITE")); 
        toReadBtn.addActionListener(e -> cardLayout.show(mainPanel, "READ"));
        toDeleteBtn.addActionListener(e -> cardLayout.show(mainPanel, "DELETE"));
        
        switchPanel.add(toAboutBtn);
        switchPanel.add(toWriteBtn);
        switchPanel.add(toReadBtn);
        switchPanel.add(toDeleteBtn);

        // Set the background color for the content pane as well
        getContentPane().setBackground(bgColor);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(switchPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        cardLayout.show(mainPanel, "READ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QardylAppLink().setVisible(true));
    }
}
