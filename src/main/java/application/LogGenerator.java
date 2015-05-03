package application;

import application.ConfigurationUtility;
import application.StatisticsGenerator;

import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.VirtualMachine;

public class LogGenerator extends Thread{
	
	private VirtualMachine virtualMachine;
	private HostSystem vHost;
	
	@Override
	public void run() {
		StringBuffer buffer = new StringBuffer();
		while(true){
			
			try {
				buffer.append(StatisticsGenerator.generate(virtualMachine));
				buffer.append(StatisticsGenerator.generate(vHost));
				ConfigurationUtility.writeLog(buffer.toString());
				buffer.setLength(0);    //clearing buffer
				Thread.sleep(21000);     //21 sec waiting period as 
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
	}
	
	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}
	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	public HostSystem getvHost() {
		return vHost;
	}
	public void setvHost(HostSystem vHost) {
		this.vHost = vHost;
	}
	
	

}
