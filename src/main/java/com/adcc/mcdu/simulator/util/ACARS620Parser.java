package com.adcc.mcdu.simulator.util;

import com.adcc.mcdu.simulator.entity.ACARS620Exception;
import com.adcc.mcdu.simulator.entity.ACARS620Msg;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ACARS620Parser {
    public ACARS620Parser() {
    }

    private static String[] parseHead(String rawMsg) throws ACARS620Exception {
        String[] result = null;
        int intIndex = rawMsg.indexOf("\r\n-  ");
        if (intIndex < 0) {
            intIndex = rawMsg.indexOf("\n-  ");
        }

        if (intIndex > 0) {
            String strHead = rawMsg.substring(0, intIndex);
            result = strHead.replace("\r\n", "\n").split("\n");
            if (result.length < 3) {
                throw new ACARS620Exception("parse message head error missing lines current " + result.length + " lines");
            }
        } else {
            result = rawMsg.replace("\r\n", "\n").split("\n");
            if (result.length < 5) {
                throw new ACARS620Exception("parse message head error missing lines current " + result.length + " lines");
            }
        }

        return result;
    }

    private static Map<String, String> parseLine1(String line1) throws ACARS620Exception {
        Map<String, String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile("(\\x01)([A-Z]{2})((?:\\s+\\S{7}){1,16})");
        Matcher matcher = pattern.matcher(line1);
        if (matcher.find()) {
            result.put("Priority", matcher.group(2).trim());
            result.put("RecvAddress", matcher.group(3).trim());
            return result;
        } else {
            throw new ACARS620Exception("parse message line1 error");
        }
    }

    private static Map<String, String> parseLine2(String line2) throws ACARS620Exception {
        Map<String, String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile("(\\.)(\\S{7}{1})((?:\\s{1}\\d{6})?)");
        Matcher matcher = pattern.matcher(line2);
        if (matcher.find()) {
            result.put("SendAddress", matcher.group(2).trim());
            result.put("SendTime", matcher.group(3).trim());
            return result;
        } else {
            throw new ACARS620Exception("parse message line2 error");
        }
    }

    private static Map<String, String> parseLine3(String line3) throws ACARS620Exception {
        Map<String, String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile("(\\x02)(\\S{3})");
        Matcher matcher = pattern.matcher(line3);
        if (matcher.find()) {
            result.put("SMI", matcher.group(2).trim());
            return result;
        } else {
            throw new ACARS620Exception("parse message line3 error");
        }
    }

    private static Map<String, String> parseLine4(String line4) {
        Map<String, String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile("((?:FI|AN|GL|AP|TP|MA|AD|OT|OF|ON|IN|FB|DS)\\s{1}[\\S&&[^/]]+)");
        Matcher matcher = pattern.matcher(line4);

        while(matcher.find()) {
            String[] strValue = matcher.group().split(" ");
            result.put(strValue[0].trim(), strValue[1].trim());
        }

        return result;
    }

    private static Map<String, String> parseLine5(String line5) {
        Map<String, String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile("(DT)((?:\\s{1}\\S+))((?:\\s{1}\\S+))((?:\\s{1}\\d{6}))((?:\\s{1}\\S+))");
        Matcher matcher = pattern.matcher(line5);
        if (matcher.find()) {
            result.put("DSP", matcher.group(2).trim());
            result.put("RGS", matcher.group(3).trim());
            result.put("RGSTime", matcher.group(4).trim());
            result.put("MSN", matcher.group(5).trim());
        }

        return result;
    }

    private static String parseFreeText(String rawMsg) throws ACARS620Exception {
        int intIndex = rawMsg.indexOf("\r\n-  ");
        if (intIndex >= 0) {
            return rawMsg.substring(intIndex + 4).trim();
        } else {
            intIndex = rawMsg.indexOf("\n-  ");
            return intIndex >= 0 ? rawMsg.substring(intIndex + 3).trim() : null;
        }
    }

    public static Optional<ACARS620Msg> parse(String rawMsg) throws ACARS620Exception {
        String[] head = parseHead(rawMsg);
        if (head != null && head.length > 0) {
            ACARS620Msg msg = new ACARS620Msg();
            Map<String, String> map = parseLine1(head[0]);
            if (map.containsKey("Priority")) {
                msg.setPriority(Strings.nullToEmpty((String)map.get("Priority")));
            }

            if (map.containsKey("RecvAddress")) {
                String[] recvAddress = Strings.nullToEmpty((String)map.get("RecvAddress")).split(" ");
                msg.setRecvAddress(recvAddress);
            }

            map = parseLine2(head[1]);
            if (map.containsKey("SendAddress")) {
                msg.setSendAddress(Strings.nullToEmpty((String)map.get("SendAddress")));
            }

            if (map.containsKey("SendTime")) {
                msg.setSendTime(Strings.nullToEmpty((String)map.get("SendTime")));
            }

            map = parseLine3(head[2]);
            if (map.containsKey("SMI")) {
                msg.setSmi(Strings.nullToEmpty((String)map.get("SMI")));
            }

            if (head.length >= 4) {
                map = parseLine4(head[3]);
                if (map.containsKey("FI")) {
                    msg.setFi(Strings.nullToEmpty((String)map.get("FI")));
                }

                if (map.containsKey("AN")) {
                    msg.setAn(Strings.nullToEmpty((String)map.get("AN")));
                }

                if (map.containsKey("MA")) {
                    msg.setMa(Strings.nullToEmpty((String)map.get("MA")));
                }

            }

            if (head.length >= 5) {
                map = parseLine5(head[4]);
                if (map.containsKey("DSP")) {
                    msg.setDsp(Strings.nullToEmpty((String)map.get("DSP")));
                }

                if (map.containsKey("RGS")) {
                    msg.setRgs(Strings.nullToEmpty((String)map.get("RGS")));
                }

                if (map.containsKey("RGSTime")) {
                    msg.setRgsTime(Strings.nullToEmpty((String)map.get("RGSTime")));
                }

                if (map.containsKey("MSN")) {
                    msg.setMsn(Strings.nullToEmpty((String)map.get("MSN")));
                }
            }

            msg.setFreeText(parseFreeText(rawMsg));
            return Optional.of(msg);
        } else {
            return Optional.absent();
        }
    }
}

