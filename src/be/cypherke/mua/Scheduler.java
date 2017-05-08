package be.cypherke.mua;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class Scheduler {
    private ScheduledThreadPoolExecutor scheduler;
    private Mua mua;

    Scheduler(Mua mua) {
        this.mua = mua;
        scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        scheduler.setRemoveOnCancelPolicy(true);
        this.startSchedules();
    }

    private void startSchedules() {
        Runnable triggerEveryMinute = () -> {
        };
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        String nextminute = DateTime.now().plusMinutes(1).toString("dd/MM/yyyy HH:mm") + ":00";
        Interval interval = (new Interval(DateTime.now(), formatter.parseDateTime(nextminute)));
        long timeuntil = (interval.toDurationMillis() / 1000) + 1;
        scheduler.scheduleWithFixedDelay(triggerEveryMinute, timeuntil, 60, TimeUnit.SECONDS);
        Runnable triggerEveryHour = () -> {
            mua.getUsersDb().save();
            mua.getTeleportsDb().save();
        };
        scheduler.scheduleAtFixedRate(triggerEveryHour, 10, 60, TimeUnit.MINUTES);
    }
}
