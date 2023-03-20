package Ex3.source;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
/*
 * vb encode - decode
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_VB {
    public static void readData(List<String> dataList, String dataPath) {

        try {
            File myFile = new File(dataPath);
            Scanner myScanner = new Scanner(myFile);
            String data = "";
            while (myScanner.hasNext()) {
                data += myScanner.nextLine().toLowerCase() + " ";
                if (data.contains("/") && data.trim().length() > 1) {
                    dataList.add(data.trim().substring(data.trim().indexOf(" "), data.trim().length() - 1).trim());
                    data = "";
                }
            }
            myScanner.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("error when read data!! " + e.getMessage());
        }
    }

    public static void writeFile(String resultPath, List<String> results) {
        try {
            FileWriter myWriter = new FileWriter(resultPath);
            for (int i = 1; i <= results.size(); i++) {
                myWriter.write(i + " " + results.get(i - 1));
                myWriter.write(System.lineSeparator());

            }

            myWriter.close();
            System.out.println("Write message success!!");

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Write result fail!! ");
            System.out.println(e.getMessage());
        }

    }

    public static int[] decodeVB(byte[] vbCodes) {
        List<Integer> res = new ArrayList<>();
        int n = 0;
        int shift = 0;

        for (byte b : vbCodes) {
            // 0x80 = 1000 000 = 128
            if ((b & 0x80) != 0) {
                n = (b & 0x7f) | (n << shift);
                res.add(n);
                n = 0;
                shift = 0;
            } else {
                // 0x7f = 0111 1111 = 127
                n = (b & 0x7f) | (n << shift);
                shift = 7;
            }
        }
        int[] result = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {
            result[i] = res.get(i);
        }
        return result;
    }

    public static byte[] encodeVB(List<Integer> n) {

        List<Byte> bytes = new ArrayList<>();
        List<Byte> tmp = new ArrayList<>();
        boolean lastByte = true;
        for (int in : n) {
            while (true) {
                // 0x7f = 0111 1111 = 127
                byte b = (byte) (in & 0x7f);

                in = in >> 7;

                // 0x80 = 1000 000 = 128
                if (lastByte) {
                    tmp.add((byte) (b | 128));
                    lastByte = false;
                } else {
                    tmp.add(b);
                }
                if (in == 0) {
                    for (int i = 0; i < tmp.size(); i++) {
                        bytes.add(tmp.get(tmp.size() - i - 1));
                    }
                    tmp.clear();
                    lastByte = true;
                    break;
                }
            }
        }
        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = bytes.get(i);
        }
        return res;
    }

    public static String toVBString(byte[] bs) {
        String[] strs = new String[bs.length];
        for (int i = 0; i < bs.length; i++) {
            strs[i] = String.format("%8s", Integer.toBinaryString(bs[i])).replace(' ', '0');
            strs[i] = strs[i].substring(strs[i].length() - 8);
        }
        return Arrays.toString(strs);
    }

    public static void initInts(List<Integer> ints, String docVec) {
        String[] tmp = docVec.split(" ");
        for (String str : tmp) {
            if (!str.equals("")) {
                ints.add(Integer.parseInt(str));
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        List<String> results = new ArrayList<>();
        String resultPath = "Ex3\\source\\resultVB.txt";

        // read file term vecs to init list ints
        // List<String> termVecs = new ArrayList<>();
        // String termVecPath = "npl-data\\npl\\term-vecs";
        // /// load data from docvecs
        // readData(termVecs, termVecPath);
        // for (String string : termVecs) {
        // initInts(ints, string);
        // results.add(Arrays.toString(encodeVB(ints)));
        // ints.clear();
        // }
        // writeFile(resultPath, results);

        ints.add(824);
        ints.add(5);
        ints.add(214577);
        System.out.println("input " + Arrays.toString(ints.toArray()));
        System.out.println(toVBString(encodeVB(ints)));
        System.out.println(Arrays.toString(encodeVB(ints)));
        System.out.println(Arrays.toString(decodeVB(encodeVB(ints))));
    }
}
