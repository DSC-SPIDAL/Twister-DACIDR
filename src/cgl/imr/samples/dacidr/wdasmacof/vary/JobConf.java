package cgl.imr.samples.dacidr.wdasmacof.vary;

import java.util.Properties;

public class JobConf {
    private String jobID;
    private Properties props;
    private int numMapTasks;
    private int numReduceTasks;

    public JobConf() {
        props = new Properties();
    }

    public JobConf(String jobID) {
        this.jobID = jobID;
    }

    public void addProperty(String key, String value){
        props.setProperty(key, value);
    }

    public String getProperty(String key){
        return props.getProperty(key);
    }

    public void setNumMapTasks(int numMapTasks) {
        this.numMapTasks = numMapTasks;
    }

    public int getNumMapTasks() {
        return numMapTasks;
    }

    public void setNumReduceTasks(int numReduceTasks) {
        this.numReduceTasks = numReduceTasks;
    }

    public int getNumReduceTasks() {
        return numReduceTasks;
    }


}
