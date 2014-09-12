/**
 * This program gets all pictures ever uploaded to pr0gramm.com
 * and saves them into folder on your system
 *
 * @author Thomas Voigt & Fabian Mielke & msmacco
 * @version 0.1
 */

package com.company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Main {

        public static void main(String[] args) throws Exception {
            Main http = new Main();
            
            // set destination folder of images
            File folder = new File("D:\\images\\");
            
            // create file array of files in folder and count all files
            File[] listOfFiles = folder.listFiles();
            System.out.println(listOfFiles.length);
            
            int startId = 0; // startId is the id where the programm should start to download
            int endId = 5000;
            String fileName; // the name of the file
            int fileNumber = 0; // the file has an ID like 700.jpg

            // create string array of all filenames
            String[] fileNumberString ;

            // go through all files in folder
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileName = listOfFiles[i].getName();

                    // cut the filename in two
                    fileNumberString = fileName.split("\\.");

                    // take first part of the cutted filename
                    fileNumber = Integer.parseInt(fileNumberString[0]);

                    // because of the loop the last file will be the startId
                    if (fileNumber > startId){
                        startId = fileNumber;
                    }

                  // wenn angenommenes file kein file ist
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            System.out.println("START ID: " + Integer.toString(startId));

            // gebe calls den wert von der startId (ID vom höchsten fileName im folder)
            // wording calls passt besser, weil es sich demnächst um api calls handeln wird
            int calls = startId;

            // flag ist parameter in request url (2-nsfl, 7-alle)
            int flag = 2;
            System.out.println("INIT: " + calls);

            // return wert von sendGet ist counter, counter ist array länge (sollte immer 240 sein)
            while(calls <= endId) {
                // thecalls bekommt return wert von sendGet
                int thecalls = http.sendGet(calls, flag);
                System.out.println("CALLS AFTER sendGet: " + calls);

                // calls wird bei jedem durchlauf theoretisch um 240 (counter = array.length) erhöht)
                calls = calls + thecalls;
            }

            System.out.println("ENDE");
        }

        // GET request gegen p0gramm.com api
        private int sendGet(int calls, int flag) throws Exception {

            // setze request url dynamisch (query parameter id ist calls)
            String url = "http://pr0gramm.com/api/items/get?id=" + Integer.toString(calls) +"&flags=" + Integer.toString(flag);
            System.out.println(url);
            URL obj = new URL(url);
            URL baseURL = new URL("http://img.pr0gramm.com/");

            // erstelle connection mit der request url
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

                //print complete response of api call and convert to string
                //System.out.println(response);

            String sResponse = response.toString();

            //create object and fill with string api response
            JSONObject object = new JSONObject(sResponse);

            //create array from json object "items"
            JSONArray array = (JSONArray) object.get("items");

            //lege counter mit array länge fest
            int counter = array.length();

            // geh array von api response durch und finde feld image und is heraus
            for(int j = 0; j < array.length(); j++) {
                JSONObject imageRecord = array.getJSONObject(j);
                String imagePath = imageRecord.getString("image");
                Integer imageId = imageRecord.getInt("id");

                // lege bufferedimage an, welches später eingelesen wird
                BufferedImage image = null;

                // try catch block für download prozess
                try
                {
                    // image url festlegen
                    URL urli = new URL("http://img.pr0gramm.com/" + imagePath);

                    // file (image) content von url einlesen
                    image = ImageIO.read(urli);
                    //BufferedImage bi = image;

                    // erstelle neues file
                    File file = new File("D:\\images\\" + imageId + ".jpg"); // just an object
                    //FileWriter fw = new FileWriter(file); // create an actual file

                    // überschreibe initial erstelltes file mit file von url
                    ImageIO.write(image,"jpg", file);
                    //fw.write(image);

                } catch (IOException e) {
                    System.out.println("Unable to retrieve Image!!!");
                    e.printStackTrace();
                }

                //counter = imageRecord.getInt("id");
                //give out the complete URL
                //System.out.println(baseURL+imagePath);
                //System.out.println("imageId: " + imageId);
            }

            System.out.println("Arraylänge " + Integer.toString(counter));

            // legt counter (arraylänge der api response) als return wert fest.
            return counter;
        }
}