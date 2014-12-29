package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppOne {

    //static PrintWriter writer;
    class FragmentOverLap {
        public FragmentPair findFragmentOverlap(FragmentPair fragmentPair) {

            Integer[][] matchingMatrix = buildMatchingMatrix(
                    fragmentPair.frag1, fragmentPair.frag2);
            Match match = findLocationLongestMatch(matchingMatrix);

            if (match.maxValue > 0) {
                match.value = getMatchingString(match, fragmentPair.frag1);
            } else {
                match.value = "";
            }
            fragmentPair.match = match;
            return fragmentPair;
        }

        public String getMatchingString(Match match, String str1) {
            int currLoc = match.xvalue;
            StringBuilder subString = new StringBuilder();
            for (int i = 0; i < match.maxValue; i++) {
                subString.append(str1.charAt(currLoc));
                currLoc = currLoc - 1;
            }
            return subString.reverse().toString();
        }

        public Match findLocationLongestMatch(Integer[][] m) {

            int maxVal = 0;
            Match match = new Match();
            for (int i = 0; i < m.length; i++) {
                for (int j = 0; j < m[0].length; j++) {
                    Integer val = m[i][j];
                    if (val > maxVal) {
                        match.xvalue = i;
                        match.yvalue = j;
                        match.maxValue = val;
                        maxVal = val;
                    }
                }
            }

            return match;
        }

        public Integer[][] buildMatchingMatrix(String str1, String str2) {
            if ((str1 == null) || (str1.length() < 1) || (str2 == null)
                    || (str2.length() < 1)) {
                return null;
            }

            Integer[][] matchMatrix = new Integer[str1.length()][str2.length()];
            for (int i = 0; i < str1.length(); i++) {
                for (int j = 0; j < str2.length(); j++) {
                    if (str1.charAt(i) != str2.charAt(j)) {
                        matchMatrix[i][j] = 0;
                    } else {
                        if ((i == 0) || (j == 0))
                            matchMatrix[i][j] = 1;
                        else
                            matchMatrix[i][j] = 1 + matchMatrix[i - 1][j - 1];

                    }
                }
            }
            return matchMatrix;
        }

    }

    class Match {
        public Integer xvalue = 0;
        public Integer yvalue = 0;
        public Integer maxValue = 0;
        public String value;

        @Override
        public String toString() {
            return "xvalue=" + xvalue + " yvalue=" + yvalue + " maxValue="
                    + maxValue + " value=" + value;
        }
    }

    class FragmentPair {
        public String frag1 = "";
        public String frag2 = "";
        public Match match = null;
        public String mergedFragment;

        @Override
        public String toString() {
            return "frag1=:" + frag1 + " frag2=" + frag2 + " MATCH[" + match
                    + "]";
        }

    }

    private String rebuild(List<String> val) {
        StringBuilder sb = new StringBuilder();
        sb.append(val.get(0));
        for (int i = 1; i < val.size(); i++) {
            sb.append(";").append(val.get(i));
        }
        return sb.toString();
    }

    public String shuffle(String value, Integer from, Integer to) {

        String results[] = value.split(";");
        ArrayList<String> valuesToShuffle = new ArrayList<String>(
                Arrays.asList(results));

        String valueToMove = valuesToShuffle.remove(from.intValue());
        valuesToShuffle.add(to, valueToMove);
        return rebuild(valuesToShuffle);

    }

    private String[] splitValues(String valueToSplit) {
        return valueToSplit.split(";");
    }

    public String reassemble(String fragmentProblem) {

        String results = defragment(fragmentProblem);
        boolean shuffling = false;

        int prevLength = splitValues(results).length;
        int shuffleIndex = prevLength - 1;
        if (prevLength > 1) {
            int from = prevLength - 1;
            int to = 1;
            while (true) {
                results = defragment(fragmentProblem);
                if (splitValues(results).length > 1) {
                    int curLength = splitValues(results).length;
                    if (curLength == prevLength) {

                        // Exhausted no more matches
                        if ((from == shuffleIndex)  && (shuffling)){
                            shuffleIndex = shuffleIndex - 1;
                            // Compared everthing to everthing
                            if (shuffleIndex == 0) {
                                break;
                            } else {
                                from = shuffleIndex;
                                to = 1;
                            }
                        } else {
                            if (shuffling) {
                                from = to;
                                to = to + 1;
                            } else {
                                shuffling = true;
                            }
                        }

                        fragmentProblem = shuffle(results, from, to);
                    } else {
                        prevLength = curLength;
                        shuffleIndex = prevLength - 1;
                        from = prevLength - 1;
                        to = 1;
                        shuffling = false;
                    }

                } else {
                    // defragged
                    break;
                }
            }
        }
        return results;
    }

    public String defragment(String fragmentProblem) {

        StringBuffer sb = new StringBuffer(fragmentProblem);
        String vals[] = sb.toString().split(";");
        int numberOfFragments = vals.length;
        int prevNumberOfFregments = numberOfFragments;

        while (numberOfFragments > 1) {
            //writer.println("\"\"\"" + sb.toString() + "\"\"\"");
            //writer.flush();
            String combined = combineLargestFragment(sb.toString());
            sb = new StringBuffer(combined);
            vals = sb.toString().split(";");
            numberOfFragments = vals.length;

            if ((numberOfFragments == prevNumberOfFregments)
                    && (numberOfFragments > 1)) {
                break;
            } else {
                prevNumberOfFregments = numberOfFragments;
            }

        }
        return sb.toString();

    }

    public String combineLargestFragment(String fragementProblem) {

        List<FragmentPair> fragmentPairs = splitIntoFragmentPairs(fragementProblem);
        int maxValue = getLocationOfLongestMatch(fragmentPairs);
        FragmentPair fragPair = mergeFragment(fragmentPairs.get(maxValue));

        StringBuilder sb = new StringBuilder();
        boolean fragmentFound = false;
        for (int i = 0; i < fragmentPairs.size(); i++) {
            FragmentPair fr = fragmentPairs.get(i);
            if (i != 0) {
                sb.append(";");
            }
            if ((maxValue == i) && (fragPair.mergedFragment.length() > 0)) {
                sb.append(fragPair.mergedFragment);
                fragmentFound = true;
            } else {

                if ((!fragmentFound) && ((i + 1) == fragmentPairs.size())) {
                    sb.append(fr.frag1).append(";").append(fr.frag2);
                } else {
                    if (fragmentFound) {
                        sb.append(fr.frag2);
                    } else {
                        sb.append(fr.frag1);
                    }
                }
            }
        }
        return sb.toString();
    }

    public FragmentPair mergeFragment(FragmentPair fragmentPair) {

        String frag1 = fragmentPair.frag1;
        String frag2 = fragmentPair.frag2;

        String newFrag = "";
        Boolean shouldCombine = shouldCombineBeginAndEnd(fragmentPair);
        if (shouldCombine) {

            if (endFragmentOneMatchesBeginFragmentTwo(fragmentPair)) {
                newFrag = frag1.substring(0, frag1.length()
                        - fragmentPair.match.value.length());
                newFrag = newFrag + frag2;
            } else {
                newFrag = frag2.substring(0, frag2.length()
                        - fragmentPair.match.value.length());
                newFrag = newFrag + frag1;
            }
        } else if (eliminateAFragment(fragmentPair)) {

            if (frag1.length() > frag2.length()) {
                newFrag = frag1;
            } else if (frag2.length() > frag1.length()) {
                newFrag = frag2;
            } else {
                newFrag = frag2;
            }
        } else {
            newFrag = "";
        }
        fragmentPair.mergedFragment = newFrag;
        // writer.println("mergedvalue:"+frag1+"="+fragmentPair.match.value+"="+frag2+":"+newFrag);
        return fragmentPair;

    }

    private boolean beginFragmentOneMatchesEndFragmentTwo(FragmentPair fragment) {

        if (fragment.frag1.startsWith(fragment.match.value)
                && fragment.frag2.endsWith(fragment.match.value)) {
            return true;
        }

        return false;
    }

    private boolean endFragmentOneMatchesBeginFragmentTwo(FragmentPair fragment) {

        if (fragment.frag1.endsWith(fragment.match.value)
                && fragment.frag2.startsWith(fragment.match.value)) {
            return true;
        }
        return false;
    }

    private boolean shouldCombineBeginAndEnd(FragmentPair fragment) {

        if (beginFragmentOneMatchesEndFragmentTwo(fragment)) {
            return true;
        }

        if (endFragmentOneMatchesBeginFragmentTwo(fragment)) {
            return true;
        }
        return false;
    }

    private boolean eliminateAFragment(FragmentPair fragment) {

        if (fragment.match.value.equals(fragment.frag1)
                || (fragment.match.value.equals(fragment.frag2))) {
            return true;
        }

        return false;
    }

    public boolean isValidMatch(FragmentPair fragment) {

        if (shouldCombineBeginAndEnd(fragment)) {
            return true;
        }

        if (eliminateAFragment(fragment)) {
            return true;
        }

        return false;
    }

    public int getLocationOfLongestMatch(List<FragmentPair> fragmentPairs) {

        int max = 0;
        int loc = 0;

        for (int i = 0; i < fragmentPairs.size(); i++) {
            FragmentPair fragmentPair = fragmentPairs.get(i);
            if (fragmentPair.match.value.length() > max) {
                if (isValidMatch(fragmentPair) || eliminateAFragment(fragmentPair)){
                    max = fragmentPair.match.value.length();
                    loc = i;
                }
            }
        }
        return loc;
    }

    public List<FragmentPair> splitIntoFragmentPairs(String value) {
        String[] fragments = value.split(";");
        if (fragments.length < 2) {
            throw new UnsupportedOperationException();
        }

        List<FragmentPair> fragmentPairs = new ArrayList<FragmentPair>();
        //writer.println("Frag1,Frag2,Match");
        for (int i = 0; (i + 1) < fragments.length; ++i) {
            FragmentPair fragmentPair = new FragmentPair();

            if (i == 0) {
                fragmentPair.frag1 = fragments[0];
                fragmentPair.frag2 = fragments[1];
            } else {
                fragmentPair.frag1 = fragments[i];
                fragmentPair.frag2 = fragments[i + 1];

            }

            FragmentOverLap framentOverLap = new FragmentOverLap();
            fragmentPairs.add(framentOverLap.findFragmentOverlap(fragmentPair));
            //writer.println("\"\"\"" + fragmentPair.frag1 + "\"\"\",\"\"\""
            //		+ fragmentPair.frag2 + "\"\"\",\"\"\""
            //		+ fragmentPair.match.value + "\"\"\"");
        }
        return fragmentPairs;

    }

    public static void main(String[] args) {
        //writer = new PrintWriter("/tmp/chp.csv", "UTF-8");
        long startTime = System.nanoTime();
        System.out.println(startTime);

        AppOne kb = new AppOne();

        try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
            String fragmentProblem;
            while ((fragmentProblem = in.readLine()) != null) {

//                System.out.println(kb.reassemble(fragmentProblem));
                kb.reassemble(fragmentProblem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //writer.close();
        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        double seconds = (double)elapsedTime / 1000000000.0;
        System.out.println(seconds);

    }

}