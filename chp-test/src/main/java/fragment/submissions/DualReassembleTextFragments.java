package fragment.submissions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created on 04/09/2014.
 Background
 Imagine you have 5 copies of the same page of text. You value this text and have no hard or
 soft copies of it. Your two year old nephew visits and, while you are not looking, rips each
 page up into fragments and gleefully plays in the “snow” he has just created.
 You need at least one copy of that page of text back ASAP. As punishment to your niece,
 who should have been supervising your nephew at the time of the incident, you set her the
 painstaking task of keying in all the paper text fragments to a text file on your shiny
 MacBook Pro. Now the task is yours. Can you reassemble a soft copy of the original
 document?
 The Challenge
 Write a program to reassemble a given set of text fragments into their original sequence. For
 this challenge your program should have a main method accepting one argument – the path to
 a well-formed UTF-8 encoded text file. Each line in the file represents a test case of the main
 functionality of your program: read it, process it and println to the console the corresponding
 defragmented output.
 Each line contains text fragments separated by a semicolon, ‘;’. You can assume that every
 fragment has length at least 2.
 Example input 1:
 O draconia;conian devil! Oh la;h lame sa;saint!
 Example output 1:
 O draconian devil! Oh lame saint!
 Example input 2:
 m quaerat voluptatem.;pora incidunt ut labore et d;, consectetur, adipisci
 velit;olore magnam aliqua;idunt ut labore et dolore magn;uptatem.;i dolorem
 ipsum qu;iquam quaerat vol;psum quia dolor sit amet, consectetur, a;ia
 dolor sit amet, conse;squam est, qui do;Neque porro quisquam est, qu;aerat
 voluptatem.;m eius modi tem;Neque porro qui;, sed quia non numquam ei;lorem
 ipsum quia dolor sit amet;ctetur, adipisci velit, sed quia non numq;unt ut
 labore et dolore magnam aliquam qu;dipisci velit, sed quia non numqua;us
 modi tempora incid;Neque porro quisquam est, qui dolorem i;uam eius modi
 tem;pora inc;am al
 Example output 2:
 Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet,
 consectetur, adipisci velit, sed quia non numquam eius modi tempora
 incidunt ut labore et dolore magnam aliquam quaerat voluptatem.
 Implementation
 For each input line, search the collection of fragments to locate the pair with the maximal
 overlap match then merge those two fragments. This operation will decrease the total number
 of fragments by one. Repeat until there is only one fragment remaining in the collection. This
 is the defragmented line / reassembled document.
 If there is more than one pair of maximally overlapping fragments in any iteration then just
 merge one of them. So long as you merge one maximally overlapping pair per iteration the
 test inputs are guaranteed to result in good and deterministic output.
 When comparing for overlaps, compare case sensitively.
 Examples:
 - "ABCDEF" and "DEFG" overlap with overlap length 3
 - "ABCDEF" and "XYZABC" overlap with overlap length 3
 - "ABCDEF" and "BCDE" overlap with overlap length 4
 - "ABCDEF" and "XCDEZ" do *not* overlap (they have matching characters in
 the middle, but the overlap does not extend to the end of either string).
 */
public class DualReassembleTextFragments {


    private static int iteration;
    public static void main(String[] args){

        long startTime = System.nanoTime();
        System.out.println(startTime);
        if(args.length > 0) {
            File f = new File(args[0]);
            if(f.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
                    String fragmentProblem;
                    List<String> list;
                    String[] fragments;
                    while ((fragmentProblem = bufferedReader.readLine()) != null) {
                        fragments = fragmentProblem.split(";");
                        list = new ArrayList<String>(Arrays.asList(fragments));
                        System.out.println(dualOverlapSearch(list));
//                        dualOverlapSearch(list);
                    }
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Provided file does not exist.");
            }
        }else{
            System.out.println("Please provide the path to the data file.");
        }
        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        double seconds = (double)elapsedTime / 1000000000.0;
        System.out.println("Run time: " + seconds);
        System.out.println("Number of iterations " + iteration);
    }

    private static String dualOverlapSearch(List<String> fragmentsList){
        String findMe, output = fragmentsList.get(0);
        int size = fragmentsList.size();
        for(int x = 1; x <= size; x++) {

            int suffixOverlap = 0, prefixOverlap = 0, matchedItem = 0;
            for(int item = fragmentsList.size() - 1; item > 0; item--) {
                findMe = fragmentsList.get(item);
                for (int i = 1; i < findMe.length(); i++) {
                    iteration++;
                    // comparing 'findMe' tail with 'output' head
                    if (output.regionMatches(0, findMe, findMe.length() - i, i)) {
                        if (i > suffixOverlap && i > prefixOverlap) {
                            suffixOverlap = i;
                            matchedItem = item;
                        }
                    }
                    // comparing 'findMe' head with 'output' tail
                    if (findMe.regionMatches(0, output, output.length() - i, i)){
                        if (i > suffixOverlap && i > prefixOverlap) {
                            prefixOverlap = i;
                            matchedItem = item;
                        }
                    }
                }
            }

            findMe = fragmentsList.get(matchedItem);
            if (suffixOverlap > prefixOverlap) {
                output = findMe.substring(0, findMe.length() - suffixOverlap) + output;
            } else {
                if(prefixOverlap != 0) {
                    output += findMe.substring(prefixOverlap);
                }
            }
            fragmentsList.remove(matchedItem);
        }
        return output;
    }
}
