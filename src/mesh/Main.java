package mesh;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class Main {
	
	public static char[] HEADER = {0x76, 0x65, 0x72, 0x73, 0x69, 0x6F, 0x6E, 0x20, 0x32, 0x2E, 0x30, 0x30, 0x0A, 0x0C, 0x00, 0x24, 0x0C};
	
	static FileInputStream openFile(String path) throws IOException {
		FileInputStream file = new FileInputStream(path);
		byte[] header = new byte[HEADER.length];
		file.read(header, 0, HEADER.length);
	    for(int i = 0; i <  HEADER.length; i++) {
	    	if(header[i] != HEADER[i]) throw new IOException("The header of the input file was not correct.");
	    }
	    return file;
	}
	
	public static void main(String[] args) {
		FileInputStream file = null;
		Scanner reader = new Scanner(System.in);
		
		//Ask for path and load file.
		do {
			System.out.println("File path: ");
			String path = reader.nextLine();
			try {
				file = openFile(path);
			} catch (IOException e) {
				System.err.println("An error occured while loading the file!");
				e.getMessage();
			}
		} while (file == null);		
		
		//Ask if we want to output as file.
		String option = "";
		do {
			System.out.println("Output as file? (yes/no)");
			option = reader.nextLine();
		} while(!(option.equals("yes") || option.equals("no")));
		
		//Main program
		try {
			
			//get count
			byte[] verts = new byte[8];
			file.read(verts, 0, 8);
			ByteBuffer bb = ByteBuffer.wrap(verts);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			int count = bb.getInt();
			int groups = bb.getInt(); //Seems to be (count / 3).
			
			//create variables.
			Triad[] triads = new Triad[count];
			byte[] footer = new byte[count * 4];
			
			//read file.
			for(int i = 0; i < count; i++) {
				triads[i] = new Triad(file);
			}
			
			//should take us to the end of the file.
			file.read(footer, 0, count * 4);
			bb = ByteBuffer.wrap(footer);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			//check footer (does this matter?)
			for(int i = 0; i < count; i++) {
				int check = bb.getInt();
				if(check != i) {
					System.out.println("There was an error reading the footer!");
					System.out.println("Check failed at number " + check + ".");
					break;
				}
			}
			
			long time = System.currentTimeMillis();
			
			//print everything.
			System.out.println("---- BEGIN DECODED MESH ----");
			
			//Set up printer based on user input
			PrintStream out = null;
			if(option.equals("yes")) {
				out = new PrintStream(new FileOutputStream("output.mesh"));
				System.out.println("(Output differed to file 'output.mesh')");
			} else {
				out = System.out;
			}
			
			//Print more.
			out.println("version 1.00");
			out.println(groups);
			for(Triad triad : triads) {
				out.print(triad);
			}
			System.out.println("---- END DECODED MESH ----");
			System.out.println("Processed " + count + " triads, composing of " + groups + " group(s) in " + ((double)(System.currentTimeMillis() - time) / 1000.0) + " seconds." );
			
			file.close();
		} catch (IOException e) {
			System.err.println("An error occured while parsing the file!");
			e.printStackTrace();
		}
		
		System.out.println("Goodbye!");
		reader.close();
	}
}
