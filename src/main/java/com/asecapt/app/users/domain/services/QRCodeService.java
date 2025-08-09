package com.asecapt.app.users.domain.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

@Service
public class QRCodeService {
    
    @Value("${app.qrcodes.upload-dir:qrcodes}")
    private String qrCodesUploadDir;
    
    private static final int QR_CODE_SIZE = 300;
    
    /**
     * Generate QR code for given text and save it to file
     * @param text The text to encode in QR code
     * @param fileName The name for the QR code file (without extension)
     * @return The path to the generated QR code file
     */
    public String generateQRCode(String text, String fileName) {
        try {
            System.out.println("Generating QR code for: " + text);
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(qrCodesUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            // Convert to image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            // Save to file
            String qrFileName = fileName + "_qr.png";
            Path qrFilePath = uploadPath.resolve(qrFileName);
            ImageIO.write(qrImage, "PNG", qrFilePath.toFile());
            
            String relativePath = Paths.get(qrCodesUploadDir, qrFileName).toString();
            System.out.println("QR code generated successfully: " + relativePath);
            
            return relativePath;
            
        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    /**
     * Generate QR code as byte array
     * @param text The text to encode in QR code
     * @return The QR code as byte array
     */
    public byte[] generateQRCodeBytes(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            
            return baos.toByteArray();
            
        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR code bytes: " + e.getMessage());
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    /**
     * Generate QR code as byte array with certificate code parameter
     * @param text The text to encode in QR code
     * @param certificateCode The certificate code (used for logging)
     * @return The QR code as byte array
     */
    public byte[] generateQRCodeBytes(String text, String certificateCode) {
        System.out.println("Generating QR code bytes for certificate: " + certificateCode);
        return generateQRCodeBytes(text);
    }
}