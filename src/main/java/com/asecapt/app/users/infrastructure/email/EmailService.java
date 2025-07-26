package com.asecapt.app.users.infrastructure.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private final SendGrid sendGrid;
    private final String fromEmail;
    private final String appUrl;

    public EmailService(
            @Value("${sendgrid.api-key}") String apiKey,
            @Value("${sendgrid.from-email}") String fromEmail,
            @Value("${app.url}") String appUrl) {
        this.sendGrid = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
        this.appUrl = appUrl;
    }

    public void sendVerificationEmail(String toEmail, String token) throws IOException {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Verifica tu cuenta de FiTech";
        
        String verificationLink = String.format("%s/verify-email?token=%s", appUrl, token);
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">¡Bienvenido a FiTech!</h2>
                <p>Gracias por registrarte. Para completar tu registro, por favor verifica tu dirección de correo electrónico haciendo clic en el siguiente botón:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">
                        Verificar mi email
                    </a>
                </div>
                <p>O copia y pega el siguiente enlace en tu navegador:</p>
                <p style="word-break: break-all;">%s</p>
                <p style="color: #666; font-size: 0.9em;">Este enlace expirará en 24 horas.</p>
                <p>Si no creaste esta cuenta, puedes ignorar este mensaje.</p>
            </div>
            """, verificationLink, verificationLink);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() >= 400) {
            throw new IOException("Error sending email: " + response.getBody());
        }
    }

    public void sendTestEmail(String toEmail) throws IOException {
        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        String subject = "Prueba de Email - FiTech";
        
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #333;">¡Prueba de Email Exitosa!</h2>
                <p>Este es un email de prueba para verificar la configuración de SendGrid en FiTech.</p>
                <div style="text-align: center; margin: 30px 0;">
                    <p style="color: #4CAF50; font-size: 1.2em;">✅ Si recibes este email, la configuración está correcta.</p>
                </div>
                <p style="color: #666; font-size: 0.9em;">Fecha y hora de envío: %s</p>
            </div>
            """.formatted(java.time.LocalDateTime.now());

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() >= 400) {
            throw new IOException("Error sending email: " + response.getBody());
        }
    }

    public void sendSupportEmail(String userName, String userEmail, String userPhone, 
                                String queryType, String subject, String message, 
                                String userType, Integer userId) throws IOException {
        Email from = new Email(fromEmail);
        
        // Direcciones de soporte 
        String[] supportEmails = {
            "maycolhiga.26@gmail.com",
            "Josecarlosganozapaton@modulosystands.com"
        };
        
        // Crear asunto con información relevante
        String emailSubject = String.format("[%s] %s - %s", 
            queryType.toUpperCase(), 
            userType != null ? userType : "USUARIO", 
            subject);
        
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                <div style="background: linear-gradient(135deg, #1976d2 0%%, #1565c0 100%%); color: white; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h2 style="margin: 0; font-size: 1.5rem;">Nueva Consulta de Soporte - FiTech</h2>
                    <p style="margin: 8px 0 0 0; opacity: 0.9;">Fecha: %s</p>
                </div>
                
                <div style="padding: 24px;">
                    <div style="background: #f8f9fa; padding: 16px; border-radius: 6px; margin-bottom: 20px;">
                        <h3 style="margin: 0 0 12px 0; color: #333; border-bottom: 2px solid #1976d2; padding-bottom: 8px;">Información del Usuario</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr style="border-bottom: 1px solid #e0e0e0;">
                                <td style="padding: 8px 0; font-weight: bold; color: #555; width: 120px;">Nombre:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e0e0e0;">
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Email:</td>
                                <td style="padding: 8px 0; color: #333;">%s</td>
                            </tr>
                            %s
                            <tr style="border-bottom: 1px solid #e0e0e0;">
                                <td style="padding: 8px 0; font-weight: bold; color: #555;">Tipo:</td>
                                <td style="padding: 8px 0;">
                                    <span style="background: %s; color: white; padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: 600;">
                                        %s
                                    </span>
                                </td>
                            </tr>
                            %s
                        </table>
                    </div>
                    
                    <div style="margin-bottom: 20px;">
                        <h3 style="margin: 0 0 12px 0; color: #333; border-bottom: 2px solid #ff9800; padding-bottom: 8px;">Consulta</h3>
                        <div style="background: #fff3e0; padding: 16px; border-radius: 6px; margin-bottom: 12px;">
                            <p style="margin: 0; font-weight: bold; color: #f57f17;">Tipo de Consulta:</p>
                            <p style="margin: 4px 0 0 0; color: #333;">%s</p>
                        </div>
                        <div style="background: #e3f2fd; padding: 16px; border-radius: 6px; margin-bottom: 12px;">
                            <p style="margin: 0; font-weight: bold; color: #1976d2;">Asunto:</p>
                            <p style="margin: 4px 0 0 0; color: #333;">%s</p>
                        </div>
                        <div style="background: #f3e5f5; padding: 16px; border-radius: 6px;">
                            <p style="margin: 0; font-weight: bold; color: #7b1fa2;">Mensaje:</p>
                            <div style="margin: 8px 0 0 0; padding: 12px; background: white; border-radius: 4px; border-left: 4px solid #7b1fa2; color: #333; line-height: 1.6;">
                                %s
                            </div>
                        </div>
                    </div>
                    
                    <div style="background: #e8f5e8; padding: 16px; border-radius: 6px; text-align: center;">
                        <p style="margin: 0; color: #2e7d32; font-weight: bold;">
                            📧 Responder directamente a: %s
                        </p>
                        <p style="margin: 8px 0 0 0; color: #555; font-size: 0.9rem;">
                            El usuario espera una respuesta en las próximas 24 horas
                        </p>
                    </div>
                </div>
                
                <div style="background: #f5f5f5; padding: 16px; text-align: center; border-radius: 0 0 8px 8px; border-top: 1px solid #e0e0e0;">
                    <p style="margin: 0; color: #666; font-size: 0.8rem;">
                        Este email fue generado automáticamente por el sistema de soporte de FiTech
                    </p>
                </div>
            </div>
            """, 
            LocalDateTime.now().toString(),
            userName,
            userEmail,
            userPhone != null && !userPhone.trim().isEmpty() ? 
                String.format("<tr style=\"border-bottom: 1px solid #e0e0e0;\"><td style=\"padding: 8px 0; font-weight: bold; color: #555;\">Teléfono:</td><td style=\"padding: 8px 0; color: #333;\">%s</td></tr>", userPhone) : "",
            userType != null && userType.equals("TRAINER") ? "#4caf50" : "#2196f3",
            userType != null ? (userType.equals("TRAINER") ? "TRAINER" : "CLIENTE") : "USUARIO",
            userId != null ? String.format("<tr><td style=\"padding: 8px 0; font-weight: bold; color: #555;\">ID Usuario:</td><td style=\"padding: 8px 0; color: #333;\">#%d</td></tr>", userId) : "",
            getQueryTypeDescription(queryType),
            subject,
            message.replace("\n", "<br>"),
            userEmail
        );

        Content content = new Content("text/html", htmlContent);
        
        // Crear email con el primer destinatario
        Email primaryTo = new Email(supportEmails[0]);
        Mail mail = new Mail(from, emailSubject, primaryTo, content);
        
        // Agregar los otros destinatarios como CC
        for (int i = 1; i < supportEmails.length; i++) {
            mail.getPersonalization().get(0).addTo(new Email(supportEmails[i]));
        }

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() >= 400) {
            throw new IOException("Error sending support email: " + response.getBody());
        }
    }

    private String getQueryTypeDescription(String type) {
        return switch (type) {
            case "technical" -> "Problema Técnico";
            case "billing" -> "Facturación y Pagos";
            case "account" -> "Mi Cuenta";
            case "service" -> "Servicios";
            case "other" -> "Otros";
            default -> type;
        };
    }
} 