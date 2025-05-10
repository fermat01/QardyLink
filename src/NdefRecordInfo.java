public class NdefRecordInfo {
    public final int tnf;
    public final byte[] type;
    public final byte[] payload;

    public NdefRecordInfo(int tnf, byte[] type, byte[] payload) {
        this.tnf = tnf;
        this.type = type;
        this.payload = payload;
    }
}


