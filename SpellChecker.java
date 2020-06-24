


// QuadraticProbing Hash table class
//
// CONSTRUCTION: an approximate initial size or default of 101
//
// ******************PUBLIC OPERATIONS*********************
// bool insert( x )       --> Insert x
// bool remove( x )       --> Remove x
// bool contains( x )     --> Return true if x is present
// void makeEmpty( )      --> Remove all items

/**
* Author: Ahmed Mazloum
*
* Description: The program will first create a hash table. The number buckets of the hash table should be about twice the number of words in the dictionary.
* Then, it will read the dictionary from the file, insert the words into the hash table, and report collision statistics.
  After reading the dictionary, the spelling checker will read a list of words from a text file. Each word will be looked up in the dictionary.
* If it is incorrect, it will be written to the standard output together with a list of suggested corrections.
*  The algorithm for generating corrections is given below.
*/

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Probing table implementation of hash tables.
 * Note that all "matching" is based on the equals method.
 * @author Mark Allen Weiss
 */
public class QuadraticProbingHashTable<AnyType>
{
    /**
     * Construct the hash table.
     */
    public QuadraticProbingHashTable( )
    {
        this( DEFAULT_TABLE_SIZE );
    }

    /**
     * Construct the hash table.
     * @param size the approximate initial size.
     */
    public QuadraticProbingHashTable( int size )
    {
        allocateArray( size );
        makeEmpty( );
    }

    /**
     * Insert into the hash table. If the item is
     * already present, do nothing.
     * @param x the item to insert.
     */
    public void insert( AnyType x )
    {
        // Insert x as active
        int currentPos = findPos( x );
        if( isActive( currentPos ) )
            return;

        array[ currentPos ] = new HashEntry<AnyType>( x, true );

        // Rehash; see Section 5.5
        if( ++currentSize > array.length / 2 ) {
            rehash();
            numCollisions = 0;
            totalChainLength = 0;
            longestChain = 0;
        }
    }

    /**
     * Expand the hash table.
     */
    private void rehash( )
    {
        HashEntry<AnyType> [ ] oldArray = array;

        // Create a new double-sized, empty table
        allocateArray( nextPrime( 2 * oldArray.length ) );
        currentSize = 0;

        // Copy table over
        for( int i = 0; i < oldArray.length; i++ )
            if( oldArray[ i ] != null && oldArray[ i ].isActive )
                insert( oldArray[ i ].element );
    }

    /**
     * Method that performs quadratic probing resolution.
     * Assumes table is at least half empty and table length is prime.
     * @param x the item to search for.
     * @return the position where the search terminates.
     */
    private int findPos( AnyType x )
    {
        chainLength =1;
        boolean newWord=true;
        int offset = 1;
        int currentPos = myhash( x );

        while( array[ currentPos ] != null &&
                !array[ currentPos ].element.equals( x ) )
        {
            currentPos += offset;  // Compute ith probe
            offset += 2;
            if( currentPos >= array.length )
                currentPos -= array.length;
            if(newWord){

                numCollisions++;
                newWord =false;
            }
            chainLength++;

        }
        totalChainLength += chainLength;
        if(chainLength > longestChain){

            longestChain = chainLength;
        }
        return currentPos;
    }

    /**
     * Remove from the hash table.
     * @param x the item to remove.
     */
    public void remove( AnyType x )
    {
        int currentPos = findPos( x );
        if( isActive( currentPos ) )
            array[ currentPos ].isActive = false;
    }

    /**
     * Find an item in the hash table.
     * @param x the item to search for.
     * @return the matching item.
     */
    public boolean contains( AnyType x )
    {
        int currentPos = findPos( x );
        return isActive( currentPos );
    }

    /**
     * Return true if currentPos exists and is active.
     * @param currentPos the result of a call to findPos.
     * @return true if currentPos is active.
     */
    private boolean isActive( int currentPos )
    {
        return array[ currentPos ] != null && array[ currentPos ].isActive;
    }

    /**
     * Make the hash table logically empty.
     */
    public void makeEmpty( )
    {
        currentSize = 0;
        for( int i = 0; i < array.length; i++ )
            array[ i ] = null;
    }

