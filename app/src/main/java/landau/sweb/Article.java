package landau.sweb;

import java.io.Serializable;

/**
 * Created by LynkMieu on 6/1/2017.
 */

public class Article implements Serializable {
    //private String title;
    String url;
    //private String thumnail;
    //private String decription;
	String localAddress;

    public Article(String url, String localAddress) {
        //this.title = title;
        this.url = url;
        this.localAddress = localAddress;
        //this.thumnail = thumnail;
        //this.decription = decription;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public String getThumnail() {
//        return thumnail;
//    }
//
//    public void setThumnail(String thumnail) {
//        this.thumnail = thumnail;
//    }
//
//    public String getDecription() {
//        return decription;
//    }
//
//    public void setDecription(String decription) {
//        this.decription = decription;
//    }

    @Override
    public String toString() {
        return "Article{" +
                //"title='" + title + '\'' +
                "url='" + url + '\'' +
                //", thumnail='" + thumnail + '\'' +
                //", decription='" + decription + '\'' +
                '}';
    }
}
