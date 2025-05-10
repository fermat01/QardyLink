import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NdefUtil {
    public static List<NdefRecordInfo> parseAllNdefRecords(byte[] data) {
        List<NdefRecordInfo> records = new ArrayList<>();
        int i = 0;
        while (i < data.length && data[i] != 0x03) i++;
        if (i >= data.length - 2) return records;
        int ndefLen = data[i + 1] & 0xFF;
        int ndefStart = i + 2;
        int ndefEnd = ndefStart + ndefLen;
        if (ndefEnd > data.length) ndefEnd = data.length;

        i = ndefStart;
        while (i < ndefEnd) {
            if (i + 1 >= ndefEnd) break;
            int header = data[i] & 0xFF;
            boolean mb = (header & 0x80) != 0;
            boolean me = (header & 0x40) != 0;
            boolean sr = (header & 0x10) != 0;
            boolean il = (header & 0x08) != 0;
            int tnf = header & 0x07;

            int typeLength = data[i + 1] & 0xFF;
            int payloadLength;
            int index = i + 2;
            if (sr) {
                payloadLength = data[index] & 0xFF;
                index += 1;
            } else {
                if (index + 3 >= ndefEnd) break;
                payloadLength = ((data[index] & 0xFF) << 24) | ((data[index + 1] & 0xFF) << 16)
                        | ((data[index + 2] & 0xFF) << 8) | (data[index + 3] & 0xFF);
                index += 4;
            }
            int idLength = il ? (data[index++] & 0xFF) : 0;

            if (index + typeLength + idLength + payloadLength > ndefEnd) break;

            byte[] type = new byte[typeLength];
            System.arraycopy(data, index, type, 0, typeLength);
            index += typeLength;

            if (il) index += idLength;

            byte[] payload = new byte[payloadLength];
            System.arraycopy(data, index, payload, 0, payloadLength);
            index += payloadLength;

            records.add(new NdefRecordInfo(tnf, type, payload));
            i = index;
            if (me) break;
        }
        return records;
    }

    public static String tryPayloadToString(NdefRecordInfo rec) {
        if (rec.tnf == 0x01 && rec.type.length == 1 && rec.type[0] == 0x55 && rec.payload.length > 1) {
            String[] uriPrefixes = {
                    "", "http://www.", "https://www.", "http://", "https://",
                    "tel:", "mailto:", "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://",
                    "sftp://", "smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://",
                    "imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:", "btspp://",
                    "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://", "file://", "urn:epc:id:",
                    "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:", "urn:nfc:"
            };
            int prefix = rec.payload[0] & 0xFF;
            String uri = (prefix < uriPrefixes.length ? uriPrefixes[prefix] : "") +
                    new String(rec.payload, 1, rec.payload.length - 1, StandardCharsets.UTF_8);
            return uri;
        }
        String text = new String(rec.payload, StandardCharsets.UTF_8).trim();
        if (text.matches("\\A\\p{Print}*\\z")) return text;
        return bytesToHex(rec.payload);
    }

    public static String tnfToString(int tnf) {
        switch (tnf) {
            case 0x0: return "Empty";
            case 0x1: return "Well-known";
            case 0x2: return "MIME Media";
            case 0x3: return "Absolute URI";
            case 0x4: return "External";
            case 0x5: return "Unknown";
            case 0x6: return "Unchanged";
            case 0x7: return "Reserved";
            default: return "Unknown";
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }

    public static byte[] createNdefUrlTlv(String url) {
        byte[] urlBytes = url.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte uriIdentifierCode = 0x00; // no prefix
        byte[] ndef = new byte[5 + urlBytes.length];
        ndef[0] = (byte) 0xD1; // MB/ME/SR/TNF=1 (well-known)
        ndef[1] = 0x01; // Type Length
        ndef[2] = (byte) (1 + urlBytes.length); // Payload Length
        ndef[3] = 0x55; // Type = 'U' (URI)
        ndef[4] = uriIdentifierCode;
        System.arraycopy(urlBytes, 0, ndef, 5, urlBytes.length);
    
        // TLV: 0x03, length, NDEF, 0xFE
        int tlvLen = 2 + ndef.length + 1;
        byte[] tlv = new byte[tlvLen];
        tlv[0] = 0x03;
        tlv[1] = (byte) ndef.length;
        System.arraycopy(ndef, 0, tlv, 2, ndef.length);
        tlv[tlv.length - 1] = (byte) 0xFE;
        return tlv;
    }
    
}
