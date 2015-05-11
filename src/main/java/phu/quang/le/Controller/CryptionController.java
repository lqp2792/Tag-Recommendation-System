package phu.quang.le.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;

import javacryption.aes.AesCtr;
import javacryption.jcryption.JCryption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CryptionController {
	@RequestMapping(value = "/encrypt")
	public void authenticate(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (req.getParameter("generateKeyPair") != null
				&& req.getParameter("generateKeyPair").equals("true")) {

			JCryption jc = new JCryption();
			KeyPair keys = jc.getKeyPair();
			request.getSession().getServletContext()
					.setAttribute("jCryptionKeys", keys);
			String e = jc.getPublicExponent();
			String n = jc.getKeyModulus();
			String md = String.valueOf(jc.getMaxDigits());

			/** Sends response **/
			PrintWriter out = response.getWriter();
			out.print("{\"e\":\"" + e + "\",\"n\":\"" + n
					+ "\",\"maxdigits\":\"" + md + "\"}");
			return;
		} else if (req.getParameter("handshake") != null
				&& req.getParameter("handshake").equals("true")) {

			JCryption jc = new JCryption((KeyPair) request.getSession()
					.getServletContext().getAttribute("jCryptionKeys"));
			String key = jc.decrypt(req.getParameter("key"));

			request.getSession().getServletContext()
					.removeAttribute("jCryptionKeys");
			request.getSession().getServletContext()
					.setAttribute("jCryptionKey", key);

			/** Encrypts password using AES **/
			String ct = AesCtr.encrypt(key, key, 256);

			/** Sends response **/
			PrintWriter out = response.getWriter();
			out.print("{\"challenge\":\"" + ct + "\"}");

			return;
		} else if (req.getParameter("decryptData") != null
				&& req.getParameter("decryptData").equals("true")
				&& req.getParameter("jCryption") != null) {

			/** Decrypts the request using password **/
			String key = (String) request.getSession().getServletContext()
					.getAttribute("jCryptionKey");

			String pt = AesCtr.decrypt(req.getParameter("jCryption"), key, 256);

			/** Sends response **/
			PrintWriter out = response.getWriter();
			out.print("{\"data\":\"" + pt + "\"}");
			return;
		}
		/** jCryption request to encrypt a String **/
		else if (req.getParameter("encryptData") != null
				&& req.getParameter("encryptData").equals("true")
				&& req.getParameter("jCryption") != null) {

			/** Encrypts the request using password **/
			String key = (String) request.getSession().getServletContext()
					.getAttribute("jCryptionKey");

			String ct = AesCtr.encrypt(req.getParameter("jCryption"), key, 256);

			/** Sends response **/
			PrintWriter out = response.getWriter();
			out.print("{\"data\":\"" + ct + "\"}");
			return;
		}
	}
}
