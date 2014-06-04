package cgl.imr.samples.dacidr.pwa.norm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cgl.imr.base.SerializationException;
import cgl.imr.base.Value;

/**
 * @author Yang Ruan (yangruan@cs.indiana.edu)
 * @author Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 */

public class Block implements Value {
    // Represents final row block number
    private int rowBlockNumber;
    // Represents final column block number
    private int columnBlockNumber;
    // represents final row size
    private int rowSize;
    // represents final column size
    private int colSize;
    // indicates if distances should be treated as transpose
    // If so, the distances is of size colSize x rowSize, else rowSize x colSize
    private boolean isTranspose = false;
    // Represents the set of distances in its original form, i.e. this may be the
    // transpose set of distances for this block.
    private short[][] score = null;
    private short[][] scoreA = null;
    private short[][] scoreAReverse = null;
    private short[][] scoreB = null;
    private short[][] scoreBReverse = null;
    
    //This is the alignment lengths
    private short[][] length = null;
    
    //This is the identicalPairs
    private short[][] identicalPairs = null;
    
    private short[][] scoreReverse = null;
    
    //This is the alignment lengths
    private short[][] lengthReverse = null;
    
    //This is the identicalPairs
    private short[][] identicalPairsReverse = null;
    

    public Block(int rowBlockNumber, int colBlockNumber) {
        this.rowBlockNumber = rowBlockNumber;
        this.columnBlockNumber = colBlockNumber;
    }

    /**
     * Instantiates a new block and initialize values based on the given byte array.
     * Note. The following values should be present in the array (in order) to
     * create an object successfully.
     *
     * int: row block number
     * int: column block number
     * boolean: is transpose or not
     * int: row size
     * int: column size
     * short[][]: (isTranspose ? colSize : rowSize) X (isTranspose ? rowSize : colSize)
     *
     * @param bytes the byte array to use in object creation
     * @throws SerializationException if an error occurs
     */
    public Block(byte[] bytes) throws SerializationException {
        this(0, 0);
        fromBytes(bytes);
    }

    /**
     * Returns the distances array for this block. Note. This may represent the transpose
     * set of distances.
     *
     * @return distance array
     */


	/**
     * Sets the distances for this block. If the array of distances represent the transpose
     * for this block then <code>isTranspose</code> should be set to <code>true</code>
     *
     * @param distances the array of distances
     * @param isTranspose indicates if the distance array represents the transpose set of distances for this block
     */
    public void setScore(short[][] score, boolean isTranspose) {
        this.score = score;
        this.isTranspose = isTranspose;
        this.rowSize = isTranspose ? score[0].length : score.length;
        this.colSize = isTranspose ? score.length : score[0].length;
    }
    
    public short[][] getScore() {
		return score;
	}

	public short[][] getLength() {
		return length;
	}

	public void setLength(short[][] length) {
		this.length = length;
	}

	public short[][] getIdenticalPairs() {
		return identicalPairs;
	}

	public void setIdenticalPairs(short[][] identicalPairs) {
		this.identicalPairs = identicalPairs;
	}

	public short[][] getScoreReverse() {
		return scoreReverse;
	}

	public void setScoreReverse(short[][] scoreReverse) {
		this.scoreReverse = scoreReverse;
	}

	public short[][] getLengthReverse() {
		return lengthReverse;
	}

	public void setLengthReverse(short[][] lengthReverse) {
		this.lengthReverse = lengthReverse;
	}

	public short[][] getIdenticalPairsReverse() {
		return identicalPairsReverse;
	}

	public void setIdenticalPairsReverse(short[][] identicalPairsReverse) {
		this.identicalPairsReverse = identicalPairsReverse;
	}

	public short[][] getScoreA() {
		return scoreA;
	}

	public void setScoreA(short[][] scoreA) {
		this.scoreA = scoreA;
	}

	public short[][] getScoreAReverse() {
		return scoreAReverse;
	}

	public void setScoreAReverse(short[][] scoreAReverse) {
		this.scoreAReverse = scoreAReverse;
	}

	public short[][] getScoreB() {
		return scoreB;
	}

	public void setScoreB(short[][] scoreB) {
		this.scoreB = scoreB;
	}

	public short[][] getScoreBReverse() {
		return scoreBReverse;
	}

	public void setScoreBReverse(short[][] scoreBReverse) {
		this.scoreBReverse = scoreBReverse;
	}

