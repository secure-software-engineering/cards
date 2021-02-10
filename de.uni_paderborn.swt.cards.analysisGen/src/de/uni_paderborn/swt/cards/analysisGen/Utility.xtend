package de.uni_paderborn.swt.cards.analysisGen

class Utility {
	static def generate(String packageName) {
		'''
		package «packageName»;		
		
		public class Utility {
			
			public static void main(String[] args) {
				
				System.out.println("Analysis not yet implemented");
				
			}

		}
		'''
	}
}