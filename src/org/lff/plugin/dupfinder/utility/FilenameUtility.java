package org.lff.plugin.dupfinder.utility;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 10:35
 */
public class FilenameUtility {

    public static void main(String[] argu) {
        String t = "jar://C:/dev/apache-tomcat-8.5.15/lib/annotations-api.jar!/";
        System.out.println(getFileName(t));
    }

    public static String getFileName(String urlName) {
        int index = urlName.indexOf("://");
        int last = urlName.indexOf("!/");
        if (index != -1 && last == -1) {
            return urlName.substring(index + 3);
        }
        if (index != -1 && last != -1) {
            return urlName.substring(index + 3, last);
        }
        return null;
    }
}
