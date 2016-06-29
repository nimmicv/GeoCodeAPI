package nimmi.com.analytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

public class QueryGenerator {
	
	final static HashMap<String,String> code_map = new HashMap<String,String>();
	static String US = "US";
	
	public QueryGenerator(){
		code_map.put("SG", "Singapore");
		code_map.put("US", "United States of America");
		code_map.put("BR", "Brazil");
		code_map.put("CA", "Canada");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QueryGenerator qg = new QueryGenerator();
		try {
			qg.generateUpdateQuery(Consts.FINAL_FILE, Consts.QUERY_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void generateUpdateQuery(String input_file,String out_file) throws IOException
	{
		File file = new File(input_file);
		FileWriter fw = new FileWriter(out_file);
		Scanner sc = null;
		String line = null;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				String[] names = line.split(Consts.COMMA);
				String countrycode = (names.length>2) ?names[names.length-1]:names[1];
				String bank_name = (names.length>2) ?line.substring(0, line.lastIndexOf(Consts.COMMA)):names[0];
				
				
				if(!US.equals(countrycode))
				{
					String countryname = code_map.get(countrycode);
					String query = "UPDATE " +Consts.CARD_ISSUE_TABLE+" SET countrycode="+Consts.SINGLE_QUOTE+countrycode+Consts.SINGLE_QUOTE+
							Consts.COMMA+"countryname="+Consts.SINGLE_QUOTE+countryname+Consts.SINGLE_QUOTE+" WHERE name="+Consts.SINGLE_QUOTE+bank_name+Consts.SINGLE_QUOTE+
							Consts.COLON;
					fw.write(query);
					fw.write(Consts.NEWLINE);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(line);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			System.out.println(line);
		}finally {
			sc.close();
			fw.close();
		}

	}

}
