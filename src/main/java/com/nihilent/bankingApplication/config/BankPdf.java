package com.nihilent.bankingApplication.config;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
//import com.itextpdf.kernel.pdf.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;

public class BankPdf implements IEventHandler {

	 private final Document document;
	    private final Image logo;
	    private final DeviceRgb iciciOrange = new DeviceRgb(239, 127, 26);
	    
	    
	    private final Long accountNumber;
	    private final String   name;
	    private final String ifsc;
	    private final String accountType;
	    
	    private final String email;
	    private final Long mobile;
	    private final Double balance;

	    private final LocalDate dob;
	    
	    public BankPdf(Document document, String logoPath , Long accountNumber,String name,String ifsc, String accountType,String email,Long mobile,Double balance,LocalDate dob) throws IOException {
	        this.document = document;
	        this.accountNumber = accountNumber;
	        this.name = name;
	        this.balance = balance;
	        this.ifsc = ifsc;
	        this.accountType = accountType;
	      
	        this.email = email;
	        this.mobile = mobile;
	        this.dob=dob;
	        ImageData imageData = ImageDataFactory.create(logoPath);
	        this.logo = new Image(imageData).scaleToFit(50, 50);
	    }

	    @Override
	    public void handleEvent(Event event) {
	        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
	        PdfDocument pdfDoc = docEvent.getDocument();
	        PdfPage page = docEvent.getPage();
	        int pageNumber = pdfDoc.getPageNumber(page);
	        Rectangle pageSize = page.getPageSize();

	        float leftMargin = document.getLeftMargin();
	        float topY = pageSize.getTop() - 20;
	        float footerY = pageSize.getBottom() + 30;

	        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
	        Canvas canvas = new Canvas(pdfCanvas, pageSize);

	        // --- HEADER ---

	        // Draw Logo (left)
	        logo.setFixedPosition(pageSize.getLeft() + leftMargin, topY -63);
	        canvas.add(logo);

	        
//	        canvas.add(logo);
	        // Title (centered)
	        canvas.showTextAligned(new Paragraph("E-Banking")
	                        .setFontSize(12)
	                        .setBold()
	                        .setFontColor(iciciOrange),
	                pageSize.getWidth() / 2, topY, TextAlignment.CENTER);

	        // Account info (left-aligned)
	        float infoY = topY - 30;

	        canvas.showTextAligned(new Paragraph("Account Number: " + accountNumber).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 60, infoY, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("Name: " + name).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 60, infoY - 12, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("Balance: ₹" + balance).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 60, infoY - 24, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("IFSC: " + ifsc).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 60, infoY - 36, TextAlignment.LEFT);

	        canvas.showTextAligned(new Paragraph("Account Type: " + accountType).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 300, infoY, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("DOB: " + dob).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 300, infoY - 12, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("Email: " + email).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 300, infoY - 24, TextAlignment.LEFT);
	        canvas.showTextAligned(new Paragraph("Mobile: " + mobile).setFontSize(9),
	                pageSize.getLeft() + leftMargin + 300, infoY - 36, TextAlignment.LEFT);

	        // Header line
	        pdfCanvas.setStrokeColor(iciciOrange)
	                .moveTo(pageSize.getLeft() + leftMargin, infoY - 45)
	                .lineTo(pageSize.getRight() - leftMargin, infoY - 45)
	                .stroke();
//	        footerY = pageSize.getBottom() + 5;
//	        topY = topY - 60;
//	        topY = pageSize.getTop() +60;
	        
	        canvas.showTextAligned(new Paragraph("Transaction Statement")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(iciciOrange),
            pageSize.getTop()-60, infoY, TextAlignment.CENTER);
	        
	        
//	        canvas.showTextAligned(new Paragraph("E-Banking Transaction Statement")
//                    .setFontSize(12)
//                    .setBold()
//                    .setFontColor(iciciOrange),
	        
	        
//            pageSize.getWidth() / 2, topY, TextAlignment.CENTER);
	        
	        
	        
	        // --- FOOTER ---

	        // Page number
	        canvas.showTextAligned(new Paragraph("Page " + pageNumber),
	                pageSize.getWidth() / 2, footerY,
	                TextAlignment.CENTER);

	        // Footer line
	        pdfCanvas.setStrokeColor(iciciOrange)
	                .moveTo(pageSize.getLeft() + leftMargin, footerY + 15)
	                .lineTo(pageSize.getRight() - leftMargin, footerY + 15)
	                .stroke();

	        canvas.close();
	    }

}
