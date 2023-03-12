import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.FileWriter;

/*
 * 
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_MatrixMarker {

    private static List<String> matrix = new ArrayList<String>();

    public static void readVocab(String vocabPath, List<String> vocabList) {
        try {
            File myFile = new File(vocabPath);
            Scanner myScanner = new Scanner(myFile);

            String data;
            String[] dataList;
            while (myScanner.hasNext()) {
                data = myScanner.nextLine();
                dataList = data.trim().toLowerCase().split(" ");
                if (dataList.length == 3) {
                    vocabList.add(dataList[1]);
                    matrix.add(new String());
                } else
                    System.out.println(data);
            }
            myScanner.close();
        } catch (Exception e) {
            System.out.println("read vocab error!  " + e.getMessage());
        }
    }

    public static void addAbookNameList(String book, List<String> bookNameList) {
        bookNameList.add(book);
        // add a col value 0 to matrix
        for (int i = 0; i < matrix.size(); i++) {
            matrix.set(i, matrix.get(i) + "0");
        }
    }

    public static void readBookContent(String bookContent, String book, List<String> vocabList,
            List<String> bookNameList) {
        // if a word in the book stored in vocabList, change the value of matrix
        // [indexOfWord, indexOfbookNameList] to 1

        for (int i = 0; i < vocabList.size(); i++) {
            if (bookContent.contains(vocabList.get(i))) {
                String tmp = matrix.get(i);
                matrix.set(i, tmp.substring(0, tmp.length() - 1) + "1");
            }
        }
    }

    public static void writeResult(String resultPath, List<String> bookNameList, List<String> vocabList) {
        try {
            File myFile = new File(resultPath);
            FileWriter myWriter = new FileWriter(resultPath);
            for (String bookString : bookNameList) {
                myWriter.write(bookString + " ");
            }
            myWriter.write(System.lineSeparator());
            for (String wordString : vocabList) {
                myWriter.write(wordString + " ");
            }
            myWriter.write(System.lineSeparator());
            for (String row : matrix) {
                myWriter.write(row);
                myWriter.write(System.lineSeparator());
                // myWriter.write(System.lineSeparator() + "/" + System.lineSeparator());
            }
            myWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Write result fail!! ");
            System.out.println(e.getMessage());
        }

        System.out.println("Write message success!!");
    }

    public static void loadResult(String resultPath, List<String> bookNameList, List<String> vocabList) {

        try {
            File myFile = new File(resultPath);
            Scanner myReader = new Scanner(myFile);
            // count: biến xác định dòng là bookNameList, word, matrix
            int count = 0;
            while (myReader.hasNext()) {
                String data = myReader.nextLine();

                if (count == 0) {
                    count++;
                    String[] dataList = data.split(" ");
                    for (String string : dataList) {
                        bookNameList.add(string);
                    }
                } else if (count == 1) {
                    count++;
                    String[] dataList = data.split(" ");
                    for (String string : dataList) {
                        vocabList.add(string);
                    }
                } else {
                    matrix.add(data);
                }
            }
            myReader.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Load result file fail!!");
            System.out.println(e.getMessage());

        }
        System.out.println("Load result success!!");
    }

    public static void loadFile(String docPath, List<String> bookNameList, List<String> vocabList) {
        String vocalPath = "npl\\term-vocab";
        readVocab(vocalPath, vocabList); // load vocab from npl data file
        // loadAllWord(vocabList); // load all vocab from doc-text
        try {
            File myFile = new File(docPath);
            Scanner myReader = new Scanner(myFile);
            String myData = "";
            String book = "";
            boolean isbookNameList = true;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.trim().equals("/")) {
                    readBookContent(myData, book, vocabList, bookNameList);
                    myData = "";
                    isbookNameList = true;
                } else if (isbookNameList) {
                    book = data.trim();
                    addAbookNameList(book, bookNameList);
                    isbookNameList = false;
                } else {
                    myData += data + " ";
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            System.out.println("File not found");
            e.printStackTrace();
        }
    }

    public static List<String> getbookNameList(int index, List<String> bookNameList) {
        List<String> bookList = new ArrayList<String>();
        if (index == -1) {
            return null;
        }
        char[] arr = matrix.get(index).toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] - '0' == 1) {
                bookList.add(bookNameList.get(i));
            }
        }
        return bookList;
    }

    public static List<String> getbookNameList(String str, List<String> bookNameList) {
        List<String> bookList = new ArrayList<String>();
        if (str.length() == 0) {
            return null;
        }
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] - '0' == 1) {
                bookList.add(bookNameList.get(i));
            }
        }
        return bookList;
    }

    public static String orWiseBit(String l1, String l2) {
        if (l1.length() == 0 || l2.length() == 0)
            return l2;
        String res = "";
        for (int i = 0; i < l1.length(); i++) {
            if (l1.charAt(i) == l2.charAt(i)) {
                res += l1.charAt(i);
            } else {
                res += "1";
            }
        }
        // System.out.println(res);
        return res;
    }

    public static String andWiseBit(String l1, String l2) {
        if (l1.length() == 0 || l2.length() == 0)
            return l2;
        String res = "";
        for (int i = 0; i < l1.length(); i++) {
            if (l1.charAt(i) == l2.charAt(i)) {
                res += l1.charAt(i);
            } else {
                res += "0";
            }
        }
        // System.out.println(res);
        return res;
    }

    public static List<Integer> getListBookRLIndex(String wordInQuery, List<String> bookNameList,
            List<String> vocabList) {
        List<Integer> reListIndex = new ArrayList<Integer>();

        for (String word : vocabList) {
            if (wordInQuery.contains(word)) {
                reListIndex.add(vocabList.indexOf(word));
            }
        }

        return reListIndex;
    }

    public static void truyVan(List<String> vocabList, List<String> bookNameList) {
        String queryPath = "npl\\query-text";
        List<String> queryList = new ArrayList<String>();
        int line = 0;
        try {
            File myFile = new File(queryPath);
            Scanner myScanner = new Scanner(myFile);
            String data = "";
            int count = 0;
            while (myScanner.hasNext()) {
                if (count == 3) {
                    System.out.println(data);
                    queryList.add(data.toLowerCase().trim().substring(data.indexOf(" ") + 1, data.length() - 2));
                    data = "";
                    count = 0;
                    line++;
                    if (line == 5) {
                        break;
                    }
                }
                data += myScanner.nextLine().trim() + " ";
                count++;
            }
            myScanner.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("truy van  exception " + e.getMessage());
        }

        String[] queSub;
        String andResult = "";
        String orResult = "";
        boolean kt = true;
        for (String que : queryList) {
            queSub = que.split(" ");
            kt = true;
            System.out.println(que);
            for (String queSubWord : queSub) {
                if (!queSubWord.equals("") && queSubWord.length() > 3) {

                    for (Integer index : getListBookRLIndex(queSubWord, bookNameList, vocabList)) {
                        if (orResult.length() == 0) {
                            orResult = matrix.get(index);
                        } else {
                            orResult = andWiseBit(orResult, matrix.get(index));
                        }
                    }
                    if (kt) {
                        andResult = orResult;
                        kt = false;
                    } else {
                        andResult = andWiseBit(andResult, orResult);

                    }
                    orResult = "";
                    // System.out.println(getbookNameList(andResult, bookNameList));

                }
            }
            System.out.println(getbookNameList(andResult, bookNameList));

        }

    }

    public static void loadAllWord(List<String> vocabList) {
        try {
            File myFile = new File("matrix12kvocab.txt");
            Scanner myReader = new Scanner(myFile);
            // count: biến xác định dòng là bookNameList, word, matrix
            int count = 0;
            while (myReader.hasNext()) {
                String data = myReader.nextLine();
                if (count == 1) {
                    String[] dataList = data.split(" ");
                    for (String string : dataList) {
                        if (string.length() > 2) {
                            vocabList.add(string);
                            matrix.add(new String());
                        }
                    }
                    break;
                }
                count++;
            }
            myReader.close();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Load full word result file fail!!");
            System.out.println(e.getMessage());

        }
    }

    public static void main(String[] args) {

        String docPath = "npl\\doc-text";
        String resultPath = "result-matrix.txt";
        List<String> bookNameList = new ArrayList<String>();
        List<String> vocabList = new ArrayList<String>();

        // tao ma tran danh dau
        // doc file van ban va ghi vao array list
        // loadFile(docPath, bookNameList, vocabList);

        // load from result file
        loadResult(resultPath, bookNameList, vocabList);

        // Xu ly truy van
        truyVan(vocabList, bookNameList);

        // luu lai ket qua
        // writeResult(resultPath, bookNameList, vocabList);

    }

}