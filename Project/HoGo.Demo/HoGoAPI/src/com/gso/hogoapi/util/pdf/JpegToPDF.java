package com.gso.hogoapi.util.pdf;

import java.io.FileOutputStream;
//The image class which will hold the input image
import com.itextpdf.text.Image;
//PdfWriter object to write the PDF document
import com.itextpdf.text.pdf.PdfWriter;
//Document object to add logical image files to PDF
import com.itextpdf.text.Document;

public class JpegToPDF {
  
  public boolean convertJpegToPDF(String jpgPathFileInput,FileOutputStream pdfPathFileOutput)
  {
	     boolean result=false;
          try {
          //Create Document Object
          Document convertJpgToPdf = new Document();
          //Create PdfWriter for Document to hold physical file
          PdfWriter.getInstance(convertJpgToPdf, pdfPathFileOutput);
          convertJpgToPdf.open();
          //Get the input image to Convert to PDF
          Image convertJpg = Image.getInstance(jpgPathFileInput);
          //Add image to Document
          convertJpgToPdf.add(convertJpg);
          //Close Document
          convertJpgToPdf.close();
          System.out.println("Successfully Converted JPG to PDF in iText");
          result=true;
          
          
      } catch (Exception i1) {
          i1.printStackTrace();
          result=false;
      } 
          
     return result;     
  } 
  
  public void convertJpegToPDF2(byte[] inputStream,FileOutputStream pdfPathFileOutput)
  {
          try {
          //Create Document Object
          Document convertJpgToPdf = new Document();
          //Create PdfWriter for Document to hold physical file
          PdfWriter.getInstance(convertJpgToPdf, pdfPathFileOutput);
          convertJpgToPdf.open();
          //Get the input image to Convert to PDF
          Image convertJpg = Image.getInstance(inputStream);
          //Add image to Document
          convertJpgToPdf.add(convertJpg);
          //Close Document
          convertJpgToPdf.close();
          System.out.println("Successfully Converted JPG to PDF in iText");
      } catch (Exception i1) {
          i1.printStackTrace();
      }
  } 
  
}

