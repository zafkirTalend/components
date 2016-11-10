package org.talend.components.stopwatch;

public class StopWatch {
    
    private static StopWatch instance;
    
    private int stageNumber;
    
    private long[] stageDurations;
    
    private long[] stageStartTimes;
    
    private boolean[] stageStarted;
    
    private StopWatch(int stageNumber) {
        this.stageNumber = stageNumber;
        stageDurations = new long[stageNumber];
        stageStartTimes = new long[stageNumber];
        stageStarted = new boolean[stageNumber];
        for (int i=0; i<stageNumber; i++) {
            stageDurations[i] = 0;
            stageStartTimes[i] = 0;
            stageStarted[i] = false;
        }
    }
    
    public static StopWatch getInstance(int stageNumber) {
        if (instance == null) {
            instance = new StopWatch(stageNumber);
        }
        return instance;
    }
    
    public void startStageHere(int stageIndex) {
//        if (stageIndex < 0 || stageIndex > stageNumber) {
//            throw new IllegalArgumentException("wrong stage index");
//        }
     
//        if (stageStarted[stageIndex]) {
//            throw new IllegalStateException("stage " + stageIndex + "already started");
//        } else {
            stageStartTimes[stageIndex] = System.nanoTime();
            stageStarted[stageIndex] = true;
//        }
    }
    
    public void finishStageHere(int stageIndex) {
//        if (stageIndex < 0 || stageIndex > stageNumber) {
//            throw new IllegalArgumentException("wrong stage index");
//        }
        
//        if (stageStarted[stageIndex]) {
            long currentTime = System.nanoTime();
            long duration = currentTime - stageStartTimes[stageIndex];
            stageDurations[stageIndex] = stageDurations[stageIndex] + duration;
            stageStarted[stageIndex] = false;
//        } else {
//            throw new IllegalStateException("stage " + stageIndex + "is not started");
//        }
    }
    
    public void showResults() {
        for (int i = 0; i < stageNumber; i++) {
            StringBuilder sb = new StringBuilder("Stage ");
            sb.append(i);
            if(stageStarted[i]) {
                sb.append(" not finished");
            } else {
                sb.append(" finished with duration ");
                double duration = stageDurations[i];
                sb.append(duration / 1000000000.0);
                sb.append(" sec");
            }
            System.out.println(sb.toString());
        }
    }

}
