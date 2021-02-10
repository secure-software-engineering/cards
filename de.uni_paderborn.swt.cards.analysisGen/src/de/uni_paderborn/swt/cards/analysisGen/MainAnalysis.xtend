package de.uni_paderborn.swt.cards.analysisGen

import java.util.List

class MainAnalysis {
	static def generate(String packageName, List<String> classNames) {
		'''
			package «packageName»;
			
			public class MainAnalysis {
				
				public static void main(String[] args) {
					«FOR clazz : classNames»
						«clazz».main(null);
					«ENDFOR»
				}
			}
		'''
	}
}