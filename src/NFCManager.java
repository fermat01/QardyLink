import javax.smartcardio.*;
import java.util.List;

public class NFCManager {
    private volatile CardTerminal terminal;

    public NFCManager() {
        // No initialization here, handled dynamically
    }

    // Dynamically find the first available reader
    public String updateAndGetCurrentReaderName() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();
            if (!terminals.isEmpty()) {
                terminal = terminals.get(0);
                return terminal.getName();
            } else {
                terminal = null;
                return "";
            }
        } catch (Exception e) {
            terminal = null;
            return "";
        }
    }

    // Check if a tag is present
    public boolean isTagPresent() {
        try {
            return terminal != null && terminal.isCardPresent();
        } catch (Exception e) {
            return false;
        }
    }

    // Read all NDEF records from the tag
    public List<NdefRecordInfo> readTag() throws Exception {
        if (terminal == null)
            throw new Exception("Aucun lecteur NFC detecté.");
        if (!terminal.waitForCardPresent(5000))
            throw new Exception("Aucune carte NFC detectée.");
        Card card = null;
        try {
            card = terminal.connect("*");
            CardChannel channel = card.getBasicChannel();
    
            byte[] tagData = new byte[160];
            int offset = 0;
            for (int page = 4; page < 44; page += 4) {
                byte[] cmd = new byte[] {
                        (byte) 0xFF, (byte) 0xB0, 0x00, (byte) page, 0x10
                };
                ResponseAPDU response = channel.transmit(new CommandAPDU(cmd));
                if (response.getSW() != 0x9000) {
                    throw new Exception("Échec de lecture de la carte NFC à la page " + page + ". SW=" + Integer.toHexString(response.getSW()));
                }
                byte[] data = response.getData();
                System.arraycopy(data, 0, tagData, offset, data.length);
                offset += data.length;
            }
            return NdefUtil.parseAllNdefRecords(tagData);
        } finally {
            if (card != null) {
                try { card.disconnect(false); } catch (Exception ignored) {}
            }
        }
    }
    

    // Write a URL to the tag
    public void writeUrlToTag(String url) throws Exception {
        if (terminal == null)
            throw new Exception("Aucun lecteur NFC detecté.");
        if (!terminal.waitForCardPresent(10000))
            throw new Exception("Aucune carte  NFC détecté dans les 10 secondes...");
        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();

        byte[] ndefMessage = NdefUtil.createNdefUrlTlv(url);
        int page = 4;
        for (int i = 0; i < ndefMessage.length; i += 4) {
            byte[] cmd = new byte[9];
            cmd[0] = (byte) 0xFF;
            cmd[1] = (byte) 0xD6;
            cmd[2] = 0x00;
            cmd[3] = (byte) page;
            cmd[4] = 0x04;
            for (int j = 0; j < 4; j++) {
                cmd[5 + j] = (i + j < ndefMessage.length) ? ndefMessage[i + j] : 0x00;
            }
            ResponseAPDU response = channel.transmit(new CommandAPDU(cmd));
            if (response.getSW() != 0x9000) {
                card.disconnect(false);
                throw new Exception("Échec d'écriture de la carte NFC à la page " + page + ". SW=" + Integer.toHexString(response.getSW()));
            }
            page++;
        }
        card.disconnect(false);
    }


    // Erase the NDEF message from the tag
public void deleteTag() throws Exception {
    if (terminal == null)
        throw new Exception("Aucun lecteur NFC detecté.");
    if (!terminal.waitForCardPresent(10000))
        throw new Exception("Aucune carte  NFC détecté dans les 10 secondes...");
    Card card = terminal.connect("*");
    CardChannel channel = card.getBasicChannel();

    // Create an empty NDEF message (TLV: 0x03 0x00 0xFE)
    // 0x03: NDEF Message TLV tag
    // 0x00: Length = 0
    // 0xFE: Terminator TLV
    byte[] emptyNdef = new byte[] { (byte)0x03, 0x00, (byte)0xFE, 0x00 };

    int page = 4;
    // Write the empty NDEF message to the first page
    byte[] cmd = new byte[9];
    cmd[0] = (byte) 0xFF;
    cmd[1] = (byte) 0xD6;
    cmd[2] = 0x00;
    cmd[3] = (byte) page;
    cmd[4] = 0x04;
    for (int j = 0; j < 4; j++) {
        cmd[5 + j] = (j < emptyNdef.length) ? emptyNdef[j] : 0x00;
    }
    ResponseAPDU response = channel.transmit(new CommandAPDU(cmd));
    if (response.getSW() != 0x9000) {
        card.disconnect(false);
        throw new Exception("Échec de l'effacement du tag de la carte NFC à la page " + page + ". SW=" + Integer.toHexString(response.getSW()));
    }
    card.disconnect(false);
}

}
