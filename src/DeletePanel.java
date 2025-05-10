import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeletePanel extends JPanel {
    public DeletePanel(NFCManager nfcManager) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Attention, une fois le tag supprimé vous perdrez les données !", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.RED);

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setPreferredSize(new Dimension(180, 40));

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    DeletePanel.this,
                    "Voulez-vous vraiment supprimer le contenu du tag NFC ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        nfcManager.deleteTag();
                        JOptionPane.showMessageDialog(DeletePanel.this,
                                "Tag supprimé avec succès !",
                                "Suppression",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DeletePanel.this,
                                "Erreur lors de la suppression du tag :\n" + ex.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Center the button in the middle of the UI
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(deleteButton, new GridBagConstraints());

        add(label, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
