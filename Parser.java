package crawler;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Parser extends SwingWorker<Boolean, Integer> {
    private WebCrawler crawler;
    private LinkTable linkTable;
    private Timer timer;
    private ConcurrentLinkedQueue<String> queue;
    private int processedDepth;
    private volatile int counter;
    private volatile int parsedPages;
    private ExecutorService service;
    private boolean timeIsOver;

    public Parser(WebCrawler crawler) {
        this.crawler = crawler;
        this.parsedPages = 0;
        this.processedDepth = 1;
        this.counter = 1;
        timeIsOver = false;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        this.linkTable = new LinkTable();
        queue = new ConcurrentLinkedQueue<>();
        resetAllCounters();
        int workers = crawler.getWorkers();
        int depth = crawler.getDepth();
        queue.add(crawler.getUrl());
        new ParserWorker(this).run(crawler.getUrl());
        crawler.disableAllElements();
        int timeDelay = 1000;
        long start = System.currentTimeMillis();
        int timeLimit = crawler.getTimeLimit();
        ActionListener time = e -> {
            crawler.setElapsedTime(formatTime(System.currentTimeMillis() - start));
            if ((System.currentTimeMillis() - start) / 1000 >= timeLimit) {
                timeIsOver = true;
            }
        };
        timer = new Timer(timeDelay, time);
        timer.start();
        service = Executors.newFixedThreadPool(workers);
        while ((processedDepth < depth || !crawler.depthIsSelected()) &&
                (!timeIsOver || !crawler.timeLimitIsSelected())) {
            service.execute(new ParserWorker(this));
            Thread.sleep(100);
            parsedPages = linkTable.parsedPages();
            publish(parsedPages);
        }

        return true;
    }

    private void resetAllCounters() {
        crawler.setParsedPages(0);
        crawler.setElapsedTime(formatTime(0));
        counter = 1;
        processedDepth = 1;
        timeIsOver = false;
    }

    private String formatTime(long milliseconds) {
        int x = (int) (milliseconds / 1000);
        int seconds = x % 60;
        x /= 60;
        int minutes = x;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void process(List<Integer> chunks) {
        int value = chunks.get(chunks.size() - 1);
        crawler.setParsedPages(value);
    }

    @Override
    protected void done() {
        service.shutdownNow();
        crawler.enableAllElements();
        crawler.unSelectRunButton();
        timer.stop();
        super.done();
    }

    public void export() {
        String path = this.crawler.getExportPath();
        File file = new File(path);
        writeToFile(linkTable, file);
    }

    private boolean writeToFile(LinkTable linkTable, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (int i = 0; i < linkTable.parsedPages(); i++) {
                fileWriter.write(linkTable.getLink(i) + "\n");
                fileWriter.write(linkTable.getTitle(i) + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public synchronized void incrementDepth() {
        processedDepth++;
    }

    public int getProcessedDepth() {
        return processedDepth;
    }

    public synchronized void decrementCounter() {
        counter--;
    }

    public synchronized void setCounter(int number) {
        counter = number;
    }

    public synchronized boolean queueIsEmpty() {
        return queue.isEmpty();
    }

    public synchronized String queuePoll() {
        return queue.poll();
    }

    public synchronized void queueAdd(String task) {
        queue.add(task);
    }

    public synchronized LinkTable getLinkTable() {
        return linkTable;
    }


    public synchronized boolean counterIsZero() {
        return counter == 0;
    }

    public synchronized int getQueueSize() {
        return queue.size();
    }

}
