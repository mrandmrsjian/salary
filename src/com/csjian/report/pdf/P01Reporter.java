package com.csjian.report.pdf;

import com.csjian.model.bean.*;
import com.csjian.util.*;

import java.io.OutputStream;
import java.util.*;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPCell;

public class P01Reporter extends PdfPageEventHelper {
  public PdfPTable table;  // The headertable.
  public PdfGState gstate;
  public PdfTemplate tpl; // A template that will hold the total number of pages.
  public BaseFont bfChinese;
  public Font FontChineseS;
  public static String companyname;
  public static String year;
  public static String month;

  public static void generatePDF(String regcode, String year, String month, CompanyBean company, SalaryBean[] salaryList, OutputStream os) throws Exception {
	Calendar cal = Calendar.getInstance();
    P01Reporter.year = year;
    P01Reporter.month = month;

	  try {
	  	if (company != null) {
        P01Reporter.companyname = company.getRegname();

     	Document doc = new Document(PageSize.A3.rotate(), 50, 50, 100, 72);
        PdfWriter writer = PdfWriter.getInstance(doc, os);
        writer.setPageEvent(new P01Reporter());
        doc.open();
	    BaseFont bfChinese = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font FontChineseS = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);;

        PdfPTable tbl = null;
        if (salaryList!=null&&salaryList.length>0) {
        	Vector pitem = salaryList[0].getItemp();
        	Vector mitem = salaryList[0].getItemm();
        	int[] total = new int[pitem.size() + mitem.size() + 4];
        	//float[] widths = new float[total.length+2];
        	//for (int i=0; i<widths.length; i++) {
        	//	widths[i] = (float)(1/(total.length+2));
        	//}

        	//float[] widths = {0.15f, 0.7f, 0.15f};
        	tbl = new PdfPTable(total.length+2);
        	tbl.setWidthPercentage(100f);
        	tbl.getDefaultCell().setBorderWidth(0.1f);
        	tbl.getDefaultCell().setFixedHeight(25);
        	tbl.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        	tbl.setHeaderRows(1);
        	tbl.addCell(new Phrase("員工編號", FontChineseS));
        	tbl.addCell(new Phrase("姓名", FontChineseS));
        	tbl.addCell(new Phrase("基本薪資", FontChineseS));
        	tbl.addCell(new Phrase("加班薪資", FontChineseS));
        	for (int j=0; j<pitem.size(); j++) {
        		String[] item = (String [])pitem.elementAt(j);
        		tbl.addCell(new Phrase(item[1], FontChineseS));
        	}
        	for (int j=0; j<mitem.size(); j++) {
        		String[] item = (String [])mitem.elementAt(j);
        		tbl.addCell(new Phrase(item[1], FontChineseS));
        	}
        	tbl.addCell(new Phrase("扣繳稅額", FontChineseS));
        	tbl.addCell(new Phrase("實發金額", FontChineseS));

        	for (int i=0; i<salaryList.length; i++) {
        		pitem = salaryList[i].getItemp();
        		mitem = salaryList[i].getItemm();
        		int k = 0;
        		tbl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        		tbl.addCell(new Phrase(salaryList[i].getEmployeeno(), FontChineseS));
        		tbl.addCell(new Phrase(salaryList[i].getName(), FontChineseS));
        		tbl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        		tbl.addCell(new Phrase(salaryList[i].getBasesalary().equals("")?"0":StringUtils.addComma(salaryList[i].getBasesalary()), FontChineseS));
        		total[k] += Integer.parseInt(salaryList[i].getBasesalary()!=null&&!salaryList[i].getBasesalary().equals("")?salaryList[i].getBasesalary():"0");
        		k++;
        		tbl.addCell(new Phrase(salaryList[i].getOversalary().equals("")?"0":StringUtils.addComma(salaryList[i].getOversalary()), FontChineseS));
        		total[k] += Integer.parseInt(salaryList[i].getOversalary()!=null&&!salaryList[i].getOversalary().equals("")?salaryList[i].getOversalary():"0");
        		k++;

        		for (int j=0; j<pitem.size(); j++) {
        			String[] item = (String [])pitem.elementAt(j);
        			tbl.addCell(new Phrase(item[2]!=null&&!item[2].equals("")?StringUtils.addComma(item[2]):"0", FontChineseS));
        			total[k] += Integer.parseInt(item[2]!=null&&!item[2].equals("")?item[2]:"0");
        			k++;
        		}

        		for (int j=0; j<mitem.size(); j++) {
        			String[] item = (String [])mitem.elementAt(j);
        			tbl.addCell(new Phrase(item[2]!=null&&!item[2].equals("")?StringUtils.addComma(item[2]):"0", FontChineseS));
        			total[k] += Integer.parseInt(item[2]!=null&&!item[2].equals("")?item[2]:"0");
        			k++;
        		}

        		tbl.addCell(new Phrase(salaryList[i].getTax().equals("")?"0":StringUtils.addComma(salaryList[i].getTax()), FontChineseS));
        		total[k] += Integer.parseInt(salaryList[i].getTax()!=null&&!salaryList[i].getTax().equals("")?salaryList[i].getTax():"0");
        		k++;
        		tbl.addCell(new Phrase(salaryList[i].getTotal().equals("")?"0":StringUtils.addComma(salaryList[i].getTotal()), FontChineseS));
        		total[k] += Integer.parseInt(salaryList[i].getTotal()!=null&&!salaryList[i].getTotal().equals("")?salaryList[i].getTotal():"0");
        		k++;
        	}

        	for (int i=0; i<(2+total.length); i++) {
        		tbl.addCell(new Phrase("", FontChineseS));
        	}

        	PdfPCell cell = new PdfPCell(new Phrase("合計", FontChineseS));
        	cell.setColspan(2);
        	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        	cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        	tbl.addCell(cell);

        	for (int i=0; i<total.length; i++) {
        		tbl.addCell(new Phrase(StringUtils.addComma(total[i]+""), FontChineseS));
        	}
        } else {
        	tbl = new PdfPTable(4);
        	tbl.setWidthPercentage(100f);
        	tbl.getDefaultCell().setBorderWidth(0.1f);
        	tbl.getDefaultCell().setFixedHeight(25);
        	tbl.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        	tbl.setHeaderRows(1);
        	tbl.addCell(new Phrase("員工編號", FontChineseS));
        	tbl.addCell(new Phrase("姓名", FontChineseS));
        	tbl.addCell(new Phrase("基本薪資", FontChineseS));
        	tbl.addCell(new Phrase("加班薪資", FontChineseS));
        	
        	tbl.addCell(new Phrase("", FontChineseS));
        	tbl.addCell(new Phrase("", FontChineseS));
        	tbl.addCell(new Phrase("", FontChineseS));
        	tbl.addCell(new Phrase("", FontChineseS));
        }        

   	    tbl.setTotalWidth(doc.right() - doc.left());
        doc.add(tbl);
        doc.close();
	  	}
    } catch ( Exception e ) {
    	System.out.println("regcode:" + regcode + "|year:" + year + "|month" + month);
    	e.printStackTrace();
      throw e;
    } 
  }

  public void onOpenDocument(PdfWriter writer, Document document){
	  try {
      bfChinese = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
      FontChineseS = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);
      Font FontChineseL = new com.lowagie.text.Font(bfChinese, 20, com.lowagie.text.Font.NORMAL);
      tpl = writer.getDirectContent().createTemplate(200, 100);

      table = new PdfPTable(2);
      PdfPCell cell = null;
      if (P01Reporter.month==null||P01Reporter.month.equals("")) cell = new PdfPCell(new Phrase(P01Reporter.companyname +  (Integer.parseInt(P01Reporter.year)-1911) + "年薪資表", FontChineseL));
      else cell = new PdfPCell(new Phrase(P01Reporter.companyname +  (Integer.parseInt(P01Reporter.year)-1911) + "年" + P01Reporter.month + "月薪資表", FontChineseL));
      cell.setColspan(2);
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setBorderWidth(0);

      table.getDefaultCell().setBorderWidth(0);
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
      table.getDefaultCell().setFixedHeight(18);

      String dateStr = ("" + (new java.sql.Date((Calendar.getInstance()).getTimeInMillis()))).substring(0,10);
      dateStr = (Integer.parseInt(dateStr.substring(0,4))-1911) + dateStr.substring(4);
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(new Phrase("", FontChineseS));
      table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
      table.addCell(new Phrase("製表日期：" + dateStr, FontChineseS));

	  } catch(Exception e) {
	    throw new ExceptionConverter(e);
	  }
  }

  public void onEndPage(PdfWriter writer, Document document){
	  try {
      PdfContentByte cb = writer.getDirectContent();
      //cb.saveState();
      bfChinese = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
      Font FontChineseS = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL);;

      table.setTotalWidth(document.right() - document.left());
      table.writeSelectedRows(0, -1, document.left(), document.getPageSize().getHeight() - 50, cb);

      String text = "第 " + writer.getPageNumber() + " 之 ";
      float textSize = bfChinese.getWidthPoint(text, 12);
      float textBase = document.bottom() - 20;
      float adjust = bfChinese.getWidthPoint("0 頁", 12);
      cb.moveTo(document.left(), 70);
      cb.lineTo(document.right(), 70);
      cb.stroke();
      cb.beginText();
      cb.setFontAndSize(bfChinese, 12);
      cb.setTextMatrix(document.right() - textSize - adjust, textBase);
      cb.showText(text);
      cb.endText();
      cb.addTemplate(tpl, document.right() - adjust, textBase);
      //cb.saveState();
    } catch (Exception e){

    }
  }

  public void onCloseDocument(PdfWriter writer, Document document) {
    tpl.beginText();
    tpl.setFontAndSize(bfChinese, 12);
    tpl.setTextMatrix(0, 0);
    tpl.showText("" + (writer.getPageNumber() - 1) + " 頁");
    tpl.endText();
  }
}