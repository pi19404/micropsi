package org.micropsi.nodenet.modules.khepera;

public class MakeLowPassFilter {
	
	double [] remeber;
	
	
	
	public MakeLowPassFilter(int steps){
		this.remeber= new double [steps];
	}
	
	public void update(double act){
		for(int i=(this.remeber.length-1);i>=1;i--){
			this.remeber[i]=this.remeber[i-1];
		}
		this.remeber[0]=act;
	}
	
	public boolean difference(){
		double [] diff =new double [(this.remeber.length-1)];
		int count=0;
		boolean negative=false;
		
		for(int i=0;i<(this.remeber.length-1);i++){
			diff[i]=this.remeber[i]-this.remeber[i+1];
			if(diff[i]<0){
				count++;
			}	
		}
		if(count==(this.remeber.length-1))
			negative=true;
		
		return negative;
	}
	//Loke if all entries in the buffer is negative
	
	public boolean negative(){
		boolean allnegative=false;
		int count=0;
		
		for(int i=0;i<this.remeber.length;i++){
			if(this.remeber[i]<0)
				count++;
		}
		if(count==this.remeber.length)
			allnegative=true;
		
		return allnegative;
	}
	
	
	public double getEntry(int index){
		return this.remeber[index];
	}
	
	
	public void clean(){
		
		for(int i=0;i<this.remeber.length;i++){
			this.remeber[i]=0;
		}
	}
	
	
	public double euclidian(){
		
		return Math.pow(this.remeber[0]-this.remeber[1],2);
		
	}
	
	
	public void copyBuffer(){
		this.remeber[0]=this.remeber[1];
	}
	
	public boolean looksame(){
		int count=0;
		for(int i=0;i<this.remeber.length-1;i++){
			if(this.remeber[i]==this.remeber[i+1]){
				count++;
			}
		}
		if(count==this.remeber.length-1)
			return true;
		else
			return false;		
		
	}
	
	
	

}
