package de.uni_paderborn.swt.cards.analysisGen

class ConsoleResultListener {
	static def generate(String packageName) {
		'''
		package «packageName»;		
		
		public class ConsoleResultListener {
			
			public static void main(String[] args) {
				
				System.out.println("Analysis not yet implemented");
				
			}

		}
		'''
	}
}