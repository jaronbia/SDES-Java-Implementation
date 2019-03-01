public class SDES {
    private String key;
    private BinaryNumber key1, key2;
    private String plaintext;
    private String ciphertext;
    private boolean modflag;  // Flag to use to signal the use of modified S1 box
    private int[] IP = { 2, 6, 3, 1, 4, 8, 5, 7 };
    private int[] EP = { 4, 1, 2, 3, 2, 3, 4, 1 };
    private int[] inverseIP = { 4, 1, 3, 5, 7, 2, 8, 6 };
    private int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 3, 2} };
    private int[][] S1 = {
            {0, 1, 2, 3},
            {2, 0, 1, 3},
            {3, 0, 1, 0},
            {2, 1, 0, 3} };
    private int[][] S1P = {
            {3, 0, 1, 0},
            {2, 0, 1, 3},
            {0, 1, 2, 3},
            {2, 1, 0, 3} };

    SDES(String key, String plaintext, boolean modflag) {
        this.key = key;
        this.plaintext = plaintext;
        this.modflag = modflag;
        keyGeneration();
    }

    SDES(String key, String plaintext) {
        this.key = key;
        this.plaintext = plaintext;
        modflag = false;
        keyGeneration();
    }

    public String encryption() {
        StringBuilder en = new StringBuilder();

        // Apply initial permutation
        for(int elem : IP) en.append(plaintext.charAt(elem-1));

        BinaryNumber e1 = fx(en.toString(), key1);
        String swappedBin = swap(e1);
        System.out.println("encryption swapped: " + swappedBin);
        BinaryNumber e2 = fx(swappedBin, key2);

        en.delete(0, en.length());
        // Retrieve inverseIP of e2
        for(int elem : inverseIP) en.append(e2.toString().charAt(elem-1));

        ciphertext = en.toString();

        System.out.println("Ciphertext: " + ciphertext);

        return ciphertext;
    }

    public String decryption() {
        StringBuilder de = new StringBuilder();

        // Apply initial permutation
        for(int elem : IP) de.append(ciphertext.charAt(elem-1));

        BinaryNumber d1 = fx(de.toString(), key2);
        String swappedBin = swap(d1);
        System.out.println("decryption swapped: " + swappedBin);
        BinaryNumber d2 = fx(swappedBin, key1);

        de.delete(0, de.length());
        // Retrieve inverseIP of d2
        for(int elem : inverseIP) de.append(d2.toString().charAt(elem-1));

        System.out.println("Decryption result: " + de.toString());

        return de.toString();
    }

    public BinaryNumber fx(String bits, BinaryNumber key) {
        StringBuilder sr = new StringBuilder();
        StringBuilder s0left = new StringBuilder();
        StringBuilder s1right = new StringBuilder();

        // Takes right side and expands it (+3 is to make sure it takes from right)
        for(int elem : EP) sr.append(bits.charAt(elem+3));

        BinaryNumber expandedPerm = new BinaryNumber(sr.toString());
        int[] xorEP = BinaryNumber.XOR(expandedPerm, key);

        sr.delete(0, sr.length());  // Empties sr

        // Find S0 box value
        for(int Lindex = 0; Lindex < xorEP.length/2; ++Lindex) s0left.append(xorEP[Lindex]);
        sr.append(lookupSbox(s0left.toString(), '0'));

        // Find S1 or S1P box value: If modflag is true then modified S1 box (S1P) will be used
        for(int Rindex = xorEP.length/2; Rindex < xorEP.length; ++Rindex) s1right.append(xorEP[Rindex]);
        sr.append(lookupSbox(s1right.toString(), !modflag ? '1' : 'p'));

        BinaryNumber p4bits = new BinaryNumber(generateP4(sr.toString())); //
        BinaryNumber leftbits = new BinaryNumber(bits.substring(0, bits.length()/2));
        BinaryNumber xorLeft = new BinaryNumber(BinaryNumber.XOR(leftbits, p4bits));

        BinaryNumber rightbits = new BinaryNumber(bits.substring(bits.length()/2, bits.length()));

        return new BinaryNumber(xorLeft.toString() + rightbits.toString());
    }

    /*
        Lookup Sbox value lookup
     */

    public String lookupSbox(String bits, char sbox) {
        String[] position = {"00", "01", "10", "11"};
        StringBuilder rowbits = new StringBuilder().append(bits.charAt(0)).append(bits.charAt(3));
        StringBuilder columnbits = new StringBuilder().append(bits.charAt(1)).append(bits.charAt(2));
        int row, column;

        // Find Row
        for(row = 0; row < position.length; ++row) {
            if(rowbits.toString().equals(position[row])) break;
        }
        // Find column
        for(column = 0; column < position.length; ++column) {
            if(columnbits.toString().equals(position[column])) break;
        }

        if(sbox == 'p') return position[S1P[row][column]];  // Lookup S1 prime

        return position[sbox == '0' ? S0[row][column] : S1[row][column]];
    }

    /*
       Swap left and right side of the binary number
    */
    public String swap(BinaryNumber b) {
        BinaryNumber bin = b;
        int halfsize = bin.size() - ( bin.size() / 2 );

        // To swap perform a left circular shift that is half the size of the BinaryNumber
        bin.lcShift(0, bin.size() - 1, halfsize);

        return bin.toString();
    }

    public void keyGeneration() {
        int[] p10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
        int[] p8 = {6, 3, 7, 4, 8, 5, 10, 9};
        StringBuilder sr = new StringBuilder();

        for(int elem : p10) sr.append(key.charAt(elem-1));
        BinaryNumber keyp10 = new BinaryNumber(sr.toString());

        keyp10.lcShift(0, ( keyp10.size() / 2 ) - 1);
        keyp10.lcShift(keyp10.size() / 2 , keyp10.size() - 1);

        sr.delete(0, sr.length());
        for(int elem : p8) sr.append(keyp10.toString().charAt(elem-1));

        key1 = new BinaryNumber(sr.toString());

        keyp10.lcShift(0, ( keyp10.size() / 2 ) - 1, 2);
        keyp10.lcShift(keyp10.size() / 2 , keyp10.size() - 1, 2);

        sr.delete(0, sr.length());
        for(int elem : p8) sr.append(keyp10.toString().charAt(elem-1));

        key2 = new BinaryNumber(sr.toString());
    }

    public String generateP4(String bits) {
        StringBuilder sr = new StringBuilder();

        sr.append(bits.charAt(1)).append(bits.charAt(3));
        sr.append(bits.charAt(2)).append(bits.charAt(0));

        return sr.toString();
    }
}
