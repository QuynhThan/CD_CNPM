package EX5.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * BIM use rlv-ass
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */
public class N19DCCN140_N19DCCN147_N19DCCN154_BIM_rlv {

	public static int N;

	public static void LoadData(List<String> data, String url) throws FileNotFoundException {
		File f1 = new File(url);
		BufferedReader reader = new BufferedReader(new FileReader(f1));
		try {
			String line = reader.readLine();
			while (line != null) {
				while (line.indexOf('/') == -1) {
					String tmp = reader.readLine();
					if (tmp == null)
						break;
					line = line + " " + tmp;
				}
				while (line.indexOf("  ") != -1)
					line = line.replaceAll("  ", " ");
				line = line.trim();
				data.add(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				reader.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public static List<String> findPopular(String query, List<String> docText) {
		List<String> popular = new ArrayList<String>();
		String arr[] = query.split("\\s");
		for (int i = 1; i < arr.length; i++) {
			// System.out.println(arr[i]);
			for (String string : docText) {
				String arrDoc[] = string.split("\\s");
				if (arrDoc[0].equals(arr[i])) {
					popular.add(string);
					break;
				}
			}
		}
		return popular;
	}

	public static int calcutatedf(String s, List<String> docText) {
		int result = 0;
		for (String string : docText) {
			String arr[] = string.split("\\s");
			for (int i = 1; i < arr.length; i++) {
				if (arr[i].equals(s)) {
					result++;
					break;
				}
			}
		}
		return result;
	}

	public static List<String> RSV(List<String> rlv_ass, List<String> docText, List<String> queryDoc) {
		List<String> rSV = new ArrayList<String>();
		Map<Integer, Float> mapResult = new HashMap<>();
		float result = 0;
		// String line;
		int index = 1;
		for (String string : queryDoc) {
			String arrQuery[] = string.split("\\s");
			List<String> popular = findPopular(rlv_ass.get(index - 1), docText);
			int S = popular.size();
			int indexDoc = 0;
			for (String string2 : docText) {
				indexDoc++;
				String arrDoc[] = string2.split("\\s");
				for (int i = 1; i < arrQuery.length; i++) {
					for (int j = 1; j < arrDoc.length - 1; j++) {
						if (arrQuery[i].equals(arrDoc[j])) {
							int s = calcutatedf(arrQuery[i], popular);
							int n = calcutatedf(arrQuery[i], docText);
							float c = (float) Math
									.log10(((s + 0.5) * (N - S - n + s + 0.5)) / ((n - s + 0.5) * (S - s + 0.5)));
							result += c;
						}
					}
				}
				mapResult.put(indexDoc, result);
				result = 0;
			}
			List<Map.Entry<Integer, Float>> list = new LinkedList<>(mapResult.entrySet());

			// Sort the list using a custom Comparator
			Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
				public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});

			// Convert the list back to a Map
			Map<Integer, Float> sortedMap = new LinkedHashMap<>();
			for (Map.Entry<Integer, Float> entry : list) {
				sortedMap.put(entry.getKey(), entry.getValue());
			}
			String line = Integer.toString(index);
			int count = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				line = line + " " + Integer.toString(entry.getKey()) + " " + Float.toString(entry.getValue());
				count++;
				if (count > 10) {
					break;
				}
			}
			line = line + " " + "/";
			index++;
			rSV.add(line);
			mapResult.clear();
			System.out.println(line.length());
			// test 1 querry
			break;
		}
		return rSV;
	}

	// Ghi file
	public static void GhiFile(String line) throws IOException {
		File f1 = new File("..\\npl-data\\KetQuaBaiTapTuanNam\\StandaloneBinaryModel");
		BufferedWriter wirte = new BufferedWriter(new FileWriter(f1, true));
		try {
			wirte.write(line);
			wirte.newLine();
			wirte.newLine();
			wirte.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void main(String[] args) throws IOException {
		String url = "..\\npl-data\\npl\\doc-vecs";
		List<String> docVecs = new ArrayList<String>();
		List<String> queryVec = new ArrayList<String>();
		List<String> rlv_ass = new ArrayList<String>();
		LoadData(docVecs, url);
		url = "..\\npl-data\\npl\\query-vec";
		LoadData(queryVec, url);

		url = "..\\npl-data\\npl\\rlv-ass";
		LoadData(rlv_ass, url);
		N = docVecs.size();
		List<String> RSV = RSV(rlv_ass, docVecs, queryVec);
		System.out.println(RSV.get(0));
		// for (String string : RSV) {
		// GhiFile(string);
		// }
	}
}
