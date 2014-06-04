/**
 * @author Yang Ruan (yangruan@indiana.edu)
 */
package cgl.imr.samples.dacidr.inter.mimds;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cgl.imr.samples.dacidr.inter.type.*;
import cgl.imr.samples.dacidr.inter.util.*;

public class Interpolation {
	private static boolean removeUnsignificant(Vec3D point, List<DistancePoint> distancePoints){
		int size = distancePoints.size();

		for(int i =0; i < size; i++){
			double tmpDistance = distancePoints.get(i).getDistance();
			Point tmpPoint = distancePoints.get(i).getPoint();
			double temp = DistanceCalculation.Euclidean(point, tmpPoint);
			System.out.println("original distance: " + tmpDistance 
					+ "\t##########\tmapped distance: " + temp);
			//if(tmpDistance == 0){
				System.out.println("ip: " + distancePoints.get(i).getIdenticalPairs()
						+ "**************" + distancePoints.get(i).getAlignedLength());
			//}
			if(Math.abs(tmpDistance - temp) > tmpDistance){
				distancePoints.remove(i);
				--size;
				--i;
			}
		}
		
		if(distancePoints.size() == 0)
			return false;
		else
			return true;
	}
	
	private static double stressCalculation(Vec3D point, List<DistancePoint> distancePoints){
		int size = distancePoints.size();
		double rawStress = 0;
		double temps = 0;

		for(int i =0; i < size; i++){
			double tmpDistance = distancePoints.get(i).getDistance();
			Point tmpPoint = distancePoints.get(i).getPoint();
			double temp = DistanceCalculation.Euclidean(point, tmpPoint);
			//if(i == 0)
			//	System.out.println("i: " + i + "###temp: " + temp + "###tmpDistance: " + tmpDistance);
			rawStress += (temp - tmpDistance) * (temp - tmpDistance);
			temps += tmpDistance * tmpDistance;
		}
		
		if(size > 0)
			rawStress = rawStress/temps;
		return rawStress;
	}

	private static Vec3D standardFindNewCoordinate(List<DistancePoint> distancePoints, Vec3D p_point, Vec3D average){
		int size = distancePoints.size();
		Vec3D point;
		double x = 0, y = 0, z = 0;
		for(int i = 0; i < size; i++){
			//delta(i,x) divided by d(i,z)
			double multiplier = 0;
			double tmpDistance = distancePoints.get(i).getDistance();
			Point tmpPoint = distancePoints.get(i).getPoint();
			//if(tmpDistance!=0){
				multiplier = tmpDistance
						/DistanceCalculation.Euclidean(p_point, 
								tmpPoint);
//			}
//			else{
//				//multiplier = 0.0000001;
//				point = new Vec3D(tmpPoint.getPosition().getX(),
//						tmpPoint.getPosition().getY(),
//						tmpPoint.getPosition().getZ());
//				return point;
//			}
			x += (p_point.getX() - tmpPoint.getPosition().getX()) * multiplier;
			y += (p_point.getY() - tmpPoint.getPosition().getY()) * multiplier;
			z += (p_point.getZ() - tmpPoint.getPosition().getZ()) * multiplier;
		}

		if(size > 0){
			x = x / (double)size + average.getX();
			y = y / (double)size + average.getY();
			z = z / (double)size + average.getZ();
		}

		point = new Vec3D(x, y, z);
		return point;
	}

	private static Vec3D averageCalculation(List<DistancePoint> distancePoints){
		Vec3D average = new Vec3D();
		double x = 0, y = 0, z = 0;
		for(int i =0; i < distancePoints.size(); i++){
			x += distancePoints.get(i).getPoint().getPosition().getX();
			y += distancePoints.get(i).getPoint().getPosition().getY();
			z += distancePoints.get(i).getPoint().getPosition().getZ();
		}
		if(distancePoints.size() > 0){
			average.setX(x/(double)distancePoints.size());
			average.setY(y/(double)distancePoints.size());
			average.setZ(z/(double)distancePoints.size());
		}

		return average;
	}

	private static List<DistancePoint> getFullDistancePointRow(
			SequencePoint outSamplePoint, List<SequencePoint> samplePoints, String type) throws Exception{
		List<DistancePoint> distancePoints = new ArrayList<DistancePoint>();

		for(int i = 0; i < samplePoints.size(); i++){
			double distance;
			DistancePoint p = null;
			if(type.equals("NW")){
				distance = DistanceCalculation.NWDistance
					(samplePoints.get(i).getSequence().toString(), 
							outSamplePoint.getSequence().toString());

				distancePoints.add(new DistancePoint(samplePoints.get(i).getPoint(), distance));
			}
			else if(type.equals(("SWG"))){
				//distance = DistanceCalculation.SWGDistanceTest(samplePoints.get(i).getSequence(), 
				//		outSamplePoint.getSequence());
				p = DistanceCalculation.SWGDistanceTest(samplePoints.get(i).getSequence(), 
								outSamplePoint.getSequence());
				if(p != null){
					p.setPoint(samplePoints.get(i).getPoint());
					distancePoints.add(p);
				}
		}
			else{
				System.err.println("Type is not right: " + type);
				throw new Exception();
			}
			//if(distance < 0 || distance > 1){
			//	System.out.println("Distance smaller than zero! " + distance);
			//	distance = 1;
			//}
			
		}
		return distancePoints;
	}

