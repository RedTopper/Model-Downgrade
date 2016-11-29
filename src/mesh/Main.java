package mesh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
			
			//Get the time the reader started parsing the file.
			long start = System.currentTimeMillis();
			
			//Get counts of vertices and polygons.
			byte[] verts = new byte[8];
			file.read(verts, 0, 8);
			ByteBuffer bb = ByteBuffer.wrap(verts);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			int triadCount = bb.getInt();
			int polyCount = bb.getInt();
			
			//Create variables.
			Triad[] triads = new Triad[triadCount];
			Poly[] polys = new Poly[polyCount];
			byte[] footer = new byte[polyCount * 3 * 4];
			
			//Read vertices.
			for(int i = 0; i < triadCount; i++) {
				triads[i] = new Triad(file);
			}
			
			//Read footer.
			file.read(footer, 0, polyCount * 3 * 4);
			bb = ByteBuffer.wrap(footer);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			//Create polygons from footer.
			for(int i = 0; i < polyCount; i++) {
				polys[i] = new Poly(triads[bb.getInt()], triads[bb.getInt()], triads[bb.getInt()]);
			}
			
			//Get the time the reader stopped parsing the file.
			long time = System.currentTimeMillis() - start;
			
			//Run user's code.
			doMagicWithData(polys);
			
			//Print the mesh as version 1.00
			doPrintAsVersionOne(polys, option, time, triadCount, polyCount);
			
			file.close();
		} catch (IOException e) {
			System.err.println("An error occured while parsing the file!");
			e.printStackTrace();
		}
		
		System.out.println("Goodbye!");
		reader.close();
	}

	private static void doPrintAsVersionOne(Poly[] polys, String option, long time, int triadCount, int polyCount) throws FileNotFoundException {
		//Print header.
		System.out.println("---- BEGIN DECODED MESH ----");
		
		//Set up printer based on user input.
		PrintStream out = null;
		if(option.equals("yes")) {
			out = new PrintStream(new FileOutputStream("output.mesh"));
			System.out.print("(Output differed to file 'output.mesh')");
		} else {
			out = System.out;
		}
		
		//Print actual file in version 1.00 format.
		out.println("version 1.00");
		out.println(polyCount);
		for(Poly p : polys) {
			out.print(p.a.toString() + p.b.toString() + p.c.toString());
		}
		System.out.println("\n---- END DECODED MESH ----");
		System.out.println("Processed " + triadCount + " triads, composing of " + polyCount + " poly(s) in " + ((double)time / 1000.0) + " seconds." );
	}

	/**
	 * README:
	 * 
	 * At this point in the code the file has been parsed. All of the polygons
	 * have been inserted into the polys[] array for you to use, manipulate, edit,
	 * or whatever you want. You can use this stub method do whatever you want.
	 */
	private static void doMagicWithData(Poly[] polys) {
		// TODO Auto-generated method stub
	}
}
