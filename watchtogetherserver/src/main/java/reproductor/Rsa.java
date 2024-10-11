package reproductor;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Rsa {

	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	private PublicKey publicKeyCliente;
	
	public Rsa() {
		generarClaves();
	}
	
	public void generarClaves() {
		
		try {
			
			KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
			generador.initialize(2048);
			KeyPair pareja = generador.generateKeyPair();
			publicKey = pareja.getPublic();
			privateKey = pareja.getPrivate();
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cargarClavePublica(String mensajeServidor) {
		try {	
			byte[] bytesClave = Base64.getDecoder().decode(mensajeServidor);
			X509EncodedKeySpec ks = new X509EncodedKeySpec(bytesClave);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			publicKeyCliente = kf.generatePublic(ks);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String encriptarConClavePublicaCliente(String mensaje) {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(128); // The AES key size in number of bits
			SecretKey claveAES = generator.generateKey();
			Cipher cifradorEnAES = Cipher.getInstance("AES");
			cifradorEnAES.init(Cipher.ENCRYPT_MODE, claveAES);
			byte[] mensajeEncriptadoEnAES = cifradorEnAES.doFinal(mensaje.getBytes());
						
			byte[] claveAESToBytes = Base64.getEncoder().encodeToString(claveAES.getEncoded()).getBytes();
			Cipher cifradorRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cifradorRSA.init(Cipher.ENCRYPT_MODE, publicKeyCliente);
			byte[] claveAESEncriptadaEnRSA = cifradorRSA.doFinal(claveAESToBytes);
			//El paquete que se envia tiene la siguiente estructura:CLAVE ASES EN RSA:MENSAJE ENCRIPTADO EN AES
			return Base64.getEncoder().encodeToString(claveAESEncriptadaEnRSA)+":"+Base64.getEncoder().encodeToString(mensajeEncriptadoEnAES);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String desencriptar(String mensajeEncriptado) {
		
		try {
			String claveAESEncriptada = mensajeEncriptado.split(":")[0];
			String mensajeEncriptadoPorAES = mensajeEncriptado.split(":")[1];
			
			byte[] claveAESEncriptadaEnRSA = Base64.getDecoder().decode(claveAESEncriptada);
			Cipher descifradorRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			descifradorRSA.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] claveAESDesencriptadaBytes = descifradorRSA.doFinal(claveAESEncriptadaEnRSA);
			
			String claveAESEncoded = new String(claveAESDesencriptadaBytes, "UTF8");
			byte[] claveAESBytes = Base64.getDecoder().decode(claveAESEncoded);
			SecretKey claveAES = new SecretKeySpec(claveAESBytes, 0, claveAESBytes.length, "AES"); 
			
			Cipher descrifradorAES = Cipher.getInstance("AES");
			descrifradorAES.init(Cipher.DECRYPT_MODE, claveAES);
			byte[] mensajeDesencriptadoBytes = descrifradorAES.doFinal(Base64.getDecoder().decode(mensajeEncriptadoPorAES));
			String mensajeDesencriptado = new String(mensajeDesencriptadoBytes);
			
			
			return mensajeDesencriptado;
		}catch (Exception e) {
			return null;
		}
		
	}

	public boolean estaLaClavePublicaDelCliente() {
			
			return publicKeyCliente != null;
			
	}
	
	public String getPublicKeyEncoded() {
		
		byte[] clave = publicKey.getEncoded();
		return Base64.getEncoder().encodeToString(clave);
	}
	
}