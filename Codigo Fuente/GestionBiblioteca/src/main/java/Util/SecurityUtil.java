package Util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para manejar operaciones de seguridad como encriptación de contraseñas.
 */
public class SecurityUtil {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getName());
    
    /**
     * Genera un hash SHA-256 para la contraseña proporcionada.
     * 
     * @param password La contraseña a encriptar
     * @return String con el hash SHA-256 en formato hexadecimal
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.log(Level.SEVERE, "Error al generar hash de contraseña", ex);
            throw new RuntimeException("Error de seguridad: no se pudo cifrar la contraseña");
        }
    }
    
    /**
     * Verifica si una contraseña coincide con un hash almacenado.
     * 
     * @param password La contraseña ingresada por el usuario
     * @param storedHash El hash almacenado en la base de datos
     * @return true si la contraseña coincide con el hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String passwordHash = hashPassword(password);
        return passwordHash.equals(storedHash);
    }
    
    /**
     * Genera un token único para verificación de email u otras operaciones.
     * 
     * @return String con el token generado
     */
    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Convierte un array de bytes a representación hexadecimal.
     * 
     * @param hash Array de bytes a convertir
     * @return String con la representación hexadecimal
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}