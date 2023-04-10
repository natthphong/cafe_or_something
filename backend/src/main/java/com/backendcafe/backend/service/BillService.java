package com.backendcafe.backend.service;

import com.backendcafe.backend.config.JwtFilter;
import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.entity.Bill;
import com.backendcafe.backend.exception.BaseException;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.repository.BillRepository;
import com.backendcafe.backend.untils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@Slf4j
public class BillService {

    @Value("${project.bills}")
    private String path;
    private final BillRepository billRepository;
    private final JwtFilter jwtFilter;

    public BillService(BillRepository billRepository, JwtFilter jwtFilter) {
        this.billRepository = billRepository;
        this.jwtFilter = jwtFilter;
    }

    public boolean validateBodyToGenerateReport(Map<String, Object> body) {
        return body.containsKey("name") && body.containsKey("contactNumber") &&
                body.containsKey("email") && body.containsKey("paymentMethod")
                && body.containsKey("productDetails") && body.containsKey("totalAmount");
    }

    public ResponseEntity<JsonModel> generateReport(Map<String, Object> body) {
        log.info("Inside GenerateReport {} path {}", body, path);
        try {
            String filename;
            if (validateBodyToGenerateReport(body)) {
                if (body.containsKey("isGenerate") && !(Boolean) body.get("isGenerate")) {
                    filename = (String) body.get("uuid");
                    log.info("isGenerated");
                } else {
                    log.info("Generate UUID");
                    filename = CafeUtils.getUUID();
                    body.put("uuid", filename);
                    insertBill(body);

                }
                String data = "Name : " + body.get("name") + "\n" + "Contact Number: " + body.get("contactNumber")
                        + "\n" + "Email: " + body.get("email") + "\n" + "Payment Method: " + body.get("paymentMethod");
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + filename + ".pdf"));
                document.open();
                setRectangleInPdf(document);
                Paragraph chunk = new Paragraph("Tar Cafe Management System", getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);
                Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(paragraph);
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);
                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) body.get("productDetails"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(table);
                Paragraph footer = new Paragraph("\n\n\nTotal: " + body.get("totalAmount") + "\n"
                        + "Thank you for visiting. Please visit again!!", getFont("Data")
                );
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);
                document.close();
                return CafeUtils.message("uuid :" + filename, HttpStatus.OK);
            }
            return CafeUtils.message("Required data not found", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows {}", data);
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("tableheder");
        Stream.of("Name", "Category", "Quantity", "Price", "Sub total")
                .forEach(title -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(title));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");

        switch (type) {
            case "Header":
                Font header = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                header.setStyle(Font.BOLD);
                return header;
            case "Data":
                Font data = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                data.setStyle(Font.BOLD);
                return data;
            default:
                return new Font();
        }

    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside RECTANGLE PDF");
        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(Map<String, Object> body) {
        log.info("Inside Insert Bill {}", body);
        try {
            Bill bill = new Bill();
            bill.setUuid((String) body.get("uuid"));
            bill.setName((String) body.get("name"));
            bill.setEmail((String) body.get("email"));
            bill.setContactNumber((String) body.get("contactNumber"));
            bill.setPaymentMethod((String) body.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) body.get("totalAmount")));
            bill.setProductDetail((String) body.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billRepository.save(bill);
            log.info("save bill");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResponseEntity<List<Bill>> getBills() {

        log.info("Inside getBills");
        try {
            List<Bill> list = new ArrayList<>();
            if (jwtFilter.isAdmin()) {
                log.info("IS ADMIN");
                list = billRepository.getAllBills();

            } else {
                log.info("USERNAME : {}", jwtFilter.getCurrentUser());
                list = billRepository.getBillByUserName(jwtFilter.getCurrentUser());
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<byte[]> getPdf(Map<String, Object> body) throws BaseException {
        log.info("Inside getPdf {}", body);
        try {
            byte[] byteArray = new byte[0];
            if (!body.containsKey("uuid") && !validateBodyToGenerateReport(body)) {
                throw new BaseException(CafeConstants.INVALID_DATA);
            }
            String filePath = path + File.separator + (String) body.get("uuid") + ".pdf";
            log.info("filepath {}", filePath);
            if (CafeUtils.isFileExit(filePath)) {
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
                body.put("isGenerate", false);
                generateReport(body);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        throw new BaseException(CafeConstants.DEFAULT_ERROR);

    }

    private byte[] getByteArray(String filePath) throws IOException {
        log.info("Inside getByteArray");
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(filePath);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    public ResponseEntity<JsonModel> deleteById(Integer billId) {
        log.info("Inside DeleteById");
        try {
            
            Optional<Bill> bill = billRepository.findById(billId);
            if (!bill.isEmpty()) {
                billRepository.delete(bill.get());
                return CafeUtils.message("DELETE BILL SUCCESS", HttpStatus.OK);

            } else {
                return CafeUtils.message("Bill Id Invalid", HttpStatus.BAD_REQUEST);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.deFault();
    }
}
