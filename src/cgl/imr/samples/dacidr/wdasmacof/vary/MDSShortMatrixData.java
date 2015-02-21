/*
 * Software License, Version 1.0
 *
 *  Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) All redistributions of source code must retain the above copyright notice,
 *  the list of authors in the original source code, this list of conditions and
 *  the disclaimer listed in this license;
 * 2) All redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the disclaimer listed in this license in
 *  the documentation and/or other materials provided with the distribution;
 * 3) Any documentation included with all redistributions must include the
 *  following acknowledgement:
 *
 * "This product includes software developed by the Community Grids Lab. For
 *  further information contact the Community Grids Lab at
 *  http://communitygrids.iu.edu/."
 *
 *  Alternatively, this acknowledgement may appear in the software itself, and
 *  wherever such third-party acknowledgments normally appear.
 *
 * 4) The name Indiana University or Community Grids Lab or Twister,
 *  shall not be used to endorse or promote products derived from this software
 *  without prior written permission from Indiana University.  For written
 *  permission, please contact the Advanced Research and Technology Institute
 *  ("ARTI") at 351 West 10th Street, Indianapolis, Indiana 46202.
 * 5) Products derived from this software may not be called Twister,
 *  nor may Indiana University or Community Grids Lab or Twister appear
 *  in their name, without prior written permission of ARTI.
 *
 *
 *  Indiana University provides no reassurances that the source code provided
 *  does not infringe the patent or any other intellectual property rights of
 *  any other entity.  Indiana University disclaims any liability to any
 *  recipient for claims brought by any other entity based on infringement of
 *  intellectual property rights or otherwise.
 *
 * LICENSEE UNDERSTANDS THAT SOFTWARE IS PROVIDED "AS IS" FOR WHICH NO
 * WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. INDIANA UNIVERSITY GIVES
 * NO WARRANTIES AND MAKES NO REPRESENTATION THAT SOFTWARE IS FREE OF
 * INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER PROPRIETARY RIGHTS.
 * INDIANA UNIVERSITY MAKES NO WARRANTIES THAT SOFTWARE IS FREE FROM "BUGS",
 * "VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS", OR OTHER HARMFUL CODE.
 * LICENSEE ASSUMES THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR
 * ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF INFORMATION
 * GENERATED USING SOFTWARE.
 */

package cgl.imr.samples.dacidr.wdasmacof.vary;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MDSShortMatrixData {
	public MDSShortMatrixData() {
	}
	public MDSShortMatrixData(short[][] data, int height, int width, int row,
			int rowOffset) {
		this.data = data;
		this.height = height;
		this.width = width;
		this.row = row;
		this.rowOffset = rowOffset;
	}
	
	short[][] data;
	int height;
	int width;
	int row = -1; // Row (or row bloc) index
	int rowOffset = -1; // row offset

	public int getRowOffset() {
		return rowOffset;
	}

	public short[][] getData() {
		return data;
	}

	public int getHeight() {
		return height;
	}

	public int getRow() {
		return row;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Loads distances stored in short format from a binary file.
	 * The distances are arranged row after row in the binar stream
	 * @param fileName The distance file
	 * @param isBigEndian true for Java style binary files and false for C# style binary files
	 * @return a 2D short array containing distances
	 * @throws IOException
	 */
	public short[][] loadDeltaFromBinFile(String fileName, boolean isBigEndian) throws IOException {
		if (!(height > 0 && width > 0)) {
			throw new IOException("Invalid number of rows or columns.");
		}

		this.data = new short[height][width];


		try (FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(fileName), StandardOpenOption.READ)) {
			long pos = 0;
			MappedByteBuffer mappedBytes = fc.map(FileChannel.MapMode.READ_ONLY, pos,
					height * width * 2); // 2 for short data
			mappedBytes.order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int element = i * width + j; // element position - not the byte position
					// We assume that Matrix values in binary files are stored in short value.
					data[i][j] = mappedBytes.getShort(element*2);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this.data;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setRowOFfset(int rowOffset) {
		this.rowOffset = rowOffset;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	/**
	 * Write the vector data into a binary file.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void writeToBinFile(String fileName) throws IOException {
		BufferedOutputStream bout = new BufferedOutputStream(
				new FileOutputStream(fileName));
		DataOutputStream dout = new DataOutputStream(bout);

		// First two parameters are the dimensions.
		//dout.writeInt(height);
		//dout.writeInt(width);
		//dout.writeInt(row);
		//dout.writeInt(rowOffset);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				dout.writeShort(data[i][j]);
			}
		}
		dout.flush();
		bout.flush();
		dout.close();
		bout.close();
	}
}
