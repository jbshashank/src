package com.ayushya.spring.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import com.ayushya.spring.bean.Invoice;
import com.ayushya.spring.repository.PurchasedItemsRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mongodb.BasicDBObject;



@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	PurchasedItemsRepository purchasedItemsRepository;

	@Override
	public String generateInvoice(String ticket_id, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		int totItems = 0;
		Document doc = new Document();
		Font blueFont = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
		Font blackFont = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
		Font whiteFont = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE);


		try {

			JSONArray arrayOfItems = purchasedItemsRepository.findBy_id(ticket_id).getJSONArray(0);
			BasicDBObject objectOfItems = (BasicDBObject) arrayOfItems.get(0);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            
            addResponseHeaders(httpServletResponse);
			
			httpServletResponse.setContentLength(baos.size());
			PdfWriter pdfWriter = PdfWriter.getInstance(doc, baos);
			doc.open();
			doc.add(new Paragraph("Ayushya\n" + "Phone: 8050825266\n" + "Email-Id: ayushya.ayushya@ayushya.com\n\n"));
			
			Paragraph TaxInvoice  = new Paragraph("Tax Invoice", blueFont);
			TaxInvoice.setAlignment(Element.ALIGN_CENTER);
			doc.add(new Paragraph(TaxInvoice));
			
			//--------CREATEING BILL TO AND INVOICE NUMBER
			
			PdfPTable BillAndInvoiceInfo = new PdfPTable(2);
			BillAndInvoiceInfo.setWidthPercentage(100);
			BillAndInvoiceInfo.setSpacingBefore(10f);
			float[] BillAndInvoiceInfoColWidth = {1f, 1f};
			BillAndInvoiceInfo.setWidths(BillAndInvoiceInfoColWidth);
			
			PdfPCell cell1 = new PdfPCell(new Paragraph("Bill To: ", blackFont));
			cell1.setBorderColor(BaseColor.WHITE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setVerticalAlignment(Element.ALIGN_CENTER);
			
			PdfPCell cell2 = new PdfPCell(new Paragraph("Invoice No. ", blackFont));
			cell2.setBorderColor(BaseColor.WHITE);
			cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell2.setVerticalAlignment(Element.ALIGN_CENTER);
			
			BillAndInvoiceInfo.addCell(cell1);
			BillAndInvoiceInfo.addCell(cell2);
			
			doc.add(BillAndInvoiceInfo);
			
			//------------------------------------------------------
			
			//--------CREATEING test AND date
			
			PdfPTable testAndDateInfo = new PdfPTable(2);
			testAndDateInfo.setWidthPercentage(100);
			testAndDateInfo.setSpacingAfter(10f);
			
			float[] testAndDateInfoColWidth = {1f, 1f};
			BillAndInvoiceInfo.setWidths(testAndDateInfoColWidth);
			
			PdfPCell cell3 = new PdfPCell(new Paragraph("test ", blackFont));
			cell3.setBorderColor(BaseColor.WHITE);
			cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell3.setVerticalAlignment(Element.ALIGN_CENTER);
			
			DateFormat dtf = new SimpleDateFormat("dd-MM-yy");
			Date date = new Date();
			String dateStr = dtf.format(date);
			PdfPCell cell4 = new PdfPCell(new Paragraph("Date: " + dateStr, blackFont));
			cell4.setBorderColor(BaseColor.WHITE);
			cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell4.setVerticalAlignment(Element.ALIGN_CENTER);
			
			testAndDateInfo.addCell(cell3);
			testAndDateInfo.addCell(cell4);
			
			doc.add(testAndDateInfo);
			
			//------------------------------------------------------
			
			//---------------CREATING TABLES FOR ITEMS-----------------
			PdfPTable itemDetailsTable = new PdfPTable(6);
			itemDetailsTable.setSpacingBefore(10f);
			itemDetailsTable.setWidthPercentage(100);
			
			float[] colWidth = {1f, 4f, 3f, 2f, 2f, 2f};
			itemDetailsTable.setWidths(colWidth);
			
			
			PdfPCell cell5 = new PdfPCell(new Paragraph("#", whiteFont));
			cell5.setBackgroundColor(BaseColor.BLUE);
			cell5.setBorderColor(BaseColor.BLUE);
			cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			PdfPCell cell6 = new PdfPCell(new Paragraph("Item Name", whiteFont));
			cell6.setBackgroundColor(BaseColor.BLUE);
			cell6.setBorderColor(BaseColor.BLUE);
			cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			PdfPCell cell7 = new PdfPCell(new Paragraph("HSN/SAC", whiteFont));
			cell7.setBackgroundColor(BaseColor.BLUE);
			cell7.setBorderColor(BaseColor.BLUE);
			cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			PdfPCell cell8 = new PdfPCell(new Paragraph("Quantity", whiteFont));
			cell8.setBackgroundColor(BaseColor.BLUE);
			cell8.setBorderColor(BaseColor.BLUE);
			cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			PdfPCell cell9 = new PdfPCell(new Paragraph("Price/Unit", whiteFont));
			cell9.setBackgroundColor(BaseColor.BLUE);
			cell9.setBorderColor(BaseColor.BLUE);
			cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			PdfPCell cell10 = new PdfPCell(new Paragraph("Amount", whiteFont));
			cell10.setBackgroundColor(BaseColor.BLUE);
			cell10.setBorderColor(BaseColor.BLUE);
			cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			itemDetailsTable.addCell(cell5);
			itemDetailsTable.addCell(cell6);
			itemDetailsTable.addCell(cell7);
			itemDetailsTable.addCell(cell8);
			itemDetailsTable.addCell(cell9);
			itemDetailsTable.addCell(cell10);
			
			doc.add(itemDetailsTable);
			
			//----------------------------------------------------------
			
			//-----------CREATING LIST OF ITEMS-------------------------
			
			PdfPTable itemDetailsList = new PdfPTable(6);
			itemDetailsList.setSpacingAfter(5f);
			itemDetailsList.setWidthPercentage(100);
			
			float[] itemDetailsListWidth = {1f, 4f, 3f, 2f, 2f, 2f};
			itemDetailsList.setWidths(itemDetailsListWidth);
			
			//---
			List l5 = new List();
			l5.setListSymbol("");
//			l5.add(new ListItem("1"));
//			l5.add(new ListItem("2"));
			for(int i =1; i <= objectOfItems.size()-2;i++) {
				l5.add(new ListItem(Integer.toString(i)));
			}
			
			PdfPCell cellForl5 = new PdfPCell();
			cellForl5.setBorderColor(BaseColor.WHITE);
			cellForl5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellForl5.addElement(l5);
			
			//--
			List l6 = new List();
			l6.setListSymbol("");

			for(int i =1; i <= objectOfItems.size()-2;i++) {
				BasicDBObject itemNameObject = (BasicDBObject) objectOfItems.get("items_"+i);
				l6.add(new ListItem(itemNameObject.get("partname").toString()));
			}
			
			PdfPCell cellForl6 = new PdfPCell();
			cellForl6.setBorderColor(BaseColor.WHITE);
			cellForl6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellForl6.addElement(l6);
			
			List l7 = new List();
			l7.setListSymbol("");
			l7.add(new ListItem(""));
			l7.add(new ListItem(""));
			
			PdfPCell cellForl7 = new PdfPCell();
			cellForl7.setBorderColor(BaseColor.WHITE);
			cellForl7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellForl7.addElement(l7);
			
			//--
			List l8 = new List();
			l8.setListSymbol("");

			for(int i =1; i <= objectOfItems.size()-2;i++) {
				BasicDBObject quantitiyObject = (BasicDBObject) objectOfItems.get("items_"+i);
				l8.add(new ListItem(quantitiyObject.get("quantity").toString()));
			}
			
			PdfPCell cellForl8 = new PdfPCell();
			cellForl8.setBorderColor(BaseColor.WHITE);
			cellForl8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellForl8.addElement(l8);
			
			//--
			List l9 = new List();
			l9.setListSymbol("");

			for(int i =1; i <= objectOfItems.size()-2;i++) {
				BasicDBObject priceObject = (BasicDBObject) objectOfItems.get("items_"+i);
				l9.add(new ListItem("Rs."+priceObject.get("price").toString()));
			}
			
			PdfPCell cellForl9 = new PdfPCell();
			cellForl9.setBorderColor(BaseColor.WHITE);
			cellForl9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellForl9.addElement(l9);
			
			//--
			List l10 = new List();
			l10.setListSymbol("");

			for(int i =1; i <= objectOfItems.size()-2;i++) {
				BasicDBObject eachTotalObject = (BasicDBObject) objectOfItems.get("items_"+i);
				float priceForEach = Float.parseFloat(eachTotalObject.get("price").toString());
				int quantityOfEach = Integer.parseInt(eachTotalObject.get("quantity").toString());
				totItems += quantityOfEach;
				float totPriceForEach = priceForEach * quantityOfEach;
				l10.add(new ListItem("Rs."+Float.toString(totPriceForEach)));
			}
			
			PdfPCell cellForl10 = new PdfPCell();
			cellForl10.setBorderColor(BaseColor.WHITE);
			cellForl10.addElement(l10);
			cellForl10.setHorizontalAlignment(Element.ALIGN_RIGHT);
			
			itemDetailsList.addCell(cellForl5);
			itemDetailsList.addCell(cellForl6);
			itemDetailsList.addCell(cellForl7);
			itemDetailsList.addCell(cellForl8);
			itemDetailsList.addCell(cellForl9);
			itemDetailsList.addCell(cellForl10);
			
			
			doc.add(itemDetailsList);
			
			LineSeparator ls = new LineSeparator();
			doc.add(ls);
			
			//-------------------------------------------------------
			
			//----------------FINAL TOTAL----------------------------
			PdfPTable finalTotal = new PdfPTable(6);
			finalTotal.setSpacingBefore(5f);
			finalTotal.setSpacingAfter(5f);
			finalTotal.setWidthPercentage(100);
			
			float[] finalTotalWidth = {1f, 4f, 3f, 2f, 2f, 2f};
			finalTotal.setWidths(finalTotalWidth);
			
			PdfPCell cell11 = new PdfPCell();
			cell11.setBorderColor(BaseColor.WHITE);
			PdfPCell cell12 = new PdfPCell(new Paragraph("Total"));
			cell12.setBorderColor(BaseColor.WHITE);
			PdfPCell cell13 = new PdfPCell();
			cell13.setBorderColor(BaseColor.WHITE);
			PdfPCell cell14 = new PdfPCell(new Paragraph(Integer.toString(totItems)));
			cell14.setBorderColor(BaseColor.WHITE);
			PdfPCell cell15 = new PdfPCell();
			cell15.setBorderColor(BaseColor.WHITE);
			PdfPCell cell16 = new PdfPCell(new Paragraph("Rs."+objectOfItems.get("totalamount").toString()));
			cell16.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell16.setBorderColor(BaseColor.WHITE);
			
			finalTotal.addCell(cell11);
			finalTotal.addCell(cell12);
			finalTotal.addCell(cell13);
			finalTotal.addCell(cell14);
			finalTotal.addCell(cell15);
			finalTotal.addCell(cell16);
			
			doc.add(finalTotal);
			
			doc.close();
			pdfWriter.close();
			httpServletResponse.setContentLength(baos.size());
            OutputStream os = httpServletResponse.getOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticket_id;
	
	}

	private void addResponseHeaders(HttpServletResponse httpServletResponse) {
		httpServletResponse.setContentType("application/pdf");
		//httpServletResponse.addHeader("Content-Disposition", "attachment; filename="+fileName+".pdf");
		httpServletResponse.setCharacterEncoding("UTF-8");
		  // setting some response headers
		httpServletResponse.setHeader("Expires", "0");
		httpServletResponse.setHeader("Cache-Control",
            "must-revalidate, post-check=0, pre-check=0");
		httpServletResponse.setHeader("Pragma", "public");
        // setting the content type
		httpServletResponse.setContentType("application/pdf");
		httpServletResponse.addHeader("Content-Disposition", "attachment; filename=a.pdf");
        // the contentlength		
	}
	
	public String addReturnedParts(String ticket_id, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		
		return "correct";
	}

	@Override
	public String saveConsumedParts(String ticket_id,Invoice purchasedItems, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		purchasedItems.set_id(ticket_id);
		purchasedItemsRepository.save(purchasedItems);
		return "success";
	}
	
//	public static void process(Document document, JSONObject json) throws JSONException, DocumentException {
//		for (String k : json.keySet()) {
//			Object object = json.get(k);
//			if (object instanceof JSONArray) {
//				JSONArray list = json.getJSONArray(k);
//				process(document, list);
//			} else if (object instanceof JSONObject) {
//				process(document, json.getJSONObject(k));
//			} else {
//				document.add(new Paragraph(k + " " + json.get(k)));
//			}
//
//		}
//	}
//
//	public static void process(Document document, JSONArray json) throws JSONException, DocumentException {
//		for (int x = 0; x < json.length(); x++) {
//			Object object = json.get(x);
//			if (object instanceof JSONArray) {
//				JSONArray list = json.getJSONArray(x);
//				process(document, list);
//			} else if (object instanceof JSONObject) {
//				process(document, json.getJSONObject(x));
//			} else {
//				document.add(new Paragraph(json.get(x).toString()));
//			}
//
//		}
//	}
//
//	public static File jsonTopdf(JSONObject json) throws IOException, DocumentException {
//
//		Document document = new Document(PageSize.A4, 70, 55, 100, 55);
//		File file = File.createTempFile("consulta", ".pdf");
//		FileOutputStream output = new FileOutputStream(file);
//		
//		PdfWriter writer = PdfWriter.getInstance(document, output);
//		writer.setEncryption("a".getBytes(), "b".getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
//        writer.createXmpMetadata();
//		writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));
//		
//
//		document.open();
//		document.addCreationDate();
//		document.addTitle("documento");
//		document.newPage();
//
//		process(document, json);
//		
//		document.close();
//		
//		return file;
//	}
	
	
	
	
}
