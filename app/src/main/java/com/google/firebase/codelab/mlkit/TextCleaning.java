package com.google.firebase.codelab.mlkit;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TextCleaning {
	
	public static class IndexComparator<T extends Comparable<? super T>> implements Comparator<Integer> {
	    private final T[] array;
	    public IndexComparator(T[] array) {
	        this.array = array;
	    }
	    public Integer[] createIndexArray() {
	        Integer[] indices = new Integer[this.array.length];
	        for (int i = 0; i < this.array.length; ++i) {
	            indices[i] = i;
	        }
	        return indices;
	    }
	    @Override
	    public int compare(Integer index1, Integer index2) {
	        return this.array[index1].compareTo(this.array[index2]);
	    }
	}
	
	public static class TwoLists<T> {
		public final List<T> first;
		public final List<T> second;
		public TwoLists(List<T> first, List<T> second) {
			this.first = first;
			this.second = second;
		}
	}
	
	public static List<MenuItem> itemsFromText(FirebaseVisionText.Line[] lineArr) {
		return null;
	}
	
	public static TwoLists<FirebaseVisionText.Line> titlesAndDescriptionsFromText(FirebaseVisionText.Line[] lineArr) {
		List<FirebaseVisionText.Line> titles = new LinkedList<FirebaseVisionText.Line>();
		List<FirebaseVisionText.Line> descriptions = new LinkedList<FirebaseVisionText.Line>();
		final double MID = dividingThickness(lineArr);
		for(int i = 0; i < lineArr.length; ++i) {
			if(thickness(lineArr[i]) > MID) {
				titles.add(lineArr[i]);
			} else {
				descriptions.add(lineArr[i]);
			}
		}
		return new TwoLists<FirebaseVisionText.Line>(titles, descriptions);
	}
	
	public static double dividingThickness(FirebaseVisionText.Line[] lineArr) {
		double[] thicknessArr = new double[lineArr.length];
		for(int i = 0; i < lineArr.length; ++i) {
			thicknessArr[i] = thickness(lineArr[i]);
		}
		double[] sThicknessArr = thicknessArr.clone();
		Arrays.sort(sThicknessArr);
		Double[] distanceArr = new Double[thicknessArr.length - 1];
		for(int i = 0; i < distanceArr.length; ++i) {
			distanceArr[i] = sThicknessArr[i + 1] - sThicknessArr[i];
		}
		Double[] sDistanceArr = distanceArr.clone();
		IndexComparator<Double> comp = new IndexComparator<Double>(sDistanceArr);
		Integer[] sDistanceArrInd = comp.createIndexArray();
		Arrays.sort(sDistanceArrInd, comp);
		System.out.println("here");
		Arrays.sort(sDistanceArr);
		int searchSize = (sDistanceArr.length / 10) + 1;
		double lowestScore = Double.MAX_VALUE;
		int bestLocation = -1;
		for(int i = 0; i < searchSize; ++i) {
			double closeness = 1.0 / sDistanceArr[(sDistanceArr.length - 1) - i];
			int location = sDistanceArrInd[(sDistanceArr.length - 1) - i];
			double unbalance = (location - (sDistanceArr.length / 2)) 
					/ ((double) sDistanceArr.length);
			if(0 > unbalance)
				unbalance *= -1;
			double score = (1 * closeness) + (1 * unbalance);
			if(score < lowestScore) {
				lowestScore = score;
				bestLocation = location;
			}
		}
		return (sThicknessArr[bestLocation] + sThicknessArr[bestLocation + 1]) / 2;
	}
	
	private static double thickness(FirebaseVisionText.Line line) {
		return ((double) line.getBoundingBox().width()) / line.getText().length();
	}
	
}
