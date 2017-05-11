package com.github.axet.wget.info;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * DownloadInfo class. Keep part information. We need to serialize this class between application restart. Thread safe.
 * 
 * @author axet
 * 
 */
@XStreamAlias("DownloadInfo")
public class DownloadInfo extends URLInfo {
    private static final long serialVersionUID = 1952592930771906713L;

    public static long PART_LENGTH = 10 * 1024 * 1024;

    @XStreamAlias("DownloadInfoPart")
    public static class Part {
        /**
         * Notify States
         */
        public enum States {
            QUEUED, DOWNLOADING, RETRYING, ERROR, STOP, DONE;
        }

        /**
         * start offset [start, end]
         */
        private long start;
        /**
         * end offset [start, end]
         */
        private long end;
        /**
         * part number
         */
        private long number;
        /**
         * number of bytes we are downloaded
         */
        private long count;

        /**
         * download state
         */
        private States state;
        /**
         * downloading error / retry error
         */
        private Throwable exception;
        /**
         * retrying delay;
         */
        private int delay;
        /**
         * retry count
         */
        private int retry;

        public Part() {
        }

        public Part(Part copy) {
            this.start = copy.start;
            this.end = copy.end;
            this.number = copy.number;
            this.count = copy.count;
            this.state = copy.state;
            this.exception = copy.exception;
            this.delay = copy.delay;
            this.retry = copy.retry;
        }

        synchronized public long getStart() {
            return start;
        }

        synchronized public void setStart(long start) {
            this.start = start;
        }

        synchronized public long getEnd() {
            return end;
        }

        synchronized public void setEnd(long end) {
            this.end = end;
        }

        synchronized public long getNumber() {
            return number;
        }

        synchronized public void setNumber(long number) {
            this.number = number;
        }

        synchronized public long getLength() {
            return end - start + 1;
        }

        synchronized public long getCount() {
            return count;
        }

        synchronized public void setCount(long count) {
            this.count = count;
        }

        synchronized public States getState() {
            return state;
        }

        synchronized public void setState(States state) {
            this.state = state;
            this.exception = null;
        }

        synchronized public void setState(States state, Throwable e) {
            this.state = state;
            this.exception = e;
        }

        synchronized public Throwable getException() {
            return exception;
        }

        synchronized public void setException(Throwable exception) {
            this.exception = exception;
        }

        synchronized public int getDelay() {
            return delay;
        }

        synchronized public void setDelay(int delay, Throwable e) {
            this.state = States.RETRYING;
            this.delay = delay;
            this.exception = e;
        }

        synchronized public int getRetry() {
            return retry;
        }

        synchronized public void setRetry(int retry) {
            this.retry = retry;
        }
    }

    /**
     * part we are going to download.
     */
    private List<Part> parts;
    private long partLength;

    /**
     * total bytes downloaded. for chunk download progress info. for one thread count - also local file size;
     */
    private long count;

    public DownloadInfo(URL source) {
        super(source);
    }

    public DownloadInfo(URL source, ProxyInfo p) {
        super(source);
        setProxy(p);
    }

    /**
     * is it a multipart download?
     * 
     * @return
     */
    synchronized public boolean multipart() {
        if (!getRange())
            return false;
        return parts != null;
    }

    synchronized public void reset() {
        setCount(0);
        if (parts != null) {
            for (Part p : parts) {
                p.setCount(0);
                p.setState(Part.States.QUEUED);
            }
        }
    }

    /**
     * for multi part download, call every time when we need to know total download progress
     */
    synchronized public void calculate() {
        setCount(0);
        for (Part p : getParts())
            setCount(getCount() + p.getCount());
    }

    synchronized public List<Part> getParts() {
        return parts;
    }

    synchronized public long getPartLength() {
        return partLength;
    }

    synchronized public void enableMultipart() {
        enableMultipart(PART_LENGTH);
    }

    synchronized public void enableMultipart(long partLength) {
        if (empty())
            throw new RuntimeException("Empty Download info, cant set multipart");

        if (!getRange())
            throw new RuntimeException("Server does not support RANGE, cant set multipart");

        this.partLength = partLength;

        long count = getLength() / partLength + 1;

        if (count > 2) {
            parts = new ArrayList<Part>();

            long start = 0;
            for (int i = 0; i < count; i++) {
                Part part = new Part();
                part.setNumber(i);
                part.setStart(start);
                part.setEnd(part.getStart() + partLength - 1);
                if (part.getEnd() > getLength() - 1)
                    part.setEnd(getLength() - 1);
                part.setState(Part.States.QUEUED);
                parts.add(part);

                start += partLength;
            }
        }
    }

    /**
     * Check if we can continue download a file from new source. Check if new souce has the same file length, title. and
     * supports for range
     * 
     * @param oldInfo
     *            old info source
     * @return true - possible to resume from new location
     */
    synchronized public boolean canResume(DownloadInfo oldInfo) {
        if (!oldInfo.getRange())
            return false;

        if (oldInfo.getContentFilename() != null && this.getContentFilename() != null) {
            if (!oldInfo.getContentFilename().equals(this.getContentFilename()))
                // one source has different name
                return false;
        } else if (oldInfo.getContentFilename() != null || this.getContentFilename() != null) {
            // one source has a have old is not
            return false;
        }

        if (oldInfo.getLength() != null && this.getLength() != null) {
            if (!oldInfo.getLength().equals(this.getLength()))
                // one source has different length
                return false;
        } else if (oldInfo.getLength() != null || this.getLength() != null) {
            // one source has length, other is not
            return false;
        }

        if (oldInfo.getContentType() != null && this.getContentType() != null) {
            if (!oldInfo.getContentType().equals(this.getContentType()))
                // one source has different getContentType
                return false;
        } else if (oldInfo.getContentType() != null || this.getContentType() != null) {
            // one source has a have old is not
            return false;
        }

        return true;
    }

    /**
     * copy resume data from oldSource;
     */
    synchronized public void resume(DownloadInfo oldSource) {
        super.resume(oldSource);
        parts = new ArrayList<Part>();
        for (int i = 0; i < oldSource.parts.size(); i++) {
            parts.add(new Part(parts.get(i)));
        }
        partLength = oldSource.partLength;
        count = oldSource.count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public void extract(final AtomicBoolean stop, final Runnable notify) {
        super.extract(stop, notify);
    }
}
