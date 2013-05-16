/**
 *
 */
package shef.mt.tools;

import shef.mt.features.util.Sentence;
import shef.mt.pipelines.ResourcePipeline;
import shef.mt.util.PropertiesManager;
import shef.mt.features.util.FeatureManager;

import java.io.*;

/**
 * Processes a file containing ngram probabilities and perplexities and sets the
 * corresponding values to the current sentence
 *
 * @author Catalina Hallett
 *
 */
public class PPLProcessor extends ResourceProcessor {

    BufferedReader br;
    String[] valNames;
    String pplFile;
    private static String ngramOutputExt = ".ppl";
    private static FeatureManager featureManager;
    
    public void initialize(PropertiesManager propertiesManager,
            			   String sourceLang, String targetLang) {
    	
    	String sourceFile = "input/source." + sourceLang;
    	String targetFile = "input/target." + targetLang;
    	
    	String pplSourcePath = propertiesManager.getString("input")
                + File.separator + sourceLang + File.separator + sourceFile
                + ngramOutputExt;
        String pplTargetPath = propertiesManager.getString("input")
                + File.separator + targetLang + File.separator + targetFile
                + ngramOutputExt;
        
        String[] valNames = new String[0];
        valNames = featureManager.getStrResources().toArray(new String[0]);
    	
    	PPLProcessor pplProcessor = new PPLProcessor();

        pplProcessor.create(pplSourcePath, valNames);

		ResourcePipeline rp = new ResourcePipeline();
    	rp.addResourceProcessor(pplProcessor);
    }    
    
    public void create(String pplFile, String[] valNames) {

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(pplFile), "utf-8"));
            //                  System.out.println(br);
            this.valNames = valNames;
            this.pplFile = pplFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < valNames.length; i++) {
            ResourceManager.registerResource(valNames[i]);
        }
    }
 
    public void processNextSentence(Sentence s) {
        try {
            String line = br.readLine();
            if (line == null) {
                System.out.println("line==null in " + new File(pplFile).getAbsolutePath() + " sent:" + s.getIndex() + " " + s.getText());
                return;
            }
            while (line.trim().isEmpty() || !line.endsWith("OOVs")) {
                line = br.readLine();
                //                            System.out.println(line);
            }
            line = br.readLine();
            //                            System.out.println(line);
            //ok, we found the line containing perplexities/log values
            String[] values = line.split(" ");
            //values we are interested in are at positions 3,5,7
//			System.out.println(line);
            if (values[3].equals("undefined")) {
                s.setValue(valNames[0], 0.0f);
            } else {
                s.setValue(valNames[0], new Float(values[3]));
            }
            if (valNames.length > 1) {
                if (values[5].equals("undefined")) {
                    s.setValue(valNames[1], 0.0f);
                } else {
                    s.setValue(valNames[1], new Float(values[5]));
                }
                if (values[7].equals("undefined")) {
                    s.setValue(valNames[2], 0.0f);
                } else {
                    s.setValue(valNames[2], new Float(values[7]));
                }
            }
        } catch (Exception e) {
            //               System.out.println(pplFile+" "+s.getText());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
