
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * nnnatc
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_nnnatc {

	// Tải dữ liệu.

	public static void ReadData(List<String> dataList, String dataPath) throws FileNotFoundException {

		File f1 = new File(dataPath);

		// System.out.println(f1.exists());

		BufferedReader read = new BufferedReader(new FileReader(f1));
		try {
			String line = read.readLine();
			while (line != null) {

				// Kiểm tra xem dòng vừa đọc có phải khoảng trắng kh
				while (line.indexOf('/') == -1) {
					String tmp = read.readLine();
					if (tmp == null)
						break;
					line = line + tmp;
				}
				while (line.indexOf("  ") != -1)
					line = line.replaceAll("  ", " ");
				line = line.trim();
				dataList.add(line);
				line = read.readLine();

			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				read.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	// Tìm dòng chứa từ s nhiều nhất. Trả về kết quả số lần xuất hiện nhiều nhất
	public static double Maxtd(String s, List<String> data) {
		double count = 0;
		double max = 0;
		for (String string : data) {
			count = Natural(s, string);
			if (count > max)
				max = count;
			count = 0;
		}
		return max;
	}

	// Tính Augment
	public static float Augment(String s, List<String> data, String doc) {
		float result = 0;
		result = (float) (0.5 + (0.5 * Natural(s, doc)) / Maxtd(s, data));
		return result;
	}

	// Tính Wt phục vụ tính cosin.
	public static float Wt(String s, String doc, List<String> data) {
		float result = 0;
		if (Natural(s, doc) == 0)
			return result;
		else
			result = (float) (1 - Math.log10(Natural(s, doc))) * Idf(s, data);
		return result;
	}

	// Tính idf
	public static float Idf(String s, List<String> data) {
		float result = 0;
		double count = 0;
		double n = (double) data.size();
		for (String string : data) {
			String la[] = string.split("\\s");
			for (int i = 1; i < la.length; i++)
				if (s.equals(la[i])) {
					count++;
					break;
				}
		}
		// System.out.println(count);
		if (count == 0)
			return result;
		result = (float) Math.log10(n / count);
		return result;
	}

	// Tính consin
	public static float Cosin(String s, List<String> data) {
		float result;
		float sumSquare = 0;
		for (String string : data) {
			sumSquare += Wt(s, string, data) * Wt(s, string, data);
		}
		result = (float) (1 / Math.sqrt(sumSquare));
		return result;
	}

	// Tính Natural.
	public static double Natural(String a, String doc) {
		double count = 0;
		String la[] = doc.split("\\s");
		for (int i = 1; i < la.length; i++)
			if (a.equals(la[i]))
				count++;
		return count;
	}

	// Kiểm tra xem phần tử có tồn tại trong mảng ko
	public static boolean CheckForExistence(String s, String la[]) {
		for (int i = 1; i < la.length; i++)
			if (s.equals(la[i]))
				return true;
		return false;
	}

	// Vector NNN
	public static List<Double> NNN(String doc, String query) {
		List<Double> nNN = new ArrayList<Double>();
		String listDoc[] = doc.split("\\s");
		String listQuery[] = query.split("\\s");
		for (int i = 1; i < listDoc.length; i++) {
			if (listDoc[i].equals("/"))
				break;
			if (CheckForExistence(listDoc[i], listQuery)) {
				double result = Natural(listDoc[i], query);
				nNN.add(result);
			}
		}
		return nNN;
	}

	// Vector Atc
	public static List<Float> ATC(String doc, String query, List<String> queryVec) {
		List<Float> aTC = new ArrayList<Float>();
		String listDoc[] = doc.split("\\s");
		String listQuery[] = query.split("\\s");
		for (int i = 1; i < listQuery.length; i++) {
			if (listQuery[i].equals("/"))
				break;
			if (CheckForExistence(listQuery[i], listDoc)) {
				float result = Augment(listQuery[i], queryVec, doc);
				// System.out.println(result);
				result = result * Idf(listQuery[i], queryVec);
				result = result * Cosin(listQuery[i], queryVec);
				aTC.add(result);
			}
		}
		return aTC;
	}

	// Tính vecToc nnn.atc
	public static void NnnAtc(List<String> data, List<String> query, List<String> nNNATC) {
		float score = 0;
		Map<Integer, Float> rank = new HashMap<>();
		int numberOrder = 0;
		int index;
		for (String stringQuery : query) {
			numberOrder++;
			if (stringQuery.equals("/"))
				break;
			index = 0;
			for (String stringData : data) {
				index++;
				if (stringData.equals("/"))
					break;
				List<Double> nNN = NNN(stringData, stringQuery);
				List<Float> aTC = ATC(stringData, stringQuery, query);
				for (int i = 0; i < aTC.size(); i++) {
					score += (float) (aTC.get(i) * nNN.get(i));
				}
				rank.put(index, score);
				score = 0;
			}
			List<Map.Entry<Integer, Float>> list = new LinkedList<>(rank.entrySet());

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
			String s = Integer.toString(numberOrder);
			System.out.println(s);
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet())
				s = s + " " + Integer.toString(entry.getKey());
			s = s + " " + "/";
			nNNATC.add(s);
			rank.clear();
		}
	}

	public static void GhiFile(String line) throws IOException {
		File f1 = new File("D:\\ChuyenDeCNPM\\KetQuaBaiTapTuanHai\\NNNATC");
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
		String s = "npl-data\\npl\\doc-vecs";
		List<String> data = new ArrayList<String>();
		ReadData(data, s);
		// System.err.println("Load xong");
		s = "npl-data\\npl\\query-vec";
		List<String> query = new ArrayList<String>();
		ReadData(query, s);
		// System.out.println("Load xong");
		List<String> nNNATC = new ArrayList<String>();
		NnnAtc(data, query.subList(0, 10), nNNATC);
		for (String string : nNNATC) {
			GhiFile(string);
		}
		System.out.println("Xong");
	}
}
