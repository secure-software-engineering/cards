package de.uni_paderborn.swt.cards.dsl.analyzer.data;

import java.util.ArrayList;
import java.util.List;

import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Sanitizer;

public class DataPoint {
	private DataType type;
	private List<Sanitizer> sanitizers;
	
	public DataPoint(DataType type, Sanitizer sanitizer){
		super();
		this.type = type;
		this.sanitizers = new ArrayList<Sanitizer>();
		if (sanitizer != null) 
			this.sanitizers.add(sanitizer);
	}
		
	@Override
	public String toString() {
		String s = "";
		s += "DataType " + type.getName() + "; ";
		if (!sanitizers.isEmpty()) {
			s+= "sanitized by {";
			for (Sanitizer san : sanitizers) {
				s+= san.getName()+ "; ";
			}
			s+= "}";
		}
		
		return s;
	}
	
	@Override
	public int hashCode() {
		int r = 0;
		r += type.hashCode();
		
		for (Sanitizer san : this.getSanitizers())
			r += san.hashCode();
		
		return r;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		
		DataPoint dp = (DataPoint) obj;
		boolean result = true;
		
		result &= dp.getType().equals(this.getType());
		result &= dp.getSanitizers().containsAll(this.getSanitizers());
		result &= this.getSanitizers().containsAll(dp.getSanitizers());
		
		return result;
	}
	
	public DataType getType() {
		return type;
	}
	
	public void setType(DataType type) {
		this.type = type;
	}
	
	public List<Sanitizer> getSanitizers() {
		return sanitizers;
	}

}