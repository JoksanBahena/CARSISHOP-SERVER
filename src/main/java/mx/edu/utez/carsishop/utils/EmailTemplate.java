package mx.edu.utez.carsishop.utils;

public class EmailTemplate {

    public String getTemplate(String htmlBody) {
        String img = "https://res.cloudinary.com/sigsa/image/upload/v1681795223/sccul/logo/logo_ydzl8i.png";

        String firma = "<div style=\"display: flex; align-items: center;\">" +
                "<div>" +
                "<h3 style=\"font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60;\">CarsiShop</h3>" +
                "<p style=\"font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60;\">carsi.shop24@gmail.com</p>" +
                "</div>" +
                "</div>";

        return "<html>" +
                "<head>" +
                "<style>" +
                "h2 { font-family: Arial, sans-serif; font-size: 24px; line-height: 1.2; color: #002e60; }" +
                "p { font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; color: #002e60; }" +
                "a { color: #002e60; text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Hola, estimado usuario.</h2>"+
                htmlBody +
                firma +
                "</body>" +
                "</html>";
    }


}
