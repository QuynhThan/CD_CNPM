package Ex3.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Gamma 
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_Gamma {

	// Nạp dữ liệu
	public static void LoadData(List<String> dataList, String s) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File f1 = new File(s);
		BufferedReader reader = new BufferedReader(new FileReader(f1));
		try {
			String line = reader.readLine();
			while (line != null) {
				// Kiểm tra xem dòng vừa đọc có phải khoảng trắng kh
				while (line.indexOf('/') == -1) {
					String tmp = reader.readLine();
					if (tmp == null)
						break;
					line = line + tmp;
				}
				while (line.indexOf("  ") != -1)
					line = line.replaceAll("  ", " ");
				line = line.trim();
				dataList.add(line);
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
			// TODO: handle finally clause
		}
	}

	// Tìm tập danh sách khoảng cách
	public static void FindListDistance(List<String> dataList) {
		String result;
		int index = 0;
		for (String string : dataList) {
			if (string.equals("/"))
				break;
			String la[] = string.split("\\s");
			String lb[] = string.split("\\s");
			result = la[0];
			result = result + " " + la[1];
			for (int i = 2; i < la.length - 1; i++) {
				int fistIndex = Integer.valueOf(lb[i - 1]);
				int lastIndex = Integer.valueOf(la[i]);
				la[i] = Integer.toString(lastIndex - fistIndex);
				result = result + " " + la[i];
			}
			result = result + " " + "/";
			dataList.set(index, result);
			result = "";
			index++;
		}
	}

	// Xác định offset
	public static String Offset(String s) {
		int decimalNumber = Integer.parseInt(s);
		String result = Integer.toBinaryString(decimalNumber);
		result = result.substring(1);
		return result;
	}

	// Xác định lenght
	public static String Length(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++)
			result += "1";
		result += "0";
		return result;
	}

	// Giai thuật ma hoa gamma
	public static List<String> EncodeGamma(List<String> dataList) {
		List<String> result = new ArrayList<String>();
		String element;
		for (String string : dataList) {
			if (string.equals("/"))
				break;
			String la[] = string.split("\\s");
			element = la[0];
			for (int i = 1; i < la.length - 1; i++) {
				if (la[i].equals("1"))
					element = element + " 0";
				else if (la[i].equals("0")) {
					element += " -1";
				} else
					element = element + " " + Length(la[i]) + Offset(la[i]);
			}
			element += " /";
			result.add(element);
			element = "";
		}
		result.add("/");
		return result;
	}

	// Giải mã gamma
	public static void DecryptionGamma(List<String> dataList) {
		int index = 0;
		String result;
		for (String string : dataList) {
			if (string.equals("/"))
				break;
			String la[] = string.split("\\s");
			result = la[0];
			for (int i = 1; i < la.length - 1; i++) {
				if (la[i].equals("-1"))
					result += " 0";
				else if (la[i].equals("0"))
					result += " 1";
				else
					result = result + " " + Integer.toString(
							Integer.parseInt("1" + la[i].substring(la[i].indexOf("0") + 1, la[i].length()), 2));

			}
			result += " /";
			dataList.set(index, result);
			index++;
		}
	}

	// Tìm danh sách nguồn gốc
	public static void FindListSource(List<String> daList) {
		int index = 0;
		String result;
		for (String string : daList) {
			if (string.equals("/"))
				break;
			String la[] = string.split("\\s");
			result = la[0] + " " + la[1];
			for (int i = 2; i < la.length - 1; i++) {
				la[i] = Integer.toString(Integer.parseInt(la[i - 1]) + Integer.parseInt(la[i]));
				result = result + " " + la[i];
			}
			result += " /";
			daList.set(index, result);
			index++;
			result = "";
		}
	}

	// Ghi file
	public static void GhiFile(String line) throws IOException {
		File f1 = new File("D:\\ChuyenDeCNPM\\KetQuaBaiTapTuanBa\\Gamma");
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
		LoadData(data, s);
		FindListDistance(data);
		List<String> result = EncodeGamma(data);
		for (String string : result) {
			GhiFile(string);
		}
		DecryptionGamma(result);
		FindListSource(result);
		for (String string : result) {
			System.out.println(string);
		}
	}
}