	/**
     * Returns the final row block number for this block. This is irrespective
     * of the fact whether the stored set of distances represents the transpose
     * or not.
     * @return final row block number
     */
    public int getRowBlockNumber() {
        return rowBlockNumber;
    }

    /**
     * Returns the final column block number for this block. This is irrespective
     * of the fact whether the stored set of distances represents the transpose
     * or not.
     * @return final column block number
     */
    public int getColumnBlockNumber() {
        return columnBlockNumber;
    }

    /**
     * Indicates if the stored set of distances in this block represents the
     * transpose values in the final output.
     * @return true if the stored distances represent the transpose, false otherwise.
     */
    public boolean isTranspose() {
        return isTranspose;
    }

    /**
     * Returns the final row size for this block. block. This is irrespective
     * of the fact whether the stored set of distances represents the transpose
     * or not.
     * @return the final row size
     */
    public int getRowSize() {
        return rowSize;
    }

    /**
     * Returns the final column size for this block. block. This is irrespective
     * of the fact whether the stored set of distances represents the transpose
     * or not.
     * @return the final column size
     */
    public int getColSize() {
        return colSize;
    }

    @Override
    /**
     * Builds a block from an array of bytes. The byte array should encode the following values
     * in order. Note. The last four values are optional.
     *
     * int: row block number
     * int: column block number
     * boolean: is transpose or not
     * int: row size
     * int: column size
     * short[][]: (isTranspose ? colSize : rowSize) X (isTranspose ? rowSize : colSize)
     */
    public void fromBytes(byte[] bytes) throws SerializationException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(baInputStream);

        try {
            this.rowBlockNumber = din.readInt();
            this.columnBlockNumber = din.readInt();

            if (din.available() > 0) {
                this.isTranspose = din.readBoolean();
                this.rowSize = din.readInt();
                this.colSize = din.readInt();

                int rows = isTranspose ? colSize : rowSize;
                int cols = isTranspose ? rowSize : colSize;
                
                this.score = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.score[i][j] = din.readShort();
                    }
                }
                
                this.length = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.length[i][j] = din.readShort();
                    }
                }
                
                this.identicalPairs = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.identicalPairs[i][j] = din.readShort();
                    }
                }
                
                this.scoreReverse = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.scoreReverse[i][j] = din.readShort();
                    }
                }
                
                this.lengthReverse = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.lengthReverse[i][j] = din.readShort();
                    }
                }
                
                this.identicalPairsReverse = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.identicalPairsReverse[i][j] = din.readShort();
                    }
                }
                
                this.scoreA = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.scoreA[i][j] = din.readShort();
                    }
                }
                
                this.scoreAReverse = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.scoreAReverse[i][j] = din.readShort();
                    }
                }
                
                this.scoreB = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.scoreB[i][j] = din.readShort();
                    }
                }
                
                this.scoreBReverse = new short[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        this.scoreBReverse[i][j] = din.readShort();
                    }
                }
                
            }
            din.close();
            baInputStream.close();

        } catch (IOException ioe) {
            throw new SerializationException(ioe);
        }
    }

    @Override
    /**
     * Serializes this block as a byte array. The following values are placed in order.
     * Note. Last four values will be present in the output only if distances array is not null
     * int: row block number
     * int: column block number
     * boolean: is transpose or not
     * int: row size
     * int: column size
     * short[][]: (isTranspose ? colSize : rowSize) X (isTranspose ? rowSize : colSize)
     */
    public byte[] getBytes() throws SerializationException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(baOutputStream);

        byte[] marshalledBytes = null;

        try {
            dout.writeInt(rowBlockNumber);
            dout.writeInt(columnBlockNumber);

            if (score != null) {
                dout.writeBoolean(isTranspose);
                dout.writeInt(rowSize);
                dout.writeInt(colSize);

                for (short[] row : score) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : length) {
                    for (short i : row) {
                        dout.writeShort(i);
                    }
                }
                for (short[] row : identicalPairs) {
                    for (short i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : scoreReverse) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : lengthReverse) {
                    for (short i : row) {
                        dout.writeShort(i);
                    }
                }
                for (short[] row : identicalPairsReverse) {
                    for (short i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : scoreA) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : scoreAReverse) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : scoreB) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
                for (short[] row : scoreBReverse) {
                    for (int i : row) {
                        dout.writeShort(i);
                    }
                }
                
            }
            dout.flush();
            marshalledBytes = baOutputStream.toByteArray();
            dout.close();
        } catch (IOException ioe) {
            throw new SerializationException(ioe);
        }
        return marshalledBytes;
    }
}