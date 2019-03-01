public class Main {
    public static void main(String[] args) {
        SDES s = new SDES("1010000010", "10111101");
        s.encryption();
        s.decryption();
        System.out.println("--------------------");
        SDES s1 = new SDES("1011010011", "00011101");
        s1.encryption();
        s1.decryption();
        System.out.println("--------------------");
        SDES s2 = new SDES("0010110011", "00111101", true);
        s2.encryption();
        s2.decryption();
    }
}