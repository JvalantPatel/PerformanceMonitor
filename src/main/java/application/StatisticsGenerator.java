package application;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricSeriesCSV;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;

public class StatisticsGenerator {
	
	private static HashMap<Integer, PerfCounterInfo> countersInfoMap;
	private static HashMap<String, Integer> countersMap;
	private static  PerfMetricId[] perfMetricIds;
	private static StringBuffer buffer;
	
	public static String generate(ManagedEntity managedEntity) throws FileNotFoundException, RuntimeFault, RemoteException{
		PerfProviderSummary pps = ConfigurationUtility.getPerformanceManager().queryPerfProviderSummary(managedEntity);
		int refreshRate = pps.getRefreshRate().intValue();
		// only return the latest one sample
		PerfQuerySpec qSpec = createPerfQuerySpec(managedEntity, 1, refreshRate);

		PerfEntityMetricBase[] pValues = null;
		try {
			pValues = ConfigurationUtility.getPerformanceManager()
					.queryPerf(new PerfQuerySpec[] { qSpec });
		} catch (RuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pValues != null) {
			displayValues(pValues,managedEntity);
		}
		return buffer.toString();
	}
	
	public static void init(){
		
		PerfCounterInfo[] perfCounterInfos = ConfigurationUtility.getPerformanceManager().getPerfCounter();
		buffer = new StringBuffer();
		countersMap = new HashMap<String, Integer>();
		countersInfoMap = new HashMap<Integer, PerfCounterInfo>();
		for (int i = 0; i < perfCounterInfos.length; i++) {
			countersInfoMap.put(perfCounterInfos[i].getKey(), perfCounterInfos[i]);
			countersMap.put(
					perfCounterInfos[i].getGroupInfo().getKey() + "."
							+ perfCounterInfos[i].getNameInfo().getKey() + "."
							+ perfCounterInfos[i].getRollupType(), perfCounterInfos[i].getKey());
		}

		perfMetricIds = createPerfMetricId(ConfigurationUtility.getPerformanceCounters());
	}
	
	private static PerfMetricId[] createPerfMetricId(String[] counters) {
		PerfMetricId[] metricIds = new PerfMetricId[counters.length];
		for (int i = 0; i < counters.length; i++) {
			PerfMetricId metricId = new PerfMetricId();
			metricId.setCounterId(countersMap.get(counters[i]));
			metricId.setInstance("*");
			metricIds[i] = metricId;
		}
		return metricIds;
	}
	
	private static void displayValues(PerfEntityMetricBase[] values,ManagedEntity managedEntity)
			throws FileNotFoundException {
		for (int i = 0; i < values.length; ++i) {
			printPerfMetricCSV((PerfEntityMetricCSV) values[i],managedEntity);
		}
	}

	private static void printPerfMetricCSV(PerfEntityMetricCSV pem,ManagedEntity managedEntity)
			throws FileNotFoundException {

		try {
			PerfMetricSeriesCSV[] csvs = pem.getValue();
			HashMap<Integer, PerfMetricSeriesCSV> stats = new HashMap<Integer, PerfMetricSeriesCSV>();

			for (int i = 0; i < csvs.length; i++) {
				stats.put(csvs[i].getId().getCounterId(), csvs[i]);
			}
			buffer.setLength(0);
			buffer.append(managedEntity.getName()).append(",");
			buffer.append(pem.getSampleInfoCSV()).append(",");
			for (String counter : ConfigurationUtility.getPerformanceCounters()) {
				Integer counterId = countersMap.get(counter);
				String value = null;
				System.out.println("Counter id: " + counterId);
				if (stats.containsKey(counterId))
					value = stats.get(counterId).getValue();
				if (value == null || Integer.parseInt(value) < 0
						|| value.length() == 0) {
					value = "0";
				}

				buffer.append(value).append(",");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append(System.getProperty("line.separator"));
			
		} catch (Exception e) {

		}
	}

	synchronized private static PerfQuerySpec createPerfQuerySpec(ManagedEntity me,
			int maxSample, int interval) {

		PerfQuerySpec qSpec = new PerfQuerySpec();
		qSpec.setEntity(me.getMOR());
		// set the maximum of metrics to be return
		qSpec.setMaxSample(new Integer(maxSample));
		qSpec.setMetricId(perfMetricIds);
		qSpec.setFormat("csv");
		qSpec.setIntervalId(new Integer(interval));
		return qSpec;
	}

}