    private int myhash( AnyType x )
    {
        int hashVal = x.hashCode( );

        hashVal %= array.length;
        if( hashVal < 0 )
            hashVal += array.length;

        return hashVal;
    }

    private static class HashEntry<AnyType>
    {
        public AnyType  element;   // the element
        public boolean isActive;  // false if marked deleted

        public HashEntry( AnyType e )
        {
            this( e, true );
        }

        public HashEntry( AnyType e, boolean i )
        {
            element  = e;
            isActive = i;
        }
    }

    private static final int DEFAULT_TABLE_SIZE = 11;

    private HashEntry<AnyType> [ ] array; // The array of elements
    private int currentSize;// The number of occupied cells
    private int numCollisions;
    private double totalChainLength;
    private int longestChain;
    private int chainLength;
    /**
     * Internal method to allocate array.
     * @param arraySize the size of the array.
     */
    @SuppressWarnings("unchecked")
    private void allocateArray( int arraySize )
    {
        array = new HashEntry[ nextPrime( arraySize ) ];
    }

    /**
     * Internal method to find a prime number at least as large as n.
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     */
    private static int nextPrime( int n )
    {
        if( n <= 0 )
            n = 3;

        if( n % 2 == 0 )
            n++;

        for( ; !isPrime( n ); n += 2 )
            ;


        return n;
    }

