/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 * average number of translations per source word in the sentence (threshold in
 * giza1: prob > 0.5) weighted by the frequency of each word in the source
 * corpus
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1034 extends Feature {

    final static Float probThresh = 0.5f;

    public Feature1034() {
        setIndex(1034);
        setDescription("average number of translations per source word in the sentence (threshold in giza1: prob > 0.5) weighted by the frequency of each word in the source corpus");
        HashSet res = new HashSet<String>();
        res.add("Giza");
        res.add("Freq");
        setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        float noTokens = source.getNoTokens();

        float probSum = 0;

        String[] tokens = source.getTokens();
        for (String word : tokens) {
            probSum += Giza.getWordProbabilityCount(word, probThresh) * FileModel.getFrequency(word);
        }

        float result = probSum / noTokens;

        setValue(result);
    }
}
