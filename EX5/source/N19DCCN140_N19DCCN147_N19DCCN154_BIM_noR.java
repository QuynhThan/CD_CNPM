package EX5.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * BIM no R
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */
public class N19DCCN140_N19DCCN147_N19DCCN154_BIM_noR {
    public static int N = 0;
    public static int vocabSize = 0;

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

    // OR list docs relevant by term of each term in query NOT SORT
    public static String getRelevantDocsByQuery(String query, List<String> relevantDocsByTerm) {
        String res = "";
        for (String index : query.split("\\s+")) {
            if (!index.equals("")) {
                String str = relevantDocsByTerm.get(Integer.parseInt(index) - 1);
                for (String docID : str.split("\\s+")) {
                    if (!res.contains(docID)) {
                        res += docID + " ";
                    }
                }
            }
        }

        return res;
    }

    public static double getBaseCi(int N, int n) {
        // IDF
        return Math.log((N - n + 0.5) / (n + 0.5));
    }

    // public static double getCi(int S, int s, int N, int n) {
    // return Math.log(((s + 0.5) * (N - S - n + s + 0.5)) / ((n - s + 0.5) * (S - s
    // + 0.5)));
    // }

    public static int[] createListOfnValue(List<String> docVecList) {
        int[] nArray = new int[vocabSize];
        for (int i = 0; i < docVecList.size(); i++) {
            for (String token : docVecList.get(i).split("\\s+")) {
                if (!token.equals("")) {
                    nArray[Integer.parseInt(token) - 1]++;
                }
            }
        }
        return nArray;
    }

    public static double[] createArrayOfCiNoRValue(int[] nArray) {
        double[] ciArray = new double[vocabSize];
        for (int i = 0; i < ciArray.length; i++) {
            ciArray[i] = getBaseCi(N, nArray[i]);
        }
        return ciArray;
    }

    public static void RSVScore(String query, String relevantDocsByQuery, List<String> docsVecList,
            List<String> relevantDocsByTerm) {
        List<String> rsvScore = new ArrayList<>();
        for (String docID : relevantDocsByQuery.split("\\s+")) {
            double docScore = 0.0;
            for (String wordId : query.split("\\s+")) {
                if (docsVecList.get(Integer.parseInt(docID) - 1).contains(" " + wordId + " ")) {
                    docScore += Math.log((docsVecList.size() + 0.5)
                            / ((relevantDocsByTerm.get(Integer.parseInt(wordId) - 1).split("\\s+")).length + 0.5));
                }
                /// log(N/df)
            }
            rsvScore.add(docScore + " - " + docID);
        }
        Collections.sort(rsvScore);
        for (int i = rsvScore.size() - 1; i > rsvScore.size() - 11; i--) {
            System.out.println(rsvScore.get(i));
        }
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

    public static void main(String[] args) {
        // split word in doc
        // make word list
        // make List word in - [doc...]
        // MODELING
        // caculating IDF
        // incidence vector is AND vector [word in querry] AND [word in docs]
        // p = count(q,d,R)/count(q,d);
        // S length list relevant_document_querry
        // s length relevant_document_querry AND relevant_doc_term
        // N number_of_total_docs
        // n length of relevant_doc_term DF

        /////////////////////////////////////////////////////
        String docVecPath = "..\\npl-data\\npl\\doc-vecs";
        String queryVecPath = "..\\npl-data\\npl\\query-vec";
        String vocabPath = "..\\npl-data\\npl\\term-vocab";
        String docTextPath = "..\\npl-data\\npl\\doc-text";
        String queryTextPath = "..\\npl-data\\npl\\query-text";
        String termVecsPath = "..\\npl-data\\npl\\term-vecs";
        String relevantDocsByQueryPath = "..\\npl-data\\npl\\rlv-ass";

        List<String> docTextList = new ArrayList<>();
        List<String> vocabList = new ArrayList<>();
        List<String> docVecList = new ArrayList<>();
        List<String> queryVecList = new ArrayList<>();
        List<String> queryTextList = new ArrayList<>();
        List<String> relevantDocsByTerm = new ArrayList<>();
        List<String> relevantDocsByQuerry = new ArrayList<>();

        // get doc vect
        readData(docVecList, docVecPath);
        N = docVecList.size();
        // get term-vecs [wordIndex][list of docsIndex have wordIndex]
        readData(relevantDocsByTerm, termVecsPath);
        vocabSize = relevantDocsByTerm.size();
        // create nList (List of n value) (df)
        int[] nArray = new int[vocabSize];
        nArray = createListOfnValue(docVecList);
        // create start ci with no R
        double[] ciList = new double[relevantDocsByTerm.size()];
        // get querry vecs
        readData(queryVecList, queryVecPath);

        // rank by ci no R
        double[] ciArray = new double[vocabSize];
        ciArray = createArrayOfCiNoRValue(nArray);

        double[] RSVList = new double[N];
        // rank 10 doc with query 1
        for (String wordIndex : queryVecList.get(0).trim().split("\\s+")) {
            for (int i = 0; i < docVecList.size(); i++) {
                String docVec = " " + docVecList.get(i) + " ";
                if (docVec.contains(" " + wordIndex + " ")) {
                    RSVList[i] += ciArray[Integer.parseInt(wordIndex) - 1];
                }

            }
        }
        Map<Integer, Double> result = new HashMap<>();
        for (int i = 0; i < RSVList.length; i++) {
            result.put(i, RSVList[i]);

        }
        result = sortByValues(result);
        int count = 0;
        System.out.println("Top 10 Doc: ");
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            int docID = entry.getKey() + 1;
            System.out.println("doc " + docID + "| RSV = " + entry.getValue());
            count++;
            if (count > 10) {
                break;
            }
        }
        // //read term vec
        // List<String> termVecList = new ArrayList<>();
        // readData(termVecList, termVecsPath);
        // //read rlv doc for querry
        // readData(relevantDocsByQuerry, relevantDocsByQueryPath);
        // int S = relevantDocsByQuerry.get(0).split("\\s+").length;
        // int s = 0;
    }
}
