package crawler;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class WebCrawler extends JFrame implements ItemListener {

    private Parser parser;
    private JToggleButton runButton;
    private JButton exportButton;
    private JLabel elapsedTimeLabel;
    private JLabel parsedPagesLabel;
    private JTextField urlField;
    private JTextField workersField;
    private JTextField depthField;
    private JTextField timeLimitField;
    private JTextField exportTextField;
    private JCheckBox depthCheckBox;
    private JCheckBox timeLimitCheckBox;

    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 280;
    private static final String DEFAULT_TITLE = "Web crawler";

    public WebCrawler() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setTitle(DEFAULT_TITLE);
        int layoutGap = 5;
        Border padding = BorderFactory.createEmptyBorder(layoutGap, layoutGap, layoutGap, layoutGap);

        //URL row
        JLabel urlSign = new JLabel("Start URL: ");
        urlSign.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 37));
        this.urlField = new JTextField("https://www.yandex.ru");
        urlField.setName("UrlTextField");
        this.runButton = new JToggleButton("Run");
        runButton.setName("RunButton");
        runButton.addItemListener(this);
        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        urlPanel.setBorder(padding);
        urlPanel.add(urlSign, BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(runButton, BorderLayout.EAST);

        //workers row
        JLabel workersSign = new JLabel("Workers: ");
        workersSign.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 41));
        this.workersField = new JTextField("1");
        JPanel workersPanel = new JPanel();
        workersPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        workersPanel.setBorder(padding);
        workersPanel.add(workersSign, BorderLayout.WEST);
        workersPanel.add(workersField, BorderLayout.CENTER);

        //Depth row
        JLabel depthSign = new JLabel("Maximum depth: ");
        this.depthField = new JTextField("50");
        depthField.setName("DepthTextField");
        this.depthCheckBox = new JCheckBox("Enabled");
        depthCheckBox.setName("DepthCheckBox");
        depthCheckBox.setSelected(true);
        depthCheckBox.addItemListener(this);
        JPanel depthPanel = new JPanel();
        depthPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        depthPanel.setBorder(padding);
        depthPanel.add(depthSign, BorderLayout.WEST);
        depthPanel.add(depthField, BorderLayout.CENTER);
        depthPanel.add(depthCheckBox, BorderLayout.EAST);

        //Time limit row
        JLabel timeLimitSign = new JLabel("Time limit:");
        timeLimitSign.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        this.timeLimitField = new JTextField(21);
        timeLimitField.setText("120");
        JLabel timeLimitSeconds = new JLabel("seconds");
        JPanel timeLimitFieldPanel = new JPanel();
        timeLimitFieldPanel.add(timeLimitField);
        timeLimitFieldPanel.add(timeLimitSeconds);
        this.timeLimitCheckBox = new JCheckBox("Enabled");
        timeLimitCheckBox.setSelected(true);
        timeLimitCheckBox.addItemListener(this);
        JPanel timeLimitPanel = new JPanel();
        timeLimitPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        timeLimitPanel.setBorder(padding);
        timeLimitPanel.add(timeLimitSign, BorderLayout.WEST);
        timeLimitPanel.add(timeLimitFieldPanel, BorderLayout.CENTER);
        timeLimitPanel.add(timeLimitCheckBox, BorderLayout.EAST);

        //Elapsed time row
        JLabel elapsedTimeSign = new JLabel("Elapsed time: ");
        elapsedTimeSign.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 17));
        this.elapsedTimeLabel = new JLabel("0:00");
        JPanel elapsedTimePanel = new JPanel();
        elapsedTimePanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        elapsedTimePanel.setBorder(padding);
        elapsedTimePanel.add(elapsedTimeSign, BorderLayout.WEST);
        elapsedTimePanel.add(elapsedTimeLabel, BorderLayout.CENTER);

        //Parsed pages row
        JLabel parsedPagesSign = new JLabel("Parsed pages: ");
        parsedPagesSign.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 11));
        this.parsedPagesLabel = new JLabel("0");
        parsedPagesLabel.setName("ParsedLabel");
        JPanel parsedPagesPanel = new JPanel();
        parsedPagesPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        parsedPagesPanel.setBorder(padding);
        parsedPagesPanel.add(parsedPagesSign, BorderLayout.WEST);
        parsedPagesPanel.add(parsedPagesLabel, BorderLayout.CENTER);

        //export row
        JLabel exportLabel = new JLabel("Export: ");
        exportLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 52));
        this.exportTextField = new JTextField("C:/Dumps/LastCrawl.txt");
        exportTextField.setName("ExportUrlTextField");
        exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        JPanel exportPanel = new JPanel();
        exportPanel.setLayout(new BorderLayout(layoutGap, layoutGap));
        exportPanel.setBorder(padding);
        exportPanel.add(exportLabel, BorderLayout.WEST);
        exportPanel.add(exportTextField, BorderLayout.CENTER);
        exportPanel.add(exportButton, BorderLayout.EAST);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(urlPanel);
        main.add(workersPanel);
        main.add(depthPanel);
        main.add(timeLimitPanel);
        main.add(elapsedTimePanel);
        main.add(parsedPagesPanel);
        main.add(exportPanel);

        add(main);

        setVisible(true);

        exportButton.addActionListener(event -> parser.export());

    }

    public String getUrl() {
        return urlField.getText();
    }

    public int getWorkers() {
        if (workersField.getText().isEmpty()) {
            return 1;
        }
        return Integer.parseInt(workersField.getText());
    }

    public int getDepth() {
        if (depthField.getText().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(depthField.getText());
    }


    public String getExportPath() {
        return exportTextField.getText();
    }

    public void disableAllElements() {
        urlField.setEnabled(false);
        workersField.setEnabled(false);
        depthField.setEnabled(false);
        depthCheckBox.setEnabled(false);
        timeLimitField.setEnabled(false);
        timeLimitCheckBox.setEnabled(false);
        exportTextField.setEnabled(false);
        exportButton.setEnabled(false);

    }

    public void setParsedPages(int value) {
        this.parsedPagesLabel.setText(String.valueOf(value));
    }


    public void enableAllElements() {
        urlField.setEnabled(true);
        workersField.setEnabled(true);
        depthField.setEnabled(true);
        depthCheckBox.setEnabled(true);
        timeLimitField.setEnabled(true);
        timeLimitCheckBox.setEnabled(true);
        exportTextField.setEnabled(true);
        exportButton.setEnabled(true);
    }

    public void setElapsedTime(String time) {
        elapsedTimeLabel.setText(time);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == runButton) {
            if (runButton.isSelected()) {
                parser = new Parser(this);
                parser.execute();
            } else {
                parser.done();
            }
        }
        if (e.getItem() == depthCheckBox) {
            depthField.setEnabled(depthCheckBox.isSelected());
        }

        if (e.getItem() == timeLimitCheckBox) {
            timeLimitField.setEnabled(timeLimitCheckBox.isSelected());
        }
    }

    public boolean depthIsSelected() {
        return depthCheckBox.isSelected();
    }

    public boolean timeLimitIsSelected() {
        return timeLimitCheckBox.isSelected();
    }

    public int getTimeLimit() {
        if (timeLimitField.getText().isEmpty()) {
            return 120;
        } else {
            return Integer.parseInt(timeLimitField.getText());
        }
    }

    public void unSelectRunButton() {
        runButton.setSelected(false);
    }
}

