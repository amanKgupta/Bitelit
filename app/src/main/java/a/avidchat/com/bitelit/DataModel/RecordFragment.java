package a.avidchat.com.bitelit.DataModel;

public class RecordFragment {
    private long startTimestamp;
    private long endTimestamp;

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public long getDuration() {
        return endTimestamp - startTimestamp;
    }
}
