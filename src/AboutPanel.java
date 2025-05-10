import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        // Set the background color to #F2F8FC
        setBackground(new Color(0xF2F8FC));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("À propos de QardyLink");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea description = new JTextArea(
            "QardyLink est une application de gestion de tags NFC, proprieté de Qardyl.\n" +
            "Vous pouvez lire, écrire et supprimer des données sur vos tags NFC facilement.\n\n" +
            "Pour en savoir plus, consultez les liens ci-dessous :"
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setOpaque(false);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- LOGO + VERSION PANEL ---
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setOpaque(false); // stays transparent, so no need to set background
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load the original image icon
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/logo_nfc.jpg"));
        // Scale the image to 64x64 pixels (adjust size as needed)
        Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);

        // Version label
        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // space between logo and text
        versionLabel.setVerticalAlignment(SwingConstants.CENTER);

        logoPanel.add(logoLabel);
        logoPanel.add(versionLabel);

        JButton btnSite1 = createLinkButton("Développé par @fermatfils", "https://github.com/fermat01");
        JButton btnSite2 = createLinkButton("Site officiel", "https://www.qardyl.com");

        add(title);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(description);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(logoPanel); // <-- Add the logo + version here
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(btnSite2);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnSite1);
    }

    private JButton createLinkButton(String text, String url) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Impossible d'ouvrir le lien.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        return button;
    }
}
