package net.gnu.util;

import org.apache.james.mime4j.codec.Base64InputStream;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.BinaryBody;
import net.gnu.util.FileUtil;
import java.util.regex.Pattern;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import net.gnu.util.ExceptionLogger;
import java.util.TreeMap;
import java.net.URLDecoder;
import java.util.List;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.io.ByteArrayInputStream;
import java.text.NumberFormat;

public class Mht2Htm {
	private static String TAG = "Mht2Htm";

	private static final String urlStr = "url(\"\")";//(([Uu][Rr][Ll]\\()|([sS][rR][cC]=))
	private static final String dataSrc = "data-src=\"\"";//[Dd][Aa][Tt][Aa]-
	private static final String base64PatStr = "\"data:([a-z]+)/([a-zA-Z0-9\\-]{2,4});base64,([^\"]+)\"";
	private static final Pattern base64Pattern = Pattern.compile(base64PatStr);
	private static final String href = "(([hH][Rr][Ee][Ff])|([sS][rR][cC]))=([\"'])([^\n]+?)\\4";
	private static final Pattern hrefPat = Pattern.compile(href);
	private static Map<String, String> mapLinkName = new TreeMap<>();
	private static Map<String, String> mapNameLink = new TreeMap<>();

	public static String mht2html(final String s_SrcMht, String s_DescHtml, boolean extractImages, boolean extractHtml) throws IOException, MimeException {
		final File f = new File(s_SrcMht);
		final String fName = f.getName();
		final String toLowerCase = fName.toLowerCase();
		if (toLowerCase.endsWith(".mht")
			|| toLowerCase.endsWith(".mhtm")
			|| toLowerCase.endsWith(".mhtml")) {
			DefaultMessageBuilder builder = new DefaultMessageBuilder();
			extractMHTFile(f, builder, s_DescHtml, fName, extractImages, extractHtml);
		}
		return null;
	}

