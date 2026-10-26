package io.github.henrymiles3.stocksbot.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.springframework.stereotype.Service;

@Service
public class ChartService {


    public File generateStockChart(String symbol, List<Double> priceHistory) throws IOException{

        double [] xData = new double[priceHistory.size()];
        double [] yData = new double[priceHistory.size()];

        for(int i = 0; i < priceHistory.size(); i++){
            xData[i] = i + 1;
            yData[i] = priceHistory.get(i);
        }

        XYChart chart = new XYChartBuilder()
            .width(600)
            .height(400)
            .title(symbol + " Stock Price Trend")
            .xAxisTitle("Time")
            .yAxisTitle("Price ($)")
            .build();
        
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesVisible(true);

        chart.addSeries(symbol, xData, yData).setMarker(SeriesMarkers.NONE);

        File tempFile = File.createTempFile("chart_" + symbol, ".png");
        BitmapEncoder.saveBitmap(chart, tempFile.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
        return tempFile;
    }
}
