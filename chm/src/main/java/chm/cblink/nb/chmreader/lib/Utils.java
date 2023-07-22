package chm.cblink.nb.chmreader.lib;

//import android.support.annotation.RequiresPermission;

import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.*;
import java.io.*;
import android.widget.*;
import android.content.*;
import org.xml.sax.*;
import java.nio.charset.*;
import android.webkit.MimeTypeMap;
import org.apache.commons.compress.PasswordRequiredException;
import net.gnu.util.FileUtil;
import net.gnu.common.ExceptionLogger;
import net.gnu.common.ParentActivity;

/**
 * Created by nguyenbinh on 29/11/2016.
 */

public class Utils {
    private static final String TAG = "Utils";
	public final CHMFile chm;
	static Pattern HTML_PAT = Pattern.compile("[^\r]*?\\.([xds]?htm[l]?|xml|js|css|txt|md|java|ini|c|cpp|h|hpp|lua|sh|bat|list|depend|jsp|mk|config|configure|machine|asm|desktop|inc|i|plist|pro|py|bak|log)", Pattern.CASE_INSENSITIVE);

	public Utils(final CHMFile chm) {
		this.chm = chm;
	}
	
    public ArrayList<String> domparse(String filePath, String extractPath, String md5) throws PasswordRequiredException {
        final ArrayList<String> listSite = new ArrayList<>();
        listSite.add(md5);
//        Document doc = Jsoup.parse(chm.getResourceAsStream(""), "UTF-8", "");
//        Elements listObject = doc.getElementsByTag("object");
//        for (Element object : listObject) {
//            Elements listParam = object.getElementsByTag("param");
//            if (listParam.size() > 0) {
//                String name = "", local = "";
//                for (Element param : listParam) {
//                    if (param.attributes().getIgnoreCase("name").equalsIgnoreCase("name")) {
//                        name = param.attributes().getIgnoreCase("value");
//                    } else if (param.attributes().getIgnoreCase("name").equalsIgnoreCase("local")) {
//                        local = param.attributes().getIgnoreCase("value");
//                    }
//                }
//                listSite.add(local);
//                object.parent().prepend("<a href=\"" + local + "\">" + name + "</a>");
//                object.remove();
//            }
//        }
//        try {
//            FileOutputStream fosHTMLMap =  new FileOutputStream(extractPath + "/" +md5);
//            fosHTMLMap.write(doc.outerHtml().getBytes());
//            fosHTMLMap.close();
//
//            FileOutputStream fosListSite =  new FileOutputStream(extractPath + "/site_map_" +md5);
//            for(String str: listSite) {
//                fosListSite.write((str+";").getBytes());
//            }
//            fosListSite.close();
//            Log.e("Utils", "write ok " + "/site_map_" +md5);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("Utils", "write ok sitemap error");
//        }
        ///////////////////////////////////////////////////
        try {

            final BufferedOutputStream fosHTMLMap = new BufferedOutputStream(new FileOutputStream(extractPath + "/" + md5+".html"));
            final BufferedOutputStream fosHTMLMapNoPreview = new BufferedOutputStream(new FileOutputStream(extractPath + "/" + md5+"_nopreview.html"));
            final BufferedOutputStream fosListSite = new BufferedOutputStream(new FileOutputStream(extractPath + "/site_map_" + md5));
            try {
                fosListSite.write((md5 + "\n").getBytes());
            } catch (IOException e) {
                ExceptionLogger.e(TAG, e.getMessage(), e);
            }
			final InputStream is = chm.getResourceAsStream("");
			ExceptionLogger.d(TAG, "domparse chm.getResourceAsStream(\"\") " + is);
            if (is != null) {
                final InputStreamReader r = new InputStreamReader(is, "UTF-8");
				final InputSource source = new InputSource(r);
				//source.setEncoding("UTF-8");
				
				SAXParserImpl.newInstance(null).parse(
					source,
                        new DefaultHandler() {
                            class MyUrl {
                                public int status = 0;
                                public String name;
                                public String local;

                                public String toString() {
                                    if (status == 1)
                                        return "<a href=\"#\">" + name + "</a>";
                                    else
                                        return "<a href=\"" + local + "\">" + name + "</a>";
                                }
                            }

                            MyUrl url = new MyUrl();
                            HashMap<String, String> myMap = new HashMap<String, String>();
                            int count = 0;

                            public void startElement(String uri, String localName, String qName,
                                                     Attributes attributes) throws SAXException {

                                if (qName.equals("param")) {
                                    count++;
                                    for (int i = 0; i < attributes.getLength(); i++) {
										myMap.put(attributes.getQName(i).toLowerCase(), attributes.getValue(i).toLowerCase());
                                    }
                                    if (myMap.get("name").equals("name") && myMap.get("value") != null) {
                                        url.name = myMap.get("value");
                                        url.status = 1;
                                    } else if (myMap.get("name").equals("local") && myMap.get("value") != null) {
                                        url.local = myMap.get("value");
                                        url.status = 2;
                                        listSite.add(url.local.replaceAll("%20", " "));
                                        try {
                                            fosListSite.write((url.local.replaceAll("%20", " ") + "\n").getBytes());
                                        } catch (IOException e) {
                                            ExceptionLogger.e(TAG, e.getMessage(), e);
                                        }
                                    }

                                    if (url.status == 2) {
                                        url.status = 0;
                                        try {
                                            fosHTMLMap.write(url.toString().getBytes());
                                        } catch (IOException e) {
                                            ExceptionLogger.e(TAG, e.getMessage(), e);
                                        }
                                    }
                                } else {
                                    if (url.status == 1) {
                                        try {
                                            fosHTMLMap.write(url.toString().getBytes());
                                            url.status = 0;
                                        } catch (IOException e) {
                                            ExceptionLogger.e(TAG, e.getMessage(), e);
                                        }
                                    }
                                }

                                if (!qName.equals("object") && !qName.equals("param"))
                                    try {
                                        fosHTMLMap.write(("<" + qName + ">").getBytes());
                                    } catch (IOException e) {
                                        ExceptionLogger.e(TAG, e.getMessage(), e);
                                    }


                            }

                            public void endElement(String uri, String localName,
                                                   String qName) throws SAXException {
                                if (!qName.equals("object") && !qName.equals("param"))
                                    try {
                                        fosHTMLMap.write(("</" + qName + ">").getBytes());
                                    } catch (IOException e) {
                                        ExceptionLogger.e(TAG, e.getMessage(), e);
                                    }
                            }
                        }
                );
            } else{
                fosHTMLMap.write("<html> <body> <ul>".getBytes());
				fosHTMLMapNoPreview.write("<html> <body> <ul>".getBytes());
				for(String fileName: chm.list()){
					if (fileName.startsWith("/")) {
						fileName = fileName.substring(1);
					}
					final String name = fileName.substring(fileName.lastIndexOf("/")+1);
					final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getExtension(name));
                    if (HTML_PAT.matcher(fileName).matches()
						|| mime != null && mime.startsWith("text")) {
                        fosListSite.write((fileName + "\n").getBytes());
                        fosHTMLMap.write(("\n<li><a href=\""+fileName+"\">" + fileName + "</a></li>").getBytes());
                        fosHTMLMapNoPreview.write(("\n<li><a href=\""+fileName+"\">" + fileName + "</a></li>").getBytes());
                        listSite.add(fileName);
                    } else if (ParentActivity.IMAGES_PATTERN.matcher(fileName).matches()) {
                        fosListSite.write((fileName + "\n").getBytes());
                        fosHTMLMap.write(("\n<li><a href=\""+fileName+"\">" + "<img src=\""+fileName+"\"/><br/>" + fileName + "</a></li>").getBytes());
                        fosHTMLMapNoPreview.write(("\n<li><a href=\""+fileName+"\">" + fileName + "</a></li>").getBytes());
                        listSite.add(fileName);
                    } else if (ParentActivity.MEDIA_PATTERN.matcher(fileName).matches()) {
                        fosListSite.write((fileName + "\n").getBytes());
                        fosHTMLMap.write(("\n<li><a href=\"" + fileName + "\">"
										 + "\n<video width=\"100%\" height=\"100%\" autoplay muted>"
										 + "<source src=\"" + fileName + "\" type=\"" + mime + "\"/>"
										 + "</video><br/>\n" + fileName + "</a></li>").getBytes());
                        fosHTMLMapNoPreview.write(("\n<li><a href=\""+fileName+"\">" + fileName + "</a></li>").getBytes());
                        listSite.add(fileName);
                    }
                }
                fosHTMLMapNoPreview.write("\n</ul> </body> </html>".getBytes());
                fosHTMLMap.write("\n</ul> </body> </html>".getBytes());
            }
            fosHTMLMap.close();
			fosHTMLMapNoPreview.close();
            fosListSite.close();
		} catch (PasswordRequiredException e) {
			throw e;
        } catch (Throwable e) {
			ExceptionLogger.e(TAG, e.getMessage(), e);
            //e.printStackTrace();
        }
        ///////////////////////////////////////////////////


