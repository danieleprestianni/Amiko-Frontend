package io.amiko.app.doctorsapp.demo;

import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;

/**
 *
 * @author Daniele Prestianni
 * @version 2.0
 */
public class RecordModel {

	/**
	 * Index of the record; Its value is -1 in case of auto-generated record
	 * (record produced by data analysis)
	 */
	private int index;
	private long timestamp;
	private int eventId; // 1 byte
	/**
	 * Utilizzato per specificare il nome di un META-EVENTO autogenerato dal
	 * sistema a seguito di analisi dei dati
	 */
	private String customEventId = null;
	private int data0, data1, data2, data3, data4, data5;

	public int getIndex() {
		return index;
	}

	/**
	 * 
	 * @return Empty string if index is -1, otherwise it returns the index value
	 *         as a string
	 */
	public String getIndexH() {
		if (this.index == -1) {
			return "";
		} else {
			return index + "";
		}
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getTimestampH() {
		Date d = new Date();
		d.setTime(this.timestamp * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatter.format(d);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getEventId() {
		return eventId;
	}

	public String getEventIdH() {
		String ret = "<undefined>";
		if(this.customEventId!=null){
			ret = this.getCustomEventId();
		}else{
			int eventIdInt = this.eventId;
			switch (eventIdInt) {
			case 0x00:
				ret = "GNR IDLE";
				break;
			case 0x10:
				ret = "SYS INIT";
				break;
			case 0x11:
				ret = "SYS WAKE UP";
				break;
			case 0x12:
				ret = "SYS SLEEP";
				break;
			case 0x13:
				ret = "SYS OPEN";
				break;
			case 0x14:
				ret = "SYS CLOSE";
				break;
			case 0x15:
				ret = "SYS INSERT";
				break;
			case 0x16:
				ret = "SYS REMOVE";
				break;
			case 0x17:
				ret = "SYS CONNECT";
				break;
			case 0x70:
				ret = "PRO VIBRATION";
				break;
			case 0x71:
				ret = "PRO PROXIMITY";
				break;
			case 0x72:
				ret = "PRO BREATH";
				break;
			}	
		}		
		return ret;
	}

	public void setEventId(int eventType) {
		this.eventId = eventType;
	}

	public int getData0() {
		return data0;
	}

	public void setData0(int data0) {
		this.data0 = data0;
	}

	public int getData1() {
		return data1;
	}

	public void setData1(int data1) {
		this.data1 = data1;
	}

	public int getData2() {
		return data2;
	}

	public void setData2(int data2) {
		this.data2 = data2;
	}

	public int getData3() {
		return data3;
	}

	public void setData3(int data3) {
		this.data3 = data3;
	}

	public int getData4() {
		return data4;
	}

	public void setData4(int data4) {
		this.data4 = data4;
	}

	public int getData5() {
		return data5;
	}

	public void setData5(int data5) {
		this.data5 = data5;
	}

	public String getCustomEventId() {
		return customEventId;
	}

	public void setCustomEventId(String customEventId) {
		this.customEventId = customEventId;
	}
	
	

}