	private static List<DistancePoint> findkNearestNoDuplicates(
			List<DistancePoint> fullDistancePoints, int kValue){
		List<DistancePoint> nearestDistancePoints = new ArrayList<DistancePoint>();

		for(int i = 0; i<fullDistancePoints.size(); i++){
			if(i < kValue){
				nearestDistancePoints.add(fullDistancePoints.get(i));
			}
			else{
				if (i == kValue)
					Collections.sort(nearestDistancePoints);
				DistancePoint distancePoint = fullDistancePoints.get(i);
				if(distancePoint.getDistance() != 0 &&
						nearestDistancePoints.get(kValue - 1).getDistance()
						> distancePoint.getDistance()){
					for(int j = 0; j < kValue; j++){
						DistancePoint nearestDistancePoint = nearestDistancePoints.get(j);
						if(distancePoint.getDistance() < nearestDistancePoint.getDistance()){
							nearestDistancePoints.add(j, distancePoint);
							nearestDistancePoints.remove(kValue);
							break;
						}
					}
				}
			}
		}
		return nearestDistancePoints;
	}

	//Exact implementation of MI-MDS from Seung-Hee Bae
	public static List<Point> MIMDS(List<SequencePoint> samplePoints, 
			List<SequencePoint> inputOutSamplePoints, int kValue, String type, int offset) throws Exception{			
		int sampleSize = samplePoints.size();
		int outSampleSize = inputOutSamplePoints.size();

		List<Point> outputOutSamplePoints = new ArrayList<Point>();
		List<DistancePoint> nearestDistancePoints;

		for(int i = 0; i < outSampleSize; i++){
			if((i + 1) % 15 == 0)
				System.out.println((i + 1) * 100 / outSampleSize + "% done!");
			SequencePoint inputOutSamplePoint = inputOutSamplePoints.get(i);

			List<DistancePoint> distancePoints = 
					getFullDistancePointRow(inputOutSamplePoint, samplePoints, type);
			nearestDistancePoints = findkNearestNoDuplicates(distancePoints, kValue);

			Vec3D average = averageCalculation(nearestDistancePoints);
			Vec3D v_point = new Vec3D(average);

			double stressNew = -1;
			int count = 0;
			
			v_point = standardFindNewCoordinate(nearestDistancePoints, v_point, average);
			double stressOld = stressCalculation(v_point, nearestDistancePoints);
//			List<DistancePoint> backup = new ArrayList<DistancePoint>();
//			backup.addAll(nearestDistancePoints);
//			System.out.println("id: " + i + "before " + nearestDistancePoints.size());
//			if(!removeUnsignificant(v_point, nearestDistancePoints)){
//				//System.out.println("id: " + i);
//				nearestDistancePoints.addAll(backup);
//			}
//			System.out.println("id: " + i + "after " + nearestDistancePoints.size());
			
			while((stressOld - stressNew) > Constants.THRESHOLD
					&& count < 1000){
				stressOld = Math.abs(stressNew);
				//stressOld = stressCalculation(v_point, nearestDistancePoints);
				v_point = standardFindNewCoordinate(nearestDistancePoints, v_point, average);
				stressNew = stressCalculation(v_point, nearestDistancePoints);
				
				//if(i == 0 && count % 10 == 0)
				//	System.out.println(String.valueOf(offset + i + sampleSize) + "|||Before Stress Old: " + stressOld + " StressNew: " + stressNew);
				count++;
			}
			//if(i == 0)
			//	System.out.println("After Stress Old: " + stressOld + " StressNew: " + stressNew);
			Point point = new Point((offset + i + sampleSize), v_point, decideGroupNearest(distancePoints, v_point));
			inputOutSamplePoints.get(i).setPoint(point);
			outputOutSamplePoints.add(point);
		}
		return outputOutSamplePoints;
	}
	
	private static int decideGroupNearest(List<DistancePoint> distancePoints, Vec3D outSamplePosition){
		for(int i = 0; i < distancePoints.size(); ++i){
			distancePoints.get(i).setDistance(
					DistanceCalculation.Euclidean(
					distancePoints.get(i).getPoint().getPosition(), outSamplePosition));
		}
		Collections.sort(distancePoints);
		if(distancePoints.size() > 0)
			return distancePoints.get(0).getPoint().getGroup();
		else
			return 99999;
	} 
}