    /**
     * Internal method to test if a number is prime.
     * Not an efficient algorithm.
     * @param n the number to test.
     * @return the result of the test.
     */
    private static boolean isPrime( int n )
    {
        if( n == 2 || n == 3 )
            return true;

        if( n == 1 || n % 2 == 0 )
            return false;

        for( int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;

        return true;
    }


    // Simple main
    /**
     * Pre:Hash table is created
     * Desc:The main method opens the files and stores the dictionary txt file in the hash map and
     * stores the words not found in the dictionary an arraylist called incorrect words. Also prints the
     * stats of the hash table
     * Post:The words in both files are stored into the hash table and arraylist
     */
    public static void main(String [ ] args ) throws IOException {

        QuadraticProbingHashTable<String> H = new QuadraticProbingHashTable<String>( );
        //opens file
        File file = new File(args[1]);
        BufferedReader words =new BufferedReader(new FileReader(file));

        String currentLine;
        //inserts the file into the hash table
        while((currentLine= words.readLine()) != null){

            H.insert(currentLine);
        }
        words.close();
        //opens file
        LineNumberReader lnr= new LineNumberReader(new FileReader(args[2]));
        String l;
        ArrayList<String> temps=new ArrayList<String>();
        ArrayList<Integer>lineList = new ArrayList<>();

        while ((l = lnr.readLine()) != null){
         Scanner s=new Scanner(l);
         //adds the value of the file into the array and adds the number of lines of the file into another array
         while(s.hasNext()){
             temps.add(s.next());
             lineList.add(lnr.getLineNumber());

         }

        }
        lnr.close();
        //finds average chain
        double averageChain= H.totalChainLength/H.currentSize;
        //finds load factor
        double loadFactor = (H.currentSize*1.0) / (H.array.length*1.0);
        File file0= new File("out.txt");
        FileWriter fw = new FileWriter(file0);
        BufferedWriter pw = new BufferedWriter(fw);

        File file2= new File("out1.txt");
        FileWriter f = new FileWriter(file2);
        BufferedWriter p = new BufferedWriter(f);

        p.write("Number of words " + H.currentSize + ", Table size " + H.array.length +", Load Factor "+ loadFactor+", Collisions "+ H.numCollisions+
                ", Average chain length "+ averageChain+", Longest chain length "+ H.longestChain + "\n");

        p.close();
        String[] tempArr = temps.toArray(new String[0]);
        ArrayList<Integer> updatedlineList=new ArrayList<>();
        ArrayList<String> incorrectWords = new ArrayList<String>();
        for(int i =0; i< tempArr.length;i++){
            //gets rid of any punctuation of the given word
            tempArr[i]= tempArr[i].replaceAll("[^a-zA-Z]", "");
            //Checks if the hash table doesn't contain the word in the arraylist
            if(!H.contains(tempArr[i])) {
                if (!H.contains(tempArr[i].toLowerCase())) {

                    incorrectWords.add(tempArr[i]);
                     updatedlineList.add(lineList.get(i));
                }
            }
        }
        generateWords(incorrectWords, H,updatedlineList, pw);
        pw.close();

        final int NUMS = 400000;
        final int GAP  =   37;

        System.out.println( "Checking... (no more output means success)" );

        for( int i = GAP; i != 0; i = ( i + GAP ) % NUMS )
            H.insert( ""+i );
        for( int i = 1; i < NUMS; i+= 2 )
            H.remove( ""+i );

        for( int i = 2; i < NUMS; i+=2 )
            if( !H.contains( ""+i ) )
                System.out.println( "Find fails ");

        for( int i = 1; i < NUMS; i+=2 )
        {
            if( H.contains( ""+i ) )
                System.out.println( "OOPS!!! " +  i  );
        }
    }


    /**
     * Method to generate the suggested words for the incorrect words
     * @param incorrectWords the size of the array
     * @param H the letter to use the hash table
     * @param lineList the array of the amount of lines the dictionary file has
     */
    static void generateWords(ArrayList<String> incorrectWords, QuadraticProbingHashTable<String> H,ArrayList<Integer> lineList, BufferedWriter pw) throws IOException {
        for(int i=0; i<incorrectWords.size();i++) {
            ArrayList<String> correctWords = new ArrayList<String>();

            for (int j = 0; j < incorrectWords.get(i).length(); j++) {
                char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

            String temp = incorrectWords.get(i);
                //deletes letter of the word
                temp = temp.substring(0,j) +temp.substring(j+1);

                //Checks in that word is apart of the dictionary and adds it in another arraylist if it is
                if(H.contains(temp.toLowerCase())){

                    correctWords.add(temp);
                }

                temp = incorrectWords.get(i);
                //swaps the letters of the word
                temp =  swap(temp, j,temp.length()-1);
                //Checks in that word is apart of the dictionary and adds it in another arraylist if it is
                if(H.contains(temp.toLowerCase())){

                    correctWords.add(temp);
                }

                //For loop that is the length of the alphabet
                for(int x =0; x < alphabet.length;x++){

                    temp = incorrectWords.get(i);

                    //adds a letter of the alphabet at the deleted position
                    temp =  temp.substring(0,j)+ alphabet[x] +temp.substring(j+1);

                    //Checks in that word is apart of the dictionary and adds it in another arraylist if it is
                    if(H.contains(temp.toLowerCase())) {

                        correctWords.add(temp);
                    }

                    //replaces every position of the word with every letter of the alphabet
                    temp=incorrectWords.get(i);
                    String temp1= incorrectWords.get(i);
                    char [] temp2 = temp1.toCharArray();
                    temp = temp.replace(temp2[j], alphabet[x]);

                    //Checks in that word is apart of the dictionary and adds it in another arraylist if it is
                    if(H.contains(temp)!=H.contains(temp)) {
                            correctWords.add(temp);
                        }


                }
        }
            print(incorrectWords.get(i), correctWords, lineList,i,pw);

        }


    }
    /**
     * Pre:A string, and two arraylists are passed in to the method
     * Desc:Prints the incorrect words with the line number and the suggested words
     * Post:Prints to the console
     */
    public static void print(String incorrectWord, ArrayList<String>correctWords,ArrayList<Integer>lineList,int j,BufferedWriter pw) throws IOException {


            pw.write(incorrectWord+ "(" +lineList.get(j) +"):");
            for (int i = 0; i < correctWords.size(); i++) {
                pw.write(correctWords.get(i) + " ");

            }
        pw.write("\n");

    }

    /**
     * Pre:A string and both positions of a nested loop is passed in the method
     * Desc:Swaps the position of the string
     * Post:The string gets swap
     */
    static String swap(String str, int i, int j)
    {
        StringBuilder strB = new StringBuilder(str);
        char l = strB.charAt(i) , r = strB.charAt(j);
        strB.setCharAt(i,r);
        strB.setCharAt(j,l);
        return strB.toString();
    }

}


