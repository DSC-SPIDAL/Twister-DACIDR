package cgl.imr.samples.dacidr.wdasmacof.fix;

/**
 * 
 * @author Jaliya Ekanayake, jekanaya@cs.indiana.edu Some code segments contain
 *         in this class are directly inherited from the C# version of the
 *         shared memory MDS program wirtten by my collegue Seung-Hee Bea.
 * 
 *         A collection of matrix related utility operations.
 * 
 */
public class MatrixUtils {
	public static double[][] matrixMultiplyInSample(short[][] A, double[] Adiag, double[][] B,
			int aHeight, int bWidth, int comm, int bz, int rowOffset, int inSampleSize, boolean inSample) {

		double[][] C = new double[aHeight][bWidth];

		int aHeightBlocks = aHeight / bz; // size = Height of A
		int aLastBlockHeight = aHeight - (aHeightBlocks * bz);
		if (aLastBlockHeight > 0) {
			aHeightBlocks++;
		}

		int bWidthBlocks = bWidth / bz; // size = Width of B
		int bLastBlockWidth = bWidth - (bWidthBlocks * bz);
		if (bLastBlockWidth > 0) {
			bWidthBlocks++;
		}

		int commnBlocks = comm / bz; // size = Width of A or Height of B
		int commLastBlockWidth = comm - (commnBlocks * bz);
		if (commLastBlockWidth > 0) {
			commnBlocks++;
		}

		int aBlockHeight = bz;
		int bBlockWidth = bz;
		int commBlockWidth = bz;

		for (int ib = 0; ib < aHeightBlocks; ib++) {
			if (aLastBlockHeight > 0 && ib == (aHeightBlocks - 1)) {
				aBlockHeight = aLastBlockHeight;
			}
			bBlockWidth = bz;
			commBlockWidth = bz;
			for (int jb = 0; jb < bWidthBlocks; jb++) {
				if (bLastBlockWidth > 0 && jb == (bWidthBlocks - 1)) {
					bBlockWidth = bLastBlockWidth;
				}
				commBlockWidth = bz;
				for (int kb = 0; kb < commnBlocks; kb++) {
					if (commLastBlockWidth > 0 && kb == (commnBlocks - 1)) {
						commBlockWidth = commLastBlockWidth;
					}

					for (int i = ib * bz; i < (ib * bz) + aBlockHeight; i++) {
						if (i + rowOffset>= inSampleSize) {
							for (int j = jb * bz; j < (jb * bz) + bBlockWidth; j++) {
								for (int k = kb * bz; k < (kb * bz)
										+ commBlockWidth; k++) {
									double aVal = 0;

									if (i + rowOffset == k) {
										aVal = Adiag[i];
									}
									else {
										//reverse the value from weight
										aVal = -(double)A[i][k];
									}

									//System.out.print(aVal);
									if((inSample && k < inSampleSize 
											|| !inSample && k >= inSampleSize) 
											&& aVal != 0 && B[k][j]!= 0)
										C[i][j] += aVal * B[k][j];
								}
							}
						}
					}
				}
			}
		}

		return C;
	}	
	
//	public static double[][] matrixMultiplyOutSample(short[][] A, double[] Adiag, double[][] B,
//			int aHeight, int bWidth, int comm, int bz, int rowOffset, int inSampleSize) {
//		
//		double[][] C = new double[aHeight][bWidth];
//
//		int aHeightBlocks = aHeight / bz; // size = Height of A
//		int aLastBlockHeight = aHeight - (aHeightBlocks * bz);
//		if (aLastBlockHeight > 0) {
//			aHeightBlocks++;
//		}
//
//		int bWidthBlocks = bWidth / bz; // size = Width of B
//		int bLastBlockWidth = bWidth - (bWidthBlocks * bz);
//		if (bLastBlockWidth > 0) {
//			bWidthBlocks++;
//		}
//
//		int commnBlocks = comm / bz; // size = Width of A or Height of B
//		int commLastBlockWidth = comm - (commnBlocks * bz);
//		if (commLastBlockWidth > 0) {
//			commnBlocks++;
//		}
//
//		int aBlockHeight = bz;
//		int bBlockWidth = bz;
//		int commBlockWidth = bz;
//
//		for (int ib = 0; ib < aHeightBlocks; ib++) {
//			if (aLastBlockHeight > 0 && ib == (aHeightBlocks - 1)) {
//				aBlockHeight = aLastBlockHeight;
//			}
//			bBlockWidth = bz;
//			commBlockWidth = bz;
//			for (int jb = 0; jb < bWidthBlocks; jb++) {
//				if (bLastBlockWidth > 0 && jb == (bWidthBlocks - 1)) {
//					bBlockWidth = bLastBlockWidth;
//				}
//				commBlockWidth = bz;
//				for (int kb = 0; kb < commnBlocks; kb++) {
//					if (commLastBlockWidth > 0 && kb == (commnBlocks - 1)) {
//						commBlockWidth = commLastBlockWidth;
//					}
//
//					for (int i = ib * bz; i < (ib * bz) + aBlockHeight; i++) {
//						for (int j = jb * bz; j < (jb * bz) + bBlockWidth; j++) {
//							for (int k = kb * bz; k < (kb * bz)
//									+ commBlockWidth; k++) {
//								
//								double aVal = 0;
//								if (i + rowOffset == k) {
//									aVal = Adiag[i];
//								}
//								else {
//									aVal = -(double)A[i][k];
//								}
//								if(k >= inSampleSize && aVal != 0 && B[k][j]!= 0) {
//									if (k > 4639)
//										System.out.print(k + " ");
//									C[i][j] += aVal * B[k][j];
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return C;
//	}

