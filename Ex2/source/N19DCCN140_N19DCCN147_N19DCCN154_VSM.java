import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * Vector Space Model
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_VSM {
    public static int docSize = 0;

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

    public static void createDF(int[] DFValueArr, List<String> docVecList, List<String> vocabList) {
        // DF danh sách tổng các doc chứa từ tương ứng với danh sách vocabList
        for (int i = 0; i < docVecList.size(); i++) {
            for (String token : docVecList.get(i).split(" ")) {
                if (!token.equals("")) {
                    DFValueArr[Integer.parseInt(token) - 1]++;
                }
            }
        }
    }

    public static void createTF(int[][] TFValueArr, List<String> docVecList, List<String> vocabList,
            List<String> docTextList, double[][] tfMaxAve) {
        // TF maxtrix
        int sumTF = 0;
        int count = 0;
        for (int i = 0; i < docVecList.size(); i++) {
            String[] tokens = docVecList.get(i).split(" ");
            for (int j = 0; j < tokens.length; j++) {
                if (!tokens[j].equals("")) {
                    int indexVocab = Integer.parseInt(tokens[j]) - 1;
                    TFValueArr[i][indexVocab] = countOccurences(docTextList.get(i), vocabList.get(indexVocab));
                    sumTF += TFValueArr[i][indexVocab];
                    count++;
                    if (TFValueArr[i][indexVocab] > tfMaxAve[i][0]) {
                        tfMaxAve[i][0] = TFValueArr[i][indexVocab];
                    }
                }
            }
            tfMaxAve[i][1] = (double) (sumTF) / count;
        }
    }

    public static int countOccurences(String doc, String token) {
        int res = 0;
        int lastIndex = doc.indexOf(token);

        while (lastIndex != -1) {
            res++;
            lastIndex = doc.indexOf(token, lastIndex + token.length());

        }

        return res;
    }

    /// REMAKE
    public static double getTF(String symbol, String textStr, double tfValue, double[] tfMaxAveValue) {
        // n - natural
        double tf = tfValue;
        switch (symbol) {
            case "l": // logarithm
                tf = 1 + Math.log10(tf + 0.1); // plus 1 to exclude log(0)
                break;

            case "a": // augmented
                tf = 0.5 + 0.5 * (tf / tfMaxAveValue[0]);
                break;

            case "b": // boolean
                if (tf > 0.0) {
                    tf = 1;
                }
                break;
            case "L": // log ave
                tf = (1 + Math.log10(tf + 0.1)) / (1 + Math.log10(tfMaxAveValue[1]));
                break;

            default:
                break;
        }
        return tf;
    }

    // create tfidf for doc (wt for docs)
    public static void createTFIDF(double[][] tfidf, int[][] tfMatrix, List<String> docTextList,
            List<String> vocabList, double[][] tfMaxAve,
            int[] DFValueArr, String scoreMethod) {

        double tf = 0;
        double df = 0;
        double idf = 0;
        String tfMethod = scoreMethod.split("[.]")[0].split("")[0];
        String dfMethod = scoreMethod.split("[.]")[0].split("")[1];
        String nLizeMethod = scoreMethod.split("[.]")[0].split("")[2];
        for (int i = 0; i < docTextList.size(); i++) {
            for (int j = 0; j < vocabList.size(); j++) {
                tf = tfMatrix[i][j];
                if (!tfMethod.equals("n")) {
                    tf = getTF(tfMethod, docTextList.get(i), tf, tfMaxAve[i]);
                }
                //
                df = DFValueArr[j];
                if (dfMethod.equals("n")) {
                    idf = 1;
                } else if (dfMethod.equals("t")) {
                    idf = Math.log10((docTextList.size() + 1) / (df + 1));
                } else if (dfMethod.equals("p")) {
                    idf = Math.max(0, Math.log10((docTextList.size() - df - 1) / (df + 1)));
                }
                // wt
                tfidf[i][j] = (tf * idf);

            }
        }
    }

    // Create tfMaxAve for query
    public static void fillTFValueForQuery(String text, List<String> vocabList, String vec, List<Double> tfValue,
            double[] tfMaxAve) {
        int count = 0;
        int sumTF = 0;
        for (String index : vec.split(" ")) {
            if (!index.equals("")) {
                tfValue.add(countOccurences(text, vocabList.get(Integer.parseInt(index) - 1)) + 0.0);
                sumTF += tfValue.get(count);
                if (tfValue.get(count) > tfMaxAve[0]) {
                    tfMaxAve[0] = tfValue.get(count);
                }
                count++;
            }
            tfMaxAve[1] = sumTF / count;
        }

    }

    // Create tfidf for query -- upgrade to create tfidf for docs
    public static void createVector(String queryStr, String queryVec, List<String> vocabList, double[] res,
            int[] DFValueArr, List<String> docTextList, String scoreMethod) {

        double tf = 0;
        double df = 0;
        double idf = 0;
        String tfMethod = scoreMethod.split("[.]")[1].split("")[0];
        String dfMethod = scoreMethod.split("[.]")[1].split("")[1];
        String nLizeMethod = scoreMethod.split("[.]")[1].split("")[2];
        int index = 0;
        int count = 0;
        List<Double> tfValue = new ArrayList<>();
        double[] tfMaxAve = new double[2];
        fillTFValueForQuery(queryStr, vocabList, queryVec, tfValue, tfMaxAve);

        for (String token : queryVec.split(" ")) {
            if (!token.equals("")) {
                tf = tfValue.get(count);
                if (!tfMethod.equals("n")) {
                    tf = getTF(tfMethod, queryStr, tf, tfMaxAve);
                }
                index = Integer.parseInt(token);
                if (index != -1) {
                    df = DFValueArr[index - 1];
                    if (dfMethod.equals("n")) {
                        idf = 1;
                    } else if (dfMethod.equals("t")) {
                        idf = Math.log10((docTextList.size() + 1) / (df + 1));
                    } else if (dfMethod.equals("p")) {
                        idf = Math.max(0, Math.log10((docTextList.size() - df - 1) / (df + 1)));
                    }
                    // df = DFValueArr[index - 1];
                    // idf = Math.log10((docTextList.size() + 1) / (df + 1));
                    res[index - 1] = idf * tf;
                }
                count++;
            }
        }
    }

    // caculatign cosine
    public static double getCosine(double[] x, double[] y) {
        double cos = 0.0;
        double dotValue = 0.0;
        for (int i = 0; i < x.length; i++) {
            dotValue += x[i] * y[i];
        }

        double magnitude1 = 0;
        for (double d : x) {
            magnitude1 += d * d;
        }
        magnitude1 = Math.sqrt(magnitude1);

        double magnitude2 = 0;
        for (double d : x) {
            magnitude2 += d * d;
        }
        magnitude2 = Math.sqrt(magnitude2);

        cos = dotValue / (magnitude1 * magnitude2 + 1);

        return cos;
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(Map<K, V> map) {
        // Create a list of entries from the Map
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        // Sort the list using a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Convert the list back to a Map
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    // cosine (c)
    public static double getLength(double[] x) {
        double res = 0;
        for (double d : x) {
            res += d * d;
        }
        return Math.sqrt(res);
    }

    // pivoted unique
    public static int getUniq(double[] x) {
        int res = 0;
        for (double d : x) {
            if (d > 0) {
                res++;
            }
        }
        return res;
    }

    public static double[] getNLize(double[] x, String type) {
        // case "n"
        double[] res = x.clone();
        switch (type) {
            case "c":
                double xLength = getLength(x);
                for (int i = 0; i < res.length; i++) {
                    res[i] = (res[i]) / (xLength + 1); // length == 0?
                }
                break;
            case "u":
                double uniq = getUniq(x);
                for (int i = 0; i < res.length; i++) {
                    res[i] = (res[i] + 1) / (uniq + 1); // uniq == 0?
                }
                break;
            case "b":
                double apha = 0.3; // ??
                double charLength = getLength(x) + 1;
                for (int i = 0; i < res.length; i++) {
                    res[i] = res[i] / charLength;
                }
                break;

            default:
                break;
        }
        return res;
    }

    public static double getScore(double[] doc, double[] que, String scoreMethod) {

        double score = 0.0;
        String nLizeDocType = scoreMethod.split("[.]")[0].split("")[2];
        String nLizeQueType = scoreMethod.split("[.]")[1].split("")[2];

        double[] docNLize = getNLize(doc, nLizeDocType);
        double[] queNLize = getNLize(que, nLizeQueType);

        for (int i = 0; i < docNLize.length; i++) {
            score += docNLize[i] * queNLize[i];
        }
        // double xLength = getLength(x) + 1;
        // double yLength = getLength(y) + 1;
        // for (int i = 0; i < x.length; i++) {
        // score += (x[i] / xLength) * (y[i] / yLength);
        // }
        return Math.sqrt(score);
    }

    // determine a ranked list of top k doc
    public static List<Integer> getTopDoc(int k, String queryStr, String queryVec, List<String> vocabList,
            List<String> docTextList, int[] DFValueArr, double[][] tfidf, String scoreMethod) {

        // String[] tokens = queryStr.split(" ");
        List<Double> cosValueArr = new ArrayList<Double>();

        double[] queryVector = new double[vocabList.size()];
        createVector(queryStr, queryVec, vocabList, queryVector, DFValueArr, docTextList, scoreMethod);

        for (double[] d : tfidf) {
            // determine by cosine similar
            // cosValueArr.add(getCosine(d, queryVector));

            // determine by
            cosValueArr.add(getScore(d, queryVector, scoreMethod));
        }
        Map<Integer, Double> docIndexAndCosValue = new HashMap<>();
        for (int i = 0; i < docTextList.size(); i++) {
            docIndexAndCosValue.put(i, cosValueArr.get(i));
        }
        docIndexAndCosValue = sortByValues(docIndexAndCosValue);
        // Collections.sort(cosValueArr);
        // Collections.reverse(cosValueArr);
        if (k == 0) {
            // k = 0 return all doc
            List<Integer> res = new ArrayList<>(docIndexAndCosValue.keySet());
            return res;
        } else if (k > 0) {
            List<Integer> res = new ArrayList<>(docIndexAndCosValue.keySet());
            for (double d : new ArrayList<>(docIndexAndCosValue.values()).subList(0, k)) {
                System.out.printf("%,.3f  ", d);
            }
            System.out.println();
            return res.subList(0, k);
        }
        return null;
    }

    public static void main(String[] args) {
        String docVecPath = "npl-data\\npl\\doc-vecs";
        String queryVecPath = "npl-data\\npl\\query-vec";
        String vocabPath = "npl-data\\npl\\term-vocab";
        String docTextPath = "npl-data\\npl\\doc-text";
        String queryTextPath = "npl-data\\npl\\query-text";

        List<String> docTextList = new ArrayList<String>();
        List<String> vocabList = new ArrayList<String>();
        List<String> docVecList = new ArrayList<String>();
        List<String> queryVecList = new ArrayList<String>();
        List<String> queryTextList = new ArrayList<String>();

        String scoreMethod = "lnc.ltc";

        // load doctext
        readData(docTextList, docTextPath);
        // load querytext
        readData(queryTextList, queryTextPath);
        // load vocab
        readData(vocabList, vocabPath);
        // init DFList
        int[] DFValueArr = new int[vocabList.size()];

        // load doc-vecs
        readData(docVecList, docVecPath);
        // load query-vec
        readData(queryVecList, queryVecPath);

        // create DFList
        createDF(DFValueArr, docVecList, vocabList);
        // create TFList
        int[][] tf = new int[docVecList.size()][vocabList.size()];
        double[][] tfMaxAve = new double[docVecList.size()][2];
        createTF(tf, docVecList, vocabList, docTextList, tfMaxAve);

        // create tf-idf matrix
        double[][] tfidf = new double[docVecList.size()][vocabList.size()];
        createTFIDF(tfidf, tf, docTextList, vocabList, tfMaxAve, DFValueArr, scoreMethod);
        System.out.println(tfidf[0].length);
        // Create vector for query
        // caculating cosine
        // determine a ranked list of top k doc

        // list top k doc id for each query
        List<List<Integer>> topKDoc = new ArrayList<List<Integer>>();
        int k = 10;
        // int queryListSize = queryVecList.size();
        int queryListSize = 10;
        for (int i = 0; i < queryListSize; i++) {
            topKDoc.add(
                    getTopDoc(k, queryTextList.get(i), queryVecList.get(i), vocabList, docTextList, DFValueArr, tfidf,
                            scoreMethod));
        }

        for (List<Integer> list : topKDoc) {
            for (Integer d : list) {
                System.out.print((d + 1) + " ");
            }
            System.out.println();
        }
    }

}
