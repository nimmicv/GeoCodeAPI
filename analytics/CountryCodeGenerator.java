package samsung.pay.com.analytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hello world!
 * 
 */

public class CountryCodeGenerator {

	
	static final String RESULTS = "results";
	static final String PLACE_ID = "place_id";
	static final String ADDRESS_COMPONENTS = "address_components";
	static final String LONG_NAME = "long_name";
	static final String SHORT_NAME = "short_name";
	static final String TYPES = "types";
	static final String COUNTRY = "country";
	String key="";
	
	
	String textSearchUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?key="+key+"&query=";
	String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?key="+key+"&place_id=";
	
	
	public static void main(String[] args) {
		System.out.println("Hello Samsung");
		CountryCodeGenerator pkt = new CountryCodeGenerator();
		try {
			pkt.generateLocationInfo(Consts.BANK_NAMES_FILE,Consts.COUNTRY_CODE_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateLocationInfo(String input_file,String out_file) throws IOException {
		File file = new File(input_file);
		FileWriter fw = new FileWriter(out_file);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String bankname_orig = sc.nextLine();
				String bankname_mod = bankname_orig.replaceAll("[^a-zA-Z]+",
						" ");
				bankname_mod = URLEncoder.encode(bankname_mod, "UTF-8");
				System.out.println("BANK = " + bankname_mod);
				String country_code = serviceRequest(bankname_mod);
				fw.write(bankname_orig + Consts.TAB + country_code);
				fw.write(Consts.NEWLINE);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sc.close();
			fw.close();
		}

	}

	public String serviceRequest(String bankName) {
		String searchUrl = textSearchUrl + bankName;
		JSONObject resultObject = executeRequest(searchUrl);
		String placeId = getPlaceId(resultObject);
		System.out.println("PlaceId = " + placeId);

		searchUrl = geocodeUrl + placeId;
		resultObject = executeRequest(searchUrl);
		String country = getCountryCode(resultObject);
		System.out
				.println("-----------------------------------------------------");
		System.out.println("country = " + country);
		System.out.println("bankName = " + bankName);
		System.out
				.println("-----------------------------------------------------");
		return country;
	}

	public String getPlaceId(JSONObject result) {
		String name = null;
		try {
			JSONArray jsonarray = result.getJSONArray(RESULTS);
			JSONObject jsonobject = jsonarray.getJSONObject(0);
			name = jsonobject.getString(PLACE_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	public String getCountryCode(JSONObject result) {
		String short_name = null;
		try {
			JSONArray jsonarray = result.getJSONArray(RESULTS);
			JSONObject jsonobject = jsonarray.getJSONObject(0);

			JSONArray jsonarray_address = jsonobject
					.getJSONArray(ADDRESS_COMPONENTS);

			for (int i = 0; i < jsonarray_address.length(); i++) {
				JSONObject address_object = jsonarray_address.getJSONObject(i);
				JSONArray msg = (JSONArray) address_object.get(TYPES);
				String value = (String) msg.get(0);
				if (COUNTRY.equalsIgnoreCase(value)) {
					String country = address_object.getString(LONG_NAME);
					short_name = address_object.getString(SHORT_NAME);
					System.out.println("country = " + country);
					System.out.println("short_name = " + short_name);
					return short_name;
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return short_name;
	}

	public JSONObject executeRequest(String url) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGetRequest = new HttpGet(url);
		HttpResponse response = null;
		String name = null;
		JSONObject result = null;

		try {
			response = httpClient.execute(httpGetRequest);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			String json_string = EntityUtils.toString(response.getEntity());
			System.out.println("Json String = " + json_string);
			try {
				result = new JSONObject(json_string);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

}
