import org.json.*;
import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;

public class Main {
    public static String xmlStringTest =
            "<?xml version=\"1.0\" ?><root><test       attrib=\"jsontext1\">tutorialspoint</test>" +
                    "<test attrib=\"jsontext2\">tutorix</test></root>";

    public static void writeJSON(JSONObject js, String fileName) throws IOException {
        FileWriter file = new FileWriter(fileName);
        file.write(js.toString());
        file.close();
        System.out.println("JSON file created: " + fileName);
    }

    public static JSONObject readXMLtoJSON(String xmlPath) throws IOException {

        System.out.println("Searching for XML: " + xmlPath);

        Path fileName = Path.of(xmlPath);
        String xml = Files.readString(fileName);
        return XML.toJSONObject(xml);
    }

    public static JSONObject extractSubObj(JSONObject json, String subObjPath) {


        JSONPointer jsonPointer = new JSONPointer(subObjPath);
        return (JSONObject) json.query(jsonPointer);
    }

    public static boolean existsKeyPath(JSONObject json, String subObjPath) {
        return extractSubObj(json, subObjPath) != null;
    }

    public static JSONObject addPrefix(JSONObject json) {
        JSONObject newJson = new JSONObject();
        Iterator it = json.keys();

        while(it.hasNext()) {
            String keyName = (String) it.next();
            String newKey = "swe262_" + keyName;

            Object innerObj = json.get(keyName);

            // Recursively dig through original json while still json object, add to new json with
            // new key
            if(innerObj instanceof JSONObject) {
                newJson.put(newKey, addPrefix((JSONObject) innerObj));
            } else {
                newJson.put(newKey, innerObj);
            }
        }
        return newJson;
    }

    public static JSONObject replaceSubObj(JSONObject json, String subObjPath) {
        JSONObject subObj = extractSubObj(json, subObjPath);
        if(subObj != null) {
            JSONObject newJson = XML.toJSONObject(xmlStringTest);
            Iterator it = subObj.keys();

            String firstSubObjKey = (String) it.next();
            json.remove(firstSubObjKey);
            json.put(firstSubObjKey, newJson);
            return json;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println(
                "1) Read an XML file (given as command line argument) into a JSON object and write the\n" +
                "JSON object back on disk as a JSON file.\n" +
                "2) Read an XML file into a JSON object, and extract some smaller sub-object inside,\n" +
                "given a certain path (use JSONPointer). Write that smaller object to disk as a JSON file.\n" +
                "3) Read an XML file into a JSON object, check if it has a certain key path\n" +
                "(given in the command line too). If so, save the JSON object to disk; if not, discard it.\n" +
                "4) Read an XML file into a JSON object, and add the prefix \"swe262_\" to all of its keys.\n" +
                "5) Read an XML file into a JSON object, replace a sub-object on a certain key path with\n" +
                "another JSON object that you construct, then write the result on disk as a JSON " +
                        "file.\n\n"  +
                "Enter the number of the desired operation: ");
        String input = sc.next();
        sc.nextLine();

        // 1) Read an XML file (given as command line argument) into a JSON object and write the
        // JSON object back on disk as a JSON file.
        if(input.equals("1")) {
            String xmlPath = sc.nextLine();
            String filename = "m1-file1.json";
            JSONObject json = readXMLtoJSON(xmlPath);
            writeJSON(json, filename);
        }

        // 2) Read an XML file into a JSON object, and extract some smaller sub-object inside,
        // given a certain path (use JSONPointer). Write that smaller object to disk as a JSON file.
        if(input.equals("2")) {
            System.out.println("Enter the XML file pathname: ");
            String xmlPath = sc.nextLine();
            String filename = "m1-file2.json";
            System.out.println("Enter the JSON subobj path: ");
            String subObjPath = sc.nextLine();
            JSONObject json = readXMLtoJSON(xmlPath);
            JSONObject subObj = extractSubObj(json, subObjPath);
            writeJSON(subObj, filename);
        }

        // 3) Read an XML file into a JSON object, check if it has a certain key path
        // (given in the command line too). If so, save the JSON object to disk; if not, discard it.
        if(input.equals("3")) {
            System.out.println("Enter the XML file pathname: ");
            String xmlPath = sc.nextLine();
            String filename = "m1-file3.json";
            System.out.println("Enter the JSON subobj path: ");
            String subObjPath = sc.nextLine();
            JSONObject json = readXMLtoJSON(xmlPath);
            if (existsKeyPath(json, subObjPath)) {
                writeJSON(json, filename);
            }
        }

        // 4) Read an XML file into a JSON object, and add the prefix "swe262_" to all of its keys.
        if(input.equals("4")) {
            System.out.println("Enter the XML file pathname: ");
            String xmlPath = sc.nextLine();
            String filename = "m1-file4.json";
            JSONObject json = readXMLtoJSON(xmlPath);
            writeJSON(addPrefix(json), filename);
        }

        // 5) Read an XML file into a JSON object, replace a sub-object on a certain key path with
        // another JSON object that you construct, then write the result on disk as a JSON file.
        if(input.equals("5")) {
            System.out.println("Enter the XML file pathname: ");
            String xmlPath = sc.nextLine();
            String filename = "m1-file5.json";
            JSONObject json = readXMLtoJSON(xmlPath);
            System.out.println("Enter the JSON subobj path: ");
            String subObjPath = sc.nextLine();
            json = replaceSubObj(json, subObjPath);
            if (json != null) {
                writeJSON(json, filename);
            }
        }
    }
}
