package fragment.submissions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marmonas on 04/09/2014.
 */
public class GenerateFragments {
    public static void main(String [] args){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("file4.txt"));
            String fragmentProblem;
            List<String> list = new ArrayList<String>();
            while ((fragmentProblem = bufferedReader.readLine()) != null) {
                list.add(fragmentProblem);
            }
            int count = 0;
            for(int i = 0; i < 50000; i++){

                bufferedWriter.write(list.get(count));
                bufferedWriter.newLine();
                count++;
                if(i % 2 == 0) {
                    count = 0;
                }
            }
            bufferedWriter.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
