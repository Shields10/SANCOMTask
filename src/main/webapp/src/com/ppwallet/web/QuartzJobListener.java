package com.ppwallet.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.jobs.QuartzJob;

/**
 * Application Lifecycle Listener implementation class QuartzJobListener
 *
 */
@WebListener
public class QuartzJobListener implements ServletContextListener {
	private  static String className = QuartzJobListener.class.getSimpleName();

	private Scheduler scheduler;
    /**
     * Default constructor. 
     */
    public QuartzJobListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent ctx)  { 
		

		try {
			if(PPWalletEnvironment.getInstance()==null) {
				PPWalletEnvironment.init();
				//PPWalletEnvironment.setComment(3,className,"===> PPWalletEnvironment is initialized......");
			}
			
		} catch (Exception e1) {
			System.out.println("CRITICAL ERROR : Failed to initialize PPWalletEnvironment "+e1.getMessage());		
		}	
		PPWalletEnvironment.setComment(3,className,"============== starting :" +className + " at "+java.time.LocalTime.now());
/*		This job is to repeat forever every 5 seconds, hence to reduce the load to the system update the interval accordingly. 
 * 		Use Cron Job instead to schedule at a specific time of the day/week/month
*/
		int jobIntervalInSeconds = 1*60*60; // every 1 hour
		JobDetail job = JobBuilder.newJob(QuartzJob.class)
	        .withIdentity("dummyJobName", "group1").build();
	
	        Trigger trigger = TriggerBuilder
	        .newTrigger()
	        .withIdentity("dummyTriggerName", "group1")
	        .withSchedule(
	            SimpleScheduleBuilder.simpleSchedule()
	                .withIntervalInSeconds(jobIntervalInSeconds).repeatForever())
	        .build();
	        
	        /*
	         *     	Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("dummyTriggerName", "group1")
					.withSchedule(
						CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
					.build();
	        */
	        try{
	        	//Thread.sleep(5000);// delay for 5 seconds to run the job
	           // PPWalletEnvironment.setComment(3,className,"============== starting the Job :" +className + " at "+java.time.LocalTime.now());

	            scheduler = ((StdSchedulerFactory) ctx.getServletContext().getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY)).getScheduler();
	            scheduler.scheduleJob(job, trigger); 
/*		schedule as many jobs here as you can depending on the requirements. Create multiple JodDetail classes and call the scheduler 
 * 		to schedule as many jobs here as possible.         
	            
*/	        }catch(Exception  e){
	        	System.out.println("CRITICAL ERROR : Failed to initialize PPWalletEnvironment "+e.getMessage());	
	        	PPWalletEnvironment.setComment(1,className,"Error is "+e.getMessage());
	        }   
        }
	
}