        return listSite;
    }

    public static ArrayList<String> getListSite(String extractPath, String md5) {
        final ArrayList<String> listSite = new ArrayList<>();

        final StringBuilder reval = new StringBuilder();
        try {
            File file = new File(extractPath + "/site_map_" + md5);
			byte[] buf = FileUtil.readFileToMemory(file);
			reval.append(new String(buf));
        } catch (IOException e) {
            ExceptionLogger.e(TAG, e.getMessage());
            return null;
        }
        final String[] arrSite = reval.toString().split("\n");
        Collections.addAll(listSite, arrSite);
        return listSite;
    }

    public static ArrayList<String> getBookmark(String extractPath, String md5) {
        final ArrayList<String> listBookMark = new ArrayList<>();
        final StringBuilder reval = new StringBuilder();
        try {
            File file = new File(extractPath + "/bookmark_" + md5);
            byte[] buf = FileUtil.readFileToMemory(file);
			reval.append(new String(buf));
        } catch (IOException e) {
			e.printStackTrace();
            //ExceptionLogger.e(TAG, e.getMessage(), e);
		}
        final String[] arrSite = reval.toString().split(";");
        for (String str : arrSite) {
            if (str.length() > 0) {
                listBookMark.add(str);
            }
        }
        return listBookMark;
    }

    public static int getHistory(String extractPath, String md5) {
        final StringBuilder reval = new StringBuilder();
        try {
            File file = new File(extractPath + "/history_" + md5);
            byte[] buf = FileUtil.readFileToMemory(file);
			reval.append(new String(buf));
        } catch (IOException e) {
            ExceptionLogger.e(TAG, e.getMessage());
            return 1;
        }
        try {
            return Integer.parseInt(reval.toString());
        }catch (Exception e){
            return 0;
        }

    }



    public static void saveBookmark(String extractPath, String md5, ArrayList<String> listBookmark) {
        try {
            final FileOutputStream fos = new FileOutputStream(extractPath + "/bookmark_" + md5, false);
            for (String str : listBookmark) {
                fos.write((str + ";").getBytes());
            }
            fos.close();
        } catch (IOException ignored) {
        }
    }

    public static void saveHistory(String extractPath, String md5, int index) {
        try {
            final FileOutputStream fos = new FileOutputStream(extractPath + "/history_" + md5, false);
            fos.write((""+index).getBytes());
            fos.close();
        } catch (IOException ignored) {
        }
    }


    private String getSiteMap(String filePath) {
        StringBuilder reval = new StringBuilder();
        try {
//            if (chm == null) {
//                chm = new CHMFile(filePath);
//            }
            byte[] buf = new byte[1024];
            InputStream in = chm.getResourceAsStream("");
            int c = 0;
            while ((c = in.read(buf)) >= 0) {
                reval.append(new String(buf, 0, c));
            }
//            chm.close();
        } catch (IOException e) {
            ExceptionLogger.e(TAG, e.getMessage(), e);
            return "";
        }
        return reval.toString();
    }

    public boolean extract(final String filePath, final String pathExtract, final String password) {
        try {
//            if (chm == null) {
//                chm = new CHMFile(filePath);
//            }
			chm.setPassword(password);
            final File filePathTemp = new File(pathExtract);
            if (!filePathTemp.exists()) {
                if (!filePathTemp.mkdirs()) throw new IOException();
            }
//            for (String file : chm.list()) {
//                String temp = pathExtract + file;
//                String tempName = temp.substring(temp.lastIndexOf("/") + 1);
//                String tempPath = temp.substring(0, temp.lastIndexOf("/"));
//                File filePathTemp = new File(tempPath);
//                if (!filePathTemp.exists()) {
//                    if (!filePathTemp.mkdirs()) throw new IOException();
//                }
//                if (tempName.length() > 0) {
//                    FileOutputStream fos = null;
//                    try {
//                        fos = new FileOutputStream(temp);
//                        byte[] buf = new byte[1024];
//                        InputStream in = chm.getResourceAsStream(file);
//                        int c;
//                        while ((c = in.read(buf)) >= 0) {
//                            fos.write(buf, 0, c);
//                        }
//                    } catch (IOException e) {
//                        Log.d("Error extract file: ", file);
//                        e.printStackTrace();
//                    } finally {
//                        if (fos != null) fos.close();
//                    }
//                }
//            }
//            chm.close();
        } catch (IOException e) {
            ExceptionLogger.e(TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }

    public static String checkSum(String path) {
        String checksum = null;
        try {
            //FileInputStream fis = new FileInputStream(path);
            final File file = new File(path);
			final StringBufferInputStream fis = new StringBufferInputStream(path + "_" + file.lastModified() + "_" + file.length());
			final MessageDigest md = MessageDigest.getInstance("MD5");
			
            //Using MessageDigest update() method to provide input
            final byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
            final byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
        } catch (IOException ex) {
			ExceptionLogger.e(TAG, ex);
        } catch (NoSuchAlgorithmException ex) {
			ExceptionLogger.e(TAG, ex);
        }
        return checksum.trim();
    }

    public boolean extractSpecificFile(String filePath, String pathExtractFile, String insideFileName) throws PasswordRequiredException, IOException {
        ExceptionLogger.d(TAG, "extractSpecificFile filePath " + filePath + ", pathExtractFile " + pathExtractFile + ", insideFileName " + insideFileName);
		//try {
//            if (chm == null) {
//                chm = new CHMFile(filePath);
//            }
			final File file = new File(pathExtractFile);
            if (file.exists()
				&& file.length() > 0) return true;
            final String path = pathExtractFile.substring(0, pathExtractFile.lastIndexOf("/"));
            final File filePathTemp = new File(path);
            if (!filePathTemp.exists()) {
                if (!filePathTemp.mkdirs())
					return false;
					//throw new IOException();
            }
            OutputStream fos = null;
            try {
                fos = new BufferedOutputStream(new FileOutputStream(pathExtractFile));
                final byte[] buf = new byte[128*1024];
                final InputStream in = chm.getResourceAsStream(insideFileName);
				if (in == null) {
					return false;
				}
                int c;
                while ((c = in.read(buf)) >= 0) {
                    fos.write(buf, 0, c);
                }
				fos.flush();
				in.close();
				ExceptionLogger.d(TAG, "extractSpecificFile successfully filePath " + filePath + ", pathExtractFile " + pathExtractFile + ", insideFileName " + insideFileName);
            } catch (PasswordRequiredException e) {
				throw e;
//			} catch (IOException e) {
//                ExceptionLogger.e(TAG, e.getMessage() + "\nError extract file: " + insideFileName, e);
//                //e.printStackTrace();
//				//Toast.makeText(ctx, "Error extract file: " + insideFileName, Toast.LENGTH_SHORT).show();
            } finally {
				FileUtil.close(fos);
                //if (fos != null) fos.close();
            }
//		} catch (IOException e) {
//            ExceptionLogger.e(TAG, e.getMessage(), e);
//            return false;
//        }
        return true;
    }

	@Override
	public String toString() {
		return chm + "";
	}
	
}