	public static double[][] matrixMultiplyInSample(float[][] A, double[][] B,
			int aHeight, int bWidth, int comm, int bz, int rowOffset, 
			int inSampleSize, boolean inSample) {

//		System.out.println("aHeight: " + aHeight);
//		System.out.println("bWidth: " + bWidth);
//		System.out.println("comm: " + comm);
//		System.out.println("bz: " + bz);
//		System.out.println("rowOffset: " + rowOffset);
//		System.out.println("inSampleSize: " + inSampleSize);
//		System.out.println("inSample: " + inSample);
		
		double[][] C = new double[aHeight][bWidth];

		int aHeightBlocks = aHeight / bz; // size = Height of A
		int aLastBlockHeight = aHeight - (aHeightBlocks * bz);
		if (aLastBlockHeight > 0) {
			aHeightBlocks++;
		}

		int bWidthBlocks = bWidth / bz; // size = Width of B
		int bLastBlockWidth = bWidth - (bWidthBlocks * bz);
		if (bLastBlockWidth > 0) {
			bWidthBlocks++;
		}

		int commnBlocks = comm / bz; // size = Width of A or Height of B
		int commLastBlockWidth = comm - (commnBlocks * bz);
		if (commLastBlockWidth > 0) {
			commnBlocks++;
		}

		int aBlockHeight = bz;
		int bBlockWidth = bz;
		int commBlockWidth = bz;

		for (int ib = 0; ib < aHeightBlocks; ib++) {
			if (aLastBlockHeight > 0 && ib == (aHeightBlocks - 1)) {
				aBlockHeight = aLastBlockHeight;
			}
			bBlockWidth = bz;
			commBlockWidth = bz;
			for (int jb = 0; jb < bWidthBlocks; jb++) {
				if (bLastBlockWidth > 0 && jb == (bWidthBlocks - 1)) {
					bBlockWidth = bLastBlockWidth;
				}
				commBlockWidth = bz;
				for (int kb = 0; kb < commnBlocks; kb++) {
					if (commLastBlockWidth > 0 && kb == (commnBlocks - 1)) {
						commBlockWidth = commLastBlockWidth;
					}

					for (int i = ib * bz; i < (ib * bz) + aBlockHeight; i++) {
						if (i + rowOffset >= inSampleSize) {
							for (int j = jb * bz; j < (jb * bz) + bBlockWidth; j++) {
								for (int k = kb * bz; k < (kb * bz)
										+ commBlockWidth; k++) {
									if((inSample && k < inSampleSize
											|| !inSample && k >= inSampleSize) 
											&& A[i][k]!= 0 && B[k][j]!= 0)
										C[i][j] += A[i][k] * B[k][j];
								}
							}
						}
					}
				}
			}
		}

		return C;
	}
	
//	public static double[][] matrixMultiplyOutSample(float[][] A, double[][] B,
//			int aHeight, int bWidth, int comm, int bz, int startCol) {
//
//		double[][] C = new double[aHeight][bWidth];
//
//		int aHeightBlocks = aHeight / bz; // size = Height of A
//		int aLastBlockHeight = aHeight - (aHeightBlocks * bz);
//		if (aLastBlockHeight > 0) {
//			aHeightBlocks++;
//		}
//
//		int bWidthBlocks = bWidth / bz; // size = Width of B
//		int bLastBlockWidth = bWidth - (bWidthBlocks * bz);
//		if (bLastBlockWidth > 0) {
//			bWidthBlocks++;
//		}
//
//		int commnBlocks = comm / bz; // size = Width of A or Height of B
//		int commLastBlockWidth = comm - (commnBlocks * bz);
//		if (commLastBlockWidth > 0) {
//			commnBlocks++;
//		}
//
//		int aBlockHeight = bz;
//		int bBlockWidth = bz;
//		int commBlockWidth = bz;
//
//		for (int ib = 0; ib < aHeightBlocks; ib++) {
//			if (aLastBlockHeight > 0 && ib == (aHeightBlocks - 1)) {
//				aBlockHeight = aLastBlockHeight;
//			}
//			bBlockWidth = bz;
//			commBlockWidth = bz;
//			for (int jb = 0; jb < bWidthBlocks; jb++) {
//				if (bLastBlockWidth > 0 && jb == (bWidthBlocks - 1)) {
//					bBlockWidth = bLastBlockWidth;
//				}
//				commBlockWidth = bz;
//				for (int kb = 0; kb < commnBlocks; kb++) {
//					if (commLastBlockWidth > 0 && kb == (commnBlocks - 1)) {
//						commBlockWidth = commLastBlockWidth;
//					}
//
//					for (int i = ib * bz; i < (ib * bz) + aBlockHeight; i++) {
//						for (int j = jb * bz; j < (jb * bz) + bBlockWidth; j++) {
//							for (int k = kb * bz; k < (kb * bz)
//									+ commBlockWidth; k++) {
//								if(k >= startCol && A[i][k]!= 0 && B[k][j]!= 0)
//									C[i][j] += A[i][k] * B[k][j];
//							}
//						}
//					}
//				}
//			}
//		}
//
//		return C;
//	}
	
