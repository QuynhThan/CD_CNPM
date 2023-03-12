import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Intersect
 * N19DCCN140-Trần Tấn Phong
 * N19DCCN147-Lê Nguyễn Duy Phương
 * N19DCCN154- Thân Ngọc Quỳnh
 */

public class N19DCCN140_N19DCCN147_N19DCCN154_Intersect {

	public static String ChiNguocGiao(String la[], String lb[], int query) {
		if (la.length == 0 || lb.length == 0)
			return null;
		String Ans = String.valueOf(query);
		String p1, p2;
		p1 = la[1];
		p2 = lb[1];
		int a = 1, b = 1;
		// System.out.println(Ans.isEmpty());
		while (p1.equals("/") == false && p2.equals("/") == false) {
			// System.out.println(p1.compareTo(p2));
			int lp1 = Integer.valueOf(p1);
			int lp2 = Integer.valueOf(p2);
			if (lp1 == lp2) {
				// System.err.println(p1);
				Ans += " ";
				Ans += p1;
				a++;
				b++;
				p1 = la[a];
				p2 = lb[b];
			} else {
				if (lp1 < lp2) {
					a++;
					p1 = la[a];
				} else {
					b++;
					p2 = lb[b];
				}
			}
		}
		Ans += " /";
		return Ans;
	}

	public static String TienXuLy(String tmp) throws FileNotFoundException {
		File f2 = new File("D:\\ChuyenDeCNPM\\npl\\term-vecs");
		BufferedReader reader = new BufferedReader(new FileReader(f2));
		try {
			String line = reader.readLine();
			while (line != null) {
				while (line.indexOf('/') == -1)
					line = line + " " + reader.readLine();
				while (line.indexOf("  ") != -1)
					line = line.replaceAll("  ", " ");
				line = line.trim();
				// System.out.println(line);
				String la[] = line.split("\\s");
				//System.out.println(La[0]);
				if (tmp.equals(la[0]))
					return line;

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
		return null;
	}

	public static void GhiFile(String line) throws IOException {
		File f1 = new File("D:\\ChuyenDeCNPM\\KetQuaBaiTapTuanMot\\Intersect");
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

	public static void main(String[] args) throws FileNotFoundException {

		File f1 = new File("D:\\ChuyenDeCNPM\\npl\\query-vec");

		// System.out.println(f1.exists());

		BufferedReader read = new BufferedReader(new FileReader(f1));
		try {
			String line = read.readLine();
			int query = 1;
			while (line != null) {

				// Kiểm tra xem dòng vừa đọc có phải khoảng trắng kh
				while (line.indexOf('/') == -1)
					line = line + read.readLine();
				while (line.indexOf("  ") != -1)
					line = line.replaceAll("  ", " ");
				line = line.trim();
				//System.out.println(line);
				String la[] = line.split("\\s");
				String temp = TienXuLy(la[1]);
				String la1[] = temp.split("\\s");
				// System.out.println(temp);
				// System.out.println(La[3]);
				String temp2 = TienXuLy(la[2]);
				String lb1[] = temp2.split("\\s");
				String ketQua = ChiNguocGiao(la1, lb1, query);
				for (int i = 3; i < la.length - 1; i++) {
					// Tìm string có của docID La[i]
					String tmp = TienXuLy(la[i]);
					// System.out.println(tmp);
					if (tmp == null)
						continue;
					String lb[] = tmp.split("\\s");
					String laa[] = ketQua.split("\\s");
					// System.err.println(KetQua);
					ketQua = ChiNguocGiao(laa, lb, query);
					// System.err.println(KetQua);
				}

				System.err.println(ketQua);
				//System.out.println("----------------------------------------");
				GhiFile(ketQua);
				line = read.readLine();
				query++;
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
}
