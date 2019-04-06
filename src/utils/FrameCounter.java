package utils;



import java.util.TimerTask;

public class FrameCounter extends TimerTask {
	public  int fps = 0;
	public int fpstemp = 0;
	
	public void newframe() {fpstemp++;}
	
	public void run() {
		fps = fpstemp;
		fpstemp=0;
	}

}
