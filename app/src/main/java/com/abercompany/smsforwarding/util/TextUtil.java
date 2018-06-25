package com.abercompany.smsforwarding.util;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String TAG = TextUtil.class.getSimpleName();


    public static Typeface NanumGothic;
    public static Typeface NanumGothicBold;
    public static Typeface NanumGothicExtraBold;

    /**
     * @param source
     * @return
     */
    public static boolean isNull(String source) {
        boolean isNull = false;
        if (source == null || TextUtils.isEmpty(source))
            isNull = true;
        return isNull;
    }

    public static boolean isYN(String source) {
        boolean isY = false;
        if (!isNull(source) && source.equals("Y")) {
            isY = true;
        }
        return isY;
    }

    /**
     * InputStream-> 문자열로 변환
     *
     * @param is
     * @return
     */
    public static String inputStreamToString(InputStream is, String charset) {
        BufferedReader br = null;
        StringBuffer sb = null;
        String data = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(is, charset));
            sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
        } catch (Exception err) {

        } finally {
            line = null;
            sb = null;
            br = null;
        }

        return data;
    }

    /*
     * 최소문자열 길이 체크
     */
    public static boolean isLenghCheck(String str, int minCheck, int maxCheck) {
        boolean isCheck = false;
        if (str.length() >= minCheck && str.length() <= maxCheck) {
            isCheck = true;
        }

        return isCheck;
    }

    /**
     * 한글 UTF-8 인코딩
     *
     * @param source
     * @return
     */
    public static String textEncoding(String source) {
        String encodingStr = null;
        try {
            encodingStr = URLEncoder.encode(source, "utf-8");
        } catch (Exception err) {
            encodingStr = "";
        }
        return encodingStr;
    }

    /**
     * 이메일 형식 체크(해당 정규화에 정확히 맞는지)
     *
     * @return
     */
    public static boolean isEmailCheck(String email) {
        boolean isConfirm = false;
        //String rule = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,5}$";
        String rule = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{1,})$";
        //isConfirm = Pattern.matches("^[0-9a-zA-Z-]+(.[0-9a-zA-Z-]+)*@(?:\\w+\\.)+\\w+$", email);
        isConfirm = Pattern.matches(rule, email);
        return isConfirm;
    }

    /***
     * 1. 영문 대문자, 소문자, 숫자, 특수문자 중 최소 2가지 이상의 문자조합으로 구성
     * 2. 최소 6자리 이상, 최대 16자리 이하로 구성
     * 3. 사용 불가능한 비밀번호 : 공백, 현재 사용 중 비밀번호, ID와 동일한 비밀번호
     * 사용 가능한 특수문자 32자 : ! " # $ % & ' ( ) * + , - . / : ; < = > ? @ [ ＼ ] ^ _ ` { | } ~
     *
     * @param password
     * @return
     */
    public static boolean isPassworkdCheck(String password) {
        String pattern01 = "(?=.[A-Za-z0-9])(?=.​*[!\"#$%&\\'\\(\\)*​+,-.\\/:;<=>?@\\[\\\\\\]^_`\\{|\\}~\\]])";
        String pattern02 = "(?=.[A-Za-z!\"#$%&\\'\\(\\)*+,-.\\/:;<=>?@\\[\\\\\\]^_`\\{|\\}~\\]])(?=.*[0-9])";
        String pattern03 = "(?=.[0-9A-Z!\"#$%&\\'\\(\\)*+,-.\\/:;<=>?@\\[\\\\\\]^_`\\{|\\}~\\]])(?=.*[a-z])";
        String pattern04 = "(?=.[a-z0-9!\"#$%&\\'\\(\\)*+,-.\\/:;<=>?@\\[\\\\\\]^_`\\{|\\}~\\]])(?=.*[A-Z])";
        String pattern = "(" + pattern01 + "|" + pattern02 + "|" + pattern03 + "|" + pattern04 + ").{8,15}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        if (m.find()) {
            JSLog.D("성공", new Throwable());
            return true;
        } else {
            JSLog.D("실패", new Throwable());
            return false;
        }
    }


    public static String makeStringWithComma(String string, boolean ignoreZero) {
        if (string.length() == 0) {
            return "";
        }
        try {
            if (string.indexOf(".") >= 0) {
                double value = Double.parseDouble(string);
                if (ignoreZero && value == 0) {
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,##0.00");
                return format.format(value);
            } else {
                long value = Long.parseLong(string);
                if (ignoreZero && value == 0) {
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,###");
                return format.format(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }


    public static SpannableStringBuilder settingLink(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new UnderlineSpan(), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static void setTextViewColorPartial(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        if(i<0)	return;
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static String convertStringtoDate(String strDate, String format) {
        String ddate = null;
        try {

            // input date format
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");

            // output date format
            //SimpleDateFormat dFormatFinal = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dFormatFinal = new SimpleDateFormat(format);

            Date date = dFormat.parse(strDate);
            ddate = dFormatFinal.format(date);
        } catch (Exception e) {
        }
        return ddate;
    }

    /*
     * 완성된 글자거나 영어 숫자면 true
     */
    public static boolean isNameCheck(String source) {
        boolean isConfirm = false;
//	      isConfirm = Pattern.matches("[가-힣ㄱ-ㅎㅏ-ㅣ]*", source);
        isConfirm = Pattern.matches("^[0-9a-zA-Z가-힣]{1,10}$", source);
//	      isConfirm = Pattern.matches("^[0-9a-zA-Z가-힣]", source);
        return isConfirm;
    }

    public static String emoticonFilter(String source) {
        String utf8tweet = "";
        try {
            byte[] utf8Bytes = source.getBytes("UTF-8");

            utf8tweet = new String(utf8Bytes, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);

        Log.d(TAG, "Before: " + utf8tweet, new Throwable());
        utf8tweet = unicodeOutlierMatcher.replaceAll(" ");
        Log.d(TAG, "After: " + utf8tweet, new Throwable());

        return utf8tweet;

    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
//		if (tv.getTag() == null) {
//		}
        tv.setTag(tv.getText());
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
//					int lineEndIndex = tv.getLayout().getLineEnd(0);
//					String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
//					tv.setText(text);
//					tv.setMovementMethod(LinkMovementMethod.getInstance());
//					tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    int subIndex = lineEndIndex - expandText.length() + 1;
                    Log.d(TAG, "lienEndIndex:" + lineEndIndex + ", " + "subIndex:" + subIndex, new Throwable());
                    if (subIndex > 0 && tv.getText().length() > subIndex) {
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText, viewMore), TextView.BufferType.SPANNABLE);
                    }
                } else {
//					int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
//					String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
//					tv.setText(text);
//					tv.setMovementMethod(LinkMovementMethod.getInstance());
//					tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText, viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setAlpha(80);
                    //ds.setTextSize();
                }

                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setMaxLines(1000);
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        //makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        //makeTextViewResizable(tv, 3, "View More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }
}
