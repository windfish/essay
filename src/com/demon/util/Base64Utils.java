package com.demon.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;

/**
 * base64编码和解码
 */
public class Base64Utils
{

	/**
	 * 编码
	 * 
	 * @param s
	 * @return
	 */
	public static String getBASE64(String s)
	{
		if (s == null)
			return "";
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
	}
	
	/**
	 * 编码
	 * 
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getBASE64(String s, String encoding) throws UnsupportedEncodingException
	{
		if (s == null)
			return "";
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes(encoding));
	}

	/**
	 * 编码
	 * 
	 * @param s
	 * @return
	 */
	public static String getBASE64(byte[] b)
	{
		if (b == null)
			return "";
		return (new sun.misc.BASE64Encoder()).encode(b);
	}

	/**
	 * 解码
	 * 
	 * @param s
	 * @return
	 */
	public static String getFromBASE64(String s)
	{
		if (s == null)
			return "";
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e)
		{
			return "";
		}
	}
	
	/**
	 * 解码
	 * 
	 * @param s
	 * @return
	 */
	public static String getFromBASE64(String s, String encoding)
	{
		if (s == null)
			return "";
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(s);
			return new String(b, encoding);
		} catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 解码
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] getFromBASE64Byte(String s)
	{
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try
		{
			byte[] b = decoder.decodeBuffer(s);
			return b;
		} catch (Exception e)
		{
			return null;
		}
	}

	public static void main(String[] ar) throws UnsupportedEncodingException
	{
//		System.out.println(getBASE64(getBASE64("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Root><Header><MessageReferenceNumber>502c8ca1c9a111e1b871cbcf97eb9675</MessageReferenceNumber><DocumentName>鍗遍櫓鍝佽繍杈撶數瀛愯矾鍗/DocumentName><DocumentVersionNumber>3.0</DocumentVersionNumber><SenderCode>909</SenderCode><MessageSendingDateTime>20120709163418</MessageSendingDateTime></Header><Body><JobOrderNumber>DP120709004277</JobOrderNumber><ShippingNoteNumber>CO120709004204</ShippingNoteNumber><JobOrderStatusCode>24</JobOrderStatusCode><OriginalDocumentNumber></OriginalDocumentNumber><TransportIndustryCode>3000</TransportIndustryCode><LoadInfo><PlaceOfLoading>瀵岄槼甯/PlaceOfLoading><CountrySubdivisionCode>330183</CountrySubdivisionCode><ResquestedLoadingDateTime>20120709144923</ResquestedLoadingDateTime></LoadInfo><UnloadInfo><GoodsReceiptPlace>妗愬簮鍘/GoodsReceiptPlace><CountrySubdivisionCode>330122</CountrySubdivisionCode><RequestedUnloadedDate>20120709163349</RequestedUnloadedDate><GoodsReceiptDateTime></GoodsReceiptDateTime></UnloadInfo><DespatchActualDateTime>20120709163604</DespatchActualDateTime><TransPortInfo><TransportModeNameCode></TransportModeNameCode><VehicleDispactionDateTime>20120709144923</VehicleDispactionDateTime><DriverInfo><ContactName>钁涢噾鑽/ContactName><IdentityDocumentNumber>330106259463943655</IdentityDocumentNumber><TelephoneNumber>13429108046</TelephoneNumber><QualificationCode>3301062594639436469</QualificationCode></DriverInfo><SupercargoInfo><ContactName>闄堟槬鍗/ContactName><IdentityDocumentNumber>330103194810021935</IdentityDocumentNumber><TelephoneNumber>13758188641</TelephoneNumber><QualificationCode>3301051943639436464</QualificationCode></SupercargoInfo><VehicleInfo><VehicleClassificationCode></VehicleClassificationCode><LicensePlateTypeCode>2</LicensePlateTypeCode><VehicleNumber>娴橝B3Q72</VehicleNumber><VehicleTonnage>1.25</VehicleTonnage><Remark></Remark></VehicleInfo><TrailerTruckInfo><VehicleNumber>娴橝B3Q72</VehicleNumber><VehicleClassificationCode></VehicleClassificationCode><VehicleTonnage>1.25</VehicleTonnage></TrailerTruckInfo></TransPortInfo><GoodsInfo><ArticleNumber></ArticleNumber><CargoHSNumber></CargoHSNumber><GoodsClassificationName>鍗遍櫓璐х墿</GoodsClassificationName><DescriptionOfGoods>93#姹芥补</DescriptionOfGoods><HazardSubstanceItemNumber>3</HazardSubstanceItemNumber><UNDGNumber></UNDGNumber><DangerousGoodsNumber></DangerousGoodsNumber><DangerousGoodsName>93#姹芥补</DangerousGoodsName><GoodsSpecification></GoodsSpecification><ArticleBatchNumber></ArticleBatchNumber><TotalNumberOfPackages>1</TotalNumberOfPackages><PackageTypeCode>CH</PackageTypeCode><ItemGrossWeight><GoodsItemGrossWeight>1.05</GoodsItemGrossWeight><MeasurementUnitCode>TNE</MeasurementUnitCode></ItemGrossWeight><ItemVolume><Cube>0</Cube><MeasurementUnitCode>MTQ</MeasurementUnitCode></ItemVolume></GoodsInfo><ContainerInfo></ContainerInfo></Body></Root>".getBytes("UTF-8")).getBytes("UTF-8")));
//		System.out.println(new String(getFromBASE64Byte("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48Um9vdD48SGVhZGVyPjxNZXNzYWdlUmVmZXJlbmNlTnVtYmVyPjk5ZmIzNmZkLTM2OTItNDc2YS04NjIzLTY1YmI3Yzg4ZWEyMjwvTWVzc2FnZVJlZmVyZW5jZU51bWJlcj48RG9jdW1lbnROYW1lPuS8geS4mui0p+eJqea1gemHj+a1geWQkee7n+iuoTwvRG9jdW1lbnROYW1lPjxEb2N1bWVudFZlcnNpb25OdW1iZXI+My4wPC9Eb2N1bWVudFZlcnNpb25OdW1iZXI+PFNlbmRlckNvZGU+Nzk4MzwvU2VuZGVyQ29kZT48TWVzc2FnZVNlbmRpbmdEYXRlVGltZT4yMDEyMTAxODE5MTUyMTwvTWVzc2FnZVNlbmRpbmdEYXRlVGltZT48L0hlYWRlcj48Qm9keT48RW50ZXJwcmlzZU5hbWU+5Lu75rCP54mp5rWBPC9FbnRlcnByaXNlTmFtZT4+PExvZ2lzdGljc0V4Y2hhbmdlQ29kZT43OTgzPC9Mb2dpc3RpY3NFeGNoYW5nZUNvZGU+PEJ1c2luZXNzVHlwZUNvZGU+MDE8L0J1c2luZXNzVHlwZUNvZGU+PEVudGVycHJpc2VOdW1iZXI+PC9FbnRlcnByaXNlTnVtYmVyPjxUcmFkZUNvdW50PjwvVHJhZGVDb3VudD48VmVoaWNsZUluT3V0Q291bnQ+PC9WZWhpY2xlSW5PdXRDb3VudD48R29vZHNTdXBwbHlJc3N1ZUNvdW50PjwvR29vZHNTdXBwbHlJc3N1ZUNvdW50PjxDb3VudFRpbWU+MjAxMjEwMTgxOTE2NTY8L0NvdW50VGltZT48RGV0YWlsPjxQbGFjZU9mRGVwYXJ0dXJlQ29kZT4zMzAwMDA8L1BsYWNlT2ZEZXBhcnR1cmVDb2RlPjxEZXN0aW5hdGlvbkNvZGU+MzMwMDAwPC9EZXN0aW5hdGlvbkNvZGU+PENvbnNpZ25tZW50R3Jvc3NXZWlnaHQ+NC40PC9Db25zaWdubWVudEdyb3NzV2VpZ2h0PjxDdWJlPjMuNjwvQ3ViZT48Q29udGFpbmVyUXVhbnRpdHk+PC9Db250YWluZXJRdWFudGl0eT48VG90YWxOdW1iZXJPZlBhY2thZ2VzPjE4NDwvVG90YWxOdW1iZXJPZlBhY2thZ2VzPjxDb25zaWdubWVudERhdGVUaW1lPjIwMTIxMDE3MDAwMDAwPC9Db25zaWdubWVudERhdGVUaW1lPjwvRGV0YWlsPjwvQm9keT48L1Jvb3Q+"), "UTF-8"));
//		System.out.println(getBASE64("我爱你"));
//		System.out.println(getFromBASE64("ztKwrsTj"));
//		System.out
//				.println(getBASE64("<?xml version=\"1.0\" encoding=\"gb2312\"?><root><CategoryInfoWather><cityId>杭州</cityId><tqrq>2008-06-27 00:00</tqrq><tqzk>雨</tqzk><tqqw>12</tqqw><tqfj>五级</tqfj><tqms>·专家称周正龙4月曾拍到华南虎清晰脚印(图)</tqms><categoryName>1002</categoryName><categoryId>1002</categoryId><infoAuthor></infoAuthor><infoTime>2008-06-27 16:20:35</infoTime></CategoryInfoWather></root>"));
//		System.out
//				.println(getFromBASE64("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iR0IyMzEyIj8+Cjxyb290PjxDYXRlZ29yeUluZm9XYXRoZXI+PGNpdHlJZD66vNbdPC9jaXR5SWQ+PHRxcnE+MjAwOC0wNi0yNyAwMDowMDwvdHFycT48dHF6az7T6jwvdHF6az48dHFxdz4xMjwvdHFxdz48dHFmaj7O5by2PC90cWZqPjx0cW1zPqGk16i80rPG1tzV/cH6NNTC1PjFxLW9u6rEz7uix+XO+r3F06EozbwpPC90cW1zPjxjYXRlZ29yeU5hbWU+MTAwMjwvY2F0ZWdvcnlOYW1lPjxjYXRlZ29yeUlkPjEwMDI8L2NhdGVnb3J5SWQ+PGluZm9BdXRob3I+PC9pbmZvQXV0aG9yPjxpbmZvVGltZT4yMDA4LTA2LTI3IDE2OjIwOjM1PC9pbmZvVGltZT48L0NhdGVnb3J5SW5mb1dhdGhlcj48L3Jvb3Q+Cg=="));
//		System.out
//				.println(getFromBASE64("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iZ2IyMzEyIj8+PHJvb3Q+PENhdGVnb3J5SW5mb1dhdGhlcj48Y2l0eUlkPrq81t08L2NpdHlJZD48dHFycT4yMDA4LTA2LTI3IDAwOjAwPC90cXJxPjx0cXprPtPqPC90cXprPjx0cXF3PjEyPC90cXF3Pjx0cWZqPs7lvLY8L3RxZmo+PHRxbXM+oaTXqLzSs8bW3NX9wfo01MLU+MXEtb27qsTPu6LH5c76vcXToSjNvCk8L3RxbXM+PGNhdGVnb3J5TmFtZT4xMDAyPC9jYXRlZ29yeU5hbWU+PGNhdGVnb3J5SWQ+MTAwMjwvY2F0ZWdvcnlJZD48aW5mb0F1dGhvcj48L2luZm9BdXRob3I+PGluZm9UaW1lPjIwMDgtMDYtMjcgMTY6MjA6MzU8L2luZm9UaW1lPjwvQ2F0ZWdvcnlJbmZvV2F0aGVyPjwvcm9vdD4="));
//		System.out
//				.println(getFromBASE64("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iR0IyMzEyIj8+PHJvb3Q+PENhdGVnb3J5SW5mb1dhdGhlcj48Y2l0eUlkPrq81t08L2NpdHlJZD48dHFycT4yMDA4LTA2LTI3IDAwOjAwPC90cXJxPjx0cXprPrTz0+o8L3Rxems+PHRxcXc+MjM8L3RxcXc+PHRxZmo+y8S8tjwvdHFmaj48dHFtcz7I58zio6yxyMjn0ru49s28xqzOxLz+o6yx5LPJwctiYXNlNjS1xLHgwuvX1rf7tK6686Os1/fOqnhtbM7EvP61xNK7uPa94bXj1rWjrLfFtb3Su7j2eG1sZG9jdW1lbnTA78Pmo6zIu7rz1eK49nhtbGRvY3VtZW5008Ox4MLrdXRmOLXEuPHKvdC0tb3TssXMyc+hoyAgIAogICAgCiAgtbHW2NDCtsHIodXiuPZ4bWzOxLz+tcTV4rj2veG149a1o6zP69PDYmFzZTY0tcS94sLrt73Kvbu51K3V4rj2zbzGrM7EvP7KsaOst6LP1sO709Cw7Leou7nUrcHLICAgCjwvdHFtcz48Y2F0ZWdvcnlOYW1lPjEwMDI8L2NhdGVnb3J5TmFtZT48Y2F0ZWdvcnlJZD4xMDAyPC9jYXRlZ29yeUlkPjxpbmZvQXV0aG9yPjwvaW5mb0F1dGhvcj48aW5mb1RpbWU+MjAwOC0wNi0yNyAxNzoyNTo1NzwvaW5mb1RpbWU+PC9DYXRlZ29yeUluZm9XYXRoZXI+PC9yb290Pgo="));
		
		try {
			byte[] files = FileUtils.toByteArray("d:\\1.txt");
			System.out.println(files.length);
			String result = new String(getFromBASE64Byte(new String(getFromBASE64Byte(new String(files)))),"utf-8");
			//System.out.println(result);
			FileWriter fw = new FileWriter(new File("d:\\2.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
