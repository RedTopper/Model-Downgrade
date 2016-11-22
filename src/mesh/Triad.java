package mesh;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Triad {
	float[] position = {0,0,0};
	float[] normal = {0,0,0};
	float[] texture = {0,0,0};
	
	static final int FLOATS_TO_READ = 9;
	static final boolean LINE_BREAK = false;
	
	public Triad(FileInputStream file) throws IOException {
		byte[] information = new byte[FLOATS_TO_READ * 4];
		if(file.read(information, 0, FLOATS_TO_READ * 4) == -1) return;
		ByteBuffer bb = ByteBuffer.wrap(information);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		read(position, bb);
		read(normal, bb);
		read(texture, bb);
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();
		NumberFormat format = new DecimalFormat("#.########");
		output(output, position, format);
		output(output, normal, format);
		output(output, texture, format);
		return output + (LINE_BREAK ? "\n" : "");
	}
	
	private void output(StringBuilder output, float[] array, NumberFormat format) {
		String sep = "";
		output.append("[");
		for(float arr : array) {
			output.append(sep);
			sep = ",";
			output.append(format.format(arr));
		}
		output.append("]");
	}
	
	private void read(float[] array, ByteBuffer bb) {
		for(int i = 0; i < array.length; i++) {
			array[i] = bb.getFloat();
		}
	}
}
