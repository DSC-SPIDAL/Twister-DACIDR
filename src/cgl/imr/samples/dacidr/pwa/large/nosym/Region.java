package cgl.imr.samples.dacidr.pwa.large.nosym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cgl.imr.base.SerializationException;
import cgl.imr.base.Value;

/**
 * Represents a collection of computable blocks.
 * 
 * @author Yang Ruan (yangruan@cs.indiana.edu)
 * @author Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 * 
 */
public class Region implements Value {
	private List<Block> blocks;

	public Region() {
		this.blocks = new ArrayList<Block>();
	}

	public Region(byte[] bytes) throws SerializationException {
		this();
		fromBytes(bytes);
	}

	public void addBlock(Block block) {
		this.blocks.add(block);
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	@Override
	public void fromBytes(byte[] bytes) throws SerializationException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(bytes);
		DataInputStream din = new DataInputStream(baInputStream);

		try {
			int count = din.readInt();
			if (count != 0) {
				int len;
				byte[] data;
				for (int i = 0; i < count; i++) {
					len = din.readInt();
					data = new byte[len];
					din.readFully(data);
					this.blocks.add(new Block(data));
				}
			}
			din.close();
			baInputStream.close();
		} catch (IOException ioe) {
			throw new SerializationException(ioe);
		}
	}

	@Override
	public byte[] getBytes() throws SerializationException {
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(baOutputStream);
		int count = this.blocks.size();

		byte[] marshalledBytes;
		try {
			dout.writeInt(count);
			byte[] data;
            for (Block block : this.blocks) {
                data = block.getBytes();
                dout.writeInt(data.length);
                dout.write(data);
            }
			dout.flush();
			marshalledBytes = baOutputStream.toByteArray();

			dout.close();
			baOutputStream.close();
		} catch (IOException ioe) {
			throw new SerializationException(ioe);
		}
		return marshalledBytes;
	}
}