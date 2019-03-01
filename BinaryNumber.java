public class BinaryNumber {

    private int[] binNumber;

    BinaryNumber(String binary) {
        binNumber = new int[binary.length()];

        for(int index = binary.length()-1; index >= 0; --index)
            binNumber[index] = (int) binary.charAt(index) - '0';
    }

    BinaryNumber(int[] binary) {
        binNumber = binary;
    }

    public int size() { return binNumber.length; }

    /*
        Left circular shift that specifies how many rotations you'd like
     */
    public void lcShift(int start, int end, int rotations) {
        for(int index = 0; index < rotations; ++index) lcShift(start, end);
    }

    /*
        Left circular shift within subarray of binary number.
     */
    public void lcShift(int start, int end) {
        int temp = binNumber[start]; // Stores element at end of left shift

        for(int index = start; index < end; ++index)  // Shift all the elements left
                binNumber[index] = binNumber[index + 1];

        binNumber[end] = temp;
    }

    /*
        XOR operation on two objects of BinaryClass type
     */
    public static int[] XOR(BinaryNumber b1, BinaryNumber b2) {
        int[] xorBin = new int[b1.binNumber.length];

        for(int index = 0; index < b1.binNumber.length; ++index)
            xorBin[index] = b1.binNumber[index] ^ b2.binNumber[index];

        return xorBin;
    }

    public String toString() {
        StringBuilder sr = new StringBuilder();
        for(int num : binNumber) sr.append(num);
        return sr.toString();
    }
}