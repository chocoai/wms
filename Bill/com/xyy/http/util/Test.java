package com.xyy.http.util;



public class Test {

	public static void main(String[] args) {
		String url = "http://127.0.0.1/service/sequenceService?apiKey=ybm100";
		Envelop envelop=new Envelop();
		envelop.getBody().put("sequence", "cdt_laonorder");
		try {
			Envelop resul=envelop.send(url);
			System.out.println(resul.getBody().getString("sequence"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
