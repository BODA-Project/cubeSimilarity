package de.uop.mics.bayerl.cube.eval;

import java.io.*;

/**
 * Created by sebastianbayerl on 02/11/15.
 */
public class TrecEvalWrapper {

    public static void main(String[] args) throws IOException {
        Process tr = Runtime.getRuntime().exec( "ls"  );
//        tr.get
        Writer wr = new OutputStreamWriter( tr.getOutputStream() );
        BufferedReader rd = new BufferedReader( new InputStreamReader( tr.getInputStream() ) );
//        wr.write( "hello, world\n" );
//        wr.flush();
        String s = rd.readLine();
        String s2 = rd.readLine();
        System.out.println( s );
        System.out.println(s2);

    }
}