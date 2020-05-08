
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Truth
 */
public class SQLSyntax {

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws FileNotFoundException {
        // Get File from data.csv
        Scanner scanner = new Scanner(new File("/Users/Truth/Desktop/SBD/data.csv"));
        //This is an algorithm for separating the csv file
        //First we separate the file with # as an ending tag then store it into an array
        
        scanner.useDelimiter("#");
        ArrayList<String> stringArray = new ArrayList<String>();
        int i = 0;
        while (scanner.hasNext()) {
            stringArray.add(scanner.nextLine().replace("#", "")); i++;
        }
        scanner.close();
        //Declaration of 2 Dimensional array used for storing
        //Array of arrays
        ArrayList<String[]> HR = new ArrayList<String[]>(); 
        
        //Split the string in the first array by ; as the column limiter.
        //Then Storing it into the HR array (the 2 Dimensional Array)
        for (int j = 0; j < stringArray.size(); j++) {
            String[] wordList = stringArray.get(j).split(";");
            HR.add(wordList);
        }
        
        for (int j = 0; j < HR.size(); j++) {
            HR.get(j)[0].replace(" ", "");
        }
        
        //Get Input User
        scanner = new Scanner(System.in);
        //Split Input User with space
        String[] inputUser = scanner.nextLine().split(" ");
        //First check, if the end of the sentence got ";", if don't go to else
        //Else prints the Error.
        if (inputUser[inputUser.length - 1].contains(";")){
            //Second Check, if the first syntax is select
            //Else prints the error
            if (inputUser[0].equalsIgnoreCase("SELECT")) {
                // Checkf for checking the "SELECT" Syntax, with fidx as the index.
                // Checkj for checking the "JOIN" Syntax, with jidx as the index.
                // Checko for checking the "ON" Syntax, with oidx as the index.
                // Checkend for checking the ";" Syntax, with eidx as the index
                // countend for counting how many ";" in a sentence
                boolean checkf = false; int fidx = -1; 
                boolean checkj = false; int jidx = -1;
                boolean checko = false; int oidx = -1;
                boolean checku = false; int uidx = -1;
                boolean checkend = false; int eidx = -1; int countend = 0;
                //Loop For checking all of the above
                for (int j = 1; j < inputUser.length; j++) {
                    if(inputUser[j].toLowerCase().equals("from")){
                        fidx = j;
                        checkf = true;
                    }
                    if(inputUser[j].toLowerCase().equals("join")){
                        jidx = j;
                        checkj = true;
                    }
                    if(inputUser[j].toLowerCase().equals("on")){
                        oidx = j;
                        checko = true;
                    }
                    if(inputUser[j].toLowerCase().contains(";")){
                        eidx = j;
                        checkend = true;
                        countend += 1;
                    }
                    if(inputUser[j].toLowerCase().equals("using")){
                        uidx = j;
                        checku = true;
                    }
                }
                //If the ";" is more than or equal to 2, then prints error
                if (countend >= 2){
                    System.out.println("SQL ERROR (More than two ;'s in a line)");
                //Else we continue if "FROM" is exist
                //and the end branch of this if prints error
                }else if (checkf){
                    //Merges the column reference after "SELECT" Syntax
                    String merge = Merger(inputUser,fidx);
                    //Counts the comma.
                    int countComma =  merge.length() - merge.replace(",", "").length();
                    //Split the merge with ","
                    ArrayList<String>  StringMerge = removeNull(merge.split(","));
                    //If the end of the column reference is , then the reference isn't complete
                    //Therefore prints error
                    if (fidx == 1){
                        System.out.println("SQL ERROR(Missing Column References After Select)");
                    //Also if the comma is more than the actual column reference, then prints error
                    }else{
                        //This is to check the index of which table the "FROM" is referencing
                        fidx = checkBelongToArray(inputUser[fidx+1].toLowerCase().replace(";", ""),HR);
                        //If FROM index is -2, which indicates no such table, prints Error
                        if(fidx==-2){
                            System.out.println("SQL ERROR(There is no such table for FROM Statement)");
                        //This else is for the first type of statement
                        //Which JOIN idx = -1 . it means no JOIN Syntax found in the check loop
                        }else if ((fidx!= -1) &&(jidx==-1)){
                            //SELECT All
                            //Make an arraylist
                                ArrayList<String> columnOutput = new ArrayList<String>();
                                    //Check the column if it is exist in HR table dictionary
                                    //Then store it into columnOutput
                                columnOutput = checkColumn(StringMerge,HR, fidx);
                                if (columnOutput.isEmpty()){
                                    System.out.println("SQL ERROR(Column not found)");
                                }else{
                                    if(StringMerge.get(0).equals("*")){
                                        //Print Result of all array elements of an array with fidx index earlier.                
                                        printResult(HR.get(fidx)[0],Arrays.toString(Arrays.copyOfRange(HR.get(fidx), 1, HR.get(fidx).length)));
                                    }else{
                                       printResult(HR.get(fidx)[0],columnOutput.toString().replace(" ",""));
                                }
                                
                            }
                        //Else, if the Syntax Referencing the second form
                        //Which is SELECT FROM JOIN ON
                        }else{
                            //If the ON is in the user input
                            //And JOIN is placed before ON
                            if ((checko || checku) && ((jidx< oidx)||(jidx< uidx))){
                                //If there is no reference in ON syntax then prints error
                                //Exit the system as well
                                if((inputUser[eidx].length() == 1) && (oidx == eidx-1)){
                                       System.out.println("SQL ERROR(Missing ON/USING Reference)");
                                       System.exit(0); 
                                }
                                //Else check the JOIN is belong to what
                                
                                
                                jidx = checkBelongToArray(inputUser[jidx+1].toLowerCase().replace(";", ""),HR);
                                //If JOIN table is not found, then prints error
                                if(jidx == -2){
                                    System.out.println("SQL ERROR(There is no such table for JOIN Statement)");
                                //Else continue
                                }else{
                                    //Make two array list
                                    boolean UseOnCheck1 = false;
                                    boolean UseOnCheck2 = false;
                                    ArrayList<String> columnOutput1 = new ArrayList<String>();
                                    ArrayList<String> columnOutput2 = new ArrayList<String>();
                                    ArrayList<String> columnOutputOn1 = new ArrayList<String>();
                                    ArrayList<String> columnOutputOn2 = new ArrayList<String>();
                                    ArrayList<String> UsingMerge = new ArrayList<String>();
                                    ArrayList<String> OnMerge = new ArrayList<String>();

                                    //Check whether the column is existing
                                    //And store it to the list
                                    
                                    if (checku){
                                        String UsingText = "";
                                        for (int j = uidx+1; j <= eidx; j++) {
                                            UsingText += inputUser[j];
                                        }
                                        UsingText = UsingText.replaceAll("[();]","");
  
                                        UsingMerge.add(UsingText);
                                        UseOnCheck1 = !checkColumn(UsingMerge,HR, fidx).isEmpty();
                                        UseOnCheck2 = !checkColumn(UsingMerge,HR, jidx).isEmpty();   
                                    }else if (checko){
                                        String OnText = ""; String replacer = "";
                                        for (int j = oidx+1; j <= eidx; j++) {
                                            OnText += inputUser[j];
                                        }
                                        OnText = OnText.replaceAll("[  ( ) ;]","");
                                        if(!OnText.contains("=")){
                                            System.out.println("SQL ERROR (No '=' sign in ON Reference)");
                                            System.exit(0);
                                        }else{
                                            String[] replaceSplitter = OnText.split("=");
                                             if (replaceSplitter.length<=1){
                                                System.out.println("SQL Error(No ON Reference)");
                                                System.exit(0);
                                             }else if ((replaceSplitter[0].length()<2)||(replaceSplitter[1].length()<2)){
                                                System.out.println("SQL ERROR (Invalid ON References) ");
                                                System.exit(0);             
                                            }else if (!replaceSplitter[0].substring(2,replaceSplitter[0].length() ).equals(replaceSplitter[1].substring(2, replaceSplitter[1].length()))){
                                                System.out.println("SQL Error(Not Equal references)");
                                                System.exit(0); 
                                            }else{
                                                OnMerge.add(replaceSplitter[0].substring(2, replaceSplitter[1].length()));
                                                UseOnCheck1 = !checkColumn(OnMerge,HR, fidx).isEmpty();
                                                UseOnCheck2 = !checkColumn(OnMerge,HR, jidx).isEmpty();  
                                            }
                                        }
                                    }
                                    
                                    columnOutput1 = checkColumn(StringMerge,HR, fidx);
                                    columnOutput2 = checkColumn(StringMerge,HR, jidx); 

                                    
                                    //If empty, prints error
                                    //Otherwise Prints the result
                                    if((columnOutput1.isEmpty())||(columnOutput2.isEmpty())||((!UseOnCheck1)||(!UseOnCheck2))){
                                        System.out.println("SQL ERROR(Column Not Found)");
                                    }else{
                                        printResult(HR.get(fidx)[0].replace(" ", ""),columnOutput1.toString());
                                        printResult(HR.get(jidx)[0].replace(" ", ""),columnOutput2.toString());                                
                                   // }else{
                                   //     System.out.println("SQL ERROR(On reference is not equal");
                                    }
                                }
                            //The Elses beyond this point is self explanatory
                            //Based on indentation
                            }else{
          
                                System.out.println("SQL ERROR(Missing ON/USING Statement)");
                            }
                            
                        }
                    }
                }else{
                    System.out.println("SQL ERROR(Missing FROM Statement)");
                }  
            }else{
                System.out.println("SQL ERROR(There is no SELECT included)");
            }
        }else{
            System.out.println("SQL ERROR (SQL Statement doesn't have proper placement/Missing ';')");
        }    
    }
    //This function returns the index
    //of the matching string in the table
    public static int checkBelongToArray(String s,ArrayList<String[]> H ){
        int idx = -2;
        for (int i = 0; i < H.size(); i++) {
            if(s.equals(H.get(i)[0].replace("\\n", "").toLowerCase())){
                idx = i;
            }
        }
        return idx;
    }
    //This function returns ArrayList that has
    //The searched column that existed in the list
    public static ArrayList<String> checkColumn(ArrayList<String> s,ArrayList<String[]> t, int idx){
        ArrayList<String> list = new ArrayList<String>();    
        for (int i = 1; i < t.get(idx).length ; i++) {
            for (int j = 0; j < s.size() ; j++) {
                if (t.get(idx)[i].toLowerCase().equals(s.get(j).toLowerCase())){
                    list.add(s.get(j));
                }
            }
        }
        return list;
    }
    //This procedure prints the result according to string parameters
    public static void printResult(String s, String p){
        System.out.println("Table :"+ s);
        System.out.println("Column List :" + p);
    }
    //This function merge a string.
    public static String Merger(String[] a, int idx){
        String merge="";
        for (int j = idx-1; j > 0; j--) {
                merge += a[j];
        }
        return merge;
    }
    
    public static ArrayList<String> removeNull(String[] a) {
    ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < a.length; i++) {
            if(a[i] != null) { 
                 values.add(a[i]);
            }
        }
      return values;
    }
    
    public static String usingRemover(String s){
        String t = s.replace("{", "");
        return t;
    }
    
}

    

      
    

    

