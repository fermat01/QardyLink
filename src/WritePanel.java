import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class WritePanel extends JPanel {
    private final JTextField urlField = new JTextField(35);
    private final JTextPane outputArea = new JTextPane(); 
    private final NFCManager nfcManager;

    public WritePanel(NFCManager nfcManager) {
        this.nfcManager = nfcManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.decode("#F3FFFF"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
        inputPanel.setBackground(getBackground());
        JLabel urlLabel = new JLabel("URL de carte:");
        urlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        urlField.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // url input size control
        JButton writeBtn = new JButton("Écris le tag");
        writeBtn.setBackground(Color.decode("#3d9c1d"));
        writeBtn.setForeground(Color.WHITE); 
        writeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        writeBtn.setOpaque(true);
        writeBtn.setBorderPainted(false);
        writeBtn.setPreferredSize(new Dimension(150, 30));
        writeBtn.addActionListener(e -> new Thread(this::writeUrlToTag).start());
        inputPanel.add(urlLabel);
        inputPanel.add(urlField);
        inputPanel.add(writeBtn);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    // Méthode utilitaire pour afficher du texte coloré
    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyledDocument doc = tp.getStyledDocument();
        Style style = tp.addStyle("Color Style", null);
        StyleConstants.setForeground(style, c);
        try {
            doc.insertString(doc.getLength(), msg, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void writeUrlToTag() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            appendToPane(outputArea, "Veuillez entrer un url !\n", Color.RED);
            urlField.setText("");
            return;
        }
        appendToPane(outputArea, "En attente de l'écriture d'un tag NFC...\n", Color.GRAY);
        try {
            nfcManager.writeUrlToTag(url);
            appendToPane(outputArea, "URL écrit avec succès sur le tag NFC !\n", Color.decode("#3d9c1d"));
            urlField.setText(""); // Clear field after success
        } catch (Exception e) {
            appendToPane(outputArea, "Erreur: " + e.getMessage() + "\n", Color.RED);
            urlField.setText(""); // Clear field after error
        }
    }
}