	private static void extractMHTFile(final File f, final DefaultMessageBuilder builder, final String s_DescHtml, final String fName, final boolean extractImages, boolean extractHtml) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			final MimeConfig config = new MimeConfig.Builder().setMaxLineLen(10000).build();
			builder.setMimeEntityConfig(config);
			final Message message = builder.parseMessage(bis);
			final Body body = message.getBody();
			extractMht(body, s_DescHtml, fName, extractImages, extractHtml);
		} finally {
			FileUtil.close(bis, fis);
		}
	}

	private static void extractMht(final Body body, String s_DescHtml, final String fName, final boolean extractImages, final boolean extractHtml) throws IOException {
		ExceptionLogger.d(TAG, "extractMht " + s_DescHtml + ", fName " + fName + ", extractImages " + extractImages + ", extractHtml " + extractHtml);
		String htmlContent = "";
		if (body instanceof Multipart) {
			if (!s_DescHtml.endsWith("/")) {
				s_DescHtml = s_DescHtml + "/";
			}
			final String resourceDirName = fName + "_files/";
			final String resourcePath = s_DescHtml + resourceDirName;
			final File resourceDir = new File(resourcePath);
			if (resourceDir.isFile() || !resourceDir.mkdirs()) {
				ExceptionLogger.e(TAG, "Can't create resources dir " + resourcePath);
			}
			final Multipart mp = (Multipart) body;
			final List<Entity> bodyParts = mp.getBodyParts();
			final NumberFormat nf = NumberFormat.getInstance();
			int ind = 0;
			String htmlFilePath = "";

			for (Entity bodyPart : bodyParts) {
				final Body b = bodyPart.getBody();
				final Entity e = b.getParent();
				ExceptionLogger.d(TAG, e.getFilename() + ", " + e.getMimeType() + ", " + e.isMultipart() 
					  + ", " + e.getDispositionType()
					  + ", " + e.getCharset() + ", " + e.getBody()
					  + ", " + e.getContentTransferEncoding() + ", \n" + e.getHeader().toString().trim()
					  );
				String filename = e.getFilename();
				String fFilename = null;
				if (filename != null) {
					fFilename = URLDecoder.decode(filename);
				}
				final String mimeType = e.getMimeType();
				String fNameSave = "";
				if (ind == 0) {
					if (b instanceof TextBody) {
						final TextBody bb = (TextBody)b;
						final InputStream is = bb.getInputStream();
						if (mimeType != null && mimeType.startsWith("text")) {
							if (fFilename != null) {
								htmlFilePath = s_DescHtml + fFilename;
								final String savedHtml = FileUtil.saveISToFile(is, s_DescHtml, fFilename, false, true);
								ExceptionLogger.i(TAG, "savedHtml1 " + savedHtml);
							} else {
								final String destHtml = fName + "." + mimeType.substring("text/".length());
								htmlFilePath = s_DescHtml + destHtml;
								final String savedHtml = FileUtil.saveISToFile(is, s_DescHtml, destHtml, false, true);
								ExceptionLogger.i(TAG, "savedHtml2 " + savedHtml);
							}
						}
						htmlContent = FileUtil.readFileByMetaTag(new File(htmlFilePath))[0]; 
						htmlContent = htmlContent.replaceAll("(?i)<base\\s+[^>]*?>", "");
						final Matcher m = base64Pattern.matcher(htmlContent);
						final StringBuffer sb = new StringBuffer();
						while (m.find()) {
							final InputStream b64is = new Base64InputStream(new ByteArrayInputStream(m.group(3).getBytes()));
							final String st = nf.format(++ind) +"." + m.group(2);
							String s = FileUtil.saveISToFile(b64is, resourcePath, st, false, true);
							ExceptionLogger.i(TAG, "resourcePath + st " + resourcePath + st + ", " + s);

							if (extractHtml) {
								m.appendReplacement(sb, "\"" + resourceDirName + st +"\"");
							}
							//Log.d(TAG, "resourceDirName+st " + resourceDirName + st);
						}
						if (sb.length() > 0) {
							if (extractHtml) {
								m.appendTail(sb);
							}
							htmlContent = sb.toString();
							FileUtil.writeFileAsCharset(new File(htmlFilePath), htmlContent, "utf-8");
						}
					}
					if (ind == 0) {
						++ind;
					}
				} else {
					if (extractImages && b instanceof BinaryBody) {
						final BinaryBody bb = (BinaryBody)b;
						final InputStream is = bb.getInputStream();

						if (fFilename != null) {
							fNameSave = FileUtil.saveISToFile(is, resourcePath, fFilename, true, false);
						} else {
							fFilename = getFileName(e);
							fNameSave = FileUtil.saveISToFile(is, resourcePath, fFilename, true, false);
						}
						final String get = mapNameLink.get(fFilename);
						mapLinkName.remove(get);
						mapLinkName.put(get, resourceDirName + fNameSave);
					} else if (extractHtml && b instanceof TextBody) {
						final TextBody bb = (TextBody)b;
						final InputStream is = bb.getInputStream();

						if (fFilename != null) {
							fNameSave = FileUtil.saveISToFile(is, resourcePath, fFilename, true, false);
						} else {
							fFilename = getFileName(e);
							fNameSave = FileUtil.saveISToFile(is, resourcePath, fFilename, true, false);
						}

						final String get = mapNameLink.get(fFilename);
						mapLinkName.remove(get);
						mapLinkName.put(get, resourceDirName + fNameSave);
						final File cssFile = new File(resourcePath, fNameSave);
						ExceptionLogger.d(TAG, "resourcePath " + resourcePath + ", fNameSave " + fNameSave + ", cssFile " + cssFile.getAbsolutePath());
						final String content = FileUtil.readCss(cssFile)[0]; 

						final Matcher m = base64Pattern.matcher(content);
						final String fN = fNameSave + "_files/";
						String st;
						final StringBuffer sb = new StringBuffer();
						while (m.find()) {
							Base64InputStream b64is = new Base64InputStream(new ByteArrayInputStream(m.group(3).getBytes()));
							st = nf.format(++ind) +"." + m.group(2);
							FileUtil.saveISToFile(b64is, resourcePath + fN, st, false, false);

							if (!extractImages) {
								m.appendReplacement(sb, "\"" + fN + st +"\"");
							}
						}
						if (sb.length() > 0) {
							if (!extractImages) {
								m.appendTail(sb);
							}
							FileUtil.writeFileAsCharset(cssFile, sb.toString(), "utf-8");
						}
					}
				}
			}
			//Log.d(TAG, "htmlContent = " + htmlContent);
			if (extractHtml) {
				final Matcher hrefMat = hrefPat.matcher(htmlContent);
				final StringBuffer sb = new StringBuffer();
				while (hrefMat.find()) {
					final String group5 = hrefMat.group(5);
					final String value5 = mapLinkName.get(group5);
					ExceptionLogger.d(TAG, "hrefMat.group=" + hrefMat.group());
					ExceptionLogger.d(TAG, "pair: " + group5 + ", " + value5);
					final String group4 = hrefMat.group(4);
					final String src = hrefMat.group(1) + "=" + group4 + value5 + group4;
					ExceptionLogger.d(TAG, "src=" + src);
					if (value5 != null) {
						hrefMat.appendReplacement(sb, src);
					}
				}
				hrefMat.appendTail(sb);
				if (sb.length() > 0) {
					FileUtil.writeFileAsCharset(new File(htmlFilePath), sb.toString(), "utf-8");
				}
			} else {
				new File(htmlFilePath).delete();
			}
		}
	}

	private static String cid = "cid:([a-zA-Z]+)-([a-zA-Z0-9-]+)@mhtml.blink";
	private static final Pattern patCID = Pattern.compile(cid);
	private static String cid2 = "<frame-([a-zA-Z0-9]+)@mhtml.blink>";
	private static final Pattern patCID2 = Pattern.compile(cid2);

	private static String getFileName(Entity e) {
		String filename = e.getFilename();
		ExceptionLogger.d(TAG, "e.getFileName " + filename);
		final String contentID;
		if (filename == null) {
			final String[] split = e.getHeader().toString().split("[\r\n]+");
			filename = getValue(split, "Content-Location");
			ExceptionLogger.d(TAG, "filename " + filename);

//			Mht2Html: null, image/jpeg, false, null, us-ascii, org.apache.james.mime4j.message.BasicBodyFactory$BinaryBody1@40965a1, base64, 
//			Content-Type: image/jpeg
//			Content-Transfer-Encoding: base64
//			Content-Location: https://scontent.cdninstagram.com/v/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com&_nc_ohc=HcmkQEgPaSEAX-vqNTF&edm=AE33r6YAAAAA&ccb=7-4&oh=6229f1fa6c37d4a8d0d7a808e3bc69a4&oe=60C2C70F&_nc_sid=c7f4db&ig_cache_key=YW5vbnltb3VzX3Byb2ZpbGVfcGlj.2-ccb7-4
//			Mht2Html: filename https://scontent.cdninstagram.com/v/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com&_nc_ohc=HcmkQEgPaSEAX-vqNTF&edm=AE33r6YAAAAA&ccb=7-4&oh=6229f1fa6c37d4a8d0d7a808e3bc69a4&oe=60C2C70F&_nc_sid=c7f4db&ig_cache_key=YW5vbnltb3VzX3Byb2ZpbGVfcGlj.2-ccb7-4
//			Mht2Html: map https://scontent.cdninstagram.com/v/t51.2885-19/44884218_345707102882519_2446069589734326272_n.jpg?_nc_ht=scontent.cdninstagram.com&amp;_nc_ohc=HcmkQEgPaSEAX-vqNTF&amp;edm=AE33r6YAAAAA&amp;ccb=7-4&amp;oh=6229f1fa6c37d4a8d0d7a808e3bc69a4&amp;oe=60C2C70F&amp;_nc_sid=c7f4db&amp;ig_cache_key=YW5vbnltb3VzX3Byb2ZpbGVfcGlj.2-ccb7-4=44884218_345707102882519_2446069589734326272_n.jpg__nc_ht=scontent.cdninstagram.com&_nc_ohc=HcmkQEgPaSEAX-vqNTF&edm=AE33r6YAAAAA&ccb=7-4&oh=6229f1fa6c37d4a8d0d7a808e3bc69a4&oe=60C2C70F&_nc_sid=c7f4db&ig_cache_key=YW5vbnltb3VzX3Byb2ZpbGVfcGlj.2-ccb7-4

			if (filename != null) {
				filename = URLDecoder.decode(filename);
				ExceptionLogger.d(TAG, "getFileName1 " + filename);
				contentID = filename;
				final Matcher matcher = patCID.matcher(filename);
				//Log.d(TAG, "getFileName contentID " + (patCID.matcher(contentID)).find());
				//Log.d(TAG, cid);
				if (matcher.find()) {
					filename = matcher.group(2);
				} 
				ExceptionLogger.d(TAG, "getFileName2 " + filename);
			} else {
//				Content-Type: text/html
//				Content-ID: <frame-537D0F17B312CC12D7374EAC3998FD7C@mhtml.blink>
//				Content-Transfer-Encoding: quoted-printable
				contentID = getValue(split, "Content-ID");
				final Matcher matcher = patCID2.matcher(contentID);
				if (matcher.find()) {
					filename = matcher.group(1);
				}
				filename = URLDecoder.decode(filename);
				ExceptionLogger.d(TAG, "getFileName3 " + filename);
			}
			int indexOf = filename.indexOf("#");
			if (indexOf > 0) {
				filename = filename.substring(0, indexOf);
			}
			filename = filename.replaceAll("[?\\:*|\"<>#+%]", "_");
			
			String ext = getValue(split, "Content-Type");
			ext = ext.substring(ext.indexOf("/") + 1);
			final int indexOfMinus = ext.indexOf("-");
			if (indexOfMinus > 0) {
				ext = ext.substring(indexOfMinus + 1);
			}
			final int indexOfPlus = ext.indexOf("+");
			if (indexOfPlus > 0) {
				ext = ext.substring(indexOfPlus + 1);
			}

			final String toLowerCase = filename.toLowerCase();
			if (!toLowerCase.endsWith("." + ext) && (!toLowerCase.endsWith(".jpg") || !ext.equals(".jpeg"))) {
				filename = filename + "." + ext;
			}
			//filename = filename.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
			indexOf = filename.lastIndexOf("/");
			final int length = filename.length();
			if (length - indexOf > 256) {
				filename = filename.substring(0, indexOf+1) + filename.substring(length - 255, length);
			}
			mapLinkName.put(contentID, filename);
			mapNameLink.put(filename, contentID);
			ExceptionLogger.d(TAG, "map " + contentID + "=" + filename);
		}
		//Log.d(TAG, "getFileName " + filename);
		return filename;
	}

	private static String getValue(final String[] split, final String key) {
		for (String s : split) {
			if (s.toLowerCase().startsWith(key.toLowerCase())) {
				return s.substring(key.length()+1).trim();
			}
		}
		return null;
	}
}
