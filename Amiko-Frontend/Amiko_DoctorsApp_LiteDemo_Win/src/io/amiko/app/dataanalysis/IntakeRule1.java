package io.amiko.app.dataanalysis;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import io.amiko.app.doctorsapp.demo.ConfigLoader;
import io.amiko.app.doctorsapp.demo.RecordModel;

public class IntakeRule1 implements IRecordModelListener {

	private static final Logger logger = Logger.getLogger(IntakeRule1.class);
	
	private int sessionIndex = -1;
	private int minIntakeDuration;
	private int maxIntakeDuration;
	private int psEnterThershold;
	private int psExitThershold;
	private int minEventTime;
	private int maxEventTime;
	private int minWorkTimeRelativeToOpen;
	private int maxWorkTimeRelativeToClose;
	// 0 - WAITING state
	// 1 - OPEN event has been acquired
	// 2 - PROXIMITY event has been acquired
	// 3 - INTAKE PATTERN has been recognized
	// 4 - VALID INTAKE PATTERN
	private int status;
	private RecordModel openRec;
	private ArrayList<RecordModel> proximityRecs = new ArrayList<RecordModel>();
	private RecordModel closeRec;

	public IntakeRule1(int minIntakeDuration, int maxIntakeDuration, int psEnterThershold, int psExitThershold,
			int minEventTime, int maxEventTime, int minWorkTimeRelativeToOpen, int maxWorkTimeRelativeToClose) {
		this.status = 0;
		this.minIntakeDuration = minIntakeDuration;
		this.maxIntakeDuration = maxIntakeDuration;
		this.psEnterThershold = psEnterThershold;
		this.psExitThershold = psExitThershold;
		this.minEventTime = minEventTime;
		this.maxEventTime = maxEventTime;
		this.minWorkTimeRelativeToOpen = minWorkTimeRelativeToOpen;
		this.maxWorkTimeRelativeToClose = maxWorkTimeRelativeToClose;
	}
	
	public IntakeRule1(){
		this.status = 0;					
		
		this.minIntakeDuration = 4;
		String valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.minopencloseduration");
		if(valueStr!=null){
			try{
				this.minIntakeDuration = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){				
				logger.error("dataanalysis.rules.intakerule1.minopencloseduration is not valid: " + valueStr);
			}
		}
		
		this.maxIntakeDuration = 120;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.maxopencloseduration");
		if(valueStr!=null){
			try{
				this.maxIntakeDuration = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.maxopencloseduration is not valid: " + valueStr);
			}
		}
		
		this.psEnterThershold = 2200;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.enterthershold");
		if(valueStr!=null){
			try{
				this.psEnterThershold = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.enterthershold is not valid: " + valueStr);
			}
		}
		
		this.psExitThershold = 2200;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.exitthershold");
		if(valueStr!=null){
			try{
				this.psExitThershold = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.exitthershold is not valid: " + valueStr);
			}
		}
		
		this.minEventTime = 13;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.minproximityeventtime");
		if(valueStr!=null){
			try{
				this.minEventTime = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.minproximityeventtime is not valid: " + valueStr);
			}
		}
		
		this.maxEventTime = 95;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.maxproximityeventtime");
		if(valueStr!=null){
			try{
				this.maxEventTime = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.maxproximityeventtime is not valid: " + valueStr);
			}
		}
		
		this.minWorkTimeRelativeToOpen = 3;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.minworktimerelativetoopen");
		if(valueStr!=null){
			try{
				this.minWorkTimeRelativeToOpen = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.minworktimerelativetoopen is not valid: " + valueStr);
			}
		}
		
		this.maxWorkTimeRelativeToClose = 100;
		valueStr = ConfigLoader.getInstance().getProperty("dataanalysis.rules.intakerule1.maxworktimerelativetoopen");
		if(valueStr!=null){
			try{
				this.maxWorkTimeRelativeToClose = Integer.parseInt(valueStr);
			}catch(NumberFormatException e){
				logger.error("dataanalysis.rules.intakerule1.maxworktimerelativetoopen is not valid: " + valueStr);
			}
		}
	}

	@Override
	/**
	 * Generated RecordModel has these properties:
	 * INDEX: a negative index used to distinguish from all other sensor RecordModel
	 * DATA0 = 0
	 * DATA1 = PROXIMITY.wrk_time
	 * DATA2 = 0
	 * DATA3 = 0
	 * DATA4 = 0
	 * DATA5 = 0 (low pif)
	 * CUSTOM EVENT ID = "INTAKE EVENT"
	 * TIMESTAMP = PROXIMITY TIMESTAMP
	 */
	public RecordModel onNewRecordModel(RecordModel rec) {
		RecordModel recResp = null;
		if (rec.getEventId() == 0x13) {
			// OPEN RECORD MODEL
			if (this.status == 0 || this.status == 1 || this.status == 3 || this.status == 4) {
				this.status = 1;
				this.openRec = rec;
			} else {
				this.status = 0;
				this.proximityRecs.clear();
			}
		} else if (rec.getEventId() == 0x14) {
			// CLOSE RECOR DMODEL
			if (this.status == 2) {
				this.status = 3;// INTAKE PATTERN recognized
				this.closeRec = rec;
				// start pattern matching evaluation
				recResp = validPattern();
				if (recResp != null) {
					this.status = 4;// VALID INTAKE PATTERN
				} else {
					this.status = 0;
					this.proximityRecs.clear();
				}
			} else {
				this.status = 0;
				this.proximityRecs.clear();
			}
		} else if (rec.getEventId() == 0x71) {
			// PROXIMITY RECORD MODEL
			if (this.status == 1 || this.status == 2) {
				this.status = 2;
				this.proximityRecs.add(rec);
			} else {
				this.status = 0;
				this.proximityRecs.clear();
			}
		} else if(rec.getEventId() == 0x72){
			if (this.status == 1 || this.status == 2) {
				this.status = 0;
				this.proximityRecs.clear();
			}
		}

		return recResp;
	}

	private RecordModel validPattern(){
		RecordModel resp = null;
		int openWorkTime = this.openRec.getData0();
		int closeWorkTime = this.closeRec.getData0();
		int deltaTime = closeWorkTime - openWorkTime;
		if(deltaTime<=this.maxIntakeDuration && deltaTime>=this.minIntakeDuration){
			// start checking for all PROXIMITY EVENT			
			for(RecordModel proximityRec:this.proximityRecs){				
				int psEnter = proximityRec.getData2();
				int psExit = proximityRec.getData3();
				int eventTime = proximityRec.getData1();
				int workTime = proximityRec.getData0();
				if(
					psEnter < this.psEnterThershold &&
					psExit < this.psExitThershold &&
					eventTime > this.minEventTime &&
					eventTime < this.maxEventTime &&
					(workTime-openWorkTime) > this.minWorkTimeRelativeToOpen &&
					(workTime-openWorkTime) < this.maxWorkTimeRelativeToClose){
					RecordModel rec = new RecordModel();
					rec.setIndex(sessionIndex);// auto-generated RecordModel
					--sessionIndex;
					rec.setData0(0);
					rec.setData1(proximityRec.getData1());
					rec.setData2(0);
					rec.setData3(0);
					rec.setData4(0);
					rec.setData5(0);
					rec.setCustomEventId("INTAKE EVENT");
					rec.setTimestamp(proximityRec.getTimestamp());					
					resp = rec;							
					break;
				}				
			}			
		}
		return resp;
	}
	
	public void resetRuleStatus(){
		this.status = 0;
		this.proximityRecs.clear();
		this.sessionIndex = -1;
	}

}