	public static double[][] naiveMM(double[][] A, double[][] B,
			int aHeight, int bWidth, int comm) {

		double[][] C = new double[aHeight][bWidth];

		for (int i = 0; i < aHeight; i++) {

			for (int j = 0; j < bWidth; j++) {

				for (int k = 0; k < comm; k++) {
					if(A[i][k]!= 0 && B[k][j]!= 0)
						C[i][j] += A[i][k] * B[k][j];
				}
			}
		}
		return C;
	}

	public static double[][] add(double[][] m1, double[][] m2)
			throws MatrixException {

		int m1Len = m1.length;
		int m2Len = m2.length;

		if (m1Len == 0 || m2Len == 0 || m1Len != m2Len) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length != m2[0].length || m1[0].length == 0
				|| m2[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}

		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];

		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j] + m2[i][j];
			}
		}

		return res;
	}

	public static double[][] sub(double[][] m1, double[][] m2)
			throws MatrixException {

		int m1Len = m1.length;
		int m2Len = m2.length;

		if (m1Len == 0 || m2Len == 0 || m1Len != m2Len) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length != m2[0].length || m1[0].length == 0
				|| m2[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}

		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];

		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j] - m2[i][j];
			}
		}

		return res;
	}

	// addition a double value to a matrix.
	public static double[][] add(double[][] m1, double s)
			throws MatrixException {

		int m1Len = m1.length;

		if (m1Len == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}

		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];

		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j] + s;
			}
		}
		return res;
	}

	// addition a double value to a matrix.
	public static double[][] sub(double[][] m1, double s)
			throws MatrixException {

		int m1Len = m1.length;

		if (m1Len == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}

		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];

		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j] - s;
			}
		}
		return res;
	}

	public static double[][] mul(double[][] m1, double[][] m2)
			throws MatrixException {

		int m1Len = m1.length;
		int m2Len = m2.length;

		if (m1Len == 0 || m2Len == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length == 0 || m2[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length != m2.length) {
			throw new MatrixException(
					"Incompatible Matrices for multiplication");
		}

		int m1Width = m1[0].length;
		int m2Width = m2[0].length;

		double[][] res = new double[m1Len][m2Width];

		double val = 0;
		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < m2Width; j++) {
				val = 0;
				for (int k = 0; k < m1Width; k++) {
					val += m1[i][k] * m2[k][j];
				}
				res[i][j] = val;
			}
		}
		return res;
	}

	// multiply a double value to a matrix.
	public static double[][] mult(double[][] m1, double s)
			throws MatrixException {
		int m1Len = m1.length;

		if (m1Len == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}
		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];

		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j] * s;
			}
		}

		return res;
	}

	public static double[][] copy(double[][] m1) throws MatrixException {

		int m1Len = m1.length;

		if (m1Len == 0) {
			throw new MatrixException("Incompatible Matrices");
		} else if (m1[0].length == 0) {
			throw new MatrixException("Incompatible Matrices");
		}
		int vecLen = m1[0].length;
		double[][] res = new double[m1Len][vecLen];
		for (int i = 0; i < m1Len; i++) {
			for (int j = 0; j < vecLen; j++) {
				res[i][j] = m1[i][j];
			}
		}
		return res;
	}
}
