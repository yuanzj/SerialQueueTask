package net.rokyinfo.task;


/**
 * Created by YuanZhiJian on 16/4/3.
 */
public abstract class RKTask implements Comparable<RKTask>{

    private String mIdentifier;

    private Object mTag;

    private Integer mSequence;

    private boolean mCanceled = false;

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public void setTag(Object tag){
        mTag = tag;
    }

    public Object getTag(){
        return mTag;
    }

    public boolean isCanceled(){
        return mCanceled;
    }

    public void cancel(){
        mCanceled = true;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String mIdentifier) {
        this.mIdentifier = mIdentifier;
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public final RKTask setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    /**
     * Returns the sequence number of this request.
     */
    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    @Override
    public int compareTo(RKTask other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    public abstract void execute();

}
