package source;

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

public class VectorSpaceModel {

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

    public static int countOccurences(String doc, String token) {
        int res = 0;
        int lastIndex = doc.indexOf(token);

        while (lastIndex != -1) {
            res++;
            lastIndex = doc.indexOf(token, lastIndex + token.length());

        }

        return res;
    }

    public static void createTFIDF(double[][] tfidf, List<String> docTextList, List<String> vocabList,
            int[] DFValueArr) {

        int tf = 0;
        int df = 0;
        double idf;
        for (int i = 0; i < docTextList.size(); i++) {
            for (int j = 0; j < vocabList.size(); j++) {
                tf = countOccurences(docTextList.get(i), vocabList.get(j));

                // lnt.ltc
                // tfidf[i][j] = 1 + Math.log10(tf + 1);

                //
                df = DFValueArr[j];
                idf = Math.log10((docTextList.size() + 1) / (df + 1));
                tfidf[i][j] = (tf * idf);
            }
        }
    }

    // Create vector for query
    public static void createVector(String queryStr, String queryVec, List<String> vocabList, double[] res,
            int[] DFValueArr, List<String> docTextList) {

        int tf = 0;
        int df = 0;
        double idf = 0.0;
        int index = 0;
        for (String token : queryVec.split(" ")) {
            if (!token.equals("")) {
                tf = countOccurences(queryStr, vocabList.get(Integer.parseInt(token) - 1));
                index = Integer.parseInt(token);
                if (index != -1) {
                    df = DFValueArr[index - 1];
                    idf = Math.log10((docTextList.size() + 1) / (df + 1));
                    res[index - 1] = idf * tf;
                }

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

    public static double getLength(double[] x) {
        double res = 0;
        for (double d : x) {
            res += d * d;
        }
        return Math.sqrt(res);
    }

    public static double getScore(double[] x, double[] y) {
        // x wt query / y wt doc
        double score = 0.0;
        double xLength = getLength(x) + 1;
        double yLength = getLength(y) + 1;
        for (int i = 0; i < x.length; i++) {
            score += (x[i] / xLength) * (y[i] / yLength);
        }
        return Math.sqrt(score);
    }

    // determine a ranked list of top k doc
    public static List<Integer> getTopDoc(int k, String queryStr, String queryVec, List<String> vocabList,
            List<String> docTextList, int[] DFValueArr, double[][] tfidf) {

        // String[] tokens = queryStr.split(" ");
        List<Double> cosValueArr = new ArrayList<Double>();

        double[] queryVector = new double[vocabList.size()];
        createVector(queryStr, queryVec, vocabList, queryVector, DFValueArr, docTextList);

        for (double[] d : tfidf) {
            // determine by cosine similar
            // cosValueArr.add(getCosine(d, queryVector));

            // determine by
            cosValueArr.add(getScore(d, queryVector));
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

        // String docVecPath = "npl-data\\npl\\doc-vecs1";
        // String queryVecPath = "npl-data\\npl\\query-vec1";
        // String vocabPath = "npl-data\\npl\\term-vocab";
        // String docTextPath = "npl-data\\npl\\doc-text1";
        // String queryTextPath = "npl-data\\npl\\query-text1";

        List<String> docTextList = new ArrayList<String>();
        List<String> vocabList = new ArrayList<String>();
        List<String> docVecList = new ArrayList<String>();
        List<String> queryVecList = new ArrayList<String>();
        List<String> queryTextList = new ArrayList<String>();
        // List<String> queryVecSyns = new ArrayList<String>();

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

        // create tf-idf matrix
        double[][] tfidf = new double[docVecList.size()][vocabList.size()];
        createTFIDF(tfidf, docTextList, vocabList, DFValueArr);
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
                    getTopDoc(k, queryTextList.get(i), queryVecList.get(i), vocabList, docTextList, DFValueArr, tfidf));
        }

        for (List<Integer> list : topKDoc) {
            for (Integer d : list) {
                System.out.print((d + 1) + " ");
            }
            System.out.println();
        }
    }

}
