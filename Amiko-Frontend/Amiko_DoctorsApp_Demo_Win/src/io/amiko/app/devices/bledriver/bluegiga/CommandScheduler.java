package io.amiko.app.devices.bledriver.bluegiga;

import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import io.amiko.app.devices.AmikoCmd;
import io.amiko.app.devices.bledriver.BLEDriverListener;
import io.amiko.app.doctorsapp.demo.ConfigLoader;

/**
 * Il thread associato a questo scheduler è impegnato fino alla scrittura sullo
 * stream di output direto al BlueGiga BLED112 Dongle.
 * 
 * <ul>
 * <li>1) Driver.writeCmd()</li>
 * <li>1.1) BGAPI.send_attclient_attribute_write()</li>
 * <li>1.1.1) BGAPITransport.sendPacket()</li>
 * </ul>
 * @author Admin
 *
 */
public class CommandScheduler implements Runnable {

	//TODO da documentare
	private static final Logger logger = Logger.getLogger(CommandScheduler.class);  
	
	private Vector<AmikoCmd> commandQueue = new Vector<AmikoCmd>();
	private BluegigaBLEDriver driver;
	private boolean amIWaiting = false;
	private long waitingStartTime = -1;
	private boolean quit = false;
	private AmikoCmd lastSentCmd = null;
	private int numRetries = 0;
	private long sleepTimeMsec = 100;
	private long maxWaitingTime = 2000;
	private long maxRetries = 3; 

	public CommandScheduler(BluegigaBLEDriver driver, BLEDriverListener listener) {
		this.driver = driver;
		
		String sleepTimeMsecStr = ConfigLoader.getInstance().getProperty("commandscheduler.sleep.time.msec");
		if(sleepTimeMsecStr!=null){
			try{
				this.sleepTimeMsec = Integer.parseInt(sleepTimeMsecStr);
			}catch(NumberFormatException e){
				logger.error("config.properties: commandscheduler.sleep.time.msec is non valid: " + sleepTimeMsecStr);
			}
		}

		String maxWaitingTimeStr = ConfigLoader.getInstance().getProperty("commandscheduler.max.waiting.time");
		if(maxWaitingTimeStr!=null){
			try{
				this.maxWaitingTime = Integer.parseInt(maxWaitingTimeStr);
			}catch(NumberFormatException e){
				logger.error("config.properties: commandscheduler.max.waiting.time is non valid: " + maxWaitingTimeStr);
			}
		}

		String maxRetriesStr = ConfigLoader.getInstance().getProperty("commandscheduler.max.retries");
		if(maxRetriesStr!=null){
			try{
				this.maxRetries = Integer.parseInt(maxRetriesStr);
			}catch(NumberFormatException e){
				logger.error("config.properties: commandscheduler.max.retries is non valid: " + maxRetriesStr);
			}
		}
	}

	@Override
	public void run() {		
		while (!quit) {
			Date date = new Date();
			if (amIWaiting) {
				if(date.getTime()-this.waitingStartTime>maxWaitingTime){
					if(this.numRetries==maxRetries){
						logger.error("[RESPONSE]-Response timeout - no response for command=" + this.lastSentCmd);					
						logger.error("[RESPONSE]-COMPLETED");
						amIWaiting = false;		
						this.numRetries = 0;
					}else{
						driver.writeCmd(this.lastSentCmd);
						++this.numRetries;
						logger.debug("[SEND-REQUEST]-Command has been sent again retry number=" + this.numRetries);
					}
				}
				try {
					Thread.currentThread().sleep(sleepTimeMsec);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				synchronized (this.commandQueue) {
					if (commandQueue.size() > 0) {
						// extract the next command (first node from the queue)
						AmikoCmd cmd = commandQueue.remove(0);
						logger.debug("[SEND-REQUEST]-Command has been extracted from the queue[" + commandQueue.size()+ "]");
						driver.writeCmd(cmd);
						this.lastSentCmd = cmd;
						this.amIWaiting = true;					
						this.waitingStartTime = date.getTime();
					}					
				}
			}
		}
	}

	/**
	 * Add a new Amiko command into the commands queue Scheduler thread will
	 * extracts the first command from that queue and sends it to the BLE stack
	 * 
	 * @param cmd
	 */
	public void pushCommand(AmikoCmd cmd) {
		synchronized (this.commandQueue) {
			this.commandQueue.addElement(cmd);			
		}
	}

	public void notifyResponse() {
		this.amIWaiting = false;
	}

	// TODO ricordarsi di invocare il metodo per interrompere il thread
	public void stop() {
		this.quit = true;
	}
	
	public void cleanCommandsQueue(){
		synchronized (this.commandQueue) {
			this.commandQueue.clear();
		}
	}

}
