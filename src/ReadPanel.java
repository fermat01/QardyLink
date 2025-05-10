import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReadPanel extends JPanel {
    private final JTextArea outputArea = new JTextArea(16, 50);
    private final NFCManager nfcManager;
    private final Font outputFont = new Font("Segoe UI", Font.PLAIN, 15);
    private volatile boolean running = true;
    private volatile String lastReaderName = "";
    private volatile boolean readerMissingDialogShown = false;

    public ReadPanel(NFCManager nfcManager) {
        this.nfcManager = nfcManager;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.decode("#F3FFFF"));

        outputArea.setEditable(false);
        outputArea.setFont(outputFont);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.getViewport().setFont(outputFont);

        add(scrollPane, BorderLayout.CENTER);

        // Initial reader check
        SwingUtilities.invokeLater(this::checkInitialReader);
    }

    private void checkInitialReader() {
        String readerName = "";
        try {
            readerName = nfcManager.updateAndGetCurrentReaderName();
        } catch (Exception e) {
            // Could not even query for readers
            showReaderMissingDialog("Erreur lors de la détection du lecteur NFC: " + e.getMessage());
            return;
        }

        if (readerName == null || readerName.isEmpty()) {
            showReaderMissingDialog("Aucun lecteur NFC détecté. Veuillez brancher un lecteur compatible et redémarrer l'application.");
        } else {
            startReaderAndTagMonitor();
        }
    }

    private void showReaderMissingDialog(String message) {
        if (!readerMissingDialogShown) {
            readerMissingDialogShown = true;
            outputArea.setEnabled(false);
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
                message,
                "Lecteur NFC non détecté",
                JOptionPane.ERROR_MESSAGE
            );
            // Optionally: Disable further actions or close the app
            // System.exit(1);
        }
    }

    private void startReaderAndTagMonitor() {
        Thread monitorThread = new Thread(() -> {
            boolean lastPresent = false;
            while (running) {
                try {
                    String readerName = nfcManager.updateAndGetCurrentReaderName();

                    if (!readerName.equals(lastReaderName)) {
                        lastReaderName = readerName;
                        if (!readerName.isEmpty()) {
                            appendStatus("Lecteur NFC détecté: " + readerName);
                            enableOutputArea(true);
                            readerMissingDialogShown = false;
                        } else {
                            appendStatus("Aucun lecteur NFC trouvé. Veuillez brancher un lecteur compatible.");
                            enableOutputArea(false);
                            if (!readerMissingDialogShown) {
                                SwingUtilities.invokeLater(() -> showReaderMissingDialog(
                                    "Aucun lecteur NFC détecté. Veuillez brancher un lecteur compatible."
                                ));
                            }
                        }
                    }

                    boolean tagPresent = !readerName.isEmpty() && nfcManager.isTagPresent();

                    if (tagPresent && !lastPresent) {
                        appendStatus("Carte NFC détectée. Lecture...");
                        readTag();
                    } else if (!tagPresent && lastPresent) {
                        appendStatus("Carte NFC retirée. Patientez quelques secondes...");
                    }

                    lastPresent = tagPresent;

                } catch (Exception e) {
                    appendStatus("Erreur: " + e.getMessage());
                    // Optionally log to file or system log here
                }

                try {
                    Thread.sleep(700); // Poll every 700ms
                } catch (InterruptedException ignored) {}
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    private void readTag() {
        try {
            List<NdefRecordInfo> records = nfcManager.readTag();
            if (records.isEmpty()) {
                appendStatus("Pas de données NDEF trouvées sur la carte.");
            } else {
                appendStatus("Données NDEF trouvées sur la carte:");
                for (int i = 0; i < records.size(); i++) {
                    NdefRecordInfo rec = records.get(i);
                    appendStatus("Récords #" + (i + 1) + ":");
                    appendStatus("  TNF: " + rec.tnf + " (" + NdefUtil.tnfToString(rec.tnf) + ")");
                    appendStatus("  Type: " + NdefUtil.bytesToHex(rec.type) + " (" + new String(rec.type) + ")");
                    appendStatus("  Payload (" + rec.payload.length + " bytes): " + NdefUtil.tryPayloadToString(rec));
                }
            }
        } catch (Exception e) {
            appendStatus("Erreur lors de la lecture de la carte: " + e.getMessage());
        }
    }

    private void appendStatus(String msg) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(msg + "\n");
            outputArea.setFont(outputFont); // Force font after every append
        });
    }

    private void enableOutputArea(boolean enable) {
        SwingUtilities.invokeLater(() -> outputArea.setEnabled(enable));
    }

    @Override
    public void removeNotify() {
        running = false;
        super.removeNotify();
    }
}
