package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

public class UltrasonicRadarPanel extends PolarChartPanel{
    private final JFreeChart ultrasonicRadarChart;
    private final XYDataset dataset;
    private XYSeries frontLeft;
    private XYSeries frontRight;
    private XYSeries behind;
    private XYSeries frontCenter;

    public static UltrasonicRadarPanel create(){

        XYSeries frontLeft = new XYSeries("FrontLeft");
        XYSeries frontRight = new XYSeries("FrontRight");
        XYSeries frontCenter = new XYSeries("FrontCenter");
        XYSeries behind = new XYSeries("Behind");

        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(frontLeft);
        dataset.addSeries(frontRight);
        dataset.addSeries(behind);
        dataset.addSeries(frontCenter);

        JFreeChart ultrasonicRadarChart = createUltrasonicRadar(dataset);

        return new UltrasonicRadarPanel( ultrasonicRadarChart,
                                         dataset,
                                         frontCenter,
                                         frontLeft,
                                         frontRight,
                                         behind);
    }

    private UltrasonicRadarPanel(JFreeChart ultrasonicRadarChart,
                                 XYDataset dataset,
                                 XYSeries frontCenter,
                                 XYSeries frontLeft,
                                 XYSeries frontRight,
                                 XYSeries behind) {
        super(ultrasonicRadarChart);
        this.ultrasonicRadarChart = ultrasonicRadarChart;
        this.dataset = dataset;
        this.frontCenter = frontCenter;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.behind = behind;
    }

    private static JFreeChart createUltrasonicRadar(XYDataset ultrasonicDataset) {
        final JFreeChart chart = ChartFactory.createPolarChart("", ultrasonicDataset, true, true, false);
        final PolarPlot plot = (PolarPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setAngleGridlinePaint(Color.BLACK);
        plot.setRadiusGridlinePaint(Color.LIGHT_GRAY);
        plot.getAxis().setRange(0, 350);
        plot.getAxis().setAutoRange(false);

        final DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) plot.getRenderer();
        renderer.setSeriesFilled(0, true);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesFilled(1, true);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesFilled(2, true);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesFilled(3, true);
        renderer.setSeriesPaint(3, Color.GREEN);
        return chart;
    }

    public void append( int frontCenter, int frontLeft, int frontRight, int behindDistance ){

        this.frontLeft.clear();
        this.frontLeft.add(0,0);
        this.frontLeft.add(-45, frontLeft);
        this.frontLeft.add(-15, frontLeft);
        this.frontLeft.add(0,0);


        this.frontRight.clear();
        this.frontRight.add(0,0);
        this.frontRight.add(15, frontRight);
        this.frontRight.add(45, frontRight);
        this.frontRight.add(0,0);


        this.behind.clear();
        this.behind.add(0,0);
        this.behind.add(165, behindDistance);
        this.behind.add(195, behindDistance);
        this.behind.add(0,0);

        this.frontCenter.clear();
        this.frontCenter.add(0, 0);
        this.frontCenter.add(-15, frontCenter);
        this.frontCenter.add(15, frontCenter);
        this.frontCenter.add(0,0);

        ultrasonicRadarChart.fireChartChanged();
    }
}
