package com.ppwallet.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ppwallet.PPWalletEnvironment;

public class QuartzJob implements Job{
	private static String className = QuartzJob.class.getSimpleName();
	private static boolean isInstance = false;
	
    /**
     * Default constructor. 
     */
    public QuartzJob() {
    	//PPWalletEnvironment.setComment(3,className,"============== inside the QuartzJob :" +className + " at "+java.time.LocalTime.now());
    	if(isInstance == false) {
    		PPWalletEnvironment.setComment(3,className,"============== inside the QuartzJob First Time:" +className + " at "+java.time.LocalTime.now()+"isInstance "+isInstance);
	    	try {
				Thread.sleep(1000);
				isInstance = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// delay for 5 seconds to run the job
    	}
        //PPWalletEnvironment.setComment(3,className,"============== starting the Job :" +className + " at "+java.time.LocalTime.now());
    }
    @Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//PPWalletEnvironment.setComment(3, className,"inside the execute method....job started=======> isInstance is "+isInstance);
		//System.out.println("============ "+classname+" Hello....job started at "+java.time.LocalTime.now());
/*
		Here write the detail batch processing with the calling of Dao classes for database read/write	
*/	}

}
