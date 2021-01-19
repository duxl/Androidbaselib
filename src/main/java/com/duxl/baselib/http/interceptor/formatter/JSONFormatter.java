package com.duxl.baselib.http.interceptor.formatter;

public class JSONFormatter {

    static final JSONFormatter FORMATTER = findJSONFormatter();

    public static String formatJSON(String source) {
        try {
            return FORMATTER.format(source);
        } catch (Exception e) {
            return "";
        }
    }

    String format(String source) {
        return "";
    }

    private static JSONFormatter findJSONFormatter() {
//        JSONFormatter jsonFormatter = OrgJsonFormatter.buildIfSupported();
//        if (jsonFormatter != null) {
//            return jsonFormatter;
//        }

        JSONFormatter gsonFormatter = GsonFormatter.buildIfSupported();
        if (gsonFormatter != null) {
            return gsonFormatter;
        }

//        JSONFormatter fastjsonFormatter = FastjsonFormatter.buildIfSupported();
//        if (fastjsonFormatter != null) {
//            return fastjsonFormatter;
//        }
//
//        JSONFormatter moshiFormatter = MoshiFormatter.buildIfSupported();
//        if (moshiFormatter != null) {
//            return moshiFormatter;
//        }

        return new JSONFormatter();
    }
}
